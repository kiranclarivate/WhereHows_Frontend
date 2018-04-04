package com.clarivate.cc.wherehows.dao;

import com.clarivate.cc.wherehows.RestClient;
import com.clarivate.cc.wherehows.model.zeppelin_api.NewNoteResponse;
import com.clarivate.cc.wherehows.util.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.util.StringUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserWorkspaceDao extends AbstractMySQLDAO{

    static Logger LOG = LoggerFactory.getLogger(UserWorkspaceDao.class);
    private static final String ZEPPELIN_NOTE_PATH = "/#/notebook/";

    private static final String GET_WORKSPACE_BY_USERNAME_DB_ID = "SELECT db_id, notebook_id, note_url, interpreter_name FROM user_workspace WHERE user_name = '$user_name' and db_id = $db_id";
    private static final String INSERT_USER_WORKSPACE = "REPLACE INTO user_workspace (user_name, db_id, notebook_id, note_url, interpreter_name) VALUES (?,?,?,?,?)";
    private static final String GET_DB_INFO = "SELECT db_id, type, alias, zeppelin_host, interpreter_name, database_name FROM db_info WHERE db_id = $db_id";
    //on duplicate key update

    public Map<String, Object> getUserWorkspace(String  user_name, int db_id, String zeppelinConfigPrefix, String tbl) {
        try {
            String sql = GET_WORKSPACE_BY_USERNAME_DB_ID
                    .replace("$user_name", user_name)
                    .replace("$db_id", Integer.toString(db_id));

            List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql);

            LOG.info("rows::" + rows);
            if (rows.isEmpty()){
                LOG.info("user's workspace not yet created for the db_id, creating one ...");
                Map<String, Object> dbInfo = getDbInfo(db_id);
                String zeppelin_host = dbInfo.get("zeppelin_host").toString();
                if (StringUtils.isEmpty(zeppelin_host)) {
                    Config config = new Config();
                    zeppelin_host = config.getString(zeppelinConfigPrefix + "zeppelin.host");
                }
                String zeppelinUrl = zeppelin_host + ZEPPELIN_NOTE_PATH;

                createNewNotebook(user_name, db_id, dbInfo, zeppelinUrl, tbl);
                return getJdbcTemplate().queryForList(sql).get(0);
            } else {
                LOG.info ("workspace already exists");
                LOG.info(rows.get(0).toString());
                return rows.get(0);
            }
        } catch (EmptyResultDataAccessException e) {
            LOG.warn("Can not find user_workspace for user_id/db_id: creating new note ..." + user_name + "/" + db_id + " : " + e.getMessage());
            return null;
        }
    }

    private String createNewNotebook(String  user_name, int db_id, Map<String, Object> dbInfo, String zeppelinUrl, String tbl){
        LOG.info("dbInfo::" + dbInfo);

        String alias = dbInfo.get("alias").toString();
        alias = StringUtils.isEmpty(alias)? "db_" + db_id : alias;   //default to db_# if not specified

        String interpreter_name = dbInfo.get("interpreter_name").toString();
        String dbType = dbInfo.get("type").toString();
        LOG.info("dbType::" + dbType);

        RestClient client = new RestClient();
        NewNoteResponse response = client.getNewNoteResponse(user_name+ "/" + alias, interpreter_name, tbl, dbType);

        String noteId = response.getBody();

        insertUserWorkspace(user_name, db_id, noteId, zeppelinUrl + response.getBody(), interpreter_name);
        return noteId;
    }

    public Map<String, Object> getDbInfo(int db_id) {
        String sql = GET_DB_INFO
                 .replace("$db_id", Integer.toString(db_id));
        LOG.info(sql);
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql);
        LOG.info ("result == " + rows);
        if (! rows.isEmpty()){
            return rows.get(0);
        } else{ //Config not done correctly
            HashMap<String, Object> result = new HashMap<>();
            //TODO: default values for db_id, type, alias, zeppelin_host, interpreter_name
            result.put("db_id", db_id);
            result.put("type", "mysql1");
            result.put("alias", "db_" + db_id);
            result.put("zeppelin_host", "");
            result.put("interpreter_name","sql");
            result.put("database_name", "");
            return result;
        }
    }

    public void insertUserWorkspace(String user_id, int db_id, String notebook_id, String url, String interpreter_name) {
        getJdbcTemplate().update(INSERT_USER_WORKSPACE, user_id, db_id, notebook_id, url, interpreter_name);
    }
}


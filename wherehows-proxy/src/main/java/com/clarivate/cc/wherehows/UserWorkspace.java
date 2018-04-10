package com.clarivate.cc.wherehows;

import com.clarivate.cc.wherehows.dao.AbstractMySQLDAO;
import com.clarivate.cc.wherehows.model.zeppelin_api.APIResponse;
import com.clarivate.cc.wherehows.util.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserWorkspace extends AbstractMySQLDAO {
    private static final String ZEPPELIN_NOTE_PATH = "/#/notebook/";
    private static final String GET_DB_INFO = "SELECT db_id, type, zeppelin_host, interpreter_name, database_name FROM db_info WHERE db_id = $db_id";
    static Logger LOG = LoggerFactory.getLogger(UserWorkspace.class);

    public String getWorkspaceUrl(String  user_name, int db_id, String zeppelinConfigPrefix, String tbl){
        Map<String, Object> dbInfo = getDbInfo(db_id);
        /*
        {db_id=82, type=oracle, zeppelin_host=http://ec2-35-163-133-71.us-west-2.compute.amazonaws.com:8890, interpreter_name=%oracle, database_name=}
         */
        String zeppelin_host = dbInfo.get("zeppelin_host").toString();
        if (StringUtils.isEmpty(zeppelin_host)) {
            Config config = new Config();
            zeppelin_host = config.getString(zeppelinConfigPrefix + "zeppelin.host");
        }
        String zeppelinUrl = zeppelin_host + ZEPPELIN_NOTE_PATH;

        String dbType = dbInfo.get("type").toString();

        ZeppelinRestClient apiClient = new ZeppelinRestClient();

        String noteId = apiClient.getUserDbNote(user_name, dbType, db_id);
        LOG.info("noteId == " + noteId);
        LOG.info(dbInfo.toString());

        if (noteId.isEmpty()){
            noteId = createNewNotebook(user_name, db_id, dbInfo, zeppelinUrl, tbl);
        } else{
            apiClient.addNewParagraph(noteId, dbInfo.get("interpreter_name").toString(), tbl, dbType);
        }

        return zeppelinUrl + noteId;
    }

    private String createNewNotebook(String user_name, int db_id, Map<String, Object> dbInfo, String zeppelinUrl, String tbl){
        LOG.info("dbInfo::" + dbInfo);

        String interpreter_name = dbInfo.get("interpreter_name").toString();
        String dbType = dbInfo.get("type").toString();
        LOG.info("dbType::" + dbType);

        String alias = dbType + "_" + db_id;

        ZeppelinRestClient client = new ZeppelinRestClient();
        APIResponse response = client.getNewNoteResponse(user_name,alias, interpreter_name, tbl, dbType);

        String noteId = response.getBody();
        return noteId;
    }

    private Map<String, Object> getDbInfo(int db_id) {
        String sql = GET_DB_INFO
                .replace("$db_id", Integer.toString(db_id));
        LOG.info(sql);
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql);
        LOG.info ("result == " + rows);
        if (! rows.isEmpty()){
            return rows.get(0);
        } else{ //Config not done correctly
            HashMap<String, Object> result = new HashMap<>();
            //TODO: add default values for db_id, type, alias, zeppelin_host, interpreter_name
            result.put("db_id", db_id);
            result.put("type", "mysql");
            result.put("alias", "mysql_" + db_id);
            result.put("zeppelin_host", "");
            result.put("interpreter_name","sql");
            result.put("database_name", "");
            return result;
        }
    }
}

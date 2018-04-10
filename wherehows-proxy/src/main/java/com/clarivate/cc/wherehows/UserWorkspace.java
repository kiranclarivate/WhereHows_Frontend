package com.clarivate.cc.wherehows;

import com.clarivate.cc.wherehows.dao.AbstractMySQLDAO;
import com.clarivate.cc.wherehows.util.Config;
import com.clarivate.cc.wherehows.zeppelin.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserWorkspace extends AbstractMySQLDAO {
    private static final String ZEPPELIN_NOTE_PATH = "/#/notebook/";
    private static final String GET_DB_INFO = "SELECT db_id, type, zeppelin_host, interpreter_name, database_name, alias FROM db_info WHERE db_id = $db_id";
    static Logger LOG = LoggerFactory.getLogger(UserWorkspace.class);

    public String getWorkspaceUrl(String user_name, int db_id, String zeppelinConfigPrefix, String tbl){
        Map<String, Object> dbInfo = getDbInfo(db_id);
        /*
        {db_id=82, type=oracle, zeppelin_host=http://ec2-35-163-133-71.us-west-2.compute.amazonaws.com:8890, interpreter_name=%oracle, database_name=, alias=}
         */

        String zeppelin_host;
        if (dbInfo.get("zeppelin_host") == null || StringUtils.isEmpty(dbInfo.get("zeppelin_host"))){  // use default in config file
            Config config = new Config();
            zeppelin_host = config.getString(zeppelinConfigPrefix + "zeppelin.host");
        } else {
            zeppelin_host = dbInfo.get("zeppelin_host").toString();
        }
        String zeppelinUrl = zeppelin_host + ZEPPELIN_NOTE_PATH;

        String dbType = dbInfo.get("type").toString();

        String alias;
        if (dbInfo.get("alias") != null && ! StringUtils.isEmpty(dbInfo.get("alias").toString()))
            alias = dbInfo.get("alias").toString();
        else {
            alias = dbType + "_" + db_id;  // use alias such as "hive_1" where 1 is db_id/job_id in job file
        }
        RestClient apiClient = new RestClient();
        String noteId = apiClient.getUserDbNote(user_name, alias);
        LOG.info("noteId == " + noteId);
        LOG.info(dbInfo.toString());

        if (noteId.isEmpty()){
            noteId = apiClient.createNewNotebook(user_name, db_id, dbInfo, alias, tbl);
        } else{
            apiClient.addNewParagraph(noteId, dbInfo.get("interpreter_name").toString(), tbl, dbType);
        }

        return zeppelinUrl + noteId;
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

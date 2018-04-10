package com.clarivate.cc.wherehows;

import com.clarivate.cc.wherehows.model.zeppelin_api.*;
import com.clarivate.cc.wherehows.util.Config;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

public class ZeppelinRestClient {
    private String zeppelinAPIUrl;
    private static Logger LOG = LoggerFactory.getLogger(ZeppelinRestClient.class);
    private static ObjectMapper mapper = new ObjectMapper();
    private static final String ZEPPELIN_API_PATH = "/api/notebook";

    public ZeppelinRestClient(String zeppelinApiUrlPrefix){
        Config config= new Config();
        zeppelinAPIUrl = config.getString(zeppelinApiUrlPrefix + "zeppelin.host") + ZEPPELIN_API_PATH;
    }

    public ZeppelinRestClient(){
        this("");
    }

    public APIResponse getNewNoteResponse (String userName, String notebookName, String interpreterName, String tbl, String dbType) {
        RestTemplate restTemplate = new RestTemplate();
        // APIResponse response = restTemplate.getForObject(zeppelinAPIUrl, APIResponse.class);

        String name = userName + "/" + notebookName;

        HttpEntity<NewNoteRequest> request = new HttpEntity<>(getNewNoteRequest(name, interpreterName, tbl, dbType));
        LOG.info("API URL:" + zeppelinAPIUrl);
        LOG.info("request:" + request.toString());
        APIResponse response = restTemplate.postForObject(zeppelinAPIUrl, request, APIResponse.class);
        LOG.info("response:" + response.toString());

        try {
            getMyNotebooks(userName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    private NewNoteRequest getNewNoteRequest(String name, String interpreterName, String tbl, String dbType){
        return new NewNoteRequest(name, interpreterName, tbl, dbType);
    }

    public void getMyNotebooks(String userName) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        NoteListResponse response = restTemplate.getForObject(zeppelinAPIUrl, NoteListResponse.class);
        System.out.println(response.getUserNotes(userName));
        System.out.println("notebook_id == " +response.getUserDbNote("richardx", "hive",1));
        LOG.info("response:" + response.getUserNotes(userName));
    }

    public String getUserDbNote(String userName, String dbType, int dbId){
        RestTemplate restTemplate = new RestTemplate();
        NoteListResponse response = restTemplate.getForObject(zeppelinAPIUrl, NoteListResponse.class);
        return response.getUserDbNote(userName, dbType, dbId);
    }

    private String getParagraphID(String notebookId, String paragraphName){
        RestTemplate restTemplate = new RestTemplate();
        NotebookStatusResponse response = restTemplate.getForObject(zeppelinAPIUrl + "/" + notebookId, NotebookStatusResponse.class);
        LOG.info("URL===" + zeppelinAPIUrl + "/" + notebookId);
        LOG.info("response===" + response.toString());

        for (Paragraph p : response.getBody().getParagraphs()){
            if (p.getTitle() != null && p.getTitle().equals(paragraphName))
                return p.getId();
        }
        return null;
    }

    public String createNewNotebook(String user_name, int db_id, Map<String, Object> dbInfo, String zeppelinUrl, String tbl){
        LOG.info("dbInfo::" + dbInfo);

        String interpreter_name = dbInfo.get("interpreter_name").toString();
        String dbType = dbInfo.get("type").toString();
        String alias = dbType + "_" + db_id;  //always use alias such as "hive_1" where 1 is db_id/job_id in job file

        APIResponse response = getNewNoteResponse(user_name,alias, interpreter_name, tbl, dbType);

        String noteId = response.getBody();
        return noteId;
    }

    private boolean moveParagraph(String noteId, String paragraphId, int index){
        RestTemplate restTemplate = new RestTemplate();
        //http://[zeppelin-server]:[zeppelin-port]/api/notebook/[noteId]/paragraph/[paragraphId]/move/[newIndex]
        String url = zeppelinAPIUrl + "/" + noteId + "/paragraph/" + paragraphId + "/move/" + index;

        LOG.info("moving to top ... " + url);

        HttpEntity<Paragraph> request = new HttpEntity<>(new Paragraph());
        APIResponse response = restTemplate.postForObject(url, request, APIResponse.class);
        return response.getStatus().equals("OK");
    }

    public void addNewParagraph(String noteId, String interpreterName, String tbl, String dbType){
        String title = "workspace_" + tbl;  // fixed naming convention
        String paragraphId = getParagraphID(noteId, title);
        if (paragraphId != null && !paragraphId.isEmpty()){
            moveParagraph(noteId, paragraphId, 0);
            return;
        }

        LOG.info("Adding new paragraph ... ");
        RestTemplate restTemplate = new RestTemplate();

        Paragraph p = new Paragraph();
        p.setTitle("workspace_" + tbl);
        String sampleSql;
        switch (dbType){
            case "hive":
                sampleSql =  StringUtils.isEmpty(tbl) ? " " : "SELECT * FROM " + tbl + " LIMIT 10\n";
                break;
            case "oracle":
                sampleSql =  StringUtils.isEmpty(tbl) ? " " : "SELECT * FROM " + tbl + " where ROWNUM <= 10;\n";
                break;
            default:
                sampleSql = "";
        }
        p.setText(interpreterName + "\n" + sampleSql);
        p.setIndex(0);
        HttpEntity<Paragraph> request = new HttpEntity<>(p);

        APIResponse response = restTemplate.postForObject(zeppelinAPIUrl + "/" + noteId + "/paragraph", request, APIResponse.class);

        LOG.info(response.toString());
    }
}
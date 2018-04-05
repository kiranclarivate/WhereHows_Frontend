package com.clarivate.cc.wherehows;

import com.clarivate.cc.wherehows.model.zeppelin_api.NewNoteRequest;
import com.clarivate.cc.wherehows.model.zeppelin_api.NewNoteResponse;
import com.clarivate.cc.wherehows.util.Config;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public NewNoteResponse getNewNoteResponse (String userName, String notebookName, String interpreterName, String tbl, String dbType) {
        RestTemplate restTemplate = new RestTemplate();
        // NewNoteResponse response = restTemplate.getForObject(zeppelinAPIUrl, NewNoteResponse.class);

        String name = userName + "/" + notebookName;

        HttpEntity<NewNoteRequest> request = new HttpEntity<>(getNewNoteRequest(name, interpreterName, tbl, dbType));
        LOG.info("API URL:" + zeppelinAPIUrl);
        LOG.info("request:" + request.toString());
        NewNoteResponse response = restTemplate.postForObject(zeppelinAPIUrl, request, NewNoteResponse.class);
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

    private void getMyNotebooks(String userName) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        NoteListResponse response = restTemplate.getForObject(zeppelinAPIUrl, NoteListResponse.class);
        LOG.info("response:" + response.getUserNotes(userName));
    }

    static class NoteListResponse{
        private String status;
        private String message;
        private List<Map<String, String>> body;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public List<Map<String, String>> getBody() {
            return body;
        }

        public void setBody(List<Map<String, String>> body) {
            this.body = body;
        }

        public List<Map<String, String>> getUserNotes(String userName){

            List<Map<String, String>> result = new ArrayList<>();
            for (Map<String, String> map : this.body){
                if (map.get("name").startsWith(userName + "/")){
                    result.add(map);
                }
            }

            return result;

        }

        public String toString(){
            return body.toString();
        }
    }
}
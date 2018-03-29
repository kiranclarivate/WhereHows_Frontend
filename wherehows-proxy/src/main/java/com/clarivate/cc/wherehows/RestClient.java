package com.clarivate.cc.wherehows;

import com.clarivate.cc.wherehows.model.zeppelin_api.NewNoteRequest;
import com.clarivate.cc.wherehows.model.zeppelin_api.NewNoteResponse;
import com.clarivate.cc.wherehows.util.Config;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

public class RestClient {
    private String zeppelinAPIUrl;

    private static final String ZEPPELIN_API_PATH = "/api/notebook";

    public RestClient(String zeppelinApiUrlPrefix){
        Config config= new Config();
        zeppelinAPIUrl = config.getString(zeppelinApiUrlPrefix + "zeppelin.host") + ZEPPELIN_API_PATH;
    }

    public RestClient(){
        this("");
    }

    public NewNoteResponse getNewNoteResponse (String name, String interpreterName, String tbl, String db_name){
        RestTemplate restTemplate = new RestTemplate();
        // NewNoteResponse response = restTemplate.getForObject(zeppelinAPIUrl, NewNoteResponse.class);

        HttpEntity<NewNoteRequest> request = new HttpEntity<>(getNewNoteRequest(name, interpreterName, tbl, db_name));
        System.out.println("API URL:" + zeppelinAPIUrl);
        System.out.println("request:" + request.toString());
        NewNoteResponse response = restTemplate.postForObject(zeppelinAPIUrl, request, NewNoteResponse.class);
        System.out.println("response:" + response.toString());
        return response;
    }

    private NewNoteRequest getNewNoteRequest(String name, String interpreterName, String tbl, String db_name){
        return new NewNoteRequest(name, interpreterName, tbl, db_name);
    }
}
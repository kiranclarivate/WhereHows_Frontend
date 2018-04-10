package com.clarivate.cc.wherehows.model.zeppelin_api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NoteListResponse{
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

    public String getUserDbNote(String userName, String alias){

        for (Map<String, String> map : this.body){
            if (map.get("name").equals(getNotebookName(userName, alias))){
                return map.get("id");
            }
        }
        return "";
    }

    public String toString(){
        return body.toString();
    }

    private String getNotebookName(String userName, String alias){
        return userName + "/" + alias;
    }
}
package com.clarivate.cc.wherehows.model.zeppelin_api;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NotebookStatusResponse {
    private static final ObjectMapper mapper = new ObjectMapper();
    private String status;
    private String message;
    private Body body;

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

    @Override
    public String toString() {
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static public class Body{
        private static final ObjectMapper bodyMapper = new ObjectMapper();
        private List<Paragraph> paragraphs;
        private String name;
        private String id;

        public List<Paragraph> getParagraphs() {
            return paragraphs;
        }

        public void setParagraphs(List<Paragraph> paragraphs) {
            this.paragraphs = paragraphs;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
        @Override
        public String toString() {
            try {
                return bodyMapper.writeValueAsString(this);
            } catch (JsonProcessingException e) {
                return null;
            }
        }
    }
}

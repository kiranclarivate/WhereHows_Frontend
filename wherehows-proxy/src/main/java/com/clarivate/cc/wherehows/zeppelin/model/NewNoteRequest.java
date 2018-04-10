package com.clarivate.cc.wherehows.zeppelin.model;

import com.clarivate.cc.wherehows.util.NotebookUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NewNoteRequest {

    static Logger LOG = LoggerFactory.getLogger(NewNoteRequest.class);

    private String name;
    private String tbl;
    //private String dbName;
    private List<Paragraph> paragraphs;

    public NewNoteRequest(String name, String interpreterName, String tbl, String dbType){
        this.name = name;
        this.tbl = tbl;
        Paragraph p = NotebookUtil.getSampleParagraph(tbl, dbType, interpreterName);
        this.paragraphs = new ArrayList<>(Arrays.asList(p));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Paragraph> getParagraphs() {
        return paragraphs;
    }

    public void setParagraphs(List<Paragraph> paragraphs) {
        this.paragraphs = paragraphs;
    }

    public String getTbl() {
        return tbl;
    }

    public void setTbl(String tbl) {
        this.tbl = tbl;
    }
}


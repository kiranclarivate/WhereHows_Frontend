package com.clarivate.cc.wherehows.model.zeppelin_api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.StringUtils;
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
        // this.dbName = dbName;

        /*
        Paragraph p1 = new Paragraph();
        p1.setTitle("paragraph 1");
        String useDBSql = StringUtils.isEmpty(dbName) ? "" : "USE " + dbName + "\n";
        LOG.info("Sample USE DB SQL = " + useDBSql);
        p1.setText(interpreterName + "\n" + useDBSql);
*/
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
        LOG.info("Sample SQL = " + sampleSql);
        p.setText(interpreterName + "\n" + sampleSql);

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


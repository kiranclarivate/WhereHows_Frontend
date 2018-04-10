package com.clarivate.cc.wherehows.util;

import com.clarivate.cc.wherehows.zeppelin.model.Paragraph;
import org.apache.commons.lang3.StringUtils;

public class NotebookUtil {
    public static Paragraph getSampleParagraph(String tbl, String dbType, String interpreterName){
        Paragraph p = new Paragraph();
        p.setTitle("workspace_" + tbl);
        String sampleSql;
        switch (dbType){
            case "hive":
                sampleSql =  StringUtils.isEmpty(tbl) ? " " : "-- sample query \nSELECT * FROM " + tbl + " LIMIT 10\n";
                break;
            case "oracle":
                sampleSql =  StringUtils.isEmpty(tbl) ? " " : "-- sample query \nSELECT * FROM " + tbl + " where ROWNUM <= 10;\n";
                break;
            default:
                sampleSql = "";
        }
        p.setText(interpreterName + "\n" + sampleSql);
        p.setIndex(0);
        return p;
    }
}

package com.clarivate.cc.wherehows.util;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.springframework.util.StringUtils;

import java.io.File;

public class Config {

    private static final String CONFIG_PATH = "application.properties";

    public String getString(String key){
        Configurations configs = new Configurations();
        try
        {
            Configuration config = configs.properties(new File(CONFIG_PATH));

            return config.getString(key);
        }
        catch (ConfigurationException cex)
        {
            return null;
        }
    }

    public String getStringOrDefault(String key, String defaultValue){
        String value = getString(key);

        if (StringUtils.isEmpty(value))
            return defaultValue;
        else
            return value;
    }

    public static void main(String[] args){  // Testing only
        Config config= new Config();
        System.out.print(config.getString("database.wherehows.url"));
    }

}


package com.clarivate.cc.wherehows.dao;

import com.clarivate.cc.wherehows.util.Config;

import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class MySQLDataSource extends DataSource
{
    public static String DatabaseType = "mysql";
    public static String MYSQL_DRIVER_CLASS = "com.mysql.jdbc.Driver";
    public static String DATABASE_WHEREHOWS_DB_USER_NAME_KEY = "database.wherehows.username";
    public static String DATABASE_WHEREHOWS_DB_USER_PASSWORD_KEY = "database.wherehows.password";
    public static String DATABASE_WHEREHOWS_DB_URL_KEY = "database.wherehows.url";

    @Override
    public String getType()
    {
        return DatabaseType;
    }

    public MySQLDataSource()
    {
        setDriverClass(MYSQL_DRIVER_CLASS);
        Config config = new Config();
        setUsername(config.getString(DATABASE_WHEREHOWS_DB_USER_NAME_KEY));
        setPassword(config.getString(DATABASE_WHEREHOWS_DB_USER_PASSWORD_KEY));
        setJdbcUrl(config.getString(DATABASE_WHEREHOWS_DB_URL_KEY));

        setIdleConnectionTestPeriodInMinutes(1);
        setIdleMaxAgeInMinutes(1);
        setMaxConnectionsPerPartition(10);
        setMinConnectionsPerPartition(5);
        setPartitionCount(3);
        setAcquireIncrement(5);
        setStatementsCacheSize(100);
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}


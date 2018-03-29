package com.clarivate.cc.wherehows.dao;

import com.jolbox.bonecp.BoneCPDataSource;

public abstract class DataSource extends BoneCPDataSource
{
    public abstract String getType();

    public DataSource()
    {
        super();
    }

    public static DataSource getDataSource()
    {
        return new MySQLDataSource();
    }
}


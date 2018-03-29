package com.clarivate.cc.wherehows.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class AbstractMySQLDAO
{
    private static JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSource.getDataSource());
    private static NamedParameterJdbcTemplate nPJdbcTemplate =
            new NamedParameterJdbcTemplate(DataSource.getDataSource());

    protected static JdbcTemplate getJdbcTemplate()
    {
        return jdbcTemplate;
    }

    protected static NamedParameterJdbcTemplate getNamedParameterJdbcTemplate()
    {
        return nPJdbcTemplate;
    }
}


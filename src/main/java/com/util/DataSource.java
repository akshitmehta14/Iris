package com.util;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class DataSource {

    public static DriverManagerDataSource getDataSource(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost/iris");
        dataSource.setUsername("akshit");
        dataSource.setPassword("qwerty700!");
        return dataSource;
    }
}
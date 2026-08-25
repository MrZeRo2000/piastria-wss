package com.romanpulov.piastriawss.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import jakarta.servlet.ServletContext;
import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Objects;

@Configuration
public class DBConfig {

    private final ServletContext context;
    private final DBProperties dbProperties;

    @Autowired
    public DBConfig(ServletContext context, DBProperties dbProperties) {
        this.context = context;
        this.dbProperties = dbProperties;
    }

    private String getUrl() {
        return context.getInitParameter("db-url");
    }

    private String getBackupPath() {
        return context.getInitParameter("db-backup-path");
    }

    @Bean
    public DBFileInfo dbFileInfo() {
        String dbUrl = getUrl();
        String[] splitDBUrl = dbUrl.split(":");
        String[] dbFileNameArray = Arrays.copyOfRange(splitDBUrl, 2, splitDBUrl.length);
        String dbFileName = String.join(":", dbFileNameArray);
        String dbBackupPath =  getBackupPath();

        return new DBFileInfo(dbFileName, dbBackupPath, dbProperties.getBackupFileName());
    }

    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(Objects.requireNonNull(dbProperties.getDriverClassName()));
        dataSource.setUrl(getUrl());
        dataSource.setUsername(dbProperties.getUserName());
        dataSource.setPassword(dbProperties.getPassword());
        return dataSource;
    }
}

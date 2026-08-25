package com.romanpulov.piastriawss.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:db.properties")
@ConfigurationProperties(prefix = "db")
public class DBProperties {
    private String driverClassName;
    private String userName;
    private String password;
    private String backupFileName;

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBackupFileName() {
        return backupFileName;
    }

    public void setBackupFileName(String backupFileName) {
        this.backupFileName = backupFileName;
    }

    @Override
    public String toString() {
        return "DBProperties{" +
                "driverClassName='" + driverClassName + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", backupFileName='" + backupFileName + '\'' +
                '}';
    }
}

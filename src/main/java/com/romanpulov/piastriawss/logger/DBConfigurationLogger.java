package com.romanpulov.piastriawss.logger;

import com.romanpulov.piastriawss.config.DBFileInfo;
import com.romanpulov.piastriawss.config.DBProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class DBConfigurationLogger {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DBProperties dbProperties;

    private final DBFileInfo dbFileInfo;

    public DBConfigurationLogger(DBProperties dbProperties, DBFileInfo dbFileInfo) {
        this.dbProperties = dbProperties;
        this.dbFileInfo = dbFileInfo;
    }

    @PostConstruct
    private void postConstruct() {
        logger.info("Database configuration: " + dbProperties + " " + dbFileInfo);
    }
}

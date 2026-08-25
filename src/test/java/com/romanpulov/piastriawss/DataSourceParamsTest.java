package com.romanpulov.piastriawss;

import com.romanpulov.piastriawss.config.DBFileInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.jdbc.datasource.DriverManagerDataSource;
import javax.sql.DataSource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DataSourceParamsTest {

    @Autowired
    DataSource dataSource;

    @Autowired
    DBFileInfo dbFileInfo;

    @Test
    void databasePathTest() {
        Assertions.assertNotNull(this.dataSource);
        String databaseUrl = ((DriverManagerDataSource) dataSource).getUrl();
        Assertions.assertNotNull(databaseUrl);
        String databaseFilePath = databaseUrl.split(":")[2];
        Assertions.assertNotNull(databaseFilePath);
        Assertions.assertEquals("db/database/piastria-test.db", databaseFilePath);
        // db/database/piastria-test.db
    }

    @Test
    void dbFileInfoTest() {
        Assertions.assertNotNull(this.dbFileInfo);
        Assertions.assertEquals("db/database/piastria-test.db", this.dbFileInfo.getDBFileName());
        Assertions.assertEquals("db/database/backup", this.dbFileInfo.getDBBackupPath());
    }
}

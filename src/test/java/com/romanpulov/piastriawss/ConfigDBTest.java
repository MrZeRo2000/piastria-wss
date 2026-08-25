package com.romanpulov.piastriawss;

import com.romanpulov.piastriawss.config.DBProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConfigDBTest {

    @Autowired
    DBProperties dbProperties;

    @Test
    void testDBProperties() {
        Assertions.assertEquals("piastria-db-backup", dbProperties.getBackupFileName());
    }
}

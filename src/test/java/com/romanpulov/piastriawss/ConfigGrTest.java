package com.romanpulov.piastriawss;

import com.romanpulov.piastriawss.config.GrProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConfigGrTest {

    @Autowired
    GrProperties grProperties;

    @Test
    void testAppProperties() {
        //should look like a version number
        Assertions.assertEquals(3, grProperties.getVersion().split("\\.").length);
    }
}

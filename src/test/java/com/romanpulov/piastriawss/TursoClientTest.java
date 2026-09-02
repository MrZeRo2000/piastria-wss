package com.romanpulov.piastriawss;

import com.romanpulov.tursocore.TursoClient;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TursoClientTest {
    @Autowired
    TursoClient client;

    @Test
    void testClient() {
        assertThat(this.client).isNotNull();
    }

    @Test
    @Tag("integration")
    void testSimpleSQL() throws Exception {
        var result = this.client.executeQuery("SELECT 15 AS value");
        assertThat(result).isNotNull();
    }
}

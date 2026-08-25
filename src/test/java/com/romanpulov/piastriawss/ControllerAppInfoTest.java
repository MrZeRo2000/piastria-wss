package com.romanpulov.piastriawss;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ControllerAppInfoTest extends ControllerMockMvcTest {

    public ControllerAppInfoTest(WebApplicationContext context) {
        super(context);
    }

    @Test
    void testAppInfo() throws Exception {
        this.mvc.perform(MockMvcRequestBuilders.get("/app-info")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.version").exists())
        ;
    }
}

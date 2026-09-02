package com.romanpulov.piastriawss;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.closeTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ControllerScanTest extends ControllerMockMvcTest {

    public ControllerScanTest(WebApplicationContext context) {
        super(context);
    }

    @Test
    @Tag("integration")
    void mainTest() throws Exception {
        try {
            MvcResult mvcResult;

            mvcResult = this.mvc.perform(MockMvcRequestBuilders.get("/scan-latest")
                            .queryParam("objectName", "Киев 15/23")
                            .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                    .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].productName", Matchers.in(new String[]{"Водоканал абонентское обслуживание", "Вывоз бытовых отходов"})))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[1].scanValue")
                    .value(anyOf(
                            closeTo(27.69, 0.0001),
                            closeTo(45.64, 0.0001)
                    ), Double.class))
                    .andReturn()
            ;

            addResult(mvcResult);

        } finally {
            Path f = Paths.get("logs/ControllerScanTest.log");
            Files.write(f, logResult, StandardCharsets.UTF_8);
        }
    }
}
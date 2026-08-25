package com.romanpulov.piastriawss;

import com.romanpulov.piastriawss.dto.PaymentGroupDTO;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ControllerPaymentGroupTest extends ControllerMockMvcTest {

    @BeforeAll
    static void prepareTestDB() {
        DBHelper.prepareTestDB();
    }

    public ControllerPaymentGroupTest(WebApplicationContext context) {
        super(context);
    }

    @Test
    void mainTest() throws Exception {

        try {
            MvcResult mvcResult;

            this.mvc.perform(MockMvcRequestBuilders.get("/payment-groups")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                    .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(0)));

            addPaymentGroup(new PaymentGroupDTO(null, "New Group", null, null));

            addPaymentGroup(new PaymentGroupDTO(null, "New Group 2", null, null));

            mvcResult = this.mvc.perform(MockMvcRequestBuilders.get("/payment-groups")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                    .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
                    .andReturn()
            ;

            addResult(mvcResult);

            json = mapper.writeValueAsString(new PaymentGroupDTO(null, "New Group 1", null, null));

            mvcResult = this.mvc.perform(MockMvcRequestBuilders.put("/payment-groups/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();

            addResult(mvcResult);

            mvcResult = this.mvc.perform(MockMvcRequestBuilders.get("/payment-groups")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                    .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is(1)))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[1].id", Matchers.is(2)))
                    .andReturn()
            ;

            addResult(mvcResult);

            addPaymentGroup(new PaymentGroupDTO(null, "Group 3", "url", "#FFFFFF"));

            mvcResult = this.mvc.perform(MockMvcRequestBuilders.get("/payment-groups")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                    .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(3)))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is(1)))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[1].id", Matchers.is(2)))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[2].id", Matchers.is(3)))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[2].url", Matchers.is("url")))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[2].color", Matchers.is("#FFFFFF")))
                    .andReturn()
            ;

            addResult(mvcResult);

            //regular move
            mvcResult = this.mvc.perform(MockMvcRequestBuilders.post("/payment-groups/operation:move_order")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
                    .queryParam("fromId", "3")
                    .queryParam("toId", "1")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();

            addResult(mvcResult);

            mvcResult = this.mvc.perform(MockMvcRequestBuilders.get("/payment-groups")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                    .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(3)))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is(3)))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[1].id", Matchers.is(1)))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[2].id", Matchers.is(2)))
                    .andReturn()
            ;

            addResult(mvcResult);

            // wrong item move
            mvcResult = this.mvc.perform(MockMvcRequestBuilders.post("/payment-groups/operation:move_order")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
                    .queryParam("fromId", "7")
                    .queryParam("toId", "1")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isNotFound())
                    .andReturn();

            addResult(mvcResult);

        } finally {
            Path f = Paths.get("logs/ControllerPaymentGroupTest.log");
            Files.write(f, logResult, StandardCharsets.UTF_8);
        }

    }

    void addPaymentGroup(PaymentGroupDTO paymentGroupDTO) throws Exception {
        json = mapper.writeValueAsString(paymentGroupDTO);

        MvcResult mvcResult = this.mvc.perform(MockMvcRequestBuilders.post("/payment-groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        addResult(mvcResult);
    }

}

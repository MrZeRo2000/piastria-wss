package com.romanpulov.piastriawss;
import com.romanpulov.piastriawss.dto.PaymentObjectDTO;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.json.JsonMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ControllerPaymentObjectTest extends ControllerMockMvcTest {

    @BeforeAll
    static void prepareTestDB() {
        DBHelper.prepareTestDB();
    }

    public ControllerPaymentObjectTest(WebApplicationContext context) {
        super(context);
    }

    @Test
    void mainTest() throws Exception {

        this.mvc.perform(MockMvcRequestBuilders.get("/payment-objects")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(0)))
        ;

        PaymentObjectDTO paymentObjectDTO = new PaymentObjectDTO(null, "New Payment Object", null, null, null);
        JsonMapper mapper = new JsonMapper();
        String json = mapper.writeValueAsString(paymentObjectDTO);
        this.mvc.perform(MockMvcRequestBuilders.post("/payment-objects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
        ;

        paymentObjectDTO = new PaymentObjectDTO(null, "Payment Object 2", "1M", "10D", 1L);
        mapper = new JsonMapper();
        json = mapper.writeValueAsString(paymentObjectDTO);
        this.mvc.perform(MockMvcRequestBuilders.post("/payment-objects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
        ;

        this.mvc.perform(MockMvcRequestBuilders.get("/payment-objects")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("New Payment Object"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].period").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].payDelay").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("Payment Object 2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].period").value("1M"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].term").value("10D"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].payDelay").value(1L))
        ;

        this.mvc.perform(MockMvcRequestBuilders.delete("/payment-objects/0")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestUrl").exists());

        this.mvc.perform(MockMvcRequestBuilders.delete("/payment-objects/1")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
        ;

        this.mvc.perform(MockMvcRequestBuilders.get("/payment-objects")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
        ;

        this.mvc.perform(MockMvcRequestBuilders.delete("/payment-objects/2")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())        ;

        this.mvc.perform(MockMvcRequestBuilders.get("/payment-objects")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(0)))
        ;

    }

}

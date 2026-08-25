package com.romanpulov.piastriawss;

import com.jayway.jsonpath.JsonPath;
import com.romanpulov.piastriawss.dto.PaymentDTO;
import com.romanpulov.piastriawss.dto.PaymentGroupDTO;
import com.romanpulov.piastriawss.dto.PaymentObjectDTO;
import com.romanpulov.piastriawss.dto.ProductDTO;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ControllerPaymentCustomTest extends ControllerMockMvcTest {

    JsonMapper mapper = new JsonMapper();
    String json;

    @BeforeAll
    static void prepareTestDB() {
        DBHelper.prepareTestDB();
    }

    public ControllerPaymentCustomTest(WebApplicationContext context) {
        super(context);
    }

    @Test
    void mainTest() throws Exception {

        MvcResult mvcResult = null;

        try {

            PaymentObjectDTO paymentObjectDTO = new PaymentObjectDTO(null, "New Payment Object", "M", "10D", null);
            json = mapper.writeValueAsString(paymentObjectDTO);
            mvcResult = this.mvc.perform(MockMvcRequestBuilders.post("/payment-objects")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8.name())
                    .content(json)
                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();

            addResult(mvcResult);

            Number paymentObjectId = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.id");
            //assertEquals(1, paymentObjectId);
            paymentObjectDTO.setId(paymentObjectId.longValue());

            PaymentGroupDTO paymentGroupDTO = new PaymentGroupDTO(null, "New Group", null, "FF0000");
            json = mapper.writeValueAsString(paymentGroupDTO);
            mvcResult = this.mvc.perform(MockMvcRequestBuilders.post("/payment-groups")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8.name())
                    .content(json)
                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn()
            ;

            addResult(mvcResult);

            Number paymentGroupId = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.id");
            paymentGroupDTO.setId(paymentGroupId.longValue());

            ProductDTO productDTO = new ProductDTO(null, "New Product", "unit", 1L);
            json = mapper.writeValueAsString(productDTO);
            mvcResult = this.mvc.perform(MockMvcRequestBuilders.post("/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8.name())
                    .content(json)
                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn()
            ;

            addResult(mvcResult);

            Number productId = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.id");
            productDTO.setId(productId.longValue());

            LocalDate currentDate = LocalDate.now();
            LocalDate periodDate = currentDate.minusMonths(1L).withDayOfMonth(1);

            PaymentDTO paymentDTO = new PaymentDTO(
                    null,
                    LocalDate.now(),
                    periodDate,
                    paymentObjectDTO,
                    paymentGroupDTO,
                    productDTO,
                    BigDecimal.valueOf(54.2),
                    BigDecimal.valueOf(53.22),
                    BigDecimal.valueOf(0.44)
            );
            json = mapper.writeValueAsString(paymentDTO);
            mvcResult = this.mvc.perform(MockMvcRequestBuilders.post("/payments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8.name())
                    .content(json)
                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn()
            ;

            addResult(mvcResult);

            mvcResult = this.mvc.perform(MockMvcRequestBuilders.get("/payments:refs")
                    .param("paymentObjectId", String.valueOf(paymentObjectId))
                    .param("paymentPeriodDate", periodDate.atStartOfDay().format(DateTimeFormatter.ISO_DATE_TIME))
                    .characterEncoding(StandardCharsets.UTF_8.name())
                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.paymentList").isArray())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.paymentList", Matchers.hasSize(1)))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.paymentObject.id").value(paymentObjectDTO.getId()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.paymentGroupList").isArray())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.paymentGroupList", Matchers.hasSize(1)))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.productList").isArray())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.productList", Matchers.hasSize(1)))
                    .andReturn()
            ;

            addResult(mvcResult);

            mvcResult = this.mvc.perform(MockMvcRequestBuilders.post("/payments:duplicate_previous_period")
                    .param("paymentObjectId", String.valueOf(paymentObjectId))
                    .param("paymentPeriodDate", periodDate.atStartOfDay().format(DateTimeFormatter.ISO_DATE_TIME))
                    .characterEncoding(StandardCharsets.UTF_8.name())
                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().is5xxServerError())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Current period is not empty"))
                    .andReturn();

            addResult(mvcResult);

            mvcResult = this.mvc.perform(MockMvcRequestBuilders.post("/payments:duplicate_previous_period")
                    .param("paymentObjectId", String.valueOf(paymentObjectId))
                    .param("paymentPeriodDate", periodDate.plusMonths(1).atStartOfDay().format(DateTimeFormatter.ISO_DATE_TIME))
                    .characterEncoding(StandardCharsets.UTF_8.name())
                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.rowsAffected").value("1"))
                    .andReturn();

            addResult(mvcResult);

            mvcResult = this.mvc.perform(MockMvcRequestBuilders.get("/payments:payment_object_totals_by_payment_period")
                    .param("paymentPeriodDate", periodDate.atStartOfDay().format(DateTimeFormatter.ISO_DATE_TIME))
                    .characterEncoding(StandardCharsets.UTF_8.name())
                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].totalAmount").value("53.22"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].paymentObject.id").value("1"))
                    .andReturn()
            ;

            addResult(mvcResult);

            mvcResult = this.mvc.perform(MockMvcRequestBuilders.get("/payments:payment_object_totals_by_date")
                    .param("currentDate", currentDate.atStartOfDay().withDayOfMonth(15).format(DateTimeFormatter.ISO_DATE_TIME))
                    .characterEncoding(StandardCharsets.UTF_8.name())
                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].paymentAmount").value("53.22"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].paymentOverdue").value(false))
                    .andReturn()
            ;

            addResult(mvcResult);

            mvcResult = this.mvc.perform(MockMvcRequestBuilders.get("/payments:payment_object_totals_by_date")
                    .param("currentDate", currentDate.atStartOfDay().withDayOfMonth(5).format(DateTimeFormatter.ISO_DATE_TIME))
                    .characterEncoding(StandardCharsets.UTF_8.name())
                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].paymentAmount").value("53.22"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].paymentOverdue").value(false))
                    .andReturn()
            ;

            addResult(mvcResult);

            mvcResult = this.mvc.perform(MockMvcRequestBuilders.get("/payments:payment_object_period_by_id")
                    .param("paymentObjectId", String.valueOf(1))
                    .param("currentDate", currentDate.atStartOfDay().withDayOfMonth(5).format(DateTimeFormatter.ISO_DATE_TIME))
                    .characterEncoding(StandardCharsets.UTF_8.name())
                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.paymentAmount").doesNotExist())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.paymentOverdue").doesNotExist())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.paymentObject.id").value(1))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.paymentObject.period").value("M"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.paymentDate").value(currentDate.atStartOfDay().withDayOfMonth(1).minusMonths(1L).format(DateTimeFormatter.ISO_DATE_TIME)))
                    .andReturn()
            ;

            addResult(mvcResult);

            mvcResult = this.mvc.perform(MockMvcRequestBuilders.get("/payments:payments_by_payment_object_and_payment_period_date_range")
                    .param("paymentObjectId", String.valueOf(1))
                    .param("paymentPeriodDateStart", periodDate.atStartOfDay().format(DateTimeFormatter.ISO_DATE_TIME))
                    .param("paymentPeriodDateEnd", periodDate.atStartOfDay().plusMonths(1L).format(DateTimeFormatter.ISO_DATE_TIME))
                    .characterEncoding(StandardCharsets.UTF_8.name())
                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.paymentObject.id").value(1))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.paymentObject.name").value("New Payment Object"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.paymentRepList[0].paymentAmount").value("53.22"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.paymentRepList[0].product.id").value(1))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.paymentRepList[1].paymentAmount").value(0))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.paymentRepList[0].paymentGroup.color").value("FF0000"))
                    .andReturn()
            ;

            addResult(mvcResult);

            mvcResult = this.mvc.perform(MockMvcRequestBuilders.get("/payments:payments_by_payment_object_and_payment_period_date_range")
                    .param("paymentObjectId", String.valueOf(1))
                    .param("paymentPeriodDateStart", periodDate.atStartOfDay().plusMonths(1L).format(DateTimeFormatter.ISO_DATE_TIME))
                    .param("paymentPeriodDateEnd", periodDate.atStartOfDay().plusMonths(2L).format(DateTimeFormatter.ISO_DATE_TIME))
                    .characterEncoding(StandardCharsets.UTF_8.name())
                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.paymentRepList[0].paymentAmount").value(0))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.paymentRepList[0].product.id").value(1))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.paymentRepList[1].paymentAmount").doesNotExist())
                    .andReturn()
            ;

            addResult(mvcResult);


            mvcResult = null;

        } finally {
            if (mvcResult != null) {
                addResult(mvcResult);
            }
            Path f = Paths.get("logs/ControllerPaymentCustomTest.log");
            Files.write(f, logResult, StandardCharsets.UTF_8);
        }

    }
}

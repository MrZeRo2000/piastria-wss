package com.romanpulov.piastriawss;

import com.romanpulov.piastriawss.entity.PaymentObject;
import com.romanpulov.piastriawss.repository.PaymentObjectRepository;
import com.romanpulov.piastriawss.service.PaymentObjectService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ServicePaymentObjectTest {
    private static final Logger log = Logger.getLogger(ServicePaymentObjectTest.class.getName());
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Autowired
    private PaymentObjectRepository paymentObjectRepository;

    @Autowired
    private PaymentObjectService paymentObjectService;

    @BeforeAll
    static void prepareTestDB() {
        DBHelper.prepareTestDB();
    }

    private void prepareTestData() {
        //(1) payment object month next
        PaymentObject nextMPaymentObject = new PaymentObject();
        nextMPaymentObject.setName("Month Next");
        nextMPaymentObject.setPeriod("M");
        nextMPaymentObject.setPayDelay(1L);
        paymentObjectRepository.save(nextMPaymentObject);

        //(2) payment object quarter current
        PaymentObject currentQPaymentObject = new PaymentObject();
        currentQPaymentObject.setName("Quarter Next term 10 days");
        currentQPaymentObject.setPeriod("Q");
        currentQPaymentObject.setPayDelay(0L);
        currentQPaymentObject.setTerm("10D");
        paymentObjectRepository.save(currentQPaymentObject);

    }

    @Test
    void mainTest() throws Exception {
        prepareTestData();

        PaymentObject nextMPaymentObject = paymentObjectRepository.findById(1L).orElseThrow(
                () -> new Exception("Next Month Payment Object not found")
        );

        LocalDate currentDate = LocalDate.of(2020, 11, 4);

        Assertions.assertEquals(
                currentDate.withDayOfMonth(1).minusMonths(1L),
                paymentObjectService.getPaymentObjectPaymentDate(nextMPaymentObject, currentDate)
        );

        Assertions.assertEquals(
                currentDate.withDayOfMonth(1).minusMonths(2L),
                paymentObjectService.getPaymentObjectPreviousPeriodPaymentDate(
                        nextMPaymentObject,
                        currentDate.withDayOfMonth(1).minusMonths(1L)
                )
        );

        PaymentObject currentQPaymentObject = paymentObjectRepository.findById(2L).orElseThrow(
                () -> new Exception("Current Quarter Payment Object not found")
        );

        Assertions.assertEquals(
                LocalDate.of(2020, 10, 1),
                paymentObjectService.getPaymentObjectPaymentDate(currentQPaymentObject, currentDate)
        );

        Assertions.assertEquals(
                LocalDate.of(2020, 7, 1),
                paymentObjectService.getPaymentObjectPreviousPeriodPaymentDate(
                        currentQPaymentObject,
                        LocalDate.of(2020, 10, 1)
                )
        );

    }
}

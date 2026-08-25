package com.romanpulov.piastriawss;

import com.romanpulov.piastriawss.entity.Payment;
import com.romanpulov.piastriawss.entity.PaymentObject;
import com.romanpulov.piastriawss.repository.PaymentObjectRepository;
import com.romanpulov.piastriawss.repository.PaymentRepository;
import com.romanpulov.piastriawss.service.PaymentTransformationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ServicePaymentTransformationTest {
    private static final String TEST_FILE_NAME = "data/Payments.xls";

    private static final Logger log = Logger.getLogger(RepositoryProductTests.class.getName());

    @BeforeAll
    static void prepareTestDB() {
        DBHelper.prepareTestDB();
    }

    @Autowired
    PaymentTransformationService paymentTransformationService;

    @Autowired
    PaymentObjectRepository paymentObjectRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Test
    void mainTest() throws Exception {
        PaymentObject paymentObject = new PaymentObject();
        paymentObject.setName("Test Object");
        paymentObject = paymentObjectRepository.save(paymentObject);

        try (InputStream in = new FileInputStream(TEST_FILE_NAME)) {
            paymentTransformationService.readAndTransformExcelStream(paymentObject, in);
        }

        List<Payment> paymentList = new ArrayList<>();
        paymentRepository.findAll().forEach(paymentList::add);

        Assertions.assertNotEquals(0, paymentList.size());
        Assertions.assertEquals(12, paymentList.stream().map(Payment::getProduct).distinct().count());

        List<Payment> periodPaymentList = paymentRepository.findByPaymentObjectIdAndPaymentPeriodDate(
                paymentObject,
                LocalDate.of(2019, 4,1),
                Sort.by("paymentGroup", "product"));
        Assertions.assertEquals(12, periodPaymentList.size());

        Assertions.assertEquals(BigDecimal.valueOf(171.47), periodPaymentList.get(0).getPaymentAmount());
        Assertions.assertEquals(BigDecimal.valueOf(32.41), periodPaymentList.get(1).getPaymentAmount());
        Assertions.assertEquals(BigDecimal.valueOf(61.6), periodPaymentList.get(4).getProductCounter());
        Assertions.assertEquals(BigDecimal.valueOf(0.22), periodPaymentList.get(7).getCommissionAmount());
    }
}

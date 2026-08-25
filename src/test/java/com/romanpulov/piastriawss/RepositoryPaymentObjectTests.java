package com.romanpulov.piastriawss;

import com.romanpulov.piastriawss.entity.PaymentObject;
import com.romanpulov.piastriawss.repository.PaymentObjectRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RepositoryPaymentObjectTests {

    private static final Logger log = Logger.getLogger(RepositoryPaymentObjectTests.class.getName());

    @BeforeAll
    static void prepareTestDB() {
        DBHelper.prepareTestDB();
    }

    @Autowired
    private PaymentObjectRepository paymentObjectRepository;

    @Test
    void mainTest() {
        Iterable<PaymentObject> paymentObjects;

        log.info("mainTest started");
        paymentObjects = paymentObjectRepository.findAll();
        assertThat(paymentObjects).isNotNull();

        PaymentObject newPaymentObject = new PaymentObject();
        newPaymentObject.setName("New Name");
        paymentObjectRepository.save(newPaymentObject);

        paymentObjects = paymentObjectRepository.findAllByOrderByOrderIdAsc();
        PaymentObject savedEntity = paymentObjects.iterator().next();

        log.info("Saved entity: " + savedEntity);

        assertThat(savedEntity.getName()).isEqualTo(newPaymentObject.getName());

        //find by id test
        Assertions.assertTrue(paymentObjectRepository.findById(1L).isPresent());
        PaymentObject getByIdEntity = paymentObjectRepository.findById(1L).get();
        assertThat(getByIdEntity.getId()).isEqualTo(1L);

        paymentObjectRepository.deleteAll();
        paymentObjects = paymentObjectRepository.findAll();
        Assertions.assertFalse(paymentObjects.iterator().hasNext());

        log.info("getTest completed");
    }

    @Test
    void validationTest() {
        Assertions.assertThrows(ConstraintViolationException.class,
                ()-> {
                        PaymentObject newPaymentObject = new PaymentObject();
                        paymentObjectRepository.save(newPaymentObject);
                    });
    }
}

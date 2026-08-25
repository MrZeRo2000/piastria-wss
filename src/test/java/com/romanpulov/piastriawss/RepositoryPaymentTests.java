package com.romanpulov.piastriawss;

import com.romanpulov.piastriawss.entity.Payment;
import com.romanpulov.piastriawss.entity.PaymentGroup;
import com.romanpulov.piastriawss.entity.PaymentObject;
import com.romanpulov.piastriawss.entity.Product;
import com.romanpulov.piastriawss.entity.converter.AmountConverter;
import com.romanpulov.piastriawss.repository.PaymentGroupRepository;
import com.romanpulov.piastriawss.repository.PaymentObjectRepository;
import com.romanpulov.piastriawss.repository.PaymentRepository;
import com.romanpulov.piastriawss.repository.ProductRepository;
import com.romanpulov.piastriawss.service.PaymentService;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RepositoryPaymentTests {

    private static final Logger log = Logger.getLogger(RepositoryPaymentTests.class.getName());

    @BeforeAll
    static void prepareTestDB() {
        DBHelper.prepareTestDB();
    }

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentObjectRepository paymentObjectRepository;

    @Autowired
    private PaymentGroupRepository paymentGroupRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PaymentService paymentService;

    @Test
    void mainTest() {
        Assertions.assertThrows(ConstraintViolationException.class, ()-> {
            Payment newPayment = new Payment();
            paymentRepository.save(newPayment);
        });

        LocalDate lastPaymentPeriodDate = LocalDate.now().minusMonths(1L).withDayOfMonth(1);

        Payment newPayment = new Payment();
        newPayment.setPaymentDate(LocalDate.now());
        newPayment.setPaymentPeriodDate(lastPaymentPeriodDate);

        //payment object
        PaymentObject newPaymentObject = new PaymentObject();
        newPaymentObject.setName("New Payment Object");
        paymentObjectRepository.save(newPaymentObject);
        newPaymentObject = paymentObjectRepository.findAll().iterator().next();
        newPayment.setPaymentObject(newPaymentObject);

        //payment group
        PaymentGroup newPaymentGroup = new PaymentGroup();
        newPaymentGroup.setName("New Payment Group");
        paymentGroupRepository.save(newPaymentGroup);
        newPaymentGroup = paymentGroupRepository.findAll().iterator().next();
        newPayment.setPaymentGroup(newPaymentGroup);

        //product
        Product newProduct = new Product();
        newProduct.setName("New Product Name");
        productRepository.save(newProduct);
        newProduct = productRepository.findAll().iterator().next();
        newPayment.setProduct(newProduct);

        BigDecimal newProductCounter = BigDecimal.valueOf(23.4);
        newPayment.setProductCounter(newProductCounter);

        BigDecimal newPaymentAmount = BigDecimal.valueOf(12.56);
        newPayment.setPaymentAmount(newPaymentAmount);

        newPayment.setCommissionAmount(BigDecimal.valueOf(0.23));

        log.info("Saving payment entity: " + newPayment);
        paymentRepository.save(newPayment);
        log.info("Setting commission amount");
        newPayment.setCommissionAmount(BigDecimal.valueOf(0.7));
        paymentRepository.save(newPayment);

        log.info("Reading new payment");
        newPayment = paymentRepository.findAll().iterator().next();
        log.info("Checking payment group");
        assertThat(newProduct).isEqualTo(newPayment.getProduct());

        log.info("Checking payment amount");
        assertThat(newPaymentAmount).isEqualTo(newPayment.getPaymentAmount());

        log.info("Checking product counter");
        assertThat(newProductCounter).isEqualTo(newPayment.getProductCounter());

        //update to another product
        String product2Name = "Product 2";
        Product product2 = new Product();
        product2.setName(product2Name);
        productRepository.save(product2);
        product2 = productRepository.findAllByOrderByOrderIdAsc().get(1);
        assertThat(product2.getName()).isEqualTo(product2Name);
        newPayment.setProduct(product2);

        paymentRepository.save(newPayment);
        newPayment = paymentRepository.findAll().iterator().next();
        assertThat(product2).isEqualTo(newPayment.getProduct());
        assertThat(newPaymentAmount).isEqualTo(newPayment.getPaymentAmount());

        //another payment
        Payment newPayment2 = new Payment();
        newPayment2.setPaymentDate(LocalDate.now());
        newPayment2.setPaymentPeriodDate(lastPaymentPeriodDate);
        newPayment2.setPaymentObject(newPaymentObject);
        newPayment2.setPaymentGroup(newPaymentGroup);
        newPayment2.setProduct(product2);
        newPayment2.setPaymentAmount(new BigDecimal("63.76"));
        newPayment2.setCommissionAmount(BigDecimal.ZERO);

        paymentRepository.save(newPayment2);

        List<Payment> findByObjectDatePayments = paymentRepository.findByPaymentObjectIdAndPaymentPeriodDate(
                newPaymentObject,
                lastPaymentPeriodDate,
                Sort.by("paymentGroup.orderId").ascending()
        );
        assertThat(findByObjectDatePayments.size()).isEqualTo(2);

        BigDecimal sumByPaymentObjectIdAndPaymentPeriodDate = paymentRepository.sumByPaymentObjectIdAndPaymentPeriodDate(
                newPaymentObject,
                lastPaymentPeriodDate
        );

        Assertions.assertEquals(
                newPayment.getPaymentAmount().add(newPayment2.getPaymentAmount()),
                sumByPaymentObjectIdAndPaymentPeriodDate.divide(AmountConverter.SCALE, MathContext.DECIMAL32)
        );

        // inserting for other objects
        PaymentObject paymentObject2 = new PaymentObject();
        paymentObject2.setName("Payment Object 2");
        paymentObject2 = paymentObjectRepository.save(paymentObject2);

        Payment newPayment3 = new Payment();
        newPayment3.setPaymentDate(LocalDate.now());
        newPayment3.setPaymentPeriodDate(lastPaymentPeriodDate);
        newPayment3.setPaymentObject(paymentObject2);
        newPayment3.setPaymentGroup(newPaymentGroup);
        newPayment3.setProduct(product2);
        newPayment3.setPaymentAmount(new BigDecimal("56.54"));
        newPayment3.setCommissionAmount(BigDecimal.ZERO);

        paymentRepository.save(newPayment3);

        List<Payment> findByPaymentPeriodDate = paymentRepository.findAllByPaymentPeriodDate(lastPaymentPeriodDate);
        assertThat(findByPaymentPeriodDate.size()).isEqualTo(3);

        Map<PaymentObject, BigDecimal> paymentObjectTotals =
                findByPaymentPeriodDate
                        .stream()
                        .collect(Collectors.groupingBy(
                                Payment::getPaymentObject,
                                Collectors.reducing(
                                        BigDecimal.ZERO,
                                        Payment::getPaymentAmount,
                                        BigDecimal::add
                                )
                        )
                        );

        assertThat(paymentObjectTotals.get(newPaymentObject)).isEqualTo(newPayment.getPaymentAmount().add(newPayment2.getPaymentAmount()));
        assertThat(paymentObjectTotals.get(paymentObject2)).isEqualTo(newPayment3.getPaymentAmount());

        //check setting null
        Assertions.assertThrows(org.springframework.transaction.TransactionSystemException.class, ()-> {
            newPayment2.setCommissionAmount(null);
            paymentRepository.save(newPayment2);
        });

        Payment updatingPayment = findByObjectDatePayments.get(0);
        int rows;
        Optional<Payment> updatedPayment;

        //updating product counter
        BigDecimal newProductCounterValue = BigDecimal.valueOf(9532.77);

        rows = paymentService.updateProductCounter(
                updatingPayment.getId(),
                newProductCounterValue,
                 LocalDate.now()
        );
        assertThat(rows).isEqualTo(1);

        updatedPayment = paymentRepository.findById(updatingPayment.getId());
        assertThat(updatedPayment.isPresent()).isTrue();
        assertThat(updatedPayment.get().getProductCounter()).isEqualTo(newProductCounterValue);

        //updating payment amount
        BigDecimal newPaymentAmountValue = BigDecimal.valueOf(634.32);

        rows = paymentService.updatePaymentAmount(
                updatingPayment.getId(),
                newPaymentAmountValue,
                LocalDate.now()
        );
        assertThat(rows).isEqualTo(1);

        updatedPayment = paymentRepository.findById(updatingPayment.getId());
        assertThat(updatedPayment.isPresent()).isTrue();
        assertThat(updatedPayment.get().getPaymentAmount()).isEqualTo(newPaymentAmountValue);

        //updating commission amount
        BigDecimal newCommissionAmountValue = new BigDecimal("1.23");

        rows = paymentService.updateCommissionAmount(
                updatingPayment.getId(),
                newCommissionAmountValue,
                LocalDate.now()
        );
        assertThat(rows).isEqualTo(1);

        updatedPayment = paymentRepository.findById(updatingPayment.getId());
        assertThat(updatedPayment.isPresent()).isTrue();
        assertThat(updatedPayment.get().getCommissionAmount()).isEqualTo(newCommissionAmountValue);

        //delete parent entity custom handling
        Assertions.assertThrows(RuntimeException.class, ()-> paymentObjectRepository.deleteAll());

        //delete parent entity custom handling
        Assertions.assertThrows(RuntimeException.class, ()-> paymentGroupRepository.deleteAll());

        //delete parent entity custom handling
        Assertions.assertThrows(RuntimeException.class, ()-> productRepository.deleteAll());

        PaymentObject paymentObject = new PaymentObject();
        paymentObject.setName("Orphaned payment object");
        paymentObjectRepository.save(paymentObject);

        List<PaymentObject> paymentObjects = paymentObjectRepository.findAllByOrderByOrderIdAsc();
        PaymentObject orphanedPaymentObject = paymentObjects.get(paymentObjects.size()-1);

        //deleting orphaned object should work
        paymentObjectRepository.delete(orphanedPaymentObject);

        //testing payment group change
        List<Payment> paymentList = new ArrayList<>();
        paymentRepository.findAll().forEach(paymentList::add);

        Assertions.assertTrue(paymentList.size() > 0);
        PaymentGroup fromPaymentGroup = paymentList.get(0).getPaymentGroup();
        PaymentObject fromPaymentObject = paymentList.get(0).getPaymentObject();

        List<Payment> paymentListOldPaymentGroup =
        paymentList.stream().filter(payment -> payment.getPaymentGroup().equals(fromPaymentGroup) && payment.getPaymentObject().equals(fromPaymentObject)).collect(Collectors.toList());
        Assertions.assertTrue(paymentListOldPaymentGroup.size() > 0);

        PaymentGroup toPaymentGroup = new PaymentGroup();
        toPaymentGroup.setName("To payment group");
        toPaymentGroup = paymentGroupRepository.save(toPaymentGroup);
        Assertions.assertNotNull(toPaymentGroup.getId());

        rows = paymentService.updatePaymentGroup(fromPaymentObject, fromPaymentGroup, toPaymentGroup);
        Assertions.assertEquals(rows, paymentListOldPaymentGroup.size());

        final PaymentGroup testToPaymentGroup = toPaymentGroup;
        List<Payment> updatedPaymentList = new ArrayList<>();
        paymentRepository.findAll().forEach(updatedPaymentList::add);

        List<Payment> paymentListNewPaymentGroup =
                updatedPaymentList.stream().filter(payment -> payment.getPaymentGroup().equals(testToPaymentGroup) && payment.getPaymentObject().equals(fromPaymentObject)).collect(Collectors.toList());
        Assertions.assertEquals(paymentListNewPaymentGroup.size(), paymentListOldPaymentGroup.size());
    }
}

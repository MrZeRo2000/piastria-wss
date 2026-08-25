package com.romanpulov.piastriawss;

import com.romanpulov.piastriawss.entity.Payment;
import com.romanpulov.piastriawss.entity.PaymentGroup;
import com.romanpulov.piastriawss.entity.PaymentObject;
import com.romanpulov.piastriawss.entity.Product;
import com.romanpulov.piastriawss.exception.NotFoundException;
import com.romanpulov.piastriawss.repository.PaymentGroupRepository;
import com.romanpulov.piastriawss.repository.PaymentObjectRepository;
import com.romanpulov.piastriawss.repository.PaymentRepository;
import com.romanpulov.piastriawss.repository.ProductRepository;
import com.romanpulov.piastriawss.service.PaymentObjectPaymentService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServicePaymentObjectPaymentTest {
    private static final Logger log = Logger.getLogger(ServicePaymentObjectPaymentTest.class.getName());
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentObjectRepository paymentObjectRepository;

    @Autowired
    private PaymentGroupRepository paymentGroupRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PaymentObjectPaymentService paymentObjectPaymentService;

    private final LocalDate currentDate = LocalDate.now().withDayOfMonth(20);
    private final LocalDate previousMonthDate = currentDate.minusMonths(1L).withDayOfMonth(1);

    private final int[] monthQuarters = {0, 0, 0, 1, 1, 1, 2, 2, 2, 3, 3, 3};

    public ServicePaymentObjectPaymentTest() {
        log.info("ServicePaymentObjectPaymentTest constructor");
    }

    private void prepareTestData() {
        //(1) empty payment object
        PaymentObject emptyPaymentObject = new PaymentObject();
        emptyPaymentObject.setName("Empty payment object");
        paymentObjectRepository.save(emptyPaymentObject);

        //(2) payment object
        PaymentObject defaultPaymentObject = new PaymentObject();
        defaultPaymentObject.setName("Month Default");
        defaultPaymentObject.setPeriod("M");
        paymentObjectRepository.save(defaultPaymentObject);

        //(3) payment object month next
        PaymentObject nextPaymentObject = new PaymentObject();
        nextPaymentObject.setName("Month Next");
        nextPaymentObject.setPeriod("M");
        nextPaymentObject.setPayDelay(1L);
        paymentObjectRepository.save(nextPaymentObject);

        //(4) payment object month current
        PaymentObject currentPaymentObject = new PaymentObject();
        currentPaymentObject.setName("Month Current");
        currentPaymentObject.setPeriod("M");
        currentPaymentObject.setPayDelay(0L);
        paymentObjectRepository.save(currentPaymentObject);

        //(5) payment object month next term 4 days
        PaymentObject next4DaysPaymentObject = new PaymentObject();
        next4DaysPaymentObject.setName("Month Next term 4 days");
        next4DaysPaymentObject.setPeriod("M");
        next4DaysPaymentObject.setPayDelay(1L);
        next4DaysPaymentObject.setTerm("4D");
        paymentObjectRepository.save(next4DaysPaymentObject);

        //(6) payment object quarter current term 10 days
        PaymentObject current10DaysQPaymentObject = new PaymentObject();
        current10DaysQPaymentObject.setName("Quarter Next term 10 days");
        current10DaysQPaymentObject.setPeriod("Q");
        current10DaysQPaymentObject.setPayDelay(0L);
        current10DaysQPaymentObject.setTerm("10D");
        paymentObjectRepository.save(current10DaysQPaymentObject);

        //payment group
        PaymentGroup newPaymentGroup = new PaymentGroup();
        newPaymentGroup.setName("New Payment Group");
        paymentGroupRepository.save(newPaymentGroup);

        //product
        Product newProduct = new Product();
        newProduct.setName("New Product Name");
        productRepository.save(newProduct);

        //payment
        Payment newPayment = new Payment();
        newPayment.setPaymentDate(LocalDate.now());
        newPayment.setPaymentPeriodDate(previousMonthDate);
        newPayment.setPaymentObject(defaultPaymentObject);
        newPayment.setPaymentGroup(newPaymentGroup);
        newPayment.setProduct(newProduct);
        newPayment.setPaymentAmount(BigDecimal.valueOf(14.22));
        newPayment.setCommissionAmount(BigDecimal.ZERO);
        paymentRepository.save(newPayment);

        //payment
        Payment newPaymentMN4D = new Payment();
        newPaymentMN4D.setPaymentDate(currentDate);
        newPaymentMN4D.setPaymentPeriodDate(previousMonthDate);
        newPaymentMN4D.setPaymentObject(next4DaysPaymentObject);
        newPaymentMN4D.setPaymentGroup(newPaymentGroup);
        newPaymentMN4D.setProduct(newProduct);
        newPaymentMN4D.setPaymentAmount(BigDecimal.valueOf(78.32));
        newPaymentMN4D.setCommissionAmount(BigDecimal.ZERO);
        paymentRepository.save(newPaymentMN4D);

        //payment quarter current
        Payment newPaymentQC10D = new Payment();
        newPaymentQC10D.setPaymentDate(currentDate);
        newPaymentQC10D.setPaymentPeriodDate(LocalDate.parse("01.04.2003", formatter));
        newPaymentQC10D.setPaymentObject(current10DaysQPaymentObject);
        newPaymentQC10D.setPaymentGroup(newPaymentGroup);
        newPaymentQC10D.setProduct(newProduct);
        newPaymentQC10D.setPaymentAmount(BigDecimal.valueOf(4.55));
        newPaymentQC10D.setCommissionAmount(BigDecimal.ZERO);
        paymentRepository.save(newPaymentQC10D);

    }

    @Test
    @Order(1)
    void testPrepare() {
        DBHelper.prepareTestDB();
        prepareTestData();
    }

    @Test
    @Order(2)
    void testTotalByPaymentObjectAndPaymentPeriod() throws Exception {
        PaymentObject monthDefaultPaymentObject =  paymentObjectRepository.
                findAllByOrderByOrderIdAsc().
                stream().
                filter(po -> po.getName().equals("Month Default")).
                findFirst().
                orElseThrow(() -> new Exception("Month Default not found"));

        assertThat(
                paymentObjectPaymentService.getTotalByPaymentObjectAndPaymentPeriod(
                        monthDefaultPaymentObject,
                        previousMonthDate
                )).isEqualTo(BigDecimal.valueOf(14.22));

        assertThat(
                paymentObjectPaymentService.getTotalAmountByPaymentObjectAndPaymentPeriod(
                        monthDefaultPaymentObject,
                        previousMonthDate
                )).isEqualTo(BigDecimal.valueOf(14.22));

        PaymentObject emptyPaymentObject =  paymentObjectRepository.
                findAllByOrderByOrderIdAsc().
                stream().
                filter(po -> po.getName().equals("Empty payment object")).
                findFirst().
                orElseThrow(() -> new Exception("Empty payment object not found"));

        assertThat(
                paymentObjectPaymentService.getTotalByPaymentObjectAndPaymentPeriod(
                        emptyPaymentObject,
                        previousMonthDate
                )).isEqualTo(BigDecimal.ZERO);

        assertThat(
                paymentObjectPaymentService.getTotalAmountByPaymentObjectAndPaymentPeriod(
                        emptyPaymentObject,
                        previousMonthDate
                )).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @Order(2)
    void testTotalCurrentDate() {
        var total = paymentObjectPaymentService
                .getPaymentObjectPeriodTotal(currentDate);
        assertThat(total).isNotNull();

        assertThat(total.get(0).getPaymentAmount()).isEqualTo(BigDecimal.ZERO);
        assertThat(total.get(1).getPaymentAmount()).isEqualTo(BigDecimal.valueOf(14.22));
        assertThat(total.get(2).getPaymentAmount()).isEqualTo(BigDecimal.ZERO);
        assertThat(total.get(3).getPaymentAmount()).isEqualTo(BigDecimal.ZERO);
        assertThat(total.get(4).getPaymentAmount()).isEqualTo(BigDecimal.valueOf(78.32));

        assertThat(total.get(0).getPaymentOverdue()).isFalse();
        assertThat(total.get(1).getPaymentOverdue()).isFalse();
        assertThat(total.get(2).getPaymentOverdue()).isFalse();
        assertThat(total.get(3).getPaymentOverdue()).isFalse();
        assertThat(total.get(4).getPaymentOverdue()).isFalse();
    }

    @Test
    @Order(2)
    void testTotalNextMonth() {
        //next month
        var total = paymentObjectPaymentService
                .getPaymentObjectPeriodTotal(currentDate.plusMonths(1L));
        assertThat(total).isNotNull();
        assertThat(total.get(4).getPaymentOverdue()).isTrue();
    }

    @Test
    @Order(2)
    void testForQuarter() {
        // for quarter

        var total_bom_04_03 = paymentObjectPaymentService.getPaymentObjectPeriodTotal(LocalDate.parse("05.04.2003", formatter));
        assertThat(total_bom_04_03.get(5).getPaymentOverdue()).isFalse();

        var total_eom_04_03 = paymentObjectPaymentService.getPaymentObjectPeriodTotal(LocalDate.parse("25.04.2003", formatter));
        assertThat(total_eom_04_03.get(5).getPaymentOverdue()).isFalse();

        var total_bom_05_03 = paymentObjectPaymentService.getPaymentObjectPeriodTotal(LocalDate.parse("02.05.2003", formatter));
        assertThat(total_bom_05_03.get(5).getPaymentOverdue()).isFalse();

        var total_eom_05_03 = paymentObjectPaymentService.getPaymentObjectPeriodTotal(LocalDate.parse("25.05.2003", formatter));
        assertThat(total_eom_05_03.get(5).getPaymentOverdue()).isFalse();

        var total_bom_07_03 = paymentObjectPaymentService.getPaymentObjectPeriodTotal(LocalDate.parse("02.07.2003", formatter));
        assertThat(total_bom_07_03.get(5).getPaymentOverdue()).isFalse();

        var total_eom_07_03 = paymentObjectPaymentService.getPaymentObjectPeriodTotal(LocalDate.parse("25.07.2003", formatter));
        assertThat(total_eom_07_03.get(5).getPaymentOverdue()).isTrue();

        var total_bom_10_03 = paymentObjectPaymentService.getPaymentObjectPeriodTotal(LocalDate.parse("02.10.2003", formatter));
        assertThat(total_bom_10_03.get(5).getPaymentOverdue()).isTrue();

        var total_eom_10_03 = paymentObjectPaymentService.getPaymentObjectPeriodTotal(LocalDate.parse("25.10.2003", formatter));
        assertThat(total_eom_10_03.get(5).getPaymentOverdue()).isTrue();

        var total_bom_02_04 = paymentObjectPaymentService.getPaymentObjectPeriodTotal(LocalDate.parse("02.02.2004", formatter));
        assertThat(total_bom_02_04.get(5).getPaymentOverdue()).isTrue();

        var total_eom_02_04 = paymentObjectPaymentService.getPaymentObjectPeriodTotal(LocalDate.parse("25.02.2004", formatter));
        assertThat(total_eom_02_04.get(5).getPaymentOverdue()).isTrue();
    }

    @Test
    @Order(2)
    void testPaymentObjectPeriodById() throws Exception {
        //checks for getPaymentObjectPeriodById
        Assertions.assertThrows(NotFoundException.class, () ->
            paymentObjectPaymentService.getPaymentObjectPeriodById(200L, currentDate)
        );

        var item_1 = paymentObjectPaymentService.getPaymentObjectPeriodById(1L, currentDate);
        assertThat(item_1.getPaymentDate()).isEqualTo(previousMonthDate);

        var item_2 = paymentObjectPaymentService.getPaymentObjectPeriodById(2L, currentDate);
        assertThat(item_2.getPaymentDate()).isEqualTo(previousMonthDate);

        var item_3 = paymentObjectPaymentService.getPaymentObjectPeriodById(3L, currentDate);
        assertThat(item_3.getPaymentDate()).isEqualTo(previousMonthDate);

        var item_4 = paymentObjectPaymentService.getPaymentObjectPeriodById(4L, currentDate);
        assertThat(item_4.getPaymentDate()).isEqualTo(currentDate.withDayOfMonth(1));

        int monthQuarter = monthQuarters[currentDate.getMonth().getValue() - 1];
        LocalDate currentQuarterStartDate = currentDate.withMonth(monthQuarter * 3 + 1).withDayOfMonth(1);

        var item_6 = paymentObjectPaymentService.getPaymentObjectPeriodById(6L, currentDate);
        assertThat(item_6.getPaymentDate()).isEqualTo(currentQuarterStartDate);
    }
}

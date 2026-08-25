package com.romanpulov.piastriawss;

import com.romanpulov.piastriawss.entity.Payment;
import com.romanpulov.piastriawss.entity.PaymentGroup;
import com.romanpulov.piastriawss.entity.PaymentObject;
import com.romanpulov.piastriawss.entity.Product;
import com.romanpulov.piastriawss.repository.PaymentGroupRepository;
import com.romanpulov.piastriawss.repository.PaymentObjectRepository;
import com.romanpulov.piastriawss.repository.PaymentRepository;
import com.romanpulov.piastriawss.repository.ProductRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.IntStream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DataGeneratorTest {

    @Autowired
    private PaymentObjectRepository paymentObjectRepository;

    @Autowired
    private PaymentGroupRepository paymentGroupRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeAll
    static void prepareTestDB() {
        DBHelper.prepareTestDB();
    }

    private PaymentObject createPaymentObject(String objectName) {
        PaymentObject paymentObject = new PaymentObject();
        paymentObject.setName(objectName);
        return paymentObjectRepository.save(paymentObject);
    }

    private PaymentGroup createPaymentGroup(String groupName, String color) {
        PaymentGroup paymentGroup = new PaymentGroup();
        paymentGroup.setName(groupName);
        paymentGroup.setColor(color);
        return paymentGroupRepository.save(paymentGroup);
    }

    private PaymentGroup createPaymentGroupWithRandomColor(String groupName) {
        PaymentGroup paymentGroup = new PaymentGroup();
        paymentGroup.setName(groupName);
        Random random = new Random();
        int nextInt = random.nextInt(0xffffff + 1);
        paymentGroup.setColor(String.format("#%06x", nextInt));
        return paymentGroupRepository.save(paymentGroup);
    }

    private Product createProduct(String productName, String unitName) {
        Product product = new Product();
        product.setName(productName);
        product.setUnitName(unitName);
        return productRepository.save(product);
    }

    private Payment createPayment(
            LocalDate paymentPeriodDate,
            PaymentObject paymentObject,
            PaymentGroup paymentGroup,
            Product product,
            BigDecimal productCounter,
            BigDecimal paymentAmount,
            BigDecimal commissionAmount) {
        Payment payment = new Payment();
        payment.setPaymentDate(LocalDate.now());
        payment.setPaymentPeriodDate(paymentPeriodDate);
        payment.setPaymentObject(paymentObject);
        payment.setPaymentGroup(paymentGroup);
        payment.setProduct(product);
        payment.setProductCounter(productCounter);
        payment.setPaymentAmount(paymentAmount);
        payment.setCommissionAmount(commissionAmount);

        return paymentRepository.save(payment);
    }

    @Test
    @Disabled
    void generateData() {
        PaymentObject paymentObject = createPaymentObject("Object 1");
        Assertions.assertTrue(paymentObject.getId() > 0);

        PaymentGroup paymentGroup = createPaymentGroup("Group 1", "#ff8888");
        Assertions.assertTrue(paymentGroup.getId() > 0);

        Product product1 = createProduct("Product 1", "m\u00B3");
        Assertions.assertTrue(product1.getId() > 0);

        Product product2 = createProduct("Product 2", "kW");
        Assertions.assertTrue(product2.getId() > 0);

        LocalDate lastPeriodDate = LocalDate.now().withDayOfMonth(1).minusMonths(1);
        LocalDate prevPeriodDate = lastPeriodDate.minusMonths(1);

        Payment payment = createPayment(prevPeriodDate, paymentObject, paymentGroup, product1,
                BigDecimal.valueOf(13),
                BigDecimal.valueOf(44.33),
                BigDecimal.valueOf(0));
        payment = createPayment(prevPeriodDate, paymentObject, paymentGroup, product2,
                BigDecimal.valueOf(23),
                BigDecimal.valueOf(7.25),
                BigDecimal.valueOf(0));
        payment = createPayment(lastPeriodDate, paymentObject, paymentGroup, product1,
                BigDecimal.valueOf(65),
                BigDecimal.valueOf(5.22),
                BigDecimal.valueOf(0.2));
        payment = createPayment(lastPeriodDate, paymentObject, paymentGroup, product2,
                BigDecimal.valueOf(95),
                BigDecimal.valueOf(8.66),
                BigDecimal.valueOf(0.1));
    }

    private static <T> T getRandomItem(List<T> items) {
        Random rand = new Random();
        return items.get(rand.nextInt(items.size()));
    }

    private static BigDecimal getRandomAmount() {
        return BigDecimal.valueOf((new Random()).nextInt(10000) / 100. + 0.5);
    }

    private static BigDecimal getRandomCommission() {
        return BigDecimal.valueOf((new Random()).nextInt(1000) / 100. + 0.1);
    }

    private static int getRandomInt(int bound) {
        return (new Random()).nextInt(bound);
    }

    private void generateDataSet(int numObjects, int numGroups, int numProducts, int numMonths) {
        final List<PaymentObject> paymentObjects = new ArrayList<>(numObjects);
        IntStream.range(1, numObjects + 1)
                .forEach(value -> paymentObjects.add(createPaymentObject("Object " + value)));

        final List<PaymentGroup> paymentGroups = new ArrayList<>(numGroups);
        IntStream.range(1, numGroups + 1)
                .forEach(value -> paymentGroups.add(createPaymentGroupWithRandomColor("Group " + value)));

        final List<Product> products = new ArrayList<>(numProducts);
        IntStream.range(1, numProducts + 1)
                .forEach(value -> products.add(createProduct(
                        "Product " + value,
                        value == 1 ? "m\u00B3" : value == 2 ? "kW" : null
                )));

        LocalDate initialDate = LocalDate.now().withDayOfMonth(1).minusMonths(1);
        final List<LocalDate> dates = new ArrayList<>();
        IntStream.range(0, numMonths)
                .forEach(value -> dates.add(initialDate.minusMonths(value)));

        Map<Product, PaymentGroup> productGroupsMap = new HashMap<>();
        products.forEach(product -> productGroupsMap.put(product, getRandomItem(paymentGroups)));

        dates.forEach(localDate ->
                paymentObjects.forEach(paymentObject ->
                        productGroupsMap.keySet().forEach(product -> {
                            createPayment(
                                    localDate,
                                    paymentObject,
                                    productGroupsMap.get(product),
                                    product,
                                    product.getName().endsWith("1") || product.getName().endsWith("2") ?
                                            BigDecimal.valueOf(getRandomInt(1000) / 10) : null,
                                    getRandomAmount(),
                                    getRandomCommission()
                            );
                        }))
                );
    }

    @Test
    @Disabled
    void generateDataBig() {
        this.generateDataSet(3, 5, 9, 24);
    }

    @Test
    @Disabled
    void generateDataSmall() {
        this.generateDataSet(1, 2, 3, 3);
    }

}

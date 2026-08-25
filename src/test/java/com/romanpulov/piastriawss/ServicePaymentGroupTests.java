package com.romanpulov.piastriawss;

import com.romanpulov.piastriawss.entity.PaymentGroup;
import com.romanpulov.piastriawss.service.PaymentGroupService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ServicePaymentGroupTests {

    private static final Logger log = Logger.getLogger(ServicePaymentGroupTests.class.getName());

    @BeforeAll
    static void prepareTestDB() {
        DBHelper.prepareTestDB();
    }

    @Autowired
    private PaymentGroupService paymentGroupService;

    @Test
    void mainTest() {
        Iterable<PaymentGroup> paymentGroups;

        paymentGroups = paymentGroupService.findAll();
        assertThat(paymentGroups).isNotNull();

        String newGroupName = "New group name";
        PaymentGroup newPaymentGroup = new PaymentGroup();
        newPaymentGroup.setName(newGroupName);
        newPaymentGroup = paymentGroupService.insert(newPaymentGroup);

        assertThat(newPaymentGroup.getId()).isGreaterThan(0);
        assertThat(newPaymentGroup.getOrderId()).isEqualTo(1L);

        List<PaymentGroup> findByName = paymentGroupService.findByName(newGroupName);
        assertThat(findByName.size()).isEqualTo(1);

        List<PaymentGroup> findByName2 = paymentGroupService.findByName(newGroupName + "added");
        assertThat(findByName2.size()).isEqualTo(0);

        // check order
        List<PaymentGroup> paymentGroupList = new ArrayList<>();
        paymentGroupService.findAll().forEach(paymentGroupList::add);
        PaymentGroup paymentGroupOrdered = paymentGroupList.get(0);
        paymentGroupOrdered.setOrderId(1L);
        paymentGroupService.insert(paymentGroupOrdered);

        paymentGroupList = new ArrayList<>();
        paymentGroupService.findAll().forEach(paymentGroupList::add);
        assertThat(paymentGroupList.get(0).getOrderId()).isEqualTo(paymentGroupOrdered.getOrderId());

        // default order
        paymentGroupService.setDefaultOrder(PaymentGroup.class);

    }
}

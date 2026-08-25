package com.romanpulov.piastriawss;

import com.romanpulov.piastriawss.entity.PaymentGroup;
import com.romanpulov.piastriawss.exception.CommonEntityNotFoundException;
import com.romanpulov.piastriawss.repository.CustomQueryRepository;
import com.romanpulov.piastriawss.service.PaymentGroupService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.IntStream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RepositoryCustomQueryTests {

    private static final Logger log = Logger.getLogger(RepositoryCustomQueryTests.class.getName());

    @BeforeAll
    static void prepareTestDB() {
        DBHelper.prepareTestDB();
    }

    @Autowired
    private CustomQueryRepository customQueryRepository;

    @Autowired
    private PaymentGroupService paymentGroupService;

    @Test
    void mainTest() {
        Long result;

        String tableName = PaymentGroup.class.getName();

        result = customQueryRepository.getMaxOrderId(tableName);
        Assertions.assertThat(result).isNull();

        result = customQueryRepository.getLongScalarQueryResult(String.format("SELECT p.id FROM %s p", tableName));
        Assertions.assertThat(result).isNull();

        result = customQueryRepository.getMaxOrderId(tableName);
        Assertions.assertThat(result).isNull();

        // create groups
        paymentGroupService.save(createNew("Group 1"));
        paymentGroupService.save(createNew("Group 2"));
        paymentGroupService.save(createNew("Group 3"));

        result = customQueryRepository.getLongScalarQueryResult(String.format("SELECT p.id FROM %s p", tableName));
        Assertions.assertThat(result).isNotNull();

        result = customQueryRepository.getLongScalarQueryResult(String.format("SELECT p.id FROM %s p", tableName));
        Assertions.assertThat(result).isNotNull();

        result = customQueryRepository.getMaxOrderId(tableName);
        Assertions.assertThat(result).isNull();

        int rowsAffected = customQueryRepository.setDefaultOrder(tableName);
        Assertions.assertThat(rowsAffected).isEqualTo(3);

        result = customQueryRepository.getMaxOrderId(tableName);
        Assertions.assertThat(result).isEqualTo(3);

        List<PaymentGroup> paymentGroupList;

        paymentGroupList = getGroups();
        Assertions.assertThat(paymentGroupList.size()).isEqualTo(3);
        Assertions.assertThat(paymentGroupList.get(0).getOrderId()).isEqualTo(1);
        Assertions.assertThat(paymentGroupList.get(1).getOrderId()).isEqualTo(2);
        Assertions.assertThat(paymentGroupList.get(2).getOrderId()).isEqualTo(3);

        customQueryRepository.moveOrder(
                "PaymentGroup",
                paymentGroupList.get(0).getId(),
                paymentGroupList.get(0).getOrderId(),
                paymentGroupList.get(2).getId(),
                paymentGroupList.get(2).getOrderId()
                );

        paymentGroupList = getGroups();
        Assertions.assertThat(paymentGroupList.get(0).getName()).isEqualTo("Group 2");
        Assertions.assertThat(paymentGroupList.get(1).getName()).isEqualTo("Group 3");
        Assertions.assertThat(paymentGroupList.get(2).getName()).isEqualTo("Group 1");

        deleteGroups();
        paymentGroupList = new ArrayList<>();
        paymentGroupService.findAll().forEach(paymentGroupList::add);
        Assertions.assertThat(paymentGroupList.size()).isEqualTo(0);

        //move to last
        createGroups(10);
        rowsAffected = customQueryRepository.setDefaultOrder(tableName);
        Assertions.assertThat(rowsAffected).isEqualTo(10);

        paymentGroupList = getGroups();
        Long[] ids = getGroupIds(paymentGroupList);
        Assertions.assertThat(ids).containsExactly(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);

        customQueryRepository.moveOrder(
                tableName,
                paymentGroupList.get(1).getId(),
                paymentGroupList.get(1).getOrderId(),
                paymentGroupList.get(9).getId(),
                paymentGroupList.get(9).getOrderId()
        );

        paymentGroupList = getGroups();
        ids = getGroupIds(paymentGroupList);
        Assertions.assertThat(ids).containsExactly(1L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 2L);

        //move to first
        customQueryRepository.moveOrder(
                tableName,
                paymentGroupList.get(1).getId(),
                paymentGroupList.get(1).getOrderId(),
                paymentGroupList.get(0).getId(),
                paymentGroupList.get(0).getOrderId()
        );

        paymentGroupList = getGroups();
        ids = getGroupIds(paymentGroupList);
        Assertions.assertThat(ids).containsExactly(3L, 1L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 2L);

        //move forward
        customQueryRepository.moveOrder(
                tableName,
                paymentGroupList.get(1).getId(),
                paymentGroupList.get(1).getOrderId(),
                paymentGroupList.get(6).getId(),
                paymentGroupList.get(6).getOrderId()
        );

        paymentGroupList = getGroups();
        ids = getGroupIds(paymentGroupList);
        Assertions.assertThat(ids).containsExactly(3L, 4L, 5L, 6L, 7L, 8L, 1L, 9L, 10L, 2L);

        //move backward
        customQueryRepository.moveOrder(
                tableName,
                paymentGroupList.get(8).getId(),
                paymentGroupList.get(8).getOrderId(),
                paymentGroupList.get(6).getId(),
                paymentGroupList.get(6).getOrderId()
        );

        paymentGroupList = getGroups();
        ids = getGroupIds(paymentGroupList);
        Assertions.assertThat(ids).containsExactly(3L, 4L, 5L, 6L, 7L, 8L, 10L, 1L, 9L, 2L);

    }

    private PaymentGroup createNew(String groupName) {
        PaymentGroup result = new PaymentGroup();
        result.setName(groupName);
        return result;
    }

    private void createGroups(int num) {
        IntStream.rangeClosed(0, num - 1).forEach(
                v -> paymentGroupService.insert(createNew("Group " + v))
        );
    }

    private List<PaymentGroup> getGroups() {
        List<PaymentGroup> paymentGroupList = new ArrayList<>();
        paymentGroupService.findAll().forEach(paymentGroupList::add);
        return paymentGroupList;
    }

    private void deleteGroups() {
        paymentGroupService.findAll().forEach(v -> {
            try {
                paymentGroupService.deleteById(v.getId());
            } catch (CommonEntityNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    private Long[] getGroupIds(List<PaymentGroup> paymentGroupList) {
        List<Long> ids = new ArrayList<>();
        paymentGroupList.forEach(p -> ids.add(p.getId()));

        Long[] result = new Long[ids.size()];
        return ids.toArray(result);
    }
}

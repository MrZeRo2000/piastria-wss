package com.romanpulov.piastriawss;

import com.romanpulov.piastriawss.entity.PaymentGroup;
import com.romanpulov.piastriawss.service.PaymentGroupService;
import org.assertj.core.api.Assertions;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.logging.Logger;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomQueryTests {

    private static final Logger log = Logger.getLogger(CustomQueryTests.class.getName());

    @BeforeAll
    static void prepareTestDB() {
        DBHelper.prepareTestDB();
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PaymentGroupService paymentGroupService;

    @Test
    @Transactional
    void mainTest() {

        PaymentGroup paymentGroup = new PaymentGroup();
        paymentGroup.setName("Group name");
        paymentGroupService.save(paymentGroup);

        Assertions.assertThat(this.entityManager).isNotNull();
        Session session = entityManager.unwrap(Session.class);
        Assertions.assertThat(session).isNotNull();

        Query<?> query = session.createQuery("SELECT p.id, p.name FROM PaymentGroup p WHERE p.orderId IS NULL", Object[].class);
        List<?> result = query.getResultList();
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(((Object[])result.get(0))[0]).isEqualTo(1L);

        Query<?> rowQuery = session.createQuery("SELECT MAX(p.id) FROM PaymentGroup p WHERE p.orderId IS NULL", Object.class);
        List<?> rowResult = rowQuery.getResultList();
        Assertions.assertThat(rowResult.get(0)).isEqualTo(1L);
        //List<row> typedResult = session.createQuery("SELECT MAX(p.id) FROM PaymentGroup p WHERE p.orderId IS NULL", row.class).getResultList();

        rowQuery = session.createQuery("SELECT MAX(p.id) FROM PaymentGroup p WHERE p.orderId IS NOT NULL", Object.class);
        rowResult = rowQuery.getResultList();
        Assertions.assertThat(rowResult.get(0)).isNull();

        NativeQuery<?> nativeQuery = session.createNativeQuery("SELECT MAX(p.payment_group_id) FROM payment_groups p WHERE p.order_id IS NULL", Object.class);
        List<?> nativeResult = nativeQuery.getResultList();
        Assertions.assertThat(nativeResult.get(0)).isEqualTo(1);

        int rows = session.createQuery("UPDATE PaymentGroup p SET p.orderId = p.id", null).executeUpdate();
        Assertions.assertThat(rows).isEqualTo(1);

        Long updatedOrderId = (Long)session.createQuery("SELECT MAX(p.orderId) FROM PaymentGroup p", Object.class).getResultList().get(0);
        Assertions.assertThat(updatedOrderId).isEqualTo(1L);

        Long scalarValue = (Long)session.createQuery("SELECT p.id FROM PaymentGroup p", Object.class).getSingleResult();
        Assertions.assertThat(scalarValue).isEqualTo(1L);

        Query<?> scalarQuery = session.createQuery("SELECT p.id FROM PaymentGroup p WHERE 1 = 5", Object.class);
        Long scalarResult = (Long)scalarQuery.getResultStream().findFirst().orElse(null);
        Assertions.assertThat(scalarResult).isNull();
    }

}

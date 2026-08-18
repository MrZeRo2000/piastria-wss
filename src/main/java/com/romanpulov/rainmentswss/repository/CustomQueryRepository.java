package com.romanpulov.rainmentswss.repository;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Repository
public class CustomQueryRepository {
    private static final String QUERY_GET_MAX_ORDER_ID = "SELECT MAX(o.orderId) AS order_id FROM %s o";
    private static final String QUERY_SET_DEFAULT_ORDER = "UPDATE %s o SET o.orderId = o.id";
    private static final String QUERY_SHIFT_ORDER_DOWN = "UPDATE %s o SET o.orderId = o.orderId + 1 WHERE o.orderId >= %d";
    private static final String QUERY_SHIFT_ORDER_UP = "UPDATE %s o SET o.orderId = o.orderId - 1 WHERE o.orderId <= %d";
    private static final String QUERY_SET_ORDER = "UPDATE %s o SET o.orderId = %d WHERE o.id = %d";

    @PersistenceContext
    private EntityManager entityManager;

    private Session getSession() {
        return entityManager.unwrap(Session.class);
    }

    @Transactional
    public Object getScalarQueryResult(String queryString) {
        List<?> resultList = getSession().createQuery(queryString, Object.class).getResultList();
        if (!resultList.isEmpty()) {
            return resultList.getFirst();
        } else {
            return null;
        }
    }

    @Transactional
    public Long getLongScalarQueryResult(String queryString) {
        Object result = getScalarQueryResult(queryString);
        if (result == null) {
            return null;
        } else if (result instanceof Long) {
            return (Long) result;
        } else {
            throw new RuntimeException("Invalid scalar query result value:" + result);
        }
    }

    @Transactional
    public Long getMaxOrderId(String tableName) {
        String queryString = String.format(QUERY_GET_MAX_ORDER_ID, tableName);
        return getLongScalarQueryResult(queryString);
    }

    @Transactional
    public int setDefaultOrder(String tableName) {
        String queryString = String.format(QUERY_SET_DEFAULT_ORDER, tableName);
        return getSession().createQuery(queryString, null).executeUpdate();
    }

    @Transactional
    public int moveOrder(
            String tableName,
            @NotNull Long fromId,
            @NotNull Long fromOrderId,
            @NotNull Long toId,
            @NotNull Long toOrderId) {
        String baseQueryString;
        if (fromOrderId > toOrderId) {
            baseQueryString = QUERY_SHIFT_ORDER_DOWN;
        } else {
            baseQueryString = QUERY_SHIFT_ORDER_UP;
        }
        String shiftQueryString = String.format(baseQueryString, tableName, toOrderId);
        String setOrderQueryString = String.format(QUERY_SET_ORDER, tableName, toOrderId, fromId);

        int rowsAffected = 0;

        rowsAffected += getSession().createQuery(shiftQueryString, null).executeUpdate();
        rowsAffected += getSession().createQuery(setOrderQueryString, null).executeUpdate();

        return rowsAffected;
    }

    public void clearEntityManager() {
        entityManager.clear();
    }
}

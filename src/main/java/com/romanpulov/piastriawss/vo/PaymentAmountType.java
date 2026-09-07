package com.romanpulov.piastriawss.vo;

public enum PaymentAmountType {
    AT_UNKNOWN,
    AT_AMOUNT,
    AT_COMMISSION_AMOUNT,
    AT_PRODUCT_COUNTER;
    public static PaymentAmountType fromString(String paymentAmountType) {
        return switch (paymentAmountType) {
            case "/productCounter" -> PaymentAmountType.AT_PRODUCT_COUNTER;
            case "/paymentAmount" -> PaymentAmountType.AT_AMOUNT;
            case "/commissionAmount" -> PaymentAmountType.AT_COMMISSION_AMOUNT;
            default -> PaymentAmountType.AT_UNKNOWN;
        };
    }
}

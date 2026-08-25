package com.romanpulov.piastriawss.dto;

import java.util.List;

/**
 * Payment with references to be used by front end for editing
 */
public class PaymentRefsDTO {
    private final List<PaymentDTO> paymentList;

    private final List<PaymentDTO> prevPeriodPaymentList;

    private final PaymentObjectDTO paymentObject;

    private final List<PaymentGroupDTO> paymentGroupList;

    private final List<ProductDTO> productList;

    public PaymentRefsDTO(
            List<PaymentDTO> paymentList,
            List<PaymentDTO> prevPeriodPaymentList,
            PaymentObjectDTO paymentObject,
            List<PaymentGroupDTO> paymentGroupList,
            List<ProductDTO> productList
    ) {
        this.paymentList = paymentList;
        this.prevPeriodPaymentList = prevPeriodPaymentList;
        this.paymentObject = paymentObject;
        this.paymentGroupList = paymentGroupList;
        this.productList = productList;
    }

    public List<PaymentDTO> getPaymentList() {
        return paymentList;
    }

    public List<PaymentDTO> getPrevPeriodPaymentList() {
        return prevPeriodPaymentList;
    }

    public PaymentObjectDTO getPaymentObject() {
        return paymentObject;
    }

    public List<PaymentGroupDTO> getPaymentGroupList() {
        return paymentGroupList;
    }

    public List<ProductDTO> getProductList() {
        return productList;
    }
}

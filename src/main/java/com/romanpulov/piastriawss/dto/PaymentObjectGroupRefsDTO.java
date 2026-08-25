package com.romanpulov.piastriawss.dto;

import java.util.List;

public class PaymentObjectGroupRefsDTO {

    private final List<PaymentObjectDTO> paymentObjectList;

    private final List<PaymentGroupDTO> paymentGroupList;

    public PaymentObjectGroupRefsDTO(List<PaymentObjectDTO> paymentObjectList, List<PaymentGroupDTO> paymentGroupList) {
        this.paymentObjectList = paymentObjectList;
        this.paymentGroupList = paymentGroupList;
    }

    public List<PaymentObjectDTO> getPaymentObjectList() {
        return paymentObjectList;
    }

    public List<PaymentGroupDTO> getPaymentGroupList() {
        return paymentGroupList;
    }
}

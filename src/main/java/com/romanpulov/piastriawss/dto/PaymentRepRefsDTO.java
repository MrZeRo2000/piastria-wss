package com.romanpulov.piastriawss.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public class PaymentRepRefsDTO {
    private final PaymentObjectDTO paymentObject;

    private final List<PaymentRepDTO> paymentRepList;

    public PaymentRepRefsDTO(PaymentObjectDTO paymentObject, List<PaymentRepDTO> paymentRepList) {
        this.paymentObject = paymentObject;
        this.paymentRepList = paymentRepList;
    }

    public PaymentObjectDTO getPaymentObject() {
        return paymentObject;
    }

    public List<PaymentRepDTO> getPaymentRepList() {
        return paymentRepList;
    }
}

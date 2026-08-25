package com.romanpulov.piastriawss.service;

import com.romanpulov.piastriawss.dto.ExtPaymentDTO;
import com.romanpulov.piastriawss.entity.Payment;
import com.romanpulov.piastriawss.entity.PaymentGroup;
import com.romanpulov.piastriawss.entity.PaymentObject;
import com.romanpulov.piastriawss.entity.Product;
import com.romanpulov.piastriawss.repository.PaymentGroupRepository;
import com.romanpulov.piastriawss.repository.PaymentRepository;
import com.romanpulov.piastriawss.repository.ProductRepository;
import com.romanpulov.piastriawss.transform.ExcelReadException;
import com.romanpulov.piastriawss.transform.ExcelReader;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentTransformationService {
    private final PaymentGroupRepository paymentGroupRepository;

    private final ProductRepository productRepository;

    private final PaymentRepository paymentRepository;

    public final ObjectProvider<ExcelReader> excelReaderObjectProvider;

    public PaymentTransformationService(
            PaymentGroupRepository paymentGroupRepository,
            ProductRepository productRepository,
            PaymentRepository paymentRepository,
            ObjectProvider<ExcelReader> excelReaderObjectProvider
    ) {
        this.paymentGroupRepository = paymentGroupRepository;
        this.productRepository = productRepository;
        this.paymentRepository = paymentRepository;
        this.excelReaderObjectProvider = excelReaderObjectProvider;
    }

    private PaymentGroup addPaymentGroup(String paymentGroupName) {
        PaymentGroup paymentGroup = new PaymentGroup();
        paymentGroup.setName(paymentGroupName);

        return paymentGroupRepository.save(paymentGroup);
    }

    private Map<String, PaymentGroup> getPaymentGroups() {
        Map<String, PaymentGroup> result = new HashMap<>();
        paymentGroupRepository.findAll().forEach(p -> result.put(p.getName(), p));

        return result;
    }

    private Product addProduct(String productName) {
        Product product = new Product();
        product.setName(productName);

        return productRepository.save(product);
    }

    private Map<String, Product> getProducts() {
        Map<String, Product> result = new HashMap<>();
        productRepository.findAll().forEach(p -> result.put(p.getName(), p));

        return result;
    }

    @Transactional
    public int transformExtData(PaymentObject paymentObject, List<ExtPaymentDTO> data) {
        int rowsAffected = 0;
        Map<String, PaymentGroup> paymentGroups = getPaymentGroups();
        Map<String, Product> products = getProducts();

        for (ExtPaymentDTO p : data) {
            Payment newPayment = new Payment();
            newPayment.setPaymentObject(paymentObject);

            PaymentGroup paymentGroup = paymentGroups.get(p.getGroupName());
            if (paymentGroup == null) {
                paymentGroup = addPaymentGroup(p.getGroupName());
                paymentGroups.put(p.getGroupName(), paymentGroup);
            }
            newPayment.setPaymentGroup(paymentGroup);

            Product product = products.get(p.getProductName());
            if (product == null) {
                product = addProduct(p.getProductName());
                products.put(p.getProductName(), product);
            }
            newPayment.setProduct(product);

            newPayment.setPaymentDate(LocalDate.now());
            newPayment.setPaymentPeriodDate(p.getPaymentPeriodDate());

            newPayment.setProductCounter(p.getProductCounter());

            newPayment.setPaymentAmount(p.getPaymentAmount() == null ? BigDecimal.ZERO : p.getPaymentAmount());
            newPayment.setCommissionAmount(p.getCommissionAmount() == null ? BigDecimal.ZERO : p.getCommissionAmount());

            paymentRepository.save(newPayment);

            rowsAffected++;
        }

        return rowsAffected;
    }

    public int readAndTransformExcelStream(PaymentObject paymentObject, InputStream inputStream) throws ExcelReadException {
        ExcelReader excelReader = excelReaderObjectProvider.getObject();
        List<ExtPaymentDTO> excelContent = excelReader.readDataContent(inputStream);
        return transformExtData(paymentObject, excelContent);
    }
}

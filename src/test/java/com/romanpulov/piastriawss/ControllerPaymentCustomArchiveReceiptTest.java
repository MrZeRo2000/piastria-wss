package com.romanpulov.piastriawss;

import com.romanpulov.piastriawss.controller.ControllerExceptionHandler;
import com.romanpulov.piastriawss.controller.PaymentCustomController;
import com.romanpulov.piastriawss.entity.PaymentObject;
import com.romanpulov.piastriawss.exception.BadRequestException;
import com.romanpulov.piastriawss.repository.PaymentObjectRepository;
import com.romanpulov.piastriawss.service.ReceiptArchiveService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.file.Path;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ControllerPaymentCustomArchiveReceiptTest {
    private static final String URL = "/payments:archive_receipt";

    // collaborators not used by the endpoint are mocked only to satisfy the controller constructor
    @Mock PaymentObjectRepository paymentObjectRepository;
    @Mock ReceiptArchiveService receiptArchiveService;

    @InjectMocks
    PaymentCustomController controller;

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ControllerExceptionHandler())
                .build();
    }

    private PaymentObject paymentObject(long id, String name) {
        PaymentObject paymentObject = new PaymentObject();
        paymentObject.setId(id);
        paymentObject.setName(name);
        return paymentObject;
    }

    @Test
    void archiveReceipt_success_archivesByPaymentObjectNameAndReturnsPath() throws Exception {
        when(paymentObjectRepository.findById(5L)).thenReturn(Optional.of(paymentObject(5L, "Киев 15/23")));
        Path archivePath = Path.of("root", "receipt", "k1523", "receipt261015_1432.zip");
        when(receiptArchiveService.archive("Киев 15/23")).thenReturn(archivePath);

        mvc.perform(MockMvcRequestBuilders.patch(URL).param("paymentObjectId", "5"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(archivePath.toString()));

        verify(receiptArchiveService).archive("Киев 15/23");
    }

    @Test
    void archiveReceipt_paymentObjectNotFound_returnsErrorAndDoesNotArchive() throws Exception {
        when(paymentObjectRepository.findById(99L)).thenReturn(Optional.empty());

        mvc.perform(MockMvcRequestBuilders.patch(URL).param("paymentObjectId", "99"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Entity with id=99 not found"));

        verifyNoInteractions(receiptArchiveService);
    }

    @Test
    void archiveReceipt_archiveFails_returnsBadRequestWithMessage() throws Exception {
        when(paymentObjectRepository.findById(5L)).thenReturn(Optional.of(paymentObject(5L, "Киев 15/23")));
        when(receiptArchiveService.archive("Киев 15/23"))
                .thenThrow(new BadRequestException("File 'old.pdf' is old to archive"));

        mvc.perform(MockMvcRequestBuilders.patch(URL).param("paymentObjectId", "5"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Bad request: File 'old.pdf' is old to archive"));
    }

    @Test
    void archiveReceipt_missingParameter_returnsBadRequest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.patch(URL))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        verifyNoInteractions(paymentObjectRepository, receiptArchiveService);
    }
}

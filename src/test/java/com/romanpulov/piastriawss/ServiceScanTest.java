package com.romanpulov.piastriawss;

import com.romanpulov.piastriawss.dto.ScanResultDTO;
import com.romanpulov.piastriawss.service.ScanService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ServiceScanTest {
    @Autowired
    private ScanService scanService;

    @Test
    @Tag("integration")
    public void testFindLatestScanResults() throws Exception {
        List<ScanResultDTO> result = this.scanService.findLatestScanResults("Киев 15/23");
        assertThat(result).isNotNull();
        assertThat(result.isEmpty()).isFalse();

        var v1 = result.stream().filter(v -> v.productName().equals("Водоканал абонентское обслуживание")).findFirst();
        assertThat(v1.isPresent()).isTrue();
        v1.ifPresent(scanResultDTO -> assertThat(scanResultDTO.scanValue()).isEqualTo(BigDecimal.valueOf(27.69d)));

        var v2 = result.stream().filter(v -> v.productName().equals("Вывоз бытовых отходов")).findFirst();
        assertThat(v2.isPresent()).isTrue();
        v2.ifPresent(scanResultDTO -> assertThat(scanResultDTO.scanValue()).isEqualTo(BigDecimal.valueOf(45.64d)));
    }
}

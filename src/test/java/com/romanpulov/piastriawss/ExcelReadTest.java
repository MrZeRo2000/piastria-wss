package com.romanpulov.piastriawss;

import com.romanpulov.piastriawss.dto.ExtPaymentDTO;
import com.romanpulov.piastriawss.transform.ExcelReader;
import org.apache.poi.ss.usermodel.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;

public class ExcelReadTest {
    private static final String TEST_FILE_NAME = "data/Payments.xls";

    static final Logger logger = LoggerFactory.getLogger(ExcelReadTest.class);

    @Test
    void test() {
        File xlsFile = new File(TEST_FILE_NAME);
        Assertions.assertTrue(xlsFile.exists());
    }

    @Test
    void readFile() throws Exception {
        try (InputStream in = new FileInputStream(TEST_FILE_NAME)) {
            Workbook wb = WorkbookFactory.create(in);
            Assertions.assertNotNull(wb);

            Sheet sheet = wb.getSheetAt(0);
            Assertions.assertNotNull(sheet);

            DataFormatter formatter = new DataFormatter();

            for (Row row: sheet) {
                Cell cell = row.getCell(0);
                if (cell != null) {
                    String cellValue = formatter.formatCellValue(cell);
                    logger.info("Cell 0 value:" + cellValue);
                }

                cell = row.getCell(3);
                if (cell != null) {
                    String cellValue = formatter.formatCellValue(cell);
                    logger.info("Cell 3 value:" + cellValue);
                }
            }
        }
    }

    @Test
    void testReaderBaseLine() throws Exception {
        //no beans
        ExcelReader excelReader = new ExcelReader();

        try (InputStream in = new FileInputStream(TEST_FILE_NAME)) {
            Sheet sheet = excelReader.readDataSheet(in);
            List<ExcelReader.BaseLine> baseLineList = excelReader.readSheetBaseLineList(sheet);
            Assertions.assertEquals("Вывоз бытовых отходов", baseLineList.get(1).productName);
            Assertions.assertEquals(13, baseLineList.size());
            baseLineList.forEach(baseLine -> logger.info(baseLine.toString()));
        }
    }

    @Test
    void testDateColumnMapping() throws Exception {
        //no beans
        ExcelReader excelReader = new ExcelReader();

        try (InputStream in = new FileInputStream(TEST_FILE_NAME)) {
            Sheet sheet = excelReader.readDataSheet(in);
            List<ExcelReader.DateColumnMapping> columnMappings = excelReader.readSheetDateColumnMapping(sheet);

            Assertions.assertEquals(LocalDate.of(2016, 10, 1), columnMappings.get(0).periodDate);
            Assertions.assertEquals(LocalDate.of(2020, 2, 1), columnMappings.get(columnMappings.size() - 1).periodDate);
            Assertions.assertEquals(0, columnMappings.size() % 2);
        }

    }

    private ExtPaymentDTO getForDateAndProduct(List<ExtPaymentDTO> content, LocalDate date, String productName) {
        Stream<ExtPaymentDTO> sp =
                content.stream().filter(p ->
                        p.getPaymentPeriodDate().equals(date) &&
                                p.getProductName().equals(productName)
                );

        List<ExtPaymentDTO> lp = sp.collect(Collectors.toList());
        Assertions.assertEquals(1, lp.size());

        return lp.get(0);
    }

    @Test
    void testReaderContents() throws Exception {
        //no beans
        ExcelReader excelReader = new ExcelReader();

        try (InputStream in = new FileInputStream(TEST_FILE_NAME)) {
            List<ExtPaymentDTO> content = excelReader.readDataContent(in);

            Assertions.assertEquals(LocalDate.of(2016, 10, 1), content.get(0).getPaymentPeriodDate());

            ExtPaymentDTO ep;

            ep = getForDateAndProduct(
                    content,
                    LocalDate.of(2016, 10, 1),
                    "Содержание домов и придомовых территорий"
            );
            Assertions.assertEquals(BigDecimal.valueOf(203.74), ep.getPaymentAmount());

            ep = getForDateAndProduct(
                    content,
                    LocalDate.of(2016, 10, 1),
                    "Электроэнергия"
            );
            Assertions.assertEquals(BigDecimal.valueOf(52.68), ep.getPaymentAmount());

            ep = getForDateAndProduct(
                    content,
                    LocalDate.of(2018, 4, 1),
                    "Горячая вода"
            );
            Assertions.assertEquals(BigDecimal.valueOf(47d), ep.getProductCounter());

            ep = getForDateAndProduct(
                    content,
                    LocalDate.of(2018, 4, 1),
                    "Газ"
            );
            Assertions.assertEquals(BigDecimal.valueOf(0.18), ep.getCommissionAmount());

            ep = getForDateAndProduct(
                    content,
                    LocalDate.of(2018, 12, 1),
                    "Домофон"
            );
            Assertions.assertEquals(BigDecimal.valueOf(237.6), ep.getPaymentAmount());

        }
    }

}

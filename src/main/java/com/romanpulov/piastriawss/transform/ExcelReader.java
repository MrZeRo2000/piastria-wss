package com.romanpulov.piastriawss.transform;

import com.romanpulov.piastriawss.dto.ExtPaymentDTO;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ExcelReader {
    private static final String PRODUCT_COUNTER_POSTFIX = "счетчик";

    DataFormatter formatter = new DataFormatter();

    public Sheet readDataSheet(InputStream inputStream) throws ExcelReadException {
        try (Workbook wb = WorkbookFactory.create(inputStream)) {
            try {
                return wb.getSheetAt(0);
            } catch (IllegalArgumentException e) {
                throw new ExcelReadException("Error reading sheet:" + e.getMessage());
            }
        } catch (IOException e) {
            throw new ExcelReadException(e.getMessage());
        }
    }

    public List<ExtPaymentDTO> readDataContent(InputStream inputStream) throws ExcelReadException {
        return readSheetContent(readDataSheet(inputStream));
    }

    public static class BaseLine {
        public final String productName;
        public final String groupName;

        public BaseLine(String productName, String groupName) {
            this.productName = productName;
            this.groupName = groupName;
        }

        @Override
        public String toString() {
            return "BaseLine{" +
                    "productName='" + productName + '\'' +
                    ", groupName='" + groupName + '\'' +
                    '}';
        }
    }

    public static class DateColumnMapping {
        public final LocalDate periodDate;
        public final int paymentColumn;
        public final int commissionColumn;

        public DateColumnMapping(LocalDate periodDate, int paymentColumn, int commissionColumn) {
            this.periodDate = periodDate;
            this.paymentColumn = paymentColumn;
            this.commissionColumn = commissionColumn;
        }

        @Override
        public String toString() {
            return "DateColumn{" +
                    "periodDate=" + periodDate +
                    ", paymentColumn=" + paymentColumn +
                    ", commissionColumn=" + commissionColumn +
                    '}';
        }
    }

    public List<BaseLine> readSheetBaseLineList(Sheet sheet) {
        List<BaseLine> baseLineList = new ArrayList<>();

        boolean isFirstRow = true;
        Cell prevGroupNameCell = null;
        for (Row row: sheet) {
            if (isFirstRow) {
                isFirstRow = false;
            } else {
                Cell productCell = row.getCell(0);
                if (productCell != null) {
                    String productName = formatter.formatCellValue(productCell);
                    Cell groupNameCell;
                    Cell nextGroupNameCell = row.getCell(1);
                    if ((nextGroupNameCell != null) && (!nextGroupNameCell.getStringCellValue().isEmpty())) {
                        groupNameCell = nextGroupNameCell;
                        prevGroupNameCell = nextGroupNameCell;
                    } else {
                        groupNameCell = prevGroupNameCell;
                    }
                    if (groupNameCell != null) {
                        Cell paymentTypeCell = row.getCell(2);
                        String groupName = formatter.formatCellValue(groupNameCell);
                        if ((paymentTypeCell != null) && (!paymentTypeCell.getStringCellValue().isEmpty())) {
                            groupName = groupName + "(" + formatter.formatCellValue(paymentTypeCell) +")";
                        }
                        baseLineList.add(new BaseLine(productName, groupName));
                    }
                }
            }
        }
        return baseLineList;
    }

    public List<DateColumnMapping> readSheetDateColumnMapping(Sheet sheet) {
        List<DateColumnMapping> columnMappings = new ArrayList<>();

        Row columnRow = sheet.getRow(0);

        int paymentColumn = 0;
        LocalDate periodDate = null;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        for (int i = 0; i < columnRow.getLastCellNum(); i++) {
            Cell cell = columnRow.getCell(i);
            if ((cell != null) && (!formatter.formatCellValue(cell).isEmpty())) {
                if ((cell.getCellType() == CellType.NUMERIC) && (DateUtil.isCellDateFormatted(cell))) {
                    periodDate = cell.getLocalDateTimeCellValue().toLocalDate();
                    paymentColumn = i;
                } else if ((periodDate != null)
                        && (i - paymentColumn == 1)
                        && (formatter.formatCellValue(cell).contains(periodDate.format(dateTimeFormatter)))) {
                    columnMappings.add(new DateColumnMapping(periodDate, paymentColumn, i));
                    periodDate = null;
                    paymentColumn = 0;
                }
            }
        }

        return columnMappings;
    }

    public BigDecimal readNumericCell(Cell cell) {
        if ((cell != null) && (cell.getCellType() == CellType.NUMERIC)) {
            return BigDecimal.valueOf(cell.getNumericCellValue());
        } else {
            return null;
        }
    }

    public List<ExtPaymentDTO> readSheetContent(Sheet sheet) {
        List<ExtPaymentDTO> content = new ArrayList<>();

        List<BaseLine> baseLineList = readSheetBaseLineList(sheet);
        List<DateColumnMapping> dateColumnMappings = readSheetDateColumnMapping(sheet);

        for (DateColumnMapping dateColumnMapping: dateColumnMappings) {
            int sheetRowNum = 0;
            for (int bln = 0; bln < baseLineList.size(); bln++) {

                //move to the next row
                sheetRowNum ++;
                Row row = sheet.getRow(sheetRowNum);
                Row counterRow = null;

                BaseLine currentBaseLine = baseLineList.get(bln);
                BaseLine counterBaseLine = null;

                //skip counters
                if (currentBaseLine.productName.endsWith(PRODUCT_COUNTER_POSTFIX)) {
                    continue;
                }

                //search for counter
                BaseLine nextBaseLine;
                if ((bln < baseLineList.size() - 2)
                        && ((nextBaseLine = baseLineList.get(bln + 1)).productName.endsWith(PRODUCT_COUNTER_POSTFIX))) {
                    counterBaseLine = nextBaseLine;
                    counterRow = sheet.getRow(sheetRowNum + 1);
                }

                String productName = currentBaseLine.productName;
                String groupName = currentBaseLine.groupName;

                BigDecimal productCounter = null;
                if (counterBaseLine != null) {
                    productCounter = readNumericCell(counterRow.getCell(dateColumnMapping.paymentColumn));
                    if (productCounter == null) {
                        productCounter = readNumericCell(counterRow.getCell(dateColumnMapping.commissionColumn));
                    }
                }

                BigDecimal paymentAmount = readNumericCell(row.getCell(dateColumnMapping.paymentColumn));
                BigDecimal commissionAmount = readNumericCell(row.getCell(dateColumnMapping.commissionColumn));

                content.add(new ExtPaymentDTO(
                        productName,
                        groupName,
                        dateColumnMapping.periodDate,
                        productCounter,
                        paymentAmount,
                        commissionAmount
                ));
            }
        }

        return content;
    }
}

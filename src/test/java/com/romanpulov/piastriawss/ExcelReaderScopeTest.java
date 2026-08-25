package com.romanpulov.piastriawss;

import com.romanpulov.piastriawss.transform.ExcelReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ExcelReaderScopeTest {

    public final ObjectProvider<ExcelReader> excelReaderObjectProvider;

    @Autowired
    public ExcelReaderScopeTest(ObjectProvider<ExcelReader> excelReaderObjectProvider) {
        this.excelReaderObjectProvider = excelReaderObjectProvider;
    }

    @Test
    public void testIdentity() {
        ExcelReader e1 = excelReaderObjectProvider.getObject();
        Assertions.assertNotNull(e1);

        ExcelReader e2 = excelReaderObjectProvider.getObject();
        Assertions.assertNotNull(e2);

        Assertions.assertNotEquals(e1, e2);
    }

}

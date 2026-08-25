package com.romanpulov.piastriawss;

import com.romanpulov.piastriawss.vo.Period;
import com.romanpulov.piastriawss.vo.PeriodType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class VOPeriodTest {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Test
    void testFromString5D() {
        Period period = Period.fromString("5D");
        Assertions.assertNotNull(period);
        Assertions.assertEquals(5, period.quantity);
        Assertions.assertEquals(PeriodType.D, period.periodType);
    }

    @Test
    void testFromStringD() {
        Period period = Period.fromString("D");
        Assertions.assertNotNull(period);
        Assertions.assertEquals(1, period.quantity);
        Assertions.assertEquals(PeriodType.D, period.periodType);
    }

    @Test
    void testFromStringM() {
        Period period = Period.fromString("M");
        Assertions.assertNotNull(period);
        Assertions.assertEquals(1, period.quantity);
        Assertions.assertEquals(PeriodType.M, period.periodType);
    }

    @Test
    void testFromStringX() {
        Period period = Period.fromString("X");
        Assertions.assertNull(period);
    }

    @Test
    void testFromString6X() {
        Period period = Period.fromString("6X");
        Assertions.assertNull(period);
    }

    @Test
    void testFromStringMD() {
        Period period = Period.fromString("MD");
        Assertions.assertNull(period);
    }

    @Test
    void testTruncatePeriod1() {
        LocalDate date = LocalDate.parse("25.03.2003", formatter);

        LocalDate truncatedToMonth = Period.truncateToPeriodType(date, PeriodType.M);
        Assertions.assertEquals(LocalDate.parse("01.03.2003", formatter), truncatedToMonth);

        LocalDate truncatedToQuarter = Period.truncateToPeriodType(date, PeriodType.Q);
        Assertions.assertEquals(LocalDate.parse("01.01.2003", formatter), truncatedToQuarter);

        Assertions.assertThrows(RuntimeException.class, () -> {
            LocalDate truncatedToDay = Period.truncateToPeriodType(date, PeriodType.D);
        });
    }

    @Test
    void testTruncatePeriod2() {
        LocalDate date = LocalDate.parse("07.12.1983", formatter);

        LocalDate truncatedToMonth = Period.truncateToPeriodType(date, PeriodType.M);
        Assertions.assertEquals(LocalDate.parse("01.12.1983", formatter), truncatedToMonth);

        LocalDate truncatedToQuarter = Period.truncateToPeriodType(date, PeriodType.Q);
        Assertions.assertEquals(LocalDate.parse("01.10.1983", formatter), truncatedToQuarter);
    }

    @Test
    void testTruncatePeriod3() {
        Assertions.assertEquals(
                LocalDate.parse("01.01.1983", formatter),
                Period.truncateToPeriodType(LocalDate.parse("02.01.1983", formatter), PeriodType.Q)
        );

        Assertions.assertEquals(
                LocalDate.parse("01.01.1983", formatter),
                Period.truncateToPeriodType(LocalDate.parse("16.02.1983", formatter), PeriodType.Q)
        );

        Assertions.assertEquals(
                LocalDate.parse("01.01.1983", formatter),
                Period.truncateToPeriodType(LocalDate.parse("08.03.1983", formatter), PeriodType.Q)
        );

        Assertions.assertEquals(
                LocalDate.parse("01.04.1983", formatter),
                Period.truncateToPeriodType(LocalDate.parse("12.04.1983", formatter), PeriodType.Q)
        );
    }
}

package com.romanpulov.piastriawss;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateTests {

    @Test
    void currentDateTest() {
        //2019-12-29 is 1577577600
        //LocalDate date = new LocalDate(1577577600);
        LocalDate date = Instant.ofEpochSecond(1577577600).atZone(ZoneId.systemDefault()).toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String text = date.format(formatter);

        assertThat(text).isEqualTo("2019-12-29");

        date = LocalDate.of(2019, 12, 29);
        Long longDate = date.toEpochDay() * 24 * 60 * 60;

        assertThat(longDate).isEqualTo(1577577600L);
    }

    void jsDateTest() {
        String dateString = "2020-02-12T15:54:42.170Z";
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;

        //LocalDate date =
    }
}

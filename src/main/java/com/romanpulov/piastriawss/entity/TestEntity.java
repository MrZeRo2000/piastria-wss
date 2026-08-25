package com.romanpulov.piastriawss.entity;

import com.romanpulov.piastriawss.entity.converter.DateConverter;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "test_table")
public class TestEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    @Column(name = "f_date")
    @Convert(converter = DateConverter.class)
    private LocalDate testDate;

    public LocalDate getTestDate() {
        return testDate;
    }

    public void setTestDate(LocalDate testDate) {
        this.testDate = testDate;
    }

    @Override
    public String toString() {
        return "TestEntity{" +
                "id=" + id +
                ", testDate=" + testDate +
                '}';
    }

}

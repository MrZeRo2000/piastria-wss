package com.romanpulov.piastriawss.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Objects;

public class RowsAffectedDTO {
    private int rowsAffected;

    @JsonCreator
    public RowsAffectedDTO(int rowsAffected) {
        this.rowsAffected = rowsAffected;
    }

    public int getRowsAffected() {
        return rowsAffected;
    }

    public void setRowsAffected(int rowsAffected) {
        this.rowsAffected = rowsAffected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RowsAffectedDTO that = (RowsAffectedDTO) o;
        return rowsAffected == that.rowsAffected;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rowsAffected);
    }

    @Override
    public String toString() {
        return "PatchResponseDTO{" +
                "rowsAffected=" + rowsAffected +
                '}';
    }
}

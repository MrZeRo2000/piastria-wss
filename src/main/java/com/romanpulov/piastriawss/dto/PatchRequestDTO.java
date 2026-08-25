package com.romanpulov.piastriawss.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatchRequestDTO {
    private String op;

    private String path;

    private String value;

    @JsonCreator
    public PatchRequestDTO(
            @JsonProperty("op") String op,
            @JsonProperty("path") String path,
            @JsonProperty("value") String value) {
        this.op = op;
        this.path = path;
        this.value = value;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatchRequestDTO that = (PatchRequestDTO) o;
        return op.equals(that.op) &&
                path.equals(that.path) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(op, path, value);
    }

    @Override
    public String toString() {
        return "PatchRequestDTO{" +
                "op='" + op + '\'' +
                ", path='" + path + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}

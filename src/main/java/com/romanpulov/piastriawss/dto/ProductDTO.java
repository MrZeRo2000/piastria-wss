package com.romanpulov.piastriawss.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDTO {

    private Long id;

    private String name;

    private String unitName;

    private Long counterPrecision;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public Long getCounterPrecision() {
        return counterPrecision;
    }

    public void setCounterPrecision(Long counterPrecision) {
        this.counterPrecision = counterPrecision;
    }

    @JsonCreator
    public ProductDTO(
            @JsonProperty("id") Long id,
            @JsonProperty("name") String name,
            @JsonProperty("unitName") String unitName,
            @JsonProperty("counterPrecision") Long counterPrecision){
        this.id = id;
        this.name = name;
        this.unitName = unitName;
        this.counterPrecision = counterPrecision;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductDTO that = (ProductDTO) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ProductDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", unitName='" + unitName + '\'' +
                ", counterPrecision='" + counterPrecision + '\'' +
                '}';
    }
}

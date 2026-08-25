package com.romanpulov.piastriawss.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentObjectDTO {

    private Long id;

    private String name;

    private String period;

    private String term;

    private Long payDelay;

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

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public Long getPayDelay() {
        return payDelay;
    }

    public void setPayDelay(Long payDelay) {
        this.payDelay = payDelay;
    }

    @JsonCreator
    public PaymentObjectDTO(
            @JsonProperty("id") Long id,
            @JsonProperty("name") String name,
            @JsonProperty("period") String period,
            @JsonProperty("term") String term,
            @JsonProperty("payDelay") Long payDelay
    ) {
        this.id = id;
        this.name = name;
        this.period = period;
        this.term = term;
        this.payDelay = payDelay;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentObjectDTO that = (PaymentObjectDTO) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "PaymentObjectDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", period='" + period + '\'' +
                ", term='" + term + '\'' +
                ", payDelay='" + payDelay + '\'' +
                '}';
    }
}

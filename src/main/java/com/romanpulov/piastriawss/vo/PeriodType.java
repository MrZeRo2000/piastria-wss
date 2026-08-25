package com.romanpulov.piastriawss.vo;

public enum PeriodType {
    D,
    M,
    Q;
    public static PeriodType defaultValue() {
        return PeriodType.M;
    }
}

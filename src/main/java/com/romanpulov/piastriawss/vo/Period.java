package com.romanpulov.piastriawss.vo;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Period {
    private static final Pattern TERM_PATTERN = Pattern.compile("(\\d+)?(\\w)");

    public final PeriodType periodType;

    public final long quantity;

    public Period(PeriodType periodType, long quantity) {
        this.periodType = periodType;
        this.quantity = quantity;
    }

    public static Period fromString(String value) {
        if (value != null && !value.isEmpty()) {
            Matcher matcher = TERM_PATTERN.matcher(value);
            if (matcher.matches() && (matcher.groupCount() == 2)) {

                PeriodType periodType;
                String termPeriod = matcher.group(2);
                try {
                    periodType = PeriodType.valueOf(termPeriod);
                } catch (IllegalArgumentException e) {
                    return null;
                }

                int quantity = 1;
                if (matcher.group(1) != null) {
                    try {
                        quantity = Integer.parseInt(matcher.group(1));
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }

                if (quantity <= 0) {
                    return null;
                } else {
                    return new Period(periodType, quantity);
                }

            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static LocalDate truncateToPeriodType(LocalDate date, PeriodType periodType) {
        switch (periodType) {
            case M:
                return date.withDayOfMonth(1);
            case Q:
                int month = date.getMonthValue();
                int quarterMonth = ((month - 1) / 3);
                return date.withDayOfMonth(1).withMonth(1 + quarterMonth * 3);
            default:
                throw new RuntimeException("Feature not implemented for " + periodType);
        }
    }

    public LocalDate addToDate(LocalDate date) {
        switch (periodType) {
            case D:
                return date.plusDays(quantity);
            case M:
                return date.plusMonths(quantity);
            case Q:
                return date.plusMonths(quantity * 3);
            default:
                throw new RuntimeException("Feature not implemented for " + periodType);
        }
    }
}

package com.payroll.model;

import static java.lang.String.format;

/**
 * Calendar model.
 * Contains calendar-related data.
 */
public record Payment(int month, int year, int paymentDate) {
    public int getCalculationMonth() {
        return month == 1 ? 12 : month - 1;
    }

    public int getCalculationYear() {
        return month == 1 ? year - 1 : year;
    }

    public String getPaymentPeriodKey() {
        return format("%04d-%02d", getCalculationYear(), getCalculationMonth());
    }

    public String toString() {
        return format("%04d-%02d-%02d", year, month, paymentDate);
    }
}

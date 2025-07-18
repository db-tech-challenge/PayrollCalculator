package com.payroll.model;

/**
 * Payment result model.
 * Contains data about the payroll calculation result.
 */
public record PaymentResult(
    String employeeId,
    double pay,
    String date,
    String settlementAccount,
    String currency,
    double basePay,
    double overtimePay,
    double deductions,
    String breakdown
) {

    public PaymentResult(String employeeId, double pay, String date, String settlementAccount, String currency) {
        this(employeeId, pay, date, settlementAccount, currency, 0.0, 0.0, 0.0, "");
    }
}

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
    String currency
) {
}

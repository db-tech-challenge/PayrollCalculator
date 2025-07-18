package com.payroll.model;


public record PayslipBreakdown(
    String employeeId,
    double base,
    double overtime,
    double deduction,
    double pay,
    String date,
    String settlementAccount,
    String currency
) {
}
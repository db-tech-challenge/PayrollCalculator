package com.payroll.model;


public record PayslipBreakdown(
    String employeeId,
    double grossBasePay,
    double overtimePay,
    double grossTotal,
    double taxDeduction,
    double netPay,
    String date,
    String settlementAccount,
    String currency,
    String breakdown
) {

    public static String formatBreakdown(double grossBasePay, double overtimePay, double taxDeduction, double netPay) {
        return String.format("Base: €%.2f | Overtime: €%.2f | Tax: €%.2f | Net: €%.2f", 
                           grossBasePay, overtimePay, taxDeduction, netPay);
    }
}
package com.payroll.model;

/**
 * Rate model.
 * Contains payment rate data.
 */
public record Rate(String employeeId, double rate, double overtimeRate) {
}

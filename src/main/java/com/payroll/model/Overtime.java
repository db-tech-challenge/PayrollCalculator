package com.payroll.model;

import java.time.LocalDate;

/**
 * Overtime model.
 * Contains data about overtime hours.
 */
public record Overtime(String employeeId, int overtimeHours, LocalDate date) {
}

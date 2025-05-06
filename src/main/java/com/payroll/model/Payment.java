package com.payroll.model;

/**
 * Calendar model.
 * Contains calendar-related data.
 */
public record Payment(int month, int year, String paymentDate) {
}

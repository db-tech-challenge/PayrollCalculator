package com.payroll.api;

import com.payroll.model.Calendar;
import com.payroll.model.Employee;
import com.payroll.model.Payment;
import com.payroll.model.Rate;
import com.payroll.model.TaxClass;
import java.util.List;
/**
 * Interface for base pay calculation service.
 * Defines methods for calculating base pay, days ratio, and tax factor.
 *
 * This service is used as a library service for external applications. Do not change the interface!!!
 */
public interface BasePayService {
    /**
     * Calculates the base pay for an employee based on their rate, payment details, tax class, and calendar data.
     *
     * @param employee - The employee for whom to calculate the base pay
     * @param rate     - The rate applicable to the employee
     * @param payment  - Payment details containing month and year
     * @param taxClass - The tax class applicable to the employee
     * @param calendar - List of calendar entries for the month
     * @return - Calculated base pay
     */
    double calculateBasePay(Employee employee, Rate rate, Payment payment, TaxClass taxClass,
                            List<Calendar> calendar);

    /**
     * Calculates the ratio of working days in a given month based on the calendar data.
     *
     * @param workingDays - Number of actual working days for the employee
     * @param calendar - List of calendar entries for the month
     * @param payment  - Payment details containing month and year
     * @return - Ratio of working days to total days in the month
     */
    double getDaysRatio(Integer workingDays, List<Calendar> calendar, Payment payment);

    /**
     * Gets the tax factor for a given tax class.
     *
     * @param taxClass - The tax class for which to get the factor
     * @return - The tax factor
     */
    double getTaxFactor(TaxClass taxClass);
}

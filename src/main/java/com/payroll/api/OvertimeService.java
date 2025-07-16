package com.payroll.api;

import com.payroll.model.Overtime;
import com.payroll.model.Rate;
import java.util.List;
import java.util.Map;

/**
 * Interface for managing overtime calculations.
 * Provides methods to aggregate overtime hours and calculate overtime pay.
 *
 * This service is used as a library service for external applications. Do not change the interface!!!
 */
public interface OvertimeService {

    /**
     * Aggregates overtime hours by month for each employee.
     *
     * @param overtimes List of overtime records
     * @return Map where keys are employee IDs and values are maps of month-year to total overtime hours
     */
    Map<String, Map<String, Integer>> aggregateOvertimesByMonth(List<Overtime> overtimes);

    /**
     * Retrieves the total overtime hours for a specific employee in a given month.
     *
     * @param overtimeMap Map of employee IDs to their monthly overtime hours
     * @param employeeId  ID of the employee
     * @param periodKey   Key representing the month and year (e.g., "2023-10")
     * @return Total overtime hours for the employee in that month
     */
    int getOvertimeHours(Map<String, Map<String, Integer>> overtimeMap, String employeeId,
                         String periodKey);

    /**
     * Calculates the overtime pay for an employee based on their rate and overtime hours.
     *
     * @param rate           The rate applicable to the employee
     * @param overtimeHours  Total overtime hours worked by the employee
     * @return Calculated overtime pay
     */
    double calculateOvertimePay(Rate rate, int overtimeHours);
}

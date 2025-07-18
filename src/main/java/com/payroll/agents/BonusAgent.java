package com.payroll.agents;

import com.payroll.model.Overtime;
import com.payroll.model.Rate;
import java.util.List;

/**
 * Robot #2: I calculate bonuses and overtime pay!
 */
public interface BonusAgent {

    /**
     * Calculate overtime pay for an employee
     */
    double calculateOvertimePay(String employeeId, List<Overtime> overtimes,
                                Rate rate, int year, int month);

    /**
     * Calculate performance bonus based on attendance
     */
    double calculatePerformanceBonus(double attendancePercentage, double baseSalary);

    /**
     * Get total bonus (overtime + performance)
     */
    double getTotalBonus(String employeeId, double attendancePercentage,
                         List<Overtime> overtimes, Rate rate, int year, int month);
}
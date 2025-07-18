package com.payroll.agents;

import com.payroll.model.Employee;
import com.payroll.model.Rate;
import com.payroll.model.TaxClass;

/**
 * Robot #3: I do all the salary math!
 */
public interface SalaryCalculatorAgent {

    /**
     * Calculate base salary considering days worked
     */
    double calculateBaseSalary(Employee employee, Rate rate,
                               int daysWorked, int totalWorkingDays);

    /**
     * Apply tax deductions
     */
    double applyTaxDeduction(double grossSalary, TaxClass taxClass);

    /**
     * Calculate net salary (base + bonus - deductions)
     */
    double calculateNetSalary(double baseSalary, double totalBonus,
                              TaxClass taxClass);
}
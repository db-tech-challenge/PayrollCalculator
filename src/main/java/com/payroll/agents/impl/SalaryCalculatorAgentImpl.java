package com.payroll.agents.impl;

import com.payroll.agents.SalaryCalculatorAgent;
import com.payroll.model.Employee;
import com.payroll.model.Rate;
import com.payroll.model.TaxClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Robot #3 Implementation: I do all the salary math!
 */
public class SalaryCalculatorAgentImpl implements SalaryCalculatorAgent {

    private static final Logger logger = LoggerFactory.getLogger(SalaryCalculatorAgentImpl.class);

    @Override
    public double calculateBaseSalary(Employee employee, Rate rate,
                                      int daysWorked, int totalWorkingDays) {
        logger.info("🤖 Calculator Robot: Calculating base salary for {}",
                employee.getEmployeeId());

        // If daysWorked is -1 (was null), use full month
        if (daysWorked == -1) {
            daysWorked = totalWorkingDays;
        }

        double monthlyRate = rate.rate();
        double daysRatio = (double) daysWorked / totalWorkingDays;
        double baseSalary = monthlyRate * daysRatio;

        logger.info("📊 Base salary: {} × ({}/{}) = {}",
                monthlyRate, daysWorked, totalWorkingDays, baseSalary);

        return baseSalary;
    }

    @Override
    public double applyTaxDeduction(double grossSalary, TaxClass taxClass) {
        logger.info("🤖 Calculator Robot: Applying tax deduction");

        double taxAmount = grossSalary * taxClass.factor();
        logger.info("💸 Tax deduction: {} × {} = {}",
                grossSalary, taxClass.factor(), taxAmount);

        return taxAmount;
    }

    @Override
    public double calculateNetSalary(double baseSalary, double totalBonus,
                                     TaxClass taxClass) {
        logger.info("🤖 Calculator Robot: Calculating net salary");

        // Gross salary = base + bonus
        double grossSalary = baseSalary + totalBonus;
        logger.info("💰 Gross salary: {} + {} = {}", baseSalary, totalBonus, grossSalary);

        // Calculate tax on gross salary
        double taxAmount = applyTaxDeduction(grossSalary, taxClass);

        // Net salary = gross - tax
        double netSalary = grossSalary - taxAmount;
        logger.info("✅ Net salary: {} - {} = {}", grossSalary, taxAmount, netSalary);

        return netSalary;
    }
}
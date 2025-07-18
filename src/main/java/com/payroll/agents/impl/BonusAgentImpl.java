package com.payroll.agents.impl;

import com.payroll.agents.BonusAgent;
import com.payroll.model.Overtime;
import com.payroll.model.Rate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Robot #2 Implementation: I calculate bonuses and overtime pay!
 */
public class BonusAgentImpl implements BonusAgent {

    private static final Logger logger = LoggerFactory.getLogger(BonusAgentImpl.class);
    private static final double OVERTIME_MULTIPLIER = 1.5;
    private static final int MAX_OVERTIME_HOURS = 10;

    @Override
    public double calculateOvertimePay(String employeeId, List<Overtime> overtimes,
                                       Rate rate, int year, int month) {
        logger.info("🤖 Bonus Robot: Calculating overtime for employee {}", employeeId);

        int totalOvertimeHours = 0;

        // Find overtime hours for this employee in this month
        for (Overtime overtime : overtimes) {
            if (overtime.employeeId().equals(employeeId) &&
                    overtime.date().getYear() == year &&
                    overtime.date().getMonthValue() == month) {

                totalOvertimeHours += overtime.overtimeHours();
                logger.debug("Found {} overtime hours on {}",
                        overtime.overtimeHours(), overtime.date());
            }
        }

        // Cap overtime hours at maximum
        int cappedHours = Math.min(totalOvertimeHours, MAX_OVERTIME_HOURS);
        if (cappedHours != totalOvertimeHours) {
            logger.warn("⚠️ Overtime hours capped from {} to {} for employee {}",
                    totalOvertimeHours, cappedHours, employeeId);
        }

        double overtimePay = cappedHours * rate.overtimeRate() * OVERTIME_MULTIPLIER;
        logger.info("💰 Overtime pay: {} hours × {} × {} = {}",
                cappedHours, rate.overtimeRate(), OVERTIME_MULTIPLIER, overtimePay);

        return overtimePay;
    }

    @Override
    public double calculatePerformanceBonus(double attendancePercentage, double baseSalary) {
        logger.info("🤖 Bonus Robot: Calculating performance bonus");

        double bonus = 0.0;

        if (attendancePercentage >= 100) {
            // Perfect attendance bonus: 5% of base salary
            bonus = baseSalary * 0.05;
            logger.info("🌟 Perfect attendance! Bonus: {}", bonus);
        } else if (attendancePercentage >= 95) {
            // Good attendance bonus: 2% of base salary
            bonus = baseSalary * 0.02;
            logger.info("👍 Good attendance! Bonus: {}", bonus);
        }

        return bonus;
    }

    @Override
    public double getTotalBonus(String employeeId, double attendancePercentage,
                                List<Overtime> overtimes, Rate rate, int year, int month) {
        double overtimePay = calculateOvertimePay(employeeId, overtimes, rate, year, month);
        double performanceBonus = calculatePerformanceBonus(attendancePercentage, rate.rate());

        double totalBonus = overtimePay + performanceBonus;
        logger.info("🎁 Total bonus for {}: {} (overtime: {}, performance: {})",
                employeeId, totalBonus, overtimePay, performanceBonus);

        return totalBonus;
    }
}
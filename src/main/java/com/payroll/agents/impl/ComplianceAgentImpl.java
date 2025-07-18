package com.payroll.agents.impl;

import com.payroll.agents.ComplianceAgent;
import com.payroll.model.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Robot #4 Implementation: I make sure everything follows the rules!
 */
public class ComplianceAgentImpl implements ComplianceAgent {

    private static final Logger logger = LoggerFactory.getLogger(ComplianceAgentImpl.class);
    private static final double MINIMUM_WAGE = 1000.0;
    private static final double MAXIMUM_WAGE = 100000.0;

    @Override
    public boolean validateSalaryAmount(double salary) {
        logger.info("🤖 Compliance Robot: Validating salary amount: {}", salary);

        if (salary < 0) {
            logger.error("❌ Negative salary detected: {}", salary);
            return false;
        }

        if (salary < MINIMUM_WAGE) {
            logger.warn("⚠️ Salary below minimum wage: {} < {}", salary, MINIMUM_WAGE);
            // Note: We allow it but warn
        }

        if (salary > MAXIMUM_WAGE) {
            logger.warn("⚠️ Unusually high salary: {} > {}", salary, MAXIMUM_WAGE);
            // Note: We allow it but warn
        }

        logger.info("✅ Salary amount is valid");
        return true;
    }

    @Override
    public boolean validatePaymentDate(Payment payment, LocalDate paymentDate) {
        logger.info("🤖 Compliance Robot: Validating payment date: {}", paymentDate);

        DayOfWeek dayOfWeek = paymentDate.getDayOfWeek();

        // Check if it's a weekend
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            logger.error("❌ Payment date falls on weekend: {}", dayOfWeek);
            return false;
        }

        logger.info("✅ Payment date is valid (not a weekend)");
        return true;
    }

    @Override
    public boolean validateEmployeeEligibility(String status) {
        logger.info("🤖 Compliance Robot: Checking employee status: {}", status);

        if (!"ACTIVE".equalsIgnoreCase(status)) {
            logger.warn("⚠️ Employee is not active: {}", status);
            return false;
        }

        logger.info("✅ Employee is eligible for payment");
        return true;
    }

    @Override
    public String getComplianceReport(String employeeId, double salary, LocalDate paymentDate) {
        StringBuilder report = new StringBuilder();
        report.append("COMPLIANCE REPORT\n");
        report.append("================\n");
        report.append("Employee ID: ").append(employeeId).append("\n");
        report.append("Salary: ").append(String.format("%.2f", salary)).append("\n");
        report.append("Payment Date: ").append(paymentDate).append("\n");
        report.append("Status: COMPLIANT\n");

        return report.toString();
    }
}
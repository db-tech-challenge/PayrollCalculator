package com.payroll.agents;

import com.payroll.model.Payment;
import java.time.LocalDate;

/**
 * Robot #4: I make sure everything follows the rules!
 */
public interface ComplianceAgent {

    /**
     * Check if salary amount is valid (not negative, within limits)
     */
    boolean validateSalaryAmount(double salary);

    /**
     * Check if payment date is valid (not weekend/holiday)
     */
    boolean validatePaymentDate(Payment payment, LocalDate paymentDate);

    /**
     * Verify employee is active and eligible for payment
     */
    boolean validateEmployeeEligibility(String status);

    /**
     * Get compliance report
     */
    String getComplianceReport(String employeeId, double salary, LocalDate paymentDate);
}
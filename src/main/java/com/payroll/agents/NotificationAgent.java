package com.payroll.agents;

import com.payroll.model.Employee;
import com.payroll.model.PaymentResult;

/**
 * Robot #5: I send notifications and save results!
 */
public interface NotificationAgent {

    /**
     * Generate settlement account from employee name
     */
    String generateSettlementAccount(Employee employee);

    /**
     * Create payment result record
     */
    PaymentResult createPaymentResult(Employee employee, double netSalary,
                                      String paymentDate, String settlementAccount);

    /**
     * Send notification (for now, just log it)
     */
    void sendPaymentNotification(Employee employee, PaymentResult result);

    /**
     * Format currency amount
     */
    String formatCurrency(double amount);
}
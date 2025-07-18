package com.payroll.agents.impl;

import com.payroll.agents.NotificationAgent;
import com.payroll.model.Employee;
import com.payroll.model.PaymentResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Robot #5 Implementation: I send notifications and save results!
 */
public class NotificationAgentImpl implements NotificationAgent {

    private static final Logger logger = LoggerFactory.getLogger(NotificationAgentImpl.class);

    @Override
    public String generateSettlementAccount(Employee employee) {
        logger.info("🤖 Notification Robot: Generating settlement account for {}",
                employee.getEmployeeId());

        String fullName = employee.getFullName();
        if (fullName == null || fullName.length() < 4) {
            logger.warn("⚠️ Invalid name for settlement account: {}", fullName);
            return "XXXX";
        }

        // Take first 4 characters of name in uppercase
        String account = fullName.substring(0, 4).toUpperCase();
        logger.info("📝 Generated settlement account: {}", account);

        return account;
    }

    @Override
    public PaymentResult createPaymentResult(Employee employee, double netSalary,
                                             String paymentDate, String settlementAccount) {
        logger.info("🤖 Notification Robot: Creating payment result for {}",
                employee.getEmployeeId());

        PaymentResult result = new PaymentResult(
                employee.getEmployeeId(),
                netSalary,
                paymentDate,
                settlementAccount,
                "EUR"
        );

        logger.info("📄 Created payment result: {}", result);
        return result;
    }

    @Override
    public void sendPaymentNotification(Employee employee, PaymentResult result) {
        logger.info("🤖 Notification Robot: Sending notification for {}",
                employee.getEmployeeId());

        // For now, just log the notification
        String message = String.format("""
            
            ====================================
            PAYMENT NOTIFICATION
            ====================================
            Employee: %s
            Employee ID: %s
            Amount: %s
            Payment Date: %s
            Account: %s
            ====================================
            
            """,
                employee.getFullName(),
                result.employeeId(),
                formatCurrency(result.pay()),
                result.date(),
                result.settlementAccount()
        );

        logger.info(message);

        // In a real system, this would send an email or SMS
        logger.info("📧 Notification sent to employee {} at {}",
                employee.getEmployeeId(), employee.getPhone());
    }

    @Override
    public String formatCurrency(double amount) {
        return String.format("€%.2f", amount);
    }
}
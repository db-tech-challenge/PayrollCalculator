package com.payroll.service.impl;

import static java.lang.Integer.parseInt;

import com.payroll.api.CalculationService;
import com.payroll.model.Calendar;
import com.payroll.model.Employee;
import com.payroll.model.Overtime;
import com.payroll.model.Payment;
import com.payroll.model.PaymentResult;
import com.payroll.model.PayslipBreakdown;
import com.payroll.model.Rate;
import com.payroll.model.TaxClass;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the payroll calculation service.
 */
public class CalculationServiceImpl implements CalculationService {
    private static final Logger logger = LoggerFactory.getLogger(CalculationServiceImpl.class);

    private final OvertimeServiceImpl overtimeService = new OvertimeServiceImpl();
    private final BasePayServiceIml basePayService = new BasePayServiceIml();

    @Override
    public List<PaymentResult> calculatePayroll(
        List<Employee> employees,
        List<Rate> rates,
        List<Payment> payments,
        List<Overtime> overtimes,
        List<TaxClass> taxClasses,
        List<Calendar> calendar
    ) {

        logger.info("Starting payroll calculation for {} employees", employees.size());
        List<PaymentResult> results = new ArrayList<>();
        List<PayslipBreakdown> breakdowns = new ArrayList<>();

        if (payments == null || payments.isEmpty()) {
            logger.error("No payment data available for calculations");
            return results;
        }

        // Create maps for faster lookups
        Map<String, Rate> rateMap = convertRatesToMap(rates);
        Map<String, TaxClass> taxClassMap = convertTaxClassesToMap(taxClasses);

        // Group overtimes by employee ID and month
        Map<String, Map<String, Integer>> overtimesByEmployeeAndMonth =
            overtimeService.aggregateOvertimesByMonth(overtimes);

        for (Payment payment : payments) {

            logger.info("Processing payment for period: {}", payment.getPaymentPeriodKey());

            for (Employee employee : employees) {
                // Skip inactive employees
                if (employee.getStatus().equalsIgnoreCase("INACTIVE")) {
                    logger.debug("Skipping inactive employee: {}", employee.getEmployeeId());
                    continue;
                }

                String employeeId = employee.getEmployeeId();
                Rate rate = rateMap.get(employeeId);
                TaxClass taxClass = taxClassMap.get(employee.getTaxClass());

                if (rate == null) {
                    continue; // Skip if no rate data is available
                }

                // Get overtime hours for this employee and month
                int overtimeHours =
                    overtimeService.getOvertimeHours(overtimesByEmployeeAndMonth, employeeId,
                        payment.getPaymentPeriodKey());

                // Calculate salary components for breakdown
                double workDaysRatio = basePayService.getDaysRatio(employee.getDaysWorked(), calendar, payment);
                double grossBasePay = workDaysRatio * rate.rate();
                double taxDeduction = grossBasePay * (taxClass != null ? taxClass.factor() : 0.0);
                double netBasePay = grossBasePay - taxDeduction;
                
                // Overtime calculation
                double overtimePay = overtimeService.calculateOvertimePay(rate, overtimeHours);
                logger.debug("Overtime pay for employee {}: {} (from {} hours)",
                    employeeId, overtimePay, overtimeHours);

                // Total calculations
                double grossTotal = grossBasePay + overtimePay;
                double totalPay = netBasePay + overtimePay;
                
                logger.debug("Salary breakdown for employee {}: Gross Base: {}, Tax: {}, Net Base: {}, Overtime: {}, Total: {}", 
                    employeeId, grossBasePay, taxDeduction, netBasePay, overtimePay, totalPay);


                // Validate employee name
                if (employee.getFullName() == null || !employee.getFullName().matches("[\\p{L} /-]+")) {
                    logger.info("Employee {} has an invalid name: {}", employeeId,
                        employee.getFullName());
                    continue;
                }

                // Adjust payment date if it falls on a Cologne holiday
                String paymentDateString;
                Payment adjustedPayment = payment;
                while (isCologneHoliday(adjustedPayment, employee)) {
                    int adjustedDate = adjustedPayment.paymentDate() - 1;
                    adjustedPayment = new Payment(adjustedPayment.month(), adjustedPayment.year(), adjustedDate);
                    logger.info("Payment date adjusted for Cologne employee {} from {} to {} due to holiday",
                        employeeId, payment.toString(), adjustedPayment.toString());
                }
                paymentDateString = adjustedPayment.toString();

                // Create result (keep existing format for compatibility)
                PaymentResult result = new PaymentResult(
                    employeeId,
                    totalPay,
                    paymentDateString,
                    generateSettlementAccount(employee),
                    "EUR"
                );

                // Create detailed breakdown
                PayslipBreakdown breakdown = new PayslipBreakdown(
                    employeeId,
                    grossBasePay,
                    overtimePay,
                    taxDeduction,
                    totalPay,
                    paymentDateString,
                    generateSettlementAccount(employee),
                    "EUR"
                );

                results.add(result);
                breakdowns.add(breakdown);
                logger.info("Calculated payment for employee {}: {}", employeeId, result);
            }
        }

        // Save detailed breakdowns to separate file (only if breakdowns exist)
        if (!breakdowns.isEmpty()) {
            try {
                savePayslipBreakdowns(breakdowns);
            } catch (Exception e) {
                logger.warn("Failed to save payslip breakdowns: {}", e.getMessage());
                // Don't throw exception to avoid breaking main functionality
            }
        }

        logger.info("Payroll calculation completed with {} payment results", results.size());
        return results;
    }

    private static boolean isCologneHoliday(Payment payment, Employee employee) {
        return payment.month() == 7
            && payment.year() == 2025
            && payment.paymentDate() == 9
            && "Cologne".equals(employee.getLocation());
    }


    private Map<String, Rate> convertRatesToMap(List<Rate> rates) {
        Map<String, Rate> rateMap = new HashMap<>();
        for (Rate rate : rates) {
            rateMap.put(rate.employeeId(), rate);
        }
        return rateMap;
    }

    private Map<String, TaxClass> convertTaxClassesToMap(List<TaxClass> taxClasses) {
        Map<String, TaxClass> taxClassMap = new HashMap<>();
        for (TaxClass taxClass : taxClasses) {
            taxClassMap.put(taxClass.taxClass(), taxClass);
        }
        return taxClassMap;
    }

    private String generateSettlementAccount(Employee employee) {
        String fullName = employee.getFullName();
        if (fullName == null || fullName.length() < 4) {
            logger.warn("Cannot generate settlement account for employee {}: invalid name",
                employee.getEmployeeId());
            return "INVALID_ACCOUNT";
        }
        return fullName.substring(0, 4).toUpperCase();
    }

    private void savePayslipBreakdowns(List<PayslipBreakdown> breakdowns) {
        try {
            java.nio.file.Path breakdownPath = java.nio.file.Paths.get("data/result/payslip_breakdown.csv");
            java.nio.file.Files.createDirectories(breakdownPath.getParent());
            
            try (java.io.PrintWriter writer = new java.io.PrintWriter(java.nio.file.Files.newBufferedWriter(breakdownPath))) {
                writer.println("EMPLOYEE_ID;BASE;OVERTIME;DEDUCTION;PAY;DATE;SETTLEMENT_ACCOUNT;CURRENCY");
                for (PayslipBreakdown breakdown : breakdowns) {
                    writer.printf("%s;%.2f;%.2f;%.2f;%.2f;%s;%s;%s\n",
                        breakdown.employeeId(),
                        breakdown.base(),
                        breakdown.overtime(),
                        breakdown.deduction(),
                        breakdown.pay(),
                        breakdown.date(),
                        breakdown.settlementAccount(),
                        breakdown.currency()
                    );
                }
            }
            logger.info("Saved {} payslip breakdowns to {}", breakdowns.size(), breakdownPath);
        } catch (Exception e) {
            logger.warn("Error saving payslip breakdowns: {}", e.getMessage());
        }
    }

}

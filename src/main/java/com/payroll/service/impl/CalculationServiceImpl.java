package com.payroll.service.impl;

import com.payroll.api.CalculationService;
import com.payroll.model.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the payroll calculation service.
 */
public class CalculationServiceImpl implements CalculationService {
    private static final Logger logger = LoggerFactory.getLogger(CalculationServiceImpl.class);
    private static final double OVERTIME_COEFFICIENT = 1.5;
    private static final int MAX_OVERTIME_HOURS = 10; // Maximum allowed overtime hours

    @Override
    public List<PaymentResult> calculatePayroll(
        List<Employee> employees,
        List<Rate> rates,
        List<Payment> payments,
        List<Overtime> overtimes,
        List<TaxClass> taxClasses,
        List<Calendar> calendar) {

        logger.info("Starting payroll calculation for {} employees", employees.size());
        List<PaymentResult> results = new ArrayList<>();

        if (payments == null || payments.isEmpty()) {
            logger.error("No payment data available for calculations");
            return results;
        }

        // Create maps for faster lookups
        Map<String, Rate> rateMap = convertRatesToMap(rates);
        Map<String, TaxClass> taxClassMap = convertTaxClassesToMap(taxClasses);

        // Group overtimes by employee ID and month
        Map<String, Map<String, Integer>> overtimesByEmployeeAndMonth =
            aggregateOvertimesByMonth(overtimes);

        for (Payment payment : payments) {
            int paymentMonth = payment.month();
            int paymentYear = payment.year();
            String paymentPeriodKey = paymentYear + "-" + paymentMonth;

            logger.info("Processing payment for period: {}", paymentPeriodKey);

            for (Employee employee : employees) {
                // Skip inactive employees
                if (employee.getStatus().equalsIgnoreCase("INACTIVE")) {
                    logger.debug("Skipping inactive employee: {}", employee.getEmployeeId());
                }

                String employeeId = employee.getEmployeeId();
                Rate rate = rateMap.get(employeeId);
                TaxClass taxClass = taxClassMap.get(employee.getTaxClass());

                if (rate == null) {
                    continue; // Skip if no rate data is available
                }

                // Get overtime hours for this employee and month
                int overtimeHours =
                    getOvertimeHours(overtimesByEmployeeAndMonth, employeeId, paymentPeriodKey);

                // Base pay calculation
                double basePay = calculateBasePay(employee, rate, payment, taxClass,calendar);
                logger.debug("Base pay for employee {}: {}", employeeId, basePay);

                // Overtime calculation
                double overtimePay = calculateOvertimePay(rate, overtimeHours);
                logger.debug("Overtime pay for employee {}: {} (from {} hours)",
                    employeeId, overtimePay, overtimeHours);

                // Total pay
                double totalPay = basePay + overtimePay;
                logger.debug("Total pay for employee {}: {}", employeeId, totalPay);


                // Validate employee name
                if(!employee.getFullName().matches("[\\p{L} /-]+")) {
                    logger.info("Employee {} has an invalid name: {}", employeeId, employee.getFullName());
                    continue;
                }

                // Create result
                PaymentResult result = new PaymentResult(
                    employeeId,
                    totalPay,
                    payment.month() + "." + payment.paymentDate() + "." + payment.year(),
                    generateSettlementAccount(employee),
                    "EUR"
                );

                results.add(result);
                logger.info("Calculated payment for employee {}: {}", employeeId, result);
            }
        }

        logger.info("Payroll calculation completed with {} payment results", results.size());
        return results;
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

    private Map<String, Map<String, Integer>> aggregateOvertimesByMonth(List<Overtime> overtimes) {
        Map<String, Map<String, Integer>> result = new HashMap<>();

        for (Overtime overtime : overtimes) {
            String employeeId = overtime.employeeId();
            LocalDate date = overtime.date();
            int hours = overtime.overtimeHours();

            if (employeeId == null || date == null) {
                continue;
            }

            String periodKey = date.getYear() + "-" + date.getMonthValue();
            Map<String, Integer> employeeOvertimes =
                result.computeIfAbsent(employeeId, k -> new HashMap<>());
            employeeOvertimes.put(periodKey, employeeOvertimes.getOrDefault(periodKey, 0) + hours);
        }

        return result;
    }

    private int getOvertimeHours(
        Map<String, Map<String, Integer>> overtimeMap,
        String employeeId,
        String periodKey) {

        Map<String, Integer> employeeOvertimes = overtimeMap.get(employeeId);
        if (employeeOvertimes == null) {
            return 0;
        }

        return employeeOvertimes.getOrDefault(periodKey, 0);
    }

    public double calculateBasePay(Employee employee, Rate rate, Payment payment,
                                   TaxClass taxClass, List<Calendar> calendar) {
        double daysRatio = getDaysRatio(calendar,payment);
        double taxFactor = getTaxFactor(taxClass);

        logger.debug("Days ratio for employee {}: {}", employee.getEmployeeId(), daysRatio);
        logger.debug("Tax factor for employee {}: {}", employee.getEmployeeId(), taxFactor);

        return employee.getDaysWorked() * daysRatio * rate.rate() * taxFactor;
    }

    private static double getTaxFactor(TaxClass taxClass) {
        return 1.0;// TODO: Implement tax factor calculation
    }


    public double calculateOvertimePay(Rate rate, int overtimeHours) {
        if (overtimeHours <= 0) {
            return 0;
        }

        int cappedHours = Math.min(overtimeHours, MAX_OVERTIME_HOURS);
        if (cappedHours != overtimeHours) {
            logger.warn("Overtime hours for employee {} limited from {} to {}",
                rate.employeeId(), overtimeHours, cappedHours);
        }

        return cappedHours * rate.overtimeRate() * OVERTIME_COEFFICIENT;
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

    private static double getDaysRatio(List<Calendar> calendar, Payment payment) {
        int workingDays = 20;
        int daysInMonth = 30;
        // TODO: Use calendar data
        return (double) workingDays / daysInMonth;
    }
}

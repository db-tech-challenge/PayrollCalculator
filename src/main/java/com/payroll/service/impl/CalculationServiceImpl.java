package com.payroll.service.impl;

import static java.lang.Integer.parseInt;

import com.payroll.api.CalculationService;
import com.payroll.model.Calendar;
import com.payroll.model.Employee;
import com.payroll.model.Overtime;
import com.payroll.model.Payment;
import com.payroll.model.PaymentResult;
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

                // Base pay calculation
                double basePay =
                    basePayService.calculateBasePay(employee, rate, payment, taxClass, calendar);
                logger.debug("Base pay for employee {}: {}", employeeId, basePay);

                // Overtime calculation
                double overtimePay = overtimeService.calculateOvertimePay(rate, overtimeHours);
                logger.debug("Overtime pay for employee {}: {} (from {} hours)",
                    employeeId, overtimePay, overtimeHours);

                // Total pay
                double totalPay = basePay + overtimePay;
                logger.debug("Total pay for employee {}: {}", employeeId, totalPay);


                // Validate employee name
                if (employee.getFullName() == null || !employee.getFullName().matches("[\\p{L} /-]+")) {
                    logger.info("Employee {} has an invalid name: {}", employeeId,
                        employee.getFullName());
                    continue;
                }

                if (isCologneHoliday(payment, employee)) {
                    logger.warn(
                        "Date of payments is on Cologne holiday",
                        employeeId);
                }

                // Create result
                PaymentResult result = new PaymentResult(
                    employeeId,
                    totalPay,
                    payment.toString(),
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


}

package com.payroll.service.impl;

import com.payroll.service.ValidationService;
import com.payroll.exception.ValidationException;
import com.payroll.model.Calendar;
import com.payroll.model.Employee;
import com.payroll.model.Overtime;
import com.payroll.model.Payment;
import com.payroll.model.Rate;
import com.payroll.model.TaxClass;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the data validation service.
 */
public class ValidationServiceImpl implements ValidationService {
    private static final Logger logger = LoggerFactory.getLogger(ValidationServiceImpl.class);

    @Override
    public void validateData(
        List<Employee> employees,
        List<Rate> rates,
        List<Payment> payments,
        List<Overtime> overtimes,
        List<TaxClass> taxClasses,
        List<Calendar> calendar
    ) throws ValidationException {
        logger.info("Starting data validation");

        // Basic data presence check
        if (employees == null || employees.isEmpty()) {
            logger.error("Employee data is missing");
            throw new ValidationException("Employee data is missing");
        }

        if (rates == null || rates.isEmpty()) {
            logger.error("Rate data is missing");
            throw new ValidationException("Rate data is missing");
        }

        if (payments == null || payments.isEmpty()) {
            logger.error("Payment data is missing");
            throw new ValidationException("Payment data is missing");
        }

        if (taxClasses == null || taxClasses.isEmpty()) {
            logger.error("Tax class data is missing");
            throw new ValidationException("Tax class data is missing");
        }

        // Calendar data check
        if (calendar == null || calendar.isEmpty()) {
            logger.error("Calendar data is missing");
            throw new ValidationException("Calendar data is missing");
        }

        // Check if each payment period has calendar data
        for (Payment payment : payments) {
            int year = payment.year();
            int month = payment.month();

            long daysInMonth = calendar.stream()
                .filter(day -> day.year() == year && day.month() == month)
                .count();

            if (daysInMonth == 0) {
                logger.error("No calendar data for payment period: {}-{}", year, month);
                throw new ValidationException("No calendar data for payment period: " + year + "-" + month);
            }

            long workingDays = calendar.stream()
                .filter(day -> day.year() == year && day.month() == month && day.isWorkingDay())
                .count();

            if (workingDays == 0) {
                logger.warn("No working days found for payment period: {}-{}", year, month);
            }
        }

        // Create maps for faster lookups
        Map<String, Employee> employeeMap = createEmployeeMap(employees);
        Map<String, Rate> rateMap = createRateMap(rates);
        Map<String, TaxClass> taxClassMap = createTaxClassMap(taxClasses);

        // Find duplicate employee IDs
        Set<String> duplicateIds = findDuplicateEmployeeIds(employees);
        if (!duplicateIds.isEmpty()) {
            logger.warn("Found duplicate employee IDs: {}", duplicateIds);
        }

        // Cross-file data consistency check
        for (Employee employee : employees) {
            String employeeId = employee.getEmployeeId();

            // Required employee fields check
            if (employee.getFullName() == null || employee.getFullName().isEmpty()) {
                logger.warn("Employee {} has no name", employeeId);
            }

            if (employee.getTaxClass() == null || employee.getTaxClass().isEmpty()) {
                logger.warn("Employee {} has no tax class", employeeId);
            }

            // Check if rate exists
            if (!rateMap.containsKey(employeeId)) {
                logger.warn("No rate found for employee {}", employeeId);
            }

            // Tax class validity check
            if (employee.getTaxClass() != null && !employee.getTaxClass().isEmpty() &&
                !taxClassMap.containsKey(employee.getTaxClass())) {
                logger.warn("Invalid tax class for employee {}: {}", employeeId, employee.getTaxClass());
            }

        }

        // Validate overtime data
        for (Overtime overtime : overtimes) {
            String employeeId = overtime.employeeId();

            // Check if employee exists
            if (!employeeMap.containsKey(employeeId)) {
                logger.warn("Overtime entry for non-existent employee: {}", employeeId);
            }

            // Check if date is valid
            if (overtime.date() == null) {
                logger.warn("Overtime entry has no date: {}", overtime);
            }
        }

        logger.info("Data validation completed");
    }

    private Set<String> findDuplicateEmployeeIds(List<Employee> employees) {
        Set<String> seenIds = new HashSet<>();
        Set<String> duplicateIds = new HashSet<>();

        for (Employee employee : employees) {
            String employeeId = employee.getEmployeeId();
            if (!seenIds.add(employeeId)) {
                duplicateIds.add(employeeId);
            }
        }

        return duplicateIds;
    }

    private Map<String, Employee> createEmployeeMap(List<Employee> employees) {
        return employees.stream()
            .collect(Collectors.toMap(
                Employee::getEmployeeId,
                e -> e,
                (existing, replacement) -> existing // Keep first occurrence in case of duplicates
            ));
    }

    private Map<String, Rate> createRateMap(List<Rate> rates) {
        return rates.stream()
            .collect(Collectors.toMap(
                Rate::employeeId,
                r -> r,
                (existing, replacement) -> existing // Keep first occurrence in case of duplicates
            ));
    }

    private Map<String, TaxClass> createTaxClassMap(List<TaxClass> taxClasses) {
        return taxClasses.stream()
            .collect(Collectors.toMap(
                TaxClass::taxClass,
                t -> t,
                (existing, replacement) -> existing // Keep first occurrence in case of duplicates
            ));
    }
}
package com.payroll.service;

import com.payroll.exception.ValidationException;
import com.payroll.model.Calendar;
import com.payroll.model.Employee;
import com.payroll.model.Overtime;
import com.payroll.model.Payment;
import com.payroll.model.Rate;
import com.payroll.model.TaxClass;
import java.util.List;

/**
 * Interface for data validation.
 * Defines methods for checking data correctness.
 */
public interface ValidationService {

    /**
     * Validates the correctness of all data.
     *
     * @param employees  List of employees
     * @param rates      Payment rates
     * @param calendar   Calendar data
     * @param overtimes  Overtime hours
     * @param taxClasses Tax classes
     * @throws ValidationException If invalid data is found
     */
    void validateData(
        List<Employee> employees,
        List<Rate> rates,
        List<Payment> payments,
        List<Overtime> overtimes,
        List<TaxClass> taxClasses,
        List<Calendar> calendar

    ) throws ValidationException;
}

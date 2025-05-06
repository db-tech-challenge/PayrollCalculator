package com.payroll.api;

import com.payroll.model.*;

import java.util.List;
import java.util.Map;

/**
 * Interface for payroll calculation.
 * Defines methods for performing calculations.
 */
public interface CalculationService {

    /**
     * Calculates payroll for a list of employees.
     *
     * @param employees  List of employees
     * @param rates      Payment rates
     * @param payments   Payment data
     * @param overtimes  Overtime hours
     * @param taxClasses Tax classes
     * @return List of calculation results
     */
    List<PaymentResult> calculatePayroll(
        List<Employee> employees,
        List<Rate> rates,
        List<Payment> payments,
        List<Overtime> overtimes,
        List<TaxClass> taxClasses,
        List<Calendar> calendar

    );
}




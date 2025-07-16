package com.payroll.api;

import com.payroll.model.Calendar;
import com.payroll.model.Employee;
import com.payroll.model.Overtime;
import com.payroll.model.Payment;
import com.payroll.model.PaymentResult;
import com.payroll.model.Rate;
import com.payroll.model.TaxClass;
import java.util.List;

/**
 * Interface for payroll calculation.
 * Defines methods for performing calculations.
 *
 * This service is used as a library service for external applications. Do not change the interface!!!
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




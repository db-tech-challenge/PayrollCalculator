package com.payroll.api;

import com.payroll.exception.DataLoadException;
import com.payroll.model.Calendar;
import com.payroll.model.Employee;
import com.payroll.model.Overtime;
import com.payroll.model.Payment;
import com.payroll.model.PaymentResult;
import com.payroll.model.Rate;
import com.payroll.model.TaxClass;
import java.util.List;

/**
 * Interface for working with data files.
 * Defines methods for loading and saving data.
 */
public interface FileService {

    /**
     * Loads employee data from a file.
     *
     * @return List of employees
     * @throws DataLoadException If an error occurs while loading data
     */
    List<Employee> loadEmployees() throws DataLoadException;

    /**
     * Loads rate data from a file.
     *
     * @return Map of rates, where the key is the employee ID
     * @throws DataLoadException If an error occurs while loading data
     */
    List<Rate> loadRates() throws DataLoadException;

    /**
     * Loads calendar data from a file.
     *
     * @return List of calendar entries
     * @throws DataLoadException If an error occurs while loading data
     */
    List<Payment> loadPayments() throws DataLoadException;

    /**
     * Loads overtime data from a file.
     *
     * @return Map of overtimes, where the key is the employee ID
     * @throws DataLoadException If an error occurs while loading data
     */
    List<Overtime> loadOvertimes() throws DataLoadException;

    /**
     * Loads tax class data from a file.
     *
     * @return Map of tax classes, where the key is the tax class ID
     * @throws DataLoadException If an error occurs while loading data
     */
    List<TaxClass> loadTaxClasses() throws DataLoadException;

    /**
     * Loads calculation results from a file.
     *
     * @param path Path to the file
     * @return List of payment results
     */
    List<PaymentResult> loadResult(String path);

    /**
     * Loads calendar data from a file.
     *
     * @return List of calendar entries
     *
     */
    List<Calendar> loadCalendar() throws DataLoadException;

    /**
     * Loads calculation results from a file.
     *
     * @return List of payment results
     */
    List<PaymentResult> loadResult();

    /**
     * Saves calculation results to a file.
     *
     * @param results Calculation results
     * @throws DataLoadException If an error occurs while saving data
     */
    void saveResults(List<PaymentResult> results) throws DataLoadException;
}

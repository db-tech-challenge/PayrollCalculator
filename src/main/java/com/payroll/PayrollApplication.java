package com.payroll;
//test branch and push

import com.payroll.service.impl.CalculationServiceImpl;
import com.payroll.service.impl.FileServiceImpl;
import com.payroll.service.impl.ValidationServiceImpl;
import com.payroll.util.DefaultFileReader;
import com.payroll.util.SimpleCsvParser;

/**
 * Main application class that initializes and starts the payroll calculator.
 */
public class PayrollApplication {

    public static final String DEFAULT_DATA_ROUTE = "data";
    // File paths constants

    public static void main(String[] args) {
        String dataRoute = args.length > 0 ? args[0] : DEFAULT_DATA_ROUTE;
        // check if dataRoute is a valid path
        if (dataRoute == null || dataRoute.isEmpty()) {
            throw new IllegalArgumentException("Data route cannot be null or empty");
        }
        // Initialization
        new PayrollCalculator(
            new FileServiceImpl(dataRoute, new SimpleCsvParser(new DefaultFileReader())),
            new ValidationServiceImpl(),
            new CalculationServiceImpl()
        ).run();

    }
}
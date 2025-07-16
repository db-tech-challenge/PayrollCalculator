package com.payroll.service.impl;

import static java.lang.Integer.parseInt;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Optional.ofNullable;

import com.payroll.service.FileService;
import com.payroll.exception.DataLoadException;
import com.payroll.model.*;
import com.payroll.util.CsvParser;

import java.io.File;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Implementation of the file handling service.
 */
public class FileServiceImpl implements FileService {
    public static final String MAIN_DATA_PATH = "main_data.csv";
    public static final String RATE_DATA_PATH = "rate.csv";
    public static final String PAYMENT_DATA_PATH = "payments.csv";
    public static final String OVERTIME_DATA_PATH = "overtime_data.csv";
    public static final String TAX_CLASS_DATA_PATH = "tax_class_data.csv";
    public static final String CALENDAR_DATA_PATH = "calendar_data.csv";
    public static final String HOLIDAYS_DATA_PATH = "local_holidays.csv";
    public static final String OUTPUT_PATH = "result/main_data_result.csv";

    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
    private final String dataRoot;
    private final CsvParser csvParser;

    public FileServiceImpl(String dataRoot, CsvParser csvParser) {
        this.dataRoot = dataRoot;
        this.csvParser = csvParser;
    }

    private <T> List<T> loadFromCsv(String filename, Function<Map<String, String>, T> mapper)
        throws DataLoadException {
        String filePath = dataRoot + File.separator + filename;
        logger.info("Loading data from {}", filePath);
        List<T> result = new ArrayList<>();

        try {
            List<Map<String, String>> records = csvParser.parseWithHeaders(filePath);
            for (Map<String, String> record : records) {
                try {
                    T item = mapper.apply(record);
                    if (item != null) {
                        result.add(item);
                        logger.debug("Loaded item: {}", item);
                    }
                } catch (Exception e) {
                    logger.warn("Failed to parse record: {}", record, e);
                }
            }
            logger.info("Loaded {} items from {}", result.size(), filePath);
            return result;
        } catch (Exception e) {
            logger.error("Error loading data from {}", filePath, e);
            throw new DataLoadException("Error loading data: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Employee> loadEmployees() throws DataLoadException {
        return loadFromCsv(MAIN_DATA_PATH, record -> {
            try {
                return new Employee(
                    record.get("Name"),
                    record.get("Location"),
                    record.get("Employee ID"),
                    record.get("Tax Class"),
                    record.get("AT Level"),
                    record.get("Status"),
                    record.get("Days Worked").matches("\\d+") ?
                        parseInt(record.get("Days Worked")) : null,
                    record.get("Phone Number"),
                    record.get("Birthday"),
                    record.get("Password")
                );
            } catch (Exception e) {
                logger.warn("Failed to create Employee from the record: {}", record);
                return null;
            }
        });
    }

    @Override
    public List<Rate> loadRates() throws DataLoadException {
        return loadFromCsv(RATE_DATA_PATH, record -> {
            try {
                return new Rate(
                    record.get("EMPLOYEE_ID"),
                    Double.parseDouble(record.get("RATE")),
                    Double.parseDouble(record.get("OVERTIME_RATE"))
                );
            } catch (Exception e) {
                logger.warn("Failed to create Rate: {}", e.getMessage());
                return null;
            }
        });
    }

    @Override
    public List<Payment> loadPayments() throws DataLoadException {
        return loadFromCsv(PAYMENT_DATA_PATH, record -> {
            try {
                return new Payment(
                    parseInt(record.get("MONTH")),
                    parseInt(record.get("YEAR")),
                    parseInt(record.get("PAYMENT_DATE"))
                );
            } catch (Exception e) {
                logger.warn("Failed to create Payment: {}", e.getMessage());
                return null;
            }
        });
    }

    @Override
    public List<Overtime> loadOvertimes() throws DataLoadException {
        return loadFromCsv(OVERTIME_DATA_PATH, record -> {
            try {
                LocalDate date = LocalDate.parse(record.get("DATE"), ofPattern("yyyy-MM-dd"));
                return new Overtime(
                    record.get("EMPLOYEE_ID"),
                    parseInt(record.get("OVERTIME_DATA")),
                    date
                );
            } catch (Exception e) {
                logger.warn("Failed to create Overtime: {}", e.getMessage());
                return null;
            }
        });
    }

    @Override
    public List<TaxClass> loadTaxClasses() throws DataLoadException {
        return loadFromCsv(TAX_CLASS_DATA_PATH, record -> {
            try {
                return new TaxClass(
                    record.get("TAX_CLASS"),
                    Double.parseDouble(record.get("FACTOR"))
                );
            } catch (Exception e) {
                logger.warn("Failed to create TaxClass: {}", e.getMessage());
                return null;
            }
        });
    }

    @Override
    public List<PaymentResult> loadResult(String filePath) {
        logger.info("Loading results from {}", filePath);
        try {
            return loadFromCsv(filePath, record -> {
                try {
                    return new PaymentResult(
                        record.get("EMPLOYEE_ID"),
                        Double.parseDouble(record.get("PAY")),
                        record.get("DATE"),
                        record.get("SETTLEMENT_ACCOUNT"),
                        record.get("CURRENCY")
                    );
                } catch (Exception e) {
                    logger.warn("Failed to create PaymentResult: {}", e.getMessage());
                    return null;
                }
            });
        } catch (DataLoadException e) {
            logger.error("Error loading results from {}", filePath, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Calendar> loadCalendar() throws DataLoadException {
        return loadFromCsv(CALENDAR_DATA_PATH, record -> {
            try {
                return Calendar.of(
                    parseInt(record.get("YEAR")),
                    parseInt(record.get("MONTH")),
                    parseInt(record.get("DAY")),
                    record.get("DAY_OF_WEEK"),
                    record.get("HOLIDAY")
                );
            } catch (Exception e) {
                logger.warn("Failed to create Calendar entry: {}", e.getMessage());
                return null;
            }
        });
    }

    @Override
    public List<PaymentResult> loadResult() {
        return loadResult(OUTPUT_PATH);
    }

    @Override
    public void saveResults(List<PaymentResult> results) throws DataLoadException {
        String filePath = dataRoot + File.separator + OUTPUT_PATH;
        logger.info("Saving results to {}", filePath);
        File file = new File(filePath);
        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            logger.error("Error creating file {}", filePath, e);
            throw new DataLoadException("Error creating file: " + e.getMessage(), e);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("EMPLOYEE_ID;PAY;DATE;SETTLEMENT_ACCOUNT;CURRENCY");
            writer.newLine();

            for (PaymentResult result : results) {
                writer.write(String.format("%s;%.2f;%s;%s;%s",
                    result.employeeId(),
                    result.pay(),
                    result.date(),
                    result.settlementAccount(),
                    result.currency()));
                writer.newLine();

                logger.debug("Saved result: {}", result);
            }

            logger.info("Saved {} results", results.size());
        } catch (IOException e) {
            logger.error("Error saving results to {}", filePath, e);
            throw new DataLoadException("Error saving results: " + e.getMessage(), e);
        }
    }
}
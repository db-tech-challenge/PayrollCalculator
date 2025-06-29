package com.payroll.util;

import java.util.List;
import java.util.Map;

/**
 * Interface for CSV parsing strategies that work with lists of lines.
 */
public interface CsvParser {
    /**
     * Parse a list of CSV lines into a list of string arrays.
     *
     * @param path Path to the CSV file
     * @return List of string arrays representing CSV rows
     */
    List<String[]> parse(String path);

    /**
     * Parse a list of CSV lines with headers into a list of maps (header -> value).
     *
     * @param path Path to the CSV file
     * @return List of maps where keys are header names and values are cell values
     */
    List<Map<String, String>> parseWithHeaders(String path);
}
package com.payroll.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple implementation of CsvParser.
 */
public class SimpleCsvParser implements CsvParser {
    private static final Logger logger = LoggerFactory.getLogger(SimpleCsvParser.class);
    private final char delimiter;
    private final FileReader fileReader;

    /**
     * Constructs a CSV parser with the default delimiter (semicolon).
     */
    public SimpleCsvParser(FileReader fileReader) {
        this(fileReader, ';');
    }

    /**
     * Constructs a CSV parser with a specified delimiter.
     *
     * @param delimiter The character to use as delimiter
     */
    public SimpleCsvParser(FileReader fileReader, char delimiter) {
        this.fileReader = fileReader;
        this.delimiter = delimiter;
    }

    @Override
    public List<String[]> parse(String path) {
        List<String> lines = fileReader.readAllLines(path);
        List<String[]> result = new ArrayList<>();

        if (lines == null || lines.isEmpty()) {
            logger.warn("Empty or null lines provided for parsing");
            return result;
        }

        for (String line : lines) {
            if (line == null || line.trim().isEmpty()) {
                continue;
            }

            result.add(parseLine(line));
        }

        return result;
    }

    @Override
    public List<Map<String, String>> parseWithHeaders(String path) {
        List<String> lines = fileReader.readAllLines(path);
        List<Map<String, String>> result = new ArrayList<>();

        if (lines == null || lines.isEmpty()) {
            logger.warn("Empty or null lines provided for parsing");
            return result;
        }

        // Get headers from the first line
        String headerLine = lines.get(0);
        if (headerLine == null || headerLine.trim().isEmpty()) {
            logger.warn("Empty header line");
            return result;
        }

        String[] headers = parseLine(headerLine);

        // Parse data rows (skip the header)
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line == null || line.trim().isEmpty()) {
                continue;
            }

            String[] values = parseLine(line);
            Map<String, String> row = new HashMap<>();

            for (int j = 0; j < headers.length && j < values.length; j++) {
                row.put(headers[j], values[j]);
            }

            result.add(row);
        }

        return result;
    }

    /**
     * Parse a single CSV line into an array of strings.
     *
     * @param line The CSV line
     * @return Array of string values
     */
    private String[] parseLine(String line) {
        if (line == null || line.isEmpty()) {
            return new String[0];
        }

        // Split by delimiter
        String[] tokens = line.split(String.valueOf(delimiter), -1);

        // Process each token
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = tokens[i].trim();
            if (tokens[i].isEmpty()) {
                tokens[i] = null;
            }
        }

        return tokens;
    }
}
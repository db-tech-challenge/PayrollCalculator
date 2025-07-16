package com.payroll.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for working with CSV files.
 */
public class CsvUtil {
    private static final Logger logger = LoggerFactory.getLogger(CsvUtil.class);
    private static final char DEFAULT_SEPARATOR = ';';

    /**
     * Parses a CSV line using the default separator.
     *
     * @param line CSV line
     * @return Array of values
     */
    public String[] parseLine(String line) {
        return parseLine(line, DEFAULT_SEPARATOR);
    }

    /**
     * Parses a CSV line using the specified separator.
     *
     * @param line CSV line
     * @param separator Separator character
     * @return Array of values
     */
    public String[] parseLine(String line, char separator) {
        if (line == null || line.isEmpty()) {
            logger.warn("Empty line provided for parsing");
            return new String[0];
        }

        String[] tokens = line.split(String.valueOf(separator), -1);

        // Handle empty values
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = tokens[i].trim();
            if (tokens[i].isEmpty()) {
                tokens[i] = null;
            }
        }

        return tokens;
    }
}

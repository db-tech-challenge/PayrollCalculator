package com.payroll.util;

import java.util.List;

/**
 * Interface for reading all lines from a file.
 */
public interface FileReader {
    /**
     * Read all lines from a file.
     *
     * @param filePath Path to the file
     * @return List of strings representing all lines in the file
     */
    List<String> readAllLines(String filePath);
}
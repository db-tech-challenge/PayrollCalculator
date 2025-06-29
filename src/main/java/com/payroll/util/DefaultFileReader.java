package com.payroll.util;

import com.payroll.exception.DataLoadException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Default implementation of FileReader using Files.readAllLines.
 */
public class DefaultFileReader implements FileReader {
    @Override
    public List<String> readAllLines(String filePath) {
        try {
            return Files.readAllLines(Paths.get(filePath));
        } catch (FileNotFoundException e) {
            throw new DataLoadException("File not found: " + filePath, e);
        } catch (IOException e) {
            throw new DataLoadException("Error reading file: " + filePath, e);
        }
    }
}
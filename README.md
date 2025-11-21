# Test change by Aylin
# Payroll Calculator

A console application for calculating employee salaries based on CSV file data.

## Project Description

Payroll Calculator processes employee data, rates, calendar information, overtime hours, and tax classes to calculate salaries according to a specified formula. The calculation results are saved to a CSV file.

## Requirements

- Java 21 or higher
- Maven

## Project Structure

PayrollCalculator/
├── src/                           # Source code
├── data/                          # Input data and CSV files
│   ├── calendar_data.csv          # Calendar input for working days
│   ├── local_holidays.csv         # Location-specific holidays
│   ├── main_data.csv              # Main employee data
│   ├── overtime_data.csv          # Overtime records
│   ├── payments.csv               # Payment info
│   ├── rate.csv                   # Hourly or salary rates
│   ├── tax_class_data.csv         # Tax classification details
│   └── result/
│       └── main_data_result.csv   # Final calculation output
├── logs/
│   └── payroll-calculator.log     # Application execution logs
├── README.md                      # Project overview and usage instructions
├── REQUIREMENTS.md                # Prerequisites and setup instructions
└── pom.xml                        # Maven project configuration

## Building and Running the Application

Maven provides a simple way to build and run the application.

1. Compile:
```
mvn clean compile
```

2. Run:
```
mvn exec:java
```

## Input and Output

The program reads CSV files from the `data/` directory and outputs results to `data/result/main_data_result.csv`.

## Calculation Logic

Salary is calculated using the formula:

```
Salary = (Dworked / Dtotal) × Rmonthly × Tcoef + Σ(Hovertime × Rovertime × 1.5)
```

Where:
- **Dworked** - Number of days worked by the employee
- **Dtotal** - Total number of working days in the month
- **Rmonthly** - Employee's monthly rate
- **Tcoef** - Tax class coefficient
- **Hovertime** - Overtime hours (maximum 10 hours)
- **Rovertime** - Overtime hourly rate
- **1.5** - Overtime multiplier

## Detailed Requirements

For detailed information about:
- Input file formats
- Output file format
- Calculation logic
- Error handling
- Special cases

Please see the [detailed requirements document](REQUIREMENTS.md).

## Preparing the Environment

Before running the application:

1. Make sure the `data` directory exists and contains all required CSV files
2. Create the output directory:
   ```
   mkdir -p data/result
   ```

## Troubleshooting

If you encounter issues:

1. Check the log files in the `logs/` directory
2. Ensure all required CSV files are present in the `data/` directory
3. Verify that the CSV files have the correct format and headers
4. Make sure you have the correct Java version installed:
   ```
   java -version
   ```
5. Make sure Maven is installed on your system or that you're using the built-in Maven in IntelliJ IDEA:
   ```
   mvn -version
   ```
6. Check that output directory exists:
   ```
   mkdir -p data/result
   ```
# Payroll Calculator Requirements

This document outlines the requirements and expected behavior of the Payroll Calculator application.

## Application Purpose
The Payroll Calculator is designed to calculate employee salaries for the previous month (reporting period) based on various input data such as working days, rates, tax classes, and overtime. The application reads data from CSV files, performs calculations according to a specific formula, and outputs the results to a CSV file.

### Key Features:
- Salary calculation for the reporting period (previous month)
- Payment scheduling that avoids weekends and holidays
- Two-component salary structure: base salary + overtime compensation
- Detailed calculation formula provided in the Calculation Logic section

### Payment Rules:
- Salaries are paid for the previous month's work
- Payment cannot be made on weekends or holidays
- If the scheduled payment date falls on a non-working day, it is adjusted to the previous working day

## Input Files

The application requires the following input files in the `data/` directory:

### 1. main_data.csv

Contains basic information about employees.

**Format:**  
name;Location;Employee ID;Tax Class;AT Level;Status;Days Worked;Phone Number;Birthday;Password

**Example:**  
Henner Römer;Leipzig;69819545;4;AT2;ACTIVE;null;0173965019;1978-08-07;JXXN3qnZgg  
Svenja Geisler;Berlin;45151585;1;AT2;ACTIVE;null;0155565119;1973-09-06;16lhR5Qt8M

**Fields:**
- **Name**: Employee's full name
- **Location**: City or region where the employee is based
- **Employee ID**: Unique identifier for the employee
- **Tax Class**: Tax class category
- **AT Level**: Salary level/classification
- **Status**: Employment status (`ACTIVE`, `INACTIVE`)
- **Days Worked**: Number of days the employee worked in the period
- **Phone Number**: Contact number
- **Birthday**: Date of birth in format `YYYY-MM-DD`
- **Password**: Password string

### 2. rate.csv

Contains information about employee rates.

**Format:**  
EMPLOYEE_ID;RATE;OVERTIME_RATE

**Example:**  
69819545;540;44  
45151585;183;34

**Fields:**
- **EMPLOYEE_ID**: Unique employee identifier
- **RATE**: Monthly rate
- **OVERTIME_RATE**: Hourly rate for overtime

### 3. calendar_data.csv

Contains detailed calendar data for each day in the pay period.

**Format:**  
YEAR;MONTH;DAY;DAY_OF_WEEK;HOLIDAY

**Example:**  
2025;12;31;WED;N  
2025;12;30;TUE;N  
2025;12;29;MON;N

**Fields:**
- **YEAR**: Year
- **MONTH**: Month number
- **DAY**: Day of the month
- **DAY_OF_WEEK**: Day of the week (MON, TUE, WED, THU, FRI, SAT, SUN)
- **HOLIDAY**: Indicates whether the day is a holiday (`Y` or `N`)

### 4. overtime_data.csv

Contains information about employee overtime hours on specific dates.

**Format:**  
EMPLOYEE_ID;OVERTIME_DATA;DATE

**Example:**  
46771500;10;2025-06-20  
71123410;10;2025-06-09

**Fields:**
- **EMPLOYEE_ID**: Unique employee identifier
- **OVERTIME_DATA**: Number of overtime hours
- **DATE**: Date when the overtime occurred (`YYYY-MM-DD`)

### 5. tax_class_data.csv

Contains information about tax classes and their coefficients.

**Format:**  
TAX_CLASS;FACTOR

**Example:**  
4;0.42  
3;0.35  
2;0.25  
1;0.15

**Fields:**
- **TAX_CLASS**: Tax class identifier
- **FACTOR**: Tax coefficient (used for tax calculation)

### 6. payments.csv

Specifies the official payment date for a given month and year.

**Format:**  
MONTH;YEAR;PAYMENT_DATE

**Example:**  
1;2025;15

**Fields:**
- **MONTH**: Month number
- **YEAR**: Year
- **PAYMENT_DATE**: Day of the month when payment is made

### 7. local_holidays.csv

Contains location-specific holidays that may affect working days calculation.

**Format:**  
YEAR;MONTH;DAY;DAY_OF_WEEK;LOCATION

**Example:**  
2025;07;09;WED;Cologne

**Fields:**
- **YEAR**: Year
- **MONTH**: Month number
- **DAY**: Day of the month
- **DAY_OF_WEEK**: Day of the week
- **LOCATION**: City or location where the holiday applies

## Output File

The application generates the following output file in the `data/result/` directory:

### main_data_result.csv

Contains the calculated salary information.

**Format:**
```
EMPLOYEE_ID;PAY;DATA;SETTLEMENT_ACCOUNT;CURRENCY
```

**Example:**
```
123;178.80;1.15.2025;SMIT;EUR
124;277.50;1.15.2025;BROW;EUR
```

**Fields:**
- **EMPLOYEE_ID**: Unique employee identifier
- **PAY**: Calculated salary amount (in major currency units)
- **DATA**: Payment date (MONTH.PAYMENT_DATE.YEAR)
- **SETTLEMENT_ACCOUNT**: Account identifier (derived from employee's name)
- **CURRENCY**: Currency code (always EUR)

## Calculation Logic

### Salary Formula

The salary calculation uses the following formula:

```
Salary = (Dworked / Dtotal) × Rmonthly × Tcoef + Σ(Hovertime × Rovertime × 1.5)
```

Where:
- **Dworked** - Number of days worked by the employee (from main_data.csv)
- **Dtotal** - Total number of working days in the month (from calendar_data.csv)
- **Rmonthly** - Employee's monthly rate (from rate.csv)
- **Tcoef** - Tax coefficient based on tax class (from tax_class_data.csv)
- **Hovertime** - Overtime hours, limited to maximum 10 hours (from overtime_data.csv)
- **Rovertime** - Overtime hourly rate (from rate.csv)
- **1.5** - Fixed coefficient for overtime calculations

### Working Days Calculation

Working days are calculated by:
1. Counting all days in the target month from calendar_data.csv
2. Excluding weekends (Saturday and Sunday)
3. Excluding national holidays (HOLIDAY=Y in calendar_data.csv)
4. Excluding location-specific holidays (from local_holidays.csv matching employee's location)

### Settlement Account Generation

The settlement account is generated by taking the first 4 letters of the employee's full name in uppercase.

**Example:**
- "Henner Römer" → "HENN"
- "Svenja Geisler" → "SVEN"

### Special Cases

1. **Inactive Employees**: Employees with STATUS=INACTIVE are excluded from salary calculations
2. **Null Days Worked**: If Days Worked is null, the employee worked the full month (Dworked = Dtotal)
3. **Maximum Overtime**: Overtime hours are capped at 10 hours maximum per occurrence
4. **Missing Data**: If an employee is missing from any required input files, they may be excluded from calculations
5. **Location-Specific Holidays**: Holidays in local_holidays.csv only apply to employees in matching locations

## Data Quality Requirements

### File Structure
- All CSV files use semicolon (`;`) as delimiter
- Headers must match exactly as specified
- No extra whitespace in headers or data fields
- Consistent date format: YYYY-MM-DD

### Data Integrity
- Employee IDs must be consistent across all files
- Tax classes must exist in tax_class_data.csv
- Dates in overtime_data.csv must exist in calendar_data.csv
- Locations in local_holidays.csv should match locations in main_data.csv

### Validation Rules
- Employee ID: Must be numeric and unique
- Rates: Must be positive integers (in cents)
- Tax Class: Must be 1, 3, or 4
- Status: Must be ACTIVE or INACTIVE
- Days Worked: Must be null or positive integer
- Dates: Must be valid dates in YYYY-MM-DD format

## Error Handling

The application should handle the following error scenarios:

1. **Missing Files**: Log error and terminate if critical files are missing
2. **Malformed CSV**: Log specific parsing errors and skip invalid rows
3. **Data Inconsistencies**: Log warnings for missing employee data across files
4. **Invalid Data Types**: Log errors and use default values where possible
5. **Calculation Errors**: Log detailed error information and continue with other employees

## Logging

The application logs its operation at various levels:
- **INFO**: General operation information (file loading, processing summary)
- **WARN**: Issues that don't prevent operation but may indicate problems
- **ERROR**: Serious issues that affect calculation results
- **DEBUG**: Detailed information useful for troubleshooting

## Test Process

To verify that the application is functioning correctly:

1. **Data Preparation**: Place all required CSV files in the `data/` directory
2. **Execution**: Run the payroll calculator application
3. **Output Verification**: Check `data/result/main_data_result.csv` exists and contains expected data
4. **Manual Calculation**: Verify calculations for a sample of employees using the formula
5. **Log Review**: Check log files for any warnings or errors
6. **Edge Case Testing**: Test with inactive employees, null values, and maximum overtime scenarios

## Sample Calculation

For employee Henner Römer (ID: 69819545):
- Monthly Rate: 54.00 EUR
- Tax Class: 4 (Factor: 0.15)
- Days Worked: null (assume 0 or exclude)
- Overtime: Check overtime_data.csv for any entries
- Location: Leipzig (check for local holidays)

Expected output format:
```
69819545;[calculated_amount];7.9.2025;HENN;EUR
```
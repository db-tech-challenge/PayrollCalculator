package com.payroll.agents;

import com.payroll.model.Employee;
import com.payroll.model.Calendar;
import java.util.List;

/**
 * Robot #1: I check attendance and count working days!
 */
public interface AttendanceAgent {

    /**
     * Calculate how many days an employee should work in a month
     */
    int calculateWorkingDays(int year, int month, List<Calendar> calendar, String location);

    /**
     * Get actual days worked by an employee
     */
    int getActualDaysWorked(Employee employee);

    /**
     * Calculate attendance percentage
     */
    double calculateAttendancePercentage(Employee employee, int workingDays);
}
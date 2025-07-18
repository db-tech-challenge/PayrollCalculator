package com.payroll.agents.impl;

import com.payroll.agents.AttendanceAgent;
import com.payroll.model.Calendar;
import com.payroll.model.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.util.List;

/**
 * Robot #1 Implementation: I check attendance and count working days!
 */
public class AttendanceAgentImpl implements AttendanceAgent {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceAgentImpl.class);

    @Override
    public int calculateWorkingDays(int year, int month, List<Calendar> calendar, String location) {
        logger.info("🤖 Attendance Robot: Calculating working days for {}/{} in {}",
                month, year, location);

        int workingDays = 0;

        for (Calendar day : calendar) {
            // Check if it's the right month and year
            if (day.year() == year && day.month() == month) {
                // Check if it's a working day (not weekend, not holiday)
                if (day.isWorkingDay()) {
                    workingDays++;
                }
            }
        }

        logger.info("📊 Found {} working days in {}/{}", workingDays, month, year);
        return workingDays;
    }

    @Override
    public int getActualDaysWorked(Employee employee) {
        // If days worked is null, assume they worked the full month
        if (employee.getDaysWorked() == null) {
            logger.info("🤖 Employee {} has null days worked - assuming full month",
                    employee.getEmployeeId());
            return -1; // Special value meaning "full month"
        }

        logger.info("🤖 Employee {} worked {} days",
                employee.getEmployeeId(), employee.getDaysWorked());
        return employee.getDaysWorked();
    }

    @Override
    public double calculateAttendancePercentage(Employee employee, int workingDays) {
        int actualDays = getActualDaysWorked(employee);

        // If -1 (null days), they worked 100%
        if (actualDays == -1) {
            return 100.0;
        }

        if (workingDays == 0) {
            return 0.0;
        }

        double percentage = (actualDays * 100.0) / workingDays;
        logger.info("📊 Attendance percentage for {}: {:.2f}%",
                employee.getEmployeeId(), percentage);

        return percentage;
    }
}
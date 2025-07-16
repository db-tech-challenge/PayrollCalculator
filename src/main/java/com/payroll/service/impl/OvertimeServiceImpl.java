package com.payroll.service.impl;

import com.payroll.api.OvertimeService;
import com.payroll.model.Overtime;
import com.payroll.model.Rate;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OvertimeServiceImpl implements OvertimeService {

    private static final Logger logger = LoggerFactory.getLogger(OvertimeServiceImpl.class);
    private static final double OVERTIME_COEFFICIENT = 1.5;
    private static final int MAX_OVERTIME_HOURS = 10; // Maximum allowed overtime hours

    public Map<String, Map<String, Integer>> aggregateOvertimesByMonth(List<Overtime> overtimes) {
        Map<String, Map<String, Integer>> result = new HashMap<>();

        for (Overtime overtime : overtimes) {
            String employeeId = overtime.employeeId();
            LocalDate date = overtime.date();
            int hours = overtime.overtimeHours();

            if (employeeId == null || date == null) {
                continue;
            }

            String periodKey = date.getYear() + "-" + date.getMonthValue();
            Map<String, Integer> employeeOvertimes =
                result.computeIfAbsent(employeeId, k -> new HashMap<>());
            employeeOvertimes.put(periodKey, employeeOvertimes.getOrDefault(periodKey, 0) + hours);
        }

        return result;
    }

    public int getOvertimeHours(
        Map<String, Map<String, Integer>> overtimeMap,
        String employeeId,
        String periodKey) {

        Map<String, Integer> employeeOvertimes = overtimeMap.get(employeeId);
        if (employeeOvertimes == null) {
            return 0;
        }

        return employeeOvertimes.getOrDefault(periodKey, 0);
    }

    public double calculateOvertimePay(Rate rate, int overtimeHours) {
        if (overtimeHours <= 0) {
            return 0;
        }

        int cappedHours = Math.min(overtimeHours, MAX_OVERTIME_HOURS);
        if (cappedHours != overtimeHours) {
            logger.warn("Overtime hours for employee {} limited from {} to {}",
                rate.employeeId(), overtimeHours, cappedHours);
        }

        return cappedHours * rate.overtimeRate() * OVERTIME_COEFFICIENT;
    }

}

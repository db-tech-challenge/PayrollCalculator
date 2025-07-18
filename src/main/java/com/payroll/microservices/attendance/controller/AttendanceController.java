package com.payroll.microservices.attendance.controller;

import com.payroll.agents.impl.AttendanceAgentImpl;
import com.payroll.model.Calendar;
import com.payroll.model.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceController.class);
    private final AttendanceAgentImpl attendanceAgent = new AttendanceAgentImpl();

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> status = new HashMap<>();
        status.put("service", "Attendance Robot");
        status.put("status", "UP");
        status.put("port", "8081");
        status.put("message", "🤖 Attendance Robot is ready to count days!");
        return status;
    }

    @PostMapping("/working-days")
    public Map<String, Object> calculateWorkingDays(@RequestBody Map<String, Object> request) {
        logger.info("🤖 Attendance Service: Received request to calculate working days");

        int year = (int) request.get("year");
        int month = (int) request.get("month");
        String location = (String) request.get("location");
        List<Calendar> calendar = (List<Calendar>) request.get("calendar");

        int workingDays = attendanceAgent.calculateWorkingDays(year, month, calendar, location);

        Map<String, Object> response = new HashMap<>();
        response.put("workingDays", workingDays);
        response.put("year", year);
        response.put("month", month);
        response.put("location", location);

        logger.info("✅ Calculated {} working days for {}/{}", workingDays, month, year);
        return response;
    }

    @PostMapping("/attendance-percentage")
    public Map<String, Object> calculateAttendance(@RequestBody Map<String, Object> request) {
        logger.info("🤖 Attendance Service: Calculating attendance percentage");

        // Create employee from request data
        Employee employee = new Employee();
        Map<String, Object> empData = (Map<String, Object>) request.get("employee");
        employee.setEmployeeId((String) empData.get("employeeId"));
        employee.setDaysWorked((Integer) empData.get("daysWorked"));

        int workingDays = (int) request.get("workingDays");

        int actualDaysWorked = attendanceAgent.getActualDaysWorked(employee);
        double attendancePercentage = attendanceAgent.calculateAttendancePercentage(
                employee, workingDays
        );

        Map<String, Object> response = new HashMap<>();
        response.put("employeeId", employee.getEmployeeId());
        response.put("actualDaysWorked", actualDaysWorked);
        response.put("attendancePercentage", attendancePercentage);
        response.put("workingDays", workingDays);

        return response;
    }
}
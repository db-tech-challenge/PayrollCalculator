package com.payroll.microservices.attendance;

import com.payroll.agents.impl.AttendanceAgentImpl;
import com.payroll.model.Calendar;
import com.payroll.model.Employee;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@SpringBootApplication
@RestController
public class AttendanceMicroservice {

    private final AttendanceAgentImpl attendanceRobot = new AttendanceAgentImpl();

    public static void main(String[] args) {
        // This makes it run on port 8081
        System.setProperty("server.port", "8081");

        System.out.println("""
            
            ╔════════════════════════════════════════╗
            ║    🤖 ATTENDANCE ROBOT SERVICE 🤖     ║
            ║         Starting on port 8081...       ║
            ╚════════════════════════════════════════╝
            """);

        SpringApplication.run(AttendanceMicroservice.class, args);
    }

    // Health check endpoint - visit http://localhost:8081/health
    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Attendance Robot");
        response.put("message", "🤖 I'm ready to count working days!");
        response.put("port", "8081");
        return response;
    }

    // Test endpoint - visit http://localhost:8081/test
    @GetMapping("/test")
    public Map<String, Object> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Attendance Robot is working!");
        response.put("example", "I can calculate working days for any month");
        response.put("timestamp", new Date());
        return response;
    }

    // Calculate working days for a specific employee
    @PostMapping("/calculate-days")
    public Map<String, Object> calculateWorkingDays(@RequestBody Map<String, Object> request) {
        System.out.println("🤖 Received request: " + request);

        // Extract data from request
        String employeeId = (String) request.get("employeeId");
        Integer year = (Integer) request.get("year");
        Integer month = (Integer) request.get("month");

        // For now, simulate the calculation (we'll add real calendar data later)
        int workingDays = 22; // Typical month has ~22 working days

        // Create response
        Map<String, Object> response = new HashMap<>();
        response.put("employeeId", employeeId);
        response.put("year", year);
        response.put("month", month);
        response.put("workingDays", workingDays);
        response.put("message", "Calculated by Attendance Robot!");

        System.out.println("✅ Sending response: " + response);
        return response;
    }
}
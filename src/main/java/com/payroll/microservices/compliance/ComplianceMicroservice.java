package com.payroll.microservices.compliance;

import com.payroll.agents.impl.ComplianceAgentImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@SpringBootApplication
@RestController
public class ComplianceMicroservice {

    private final ComplianceAgentImpl complianceRobot = new ComplianceAgentImpl();

    public static void main(String[] args) {
        System.setProperty("server.port", "8084");
        System.out.println("""
            
            ╔════════════════════════════════════════╗
            ║    📋 COMPLIANCE ROBOT SERVICE 📋      ║
            ║         Starting on port 8084...       ║
            ╚════════════════════════════════════════╝
            """);
        SpringApplication.run(ComplianceMicroservice.class, args);
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Compliance Robot");
        response.put("message", "📋 Ready to validate compliance!");
        response.put("port", "8084");
        return response;
    }

    @PostMapping("/validate")
    public Map<String, Object> validatePayroll(@RequestBody Map<String, Object> request) {
        System.out.println("📋 Compliance Robot received: " + request);

        String employeeId = (String) request.get("employeeId");
        Double netSalary = ((Number) request.get("netSalary")).doubleValue();
        String status = (String) request.get("employeeStatus");

        boolean isValid = true;
        List<String> issues = new ArrayList<>();

        // Check minimum wage
        if (netSalary < 1000) {
            issues.add("Salary below minimum wage");
            isValid = false;
        }

        // Check employee status
        if (!"ACTIVE".equals(status)) {
            issues.add("Employee is not active");
            isValid = false;
        }

        // Check maximum salary (just as an example)
        if (netSalary > 100000) {
            issues.add("Unusually high salary - needs review");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("employeeId", employeeId);
        response.put("isCompliant", isValid);
        response.put("issues", issues);
        response.put("message", "Validated by Compliance Robot!");

        System.out.println("✅ Compliance check: " + (isValid ? "PASSED" : "FAILED"));
        return response;
    }
}
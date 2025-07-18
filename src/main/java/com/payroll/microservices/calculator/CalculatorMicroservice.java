package com.payroll.microservices.calculator;

import com.payroll.agents.impl.SalaryCalculatorAgentImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@SpringBootApplication
@RestController
public class CalculatorMicroservice {

    private final SalaryCalculatorAgentImpl calculatorRobot = new SalaryCalculatorAgentImpl();

    public static void main(String[] args) {
        System.setProperty("server.port", "8083");
        System.out.println("""
            
            ╔════════════════════════════════════════╗
            ║    💰 CALCULATOR ROBOT SERVICE 💰      ║
            ║         Starting on port 8083...       ║
            ╚════════════════════════════════════════╝
            """);
        SpringApplication.run(CalculatorMicroservice.class, args);
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Calculator Robot");
        response.put("message", "💰 Ready to calculate salaries!");
        response.put("port", "8083");
        return response;
    }

    @PostMapping("/calculate-salary")
    public Map<String, Object> calculateSalary(@RequestBody Map<String, Object> request) {
        System.out.println("💰 Calculator Robot received: " + request);

        String employeeId = (String) request.get("employeeId");
        Double monthlyRate = ((Number) request.get("monthlyRate")).doubleValue();
        Integer daysWorked = (Integer) request.get("daysWorked");
        Integer totalWorkingDays = (Integer) request.get("totalWorkingDays");
        Double performanceBonus = ((Number) request.get("performanceBonus")).doubleValue();
        Double taxRate = ((Number) request.get("taxRate")).doubleValue();

        // Calculate base salary
        double baseSalary = (daysWorked.doubleValue() / totalWorkingDays) * monthlyRate;

        // Add bonus
        double grossSalary = baseSalary + performanceBonus;

        // Apply tax
        double taxAmount = grossSalary * taxRate;
        double netSalary = grossSalary - taxAmount;

        Map<String, Object> response = new HashMap<>();
        response.put("employeeId", employeeId);
        response.put("baseSalary", baseSalary);
        response.put("grossSalary", grossSalary);
        response.put("taxAmount", taxAmount);
        response.put("netSalary", netSalary);
        response.put("message", "Salary calculated by Calculator Robot!");

        System.out.println("✅ Net salary calculated: " + netSalary);
        return response;
    }
}
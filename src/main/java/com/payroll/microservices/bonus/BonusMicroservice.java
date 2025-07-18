package com.payroll.microservices.bonus;

import com.payroll.agents.impl.BonusAgentImpl;
import com.payroll.model.Overtime;
import com.payroll.model.Rate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@SpringBootApplication
@RestController
public class BonusMicroservice {

    private final BonusAgentImpl bonusRobot = new BonusAgentImpl();

    public static void main(String[] args) {
        System.setProperty("server.port", "8082");
        System.out.println("""
            
            ╔════════════════════════════════════════╗
            ║      🎁 BONUS ROBOT SERVICE 🎁        ║
            ║         Starting on port 8082...       ║
            ╚════════════════════════════════════════╝
            """);
        SpringApplication.run(BonusMicroservice.class, args);
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Bonus Robot");
        response.put("message", "🎁 Ready to calculate bonuses!");
        response.put("port", "8082");
        return response;
    }

    @PostMapping("/calculate-bonus")
    public Map<String, Object> calculateBonus(@RequestBody Map<String, Object> request) {
        System.out.println("🎁 Bonus Robot received: " + request);

        String employeeId = (String) request.get("employeeId");
        Double attendancePercentage = ((Number) request.get("attendancePercentage")).doubleValue();
        Double monthlyRate = ((Number) request.get("monthlyRate")).doubleValue();

        // Simple bonus calculation
        double performanceBonus = 0;
        if (attendancePercentage >= 100) {
            performanceBonus = monthlyRate * 0.05; // 5% bonus for perfect attendance
        } else if (attendancePercentage >= 95) {
            performanceBonus = monthlyRate * 0.02; // 2% bonus for good attendance
        }

        Map<String, Object> response = new HashMap<>();
        response.put("employeeId", employeeId);
        response.put("performanceBonus", performanceBonus);
        response.put("attendancePercentage", attendancePercentage);
        response.put("message", "Bonus calculated by Bonus Robot!");

        System.out.println("✅ Bonus calculated: " + performanceBonus);
        return response;
    }
}
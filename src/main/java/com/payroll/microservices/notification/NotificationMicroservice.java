package com.payroll.microservices.notification;

import com.payroll.agents.impl.NotificationAgentImpl;
import com.payroll.model.PaymentResult;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@SpringBootApplication
@RestController
public class NotificationMicroservice {

    private final NotificationAgentImpl notificationRobot = new NotificationAgentImpl();

    public static void main(String[] args) {
        System.setProperty("server.port", "8085");
        System.out.println("""
            
            ╔════════════════════════════════════════╗
            ║   📧 NOTIFICATION ROBOT SERVICE 📧     ║
            ║         Starting on port 8085...       ║
            ╚════════════════════════════════════════╝
            """);
        SpringApplication.run(NotificationMicroservice.class, args);
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Notification Robot");
        response.put("message", "📧 Ready to send notifications!");
        response.put("port", "8085");
        return response;
    }

    @PostMapping("/send-notification")
    public Map<String, Object> sendNotification(@RequestBody Map<String, Object> request) {
        System.out.println("📧 Notification Robot received: " + request);

        String employeeId = (String) request.get("employeeId");
        String employeeName = (String) request.get("employeeName");
        Double netSalary = ((Number) request.get("netSalary")).doubleValue();
        String paymentDate = (String) request.get("paymentDate");

        // Generate settlement account (first 4 letters)
        String settlementAccount = employeeName.length() >= 4 ?
                employeeName.substring(0, 4).toUpperCase() : "XXXX";

        // Create notification message
        String message = String.format("""
            Dear %s,
            
            Your salary has been processed!
            Amount: €%.2f
            Payment Date: %s
            Settlement Account: %s
            
            Thank you for your hard work!
            """, employeeName, netSalary, paymentDate, settlementAccount);

        Map<String, Object> response = new HashMap<>();
        response.put("employeeId", employeeId);
        response.put("notificationSent", true);
        response.put("settlementAccount", settlementAccount);
        response.put("message", message);

        System.out.println("✅ Notification sent!");
        System.out.println(message);

        return response;
    }
}
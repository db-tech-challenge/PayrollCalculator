package com.payroll.orchestrator;  // Different package!

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.*;

@SpringBootApplication
@RestController
public class PayrollOrchestrator {

    private final RestTemplate restTemplate = new RestTemplate();

    // Service URLs
    private static final String ATTENDANCE_SERVICE = "http://localhost:8081";
    private static final String BONUS_SERVICE = "http://localhost:8082";
    private static final String CALCULATOR_SERVICE = "http://localhost:8083";
    private static final String COMPLIANCE_SERVICE = "http://localhost:8084";
    private static final String NOTIFICATION_SERVICE = "http://localhost:8085";

    public static void main(String[] args) {
        System.setProperty("server.port", "8080");
        System.out.println("""
            
            ╔════════════════════════════════════════╗
            ║    🎭 PAYROLL ORCHESTRATOR 🎭          ║
            ║         Starting on port 8080...       ║
            ╚════════════════════════════════════════╝
            
            Make sure all 5 robot services are running:
            - Attendance Robot: http://localhost:8081
            - Bonus Robot: http://localhost:8082
            - Calculator Robot: http://localhost:8083
            - Compliance Robot: http://localhost:8084
            - Notification Robot: http://localhost:8085
            """);
        SpringApplication.run(PayrollOrchestrator.class, args);
    }

    @GetMapping("/")
    public String home() {
        return """
            <html>
            <head>
                <title>Payroll Orchestrator</title>
                <style>
                    body { font-family: Arial; padding: 20px; background: #f0f0f0; }
                    .container { max-width: 800px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }
                    h1 { color: #333; }
                    .endpoint { background: #e8f4f8; padding: 10px; margin: 10px 0; border-radius: 5px; }
                    .button { background: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; display: inline-block; margin: 5px; }
                    .button:hover { background: #45a049; }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>🎭 Payroll Microservice Orchestrator</h1>
                    <p>This orchestrator coordinates all 5 robot services to process payroll.</p>
                    
                    <h2>Available Endpoints:</h2>
                    
                    <div class="endpoint">
                        <strong>GET /health-check</strong><br>
                        Check if all robot services are running
                        <br><a href="/health-check" class="button">Check Health</a>
                    </div>
                    
                    <div class="endpoint">
                        <strong>POST /process-payroll</strong><br>
                        Process payroll for an employee by calling all robots in sequence
                    </div>
                    
                    <h2>Test with Postman:</h2>
                    <pre style="background: #f5f5f5; padding: 15px; border-radius: 5px;">
POST http://localhost:8080/process-payroll
Content-Type: application/json

{
    "employeeId": "65882437",
    "employeeName": "Magret Kramer",
    "employeeStatus": "ACTIVE",
    "daysWorked": 21,
    "monthlyRate": 5590,
    "taxRate": 0.2
}
                    </pre>
                </div>
            </body>
            </html>
            """;
    }

    @GetMapping("/health-check")
    public Map<String, Object> checkAllServices() {
        Map<String, Object> healthStatus = new LinkedHashMap<>();

        healthStatus.put("orchestrator", Map.of("status", "UP", "port", 8080));

        // Check each service
        checkAndAddService(healthStatus, "attendance", ATTENDANCE_SERVICE + "/health");
        checkAndAddService(healthStatus, "bonus", BONUS_SERVICE + "/health");
        checkAndAddService(healthStatus, "calculator", CALCULATOR_SERVICE + "/health");
        checkAndAddService(healthStatus, "compliance", COMPLIANCE_SERVICE + "/health");
        checkAndAddService(healthStatus, "notification", NOTIFICATION_SERVICE + "/health");

        // Count how many are up
        long servicesUp = healthStatus.values().stream()
                .filter(status -> status instanceof Map && "UP".equals(((Map) status).get("status")))
                .count();

        healthStatus.put("summary", Map.of(
                "total", 6,
                "up", servicesUp,
                "down", 6 - servicesUp,
                "overall", servicesUp == 6 ? "ALL SERVICES UP 🟢" : "SOME SERVICES DOWN 🔴"
        ));

        return healthStatus;
    }

    private void checkAndAddService(Map<String, Object> healthStatus, String name, String url) {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            healthStatus.put(name, response.getBody());
        } catch (Exception e) {
            healthStatus.put(name, Map.of("status", "DOWN", "error", "Cannot connect"));
        }
    }

    @PostMapping("/process-payroll")
    public Map<String, Object> processPayroll(@RequestBody Map<String, Object> request) {
        System.out.println("\n🎭 ORCHESTRATOR: Starting payroll processing...");
        System.out.println("Request: " + request);

        Map<String, Object> response = new LinkedHashMap<>();
        List<String> processLog = new ArrayList<>();

        try {
            // Extract employee data with safe type conversion
            String employeeId = String.valueOf(request.get("employeeId"));
            String employeeName = String.valueOf(request.get("employeeName"));
            String employeeStatus = String.valueOf(request.getOrDefault("employeeStatus", "ACTIVE"));

            // Safe number conversions
            Number daysWorkedNum = (Number) request.getOrDefault("daysWorked", 22);
            Integer daysWorked = daysWorkedNum.intValue();

            Number monthlyRateNum = (Number) request.getOrDefault("monthlyRate", 5000);
            Double monthlyRate = monthlyRateNum.doubleValue();

            Number taxRateNum = (Number) request.getOrDefault("taxRate", 0.2);
            Double taxRate = taxRateNum.doubleValue();

            response.put("employeeId", employeeId);
            response.put("employeeName", employeeName);

            // Step 1: Call Attendance Robot
            processLog.add("🤖 Step 1: Calling Attendance Robot...");
            Map<String, Object> attendanceRequest = Map.of(
                    "employeeId", employeeId,
                    "year", 2025,
                    "month", 6
            );

            try {
                Map<String, Object> attendanceResponse = callService(
                        ATTENDANCE_SERVICE + "/calculate-days", attendanceRequest);

                Integer workingDays = (Integer) attendanceResponse.get("workingDays");
                double attendancePercentage = (daysWorked * 100.0) / workingDays;

                processLog.add("✅ Attendance calculated: " + daysWorked + "/" + workingDays +
                        " days (" + String.format("%.1f%%", attendancePercentage) + ")");

                // Step 2: Call Bonus Robot
                processLog.add("🤖 Step 2: Calling Bonus Robot...");
                Map<String, Object> bonusRequest = Map.of(
                        "employeeId", employeeId,
                        "attendancePercentage", attendancePercentage,
                        "monthlyRate", monthlyRate
                );

                Map<String, Object> bonusResponse = callService(
                        BONUS_SERVICE + "/calculate-bonus", bonusRequest);

                Double performanceBonus = (Double) bonusResponse.get("performanceBonus");
                processLog.add("✅ Bonus calculated: €" + String.format("%.2f", performanceBonus));

                // Step 3: Call Calculator Robot
                processLog.add("🤖 Step 3: Calling Calculator Robot...");
                Map<String, Object> calculatorRequest = Map.of(
                        "employeeId", employeeId,
                        "monthlyRate", monthlyRate,
                        "daysWorked", daysWorked,
                        "totalWorkingDays", workingDays,
                        "performanceBonus", performanceBonus,
                        "taxRate", taxRate
                );

                Map<String, Object> calculatorResponse = callService(
                        CALCULATOR_SERVICE + "/calculate-salary", calculatorRequest);

                Double netSalary = (Double) calculatorResponse.get("netSalary");
                processLog.add("✅ Net salary calculated: €" + String.format("%.2f", netSalary));

                // Step 4: Call Compliance Robot
                processLog.add("🤖 Step 4: Calling Compliance Robot...");
                Map<String, Object> complianceRequest = Map.of(
                        "employeeId", employeeId,
                        "netSalary", netSalary,
                        "employeeStatus", employeeStatus
                );

                Map<String, Object> complianceResponse = callService(
                        COMPLIANCE_SERVICE + "/validate", complianceRequest);

                Boolean isCompliant = (Boolean) complianceResponse.get("isCompliant");
                processLog.add(isCompliant ? "✅ Compliance check: PASSED" : "❌ Compliance check: FAILED");

                // Step 5: Call Notification Robot (only if compliant)
                if (isCompliant) {
                    processLog.add("🤖 Step 5: Calling Notification Robot...");
                    Map<String, Object> notificationRequest = Map.of(
                            "employeeId", employeeId,
                            "employeeName", employeeName,
                            "netSalary", netSalary,
                            "paymentDate", "2025-07-09"
                    );

                    Map<String, Object> notificationResponse = callService(
                            NOTIFICATION_SERVICE + "/send-notification", notificationRequest);

                    processLog.add("✅ Notification sent!");
                    response.put("notificationMessage", notificationResponse.get("message"));
                }

                // Success response
                response.put("success", true);
                response.put("processLog", processLog);
                response.put("finalNetSalary", netSalary);
                response.put("isCompliant", isCompliant);

            } catch (Exception e) {
                throw new RuntimeException("Error during service calls: " + e.getMessage(), e);
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            processLog.add("❌ Error: " + e.getMessage());
            response.put("processLog", processLog);
            e.printStackTrace();
        }

        System.out.println("Response: " + response);
        return response;
    }

    private Map<String, Object> callService(String url, Map<String, Object> request) {
        System.out.println("Calling: " + url);
        System.out.println("With request: " + request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, Map.class);

            System.out.println("Response: " + response.getBody());
            return response.getBody();

        } catch (Exception e) {
            System.err.println("Error calling service: " + e.getMessage());
            throw new RuntimeException("Failed to call " + url + ": " + e.getMessage());
        }
    }
}
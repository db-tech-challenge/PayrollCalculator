package com.payroll.microservices.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Client to communicate with robot microservices
 */
public class RobotServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(RobotServiceClient.class);
    private final RestTemplate restTemplate = new RestTemplate();

    // Service URLs
    private static final String ATTENDANCE_SERVICE = "http://localhost:8081/attendance";
    private static final String BONUS_SERVICE = "http://localhost:8082/bonus";
    private static final String CALCULATOR_SERVICE = "http://localhost:8083/calculator";
    private static final String COMPLIANCE_SERVICE = "http://localhost:8084/compliance";
    private static final String NOTIFICATION_SERVICE = "http://localhost:8085/notification";

    public Map<String, Object> callAttendanceService(Map<String, Object> request, String endpoint) {
        return callService(ATTENDANCE_SERVICE + endpoint, request);
    }

    public Map<String, Object> callBonusService(Map<String, Object> request) {
        return callService(BONUS_SERVICE + "/calculate", request);
    }

    public Map<String, Object> callCalculatorService(Map<String, Object> request) {
        return callService(CALCULATOR_SERVICE + "/calculate", request);
    }

    public Map<String, Object> callComplianceService(Map<String, Object> request) {
        return callService(COMPLIANCE_SERVICE + "/validate", request);
    }

    public Map<String, Object> callNotificationService(Map<String, Object> request) {
        return callService(NOTIFICATION_SERVICE + "/send", request);
    }

    private Map<String, Object> callService(String url, Map<String, Object> request) {
        try {
            logger.info("📡 Calling service: {}", url);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, Map.class
            );

            logger.info("✅ Service responded successfully");
            return response.getBody();

        } catch (Exception e) {
            logger.error("❌ Error calling service {}: {}", url, e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return errorResponse;
        }
    }

    public boolean checkServiceHealth(String serviceName) {
        String healthUrl = "";
        switch (serviceName) {
            case "ATTENDANCE": healthUrl = ATTENDANCE_SERVICE + "/health"; break;
            case "BONUS": healthUrl = BONUS_SERVICE + "/health"; break;
            case "CALCULATOR": healthUrl = CALCULATOR_SERVICE + "/health"; break;
            case "COMPLIANCE": healthUrl = COMPLIANCE_SERVICE + "/health"; break;
            case "NOTIFICATION": healthUrl = NOTIFICATION_SERVICE + "/health"; break;
        }

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(healthUrl, Map.class);
            Map<String, String> health = response.getBody();
            logger.info("🟢 {} service is {}", serviceName, health.get("status"));
            return "UP".equals(health.get("status"));
        } catch (Exception e) {
            logger.error("🔴 {} service is DOWN: {}", serviceName, e.getMessage());
            return false;
        }
    }
}
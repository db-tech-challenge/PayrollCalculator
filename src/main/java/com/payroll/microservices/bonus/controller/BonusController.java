package com.payroll.microservices.bonus.controller;

import com.payroll.agents.impl.BonusAgentImpl;
import com.payroll.model.Overtime;
import com.payroll.model.Rate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bonus")
public class BonusController {

    private static final Logger logger = LoggerFactory.getLogger(BonusController.class);
    private final BonusAgentImpl bonusAgent = new BonusAgentImpl();

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> status = new HashMap<>();
        status.put("service", "Bonus Robot");
        status.put("status", "UP");
        status.put("port", "8082");
        status.put("message", "🤖 Bonus Robot is ready to calculate rewards!");
        return status;
    }

    @PostMapping("/calculate")
    public Map<String, Object> calculateBonus(@RequestBody Map<String, Object> request) {
        logger.info("🤖 Bonus Service: Calculating bonus for employee");

        String employeeId = (String) request.get("employeeId");
        double attendancePercentage = (double) request.get("attendancePercentage");
        int year = (int) request.get("year");
        int month = (int) request.get("month");

        // Parse rate data
        Map<String, Object> rateData = (Map<String, Object>) request.get("rate");
        Rate rate = new Rate(
                employeeId,
                (double) rateData.get("rate"),
                (double) rateData.get("overtimeRate")
        );

        // Parse overtime data
        List<Map<String, Object>> overtimeData =
                (List<Map<String, Object>>) request.get("overtimes");
        List<Overtime> overtimes = new ArrayList<>();

        if (overtimeData != null) {
            for (Map<String, Object> ot : overtimeData) {
                overtimes.add(new Overtime(
                        (String) ot.get("employeeId"),
                        (int) ot.get("hours"),
                        LocalDate.parse((String) ot.get("date"))
                ));
            }
        }

        double totalBonus = bonusAgent.getTotalBonus(
                employeeId, attendancePercentage, overtimes, rate, year, month
        );

        double overtimePay = bonusAgent.calculateOvertimePay(
                employeeId, overtimes, rate, year, month
        );

        double performanceBonus = bonusAgent.calculatePerformanceBonus(
                attendancePercentage, rate.rate()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("employeeId", employeeId);
        response.put("totalBonus", totalBonus);
        response.put("overtimePay", overtimePay);
        response.put("performanceBonus", performanceBonus);
        response.put("attendancePercentage", attendancePercentage);

        logger.info("✅ Total bonus calculated: {}", totalBonus);
        return response;
    }
}
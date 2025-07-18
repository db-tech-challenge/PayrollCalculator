package com.payroll.agents;

import com.payroll.agents.impl.*;
import com.payroll.model.*;
import com.payroll.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The Orchestrator - Coordinates all robots to process payroll!
 */
public class RobotOrchestrator {

    private static final Logger logger = LoggerFactory.getLogger(RobotOrchestrator.class);

    // Our team of robots!
    private final AttendanceAgent attendanceRobot;
    private final BonusAgent bonusRobot;
    private final SalaryCalculatorAgent calculatorRobot;
    private final ComplianceAgent complianceRobot;
    private final NotificationAgent notificationRobot;

    // File service for loading data
    private final FileService fileService;

    public RobotOrchestrator(FileService fileService) {
        this.fileService = fileService;

        // Initialize our robot team!
        this.attendanceRobot = new AttendanceAgentImpl();
        this.bonusRobot = new BonusAgentImpl();
        this.calculatorRobot = new SalaryCalculatorAgentImpl();
        this.complianceRobot = new ComplianceAgentImpl();
        this.notificationRobot = new NotificationAgentImpl();

        logger.info("🤖 Robot Orchestra initialized! All 5 robots ready to work!");
    }

    /**
     * Process payroll using our robot team!
     */
    public List<PaymentResult> processPayrollWithRobots() {
        logger.info("🎭 ORCHESTRATOR: Starting robot-powered payroll processing!");

        List<PaymentResult> results = new ArrayList<>();

        try {
            // Load all data
            logger.info("📁 Loading data files...");
            List<Employee> employees = fileService.loadEmployees();
            List<Rate> rates = fileService.loadRates();
            List<Calendar> calendar = fileService.loadCalendar();
            List<Overtime> overtimes = fileService.loadOvertimes();
            List<TaxClass> taxClasses = fileService.loadTaxClasses();
            List<Payment> payments = fileService.loadPayments();

            // Create lookup maps for efficiency
            Map<String, Rate> rateMap = rates.stream()
                    .collect(Collectors.toMap(Rate::employeeId, r -> r));
            Map<String, TaxClass> taxClassMap = taxClasses.stream()
                    .collect(Collectors.toMap(TaxClass::taxClass, t -> t));

            // Process each payment period
            for (Payment payment : payments) {
                logger.info("\n🗓️ Processing payment for period: {}",
                        payment.getPaymentPeriodKey());

                LocalDate paymentDate = LocalDate.of(
                        payment.year(),
                        payment.month(),
                        payment.paymentDate()
                );

                // Process each employee
                for (Employee employee : employees) {
                    logger.info("\n👤 Processing employee: {} ({})",
                            employee.getFullName(), employee.getEmployeeId());

                    // Create a message to pass between robots
                    PayrollMessage message = new PayrollMessage(
                            employee.getEmployeeId(),
                            "ORCHESTRATOR"
                    );

                    try {
                        // Process through each robot
                        boolean success = processEmployeeThroughRobots(
                                employee, payment, paymentDate,
                                rateMap, taxClassMap,
                                calendar, overtimes,
                                message, results
                        );

                        if (success) {
                            logger.info("✅ Successfully processed employee {}",
                                    employee.getEmployeeId());
                        } else {
                            logger.warn("⚠️ Skipped employee {} due to validation",
                                    employee.getEmployeeId());
                        }

                    } catch (Exception e) {
                        logger.error("❌ Error processing employee {}: {}",
                                employee.getEmployeeId(), e.getMessage());
                    }
                }
            }

            logger.info("\n🎉 ORCHESTRATOR: Payroll processing complete! " +
                    "Processed {} payments", results.size());

        } catch (Exception e) {
            logger.error("❌ ORCHESTRATOR ERROR: {}", e.getMessage(), e);
        }

        return results;
    }

    /**
     * Process a single employee through all robots
     */
    private boolean processEmployeeThroughRobots(
            Employee employee, Payment payment, LocalDate paymentDate,
            Map<String, Rate> rateMap, Map<String, TaxClass> taxClassMap,
            List<Calendar> calendar, List<Overtime> overtimes,
            PayrollMessage message, List<PaymentResult> results) {

        // ROBOT #1: ATTENDANCE
        logger.info("🤖 Sending to Robot #1 (Attendance)...");
        message.setToAgent("ATTENDANCE");

        int workingDays = attendanceRobot.calculateWorkingDays(
                payment.getCalculationYear(),
                payment.getCalculationMonth(),
                calendar,
                employee.getLocation()
        );

        int actualDaysWorked = attendanceRobot.getActualDaysWorked(employee);
        double attendancePercentage = attendanceRobot.calculateAttendancePercentage(
                employee, workingDays
        );

        message.addData("workingDays", workingDays)
                .addData("actualDaysWorked", actualDaysWorked)
                .addData("attendancePercentage", attendancePercentage);

        // ROBOT #2: BONUS
        logger.info("🤖 Sending to Robot #2 (Bonus)...");
        message.setToAgent("BONUS");

        Rate rate = rateMap.get(employee.getEmployeeId());
        if (rate == null) {
            logger.warn("No rate found for employee {}", employee.getEmployeeId());
            return false;
        }

        double totalBonus = bonusRobot.getTotalBonus(
                employee.getEmployeeId(),
                attendancePercentage,
                overtimes,
                rate,
                payment.getCalculationYear(),
                payment.getCalculationMonth()
        );

        message.addData("totalBonus", totalBonus)
                .addData("rate", rate);

        // ROBOT #3: SALARY CALCULATOR
        logger.info("🤖 Sending to Robot #3 (Calculator)...");
        message.setToAgent("CALCULATOR");

        TaxClass taxClass = taxClassMap.get(employee.getTaxClass());
        if (taxClass == null) {
            logger.warn("No tax class found for employee {}", employee.getEmployeeId());
            return false;
        }

        double baseSalary = calculatorRobot.calculateBaseSalary(
                employee, rate, actualDaysWorked, workingDays
        );

        double netSalary = calculatorRobot.calculateNetSalary(
                baseSalary, totalBonus, taxClass
        );

        message.addData("baseSalary", baseSalary)
                .addData("netSalary", netSalary)
                .addData("taxClass", taxClass);

        // ROBOT #4: COMPLIANCE
        logger.info("🤖 Sending to Robot #4 (Compliance)...");
        message.setToAgent("COMPLIANCE");

        boolean salaryValid = complianceRobot.validateSalaryAmount(netSalary);
        boolean dateValid = complianceRobot.validatePaymentDate(payment, paymentDate);
        boolean employeeValid = complianceRobot.validateEmployeeEligibility(
                employee.getStatus()
        );

        if (!salaryValid || !dateValid || !employeeValid) {
            logger.warn("Compliance check failed for employee {}",
                    employee.getEmployeeId());
            return false;
        }

        String complianceReport = complianceRobot.getComplianceReport(
                employee.getEmployeeId(), netSalary, paymentDate
        );
        logger.debug(complianceReport);

        // ROBOT #5: NOTIFICATION
        logger.info("🤖 Sending to Robot #5 (Notification)...");
        message.setToAgent("NOTIFICATION");

        String settlementAccount = notificationRobot.generateSettlementAccount(employee);
        PaymentResult result = notificationRobot.createPaymentResult(
                employee, netSalary, payment.toString(), settlementAccount
        );

        notificationRobot.sendPaymentNotification(employee, result);

        // Add to results
        results.add(result);

        logger.info("🎯 Message journey complete! Final data: {}", message);

        return true;
    }
}
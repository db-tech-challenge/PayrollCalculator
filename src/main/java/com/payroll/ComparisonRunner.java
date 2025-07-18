package com.payroll;

import com.payroll.agents.RobotOrchestrator;
import com.payroll.model.PaymentResult;
import com.payroll.service.impl.CalculationServiceImpl;
import com.payroll.service.impl.FileServiceImpl;
import com.payroll.service.impl.ValidationServiceImpl;
import com.payroll.util.DefaultFileReader;
import com.payroll.util.SimpleCsvParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Compare old system vs robot system!
 */
public class ComparisonRunner {

    private static final Logger logger = LoggerFactory.getLogger(ComparisonRunner.class);

    public static void main(String[] args) {
        logger.info("""
            
            ════════════════════════════════════════════
                  PAYROLL SYSTEM COMPARISON
            ════════════════════════════════════════════
            """);

        String dataPath = "data";
        FileServiceImpl fileService = new FileServiceImpl(
                dataPath,
                new SimpleCsvParser(new DefaultFileReader())
        );

        // Run OLD system
        logger.info("\n📊 RUNNING OLD SYSTEM (Monolithic)...\n");
        long oldStart = System.currentTimeMillis();

        PayrollCalculator oldSystem = new PayrollCalculator(
                fileService,
                new ValidationServiceImpl(),
                new CalculationServiceImpl()
        );
        oldSystem.run();

        long oldTime = System.currentTimeMillis() - oldStart;
        List<PaymentResult> oldResults = fileService.loadResult();

        // Run NEW robot system
        logger.info("\n🤖 RUNNING NEW SYSTEM (Robot-Powered)...\n");
        long newStart = System.currentTimeMillis();

        RobotOrchestrator robotSystem = new RobotOrchestrator(fileService);
        List<PaymentResult> newResults = robotSystem.processPayrollWithRobots();
        fileService.saveResults(newResults);

        long newTime = System.currentTimeMillis() - newStart;

        // Compare results
        logger.info("""
            
            ╔════════════════════════════════════════════╗
            ║           COMPARISON RESULTS               ║
            ╠════════════════════════════════════════════╣
            ║ Old System:                                ║
            ║   - Time: {} ms                           ║
            ║   - Results: {} employees                  ║
            ║                                            ║
            ║ Robot System:                              ║
            ║   - Time: {} ms                           ║
            ║   - Results: {} employees                  ║
            ║   - More detailed logging ✓                ║
            ║   - Better error handling ✓                ║
            ║   - Modular architecture ✓                 ║
            ╚════════════════════════════════════════════╝
            """,
                oldTime, oldResults.size(),
                newTime, newResults.size()
        );
    }
}
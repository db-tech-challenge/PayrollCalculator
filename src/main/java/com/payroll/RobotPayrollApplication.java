package com.payroll;

import com.payroll.agents.RobotOrchestrator;
import com.payroll.model.PaymentResult;
import com.payroll.service.impl.FileServiceImpl;
import com.payroll.util.DefaultFileReader;
import com.payroll.util.SimpleCsvParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * New Robot-Powered Payroll Application!
 */
public class RobotPayrollApplication {

    private static final Logger logger = LoggerFactory.getLogger(RobotPayrollApplication.class);

    public static void main(String[] args) {
        logger.info("""
            
            ╔═══════════════════════════════════════════╗
            ║      🤖 ROBOT PAYROLL SYSTEM 🤖          ║
            ║    Powered by 5 Specialized Robots!      ║
            ╚═══════════════════════════════════════════╝
            """);

        try {
            // Initialize file service
            String dataPath = args.length > 0 ? args[0] : "data";
            FileServiceImpl fileService = new FileServiceImpl(
                    dataPath,
                    new SimpleCsvParser(new DefaultFileReader())
            );

            // Create the robot orchestrator
            RobotOrchestrator orchestrator = new RobotOrchestrator(fileService);

            // Process payroll with robots!
            logger.info("\n🚀 Starting robot-powered payroll processing...\n");
            List<PaymentResult> results = orchestrator.processPayrollWithRobots();

            // Save results
            if (!results.isEmpty()) {
                logger.info("\n💾 Saving results...");
                fileService.saveResults(results);

                logger.info("""
                    
                    ╔═══════════════════════════════════════════╗
                    ║           ✅ SUCCESS! ✅                  ║
                    ║   Processed {} employees successfully!    ║
                    ║   Check: data/result/main_data_result.csv ║
                    ╚═══════════════════════════════════════════╝
                    """, results.size());
            } else {
                logger.warn("No results to save!");
            }

        } catch (Exception e) {
            logger.error("Error in robot payroll system: ", e);
        }
    }
}
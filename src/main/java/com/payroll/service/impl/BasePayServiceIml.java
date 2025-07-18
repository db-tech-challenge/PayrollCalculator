package com.payroll.service.impl;

import com.payroll.model.Calendar;
import com.payroll.model.Employee;
import com.payroll.model.Payment;
import com.payroll.model.Rate;
import com.payroll.model.TaxClass;
import com.payroll.api.BasePayService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasePayServiceIml implements BasePayService {

    private static final Logger logger = LoggerFactory.getLogger(BasePayServiceIml.class);

    public double calculateBasePay(Employee employee, Rate rate, Payment payment,
                                   TaxClass taxClass, List<Calendar> calendar) {
        double workDaysPayFactor = getDaysRatio(employee.getDaysWorked(), calendar, payment);
        double taxFactor = getTaxFactor(taxClass);

        logger.debug("Days ratio for employee {}: {}", employee.getEmployeeId(), workDaysPayFactor);
        logger.debug("Tax factor for employee {}: {}", employee.getEmployeeId(), taxFactor);

        return workDaysPayFactor * rate.rate() * taxFactor;
    }

    public double getDaysRatio(Integer daysWorked, List<Calendar> calendar, Payment payment) {
        int totalWorkingDays = calculateTotalWorkingDays(calendar, payment);
        int actualDaysWorked = (daysWorked != null) ? daysWorked : totalWorkingDays;
        
        if (totalWorkingDays == 0) {
            logger.warn("No working days found for payment period {}", payment.getPaymentPeriodKey());
            return 0;
        }
        
        double ratio = (double) actualDaysWorked / totalWorkingDays;
        logger.debug("Days worked: {}, Total working days: {}, Ratio: {}", 
                    actualDaysWorked, totalWorkingDays, ratio);
        
        return ratio;
    }
    
    private int calculateTotalWorkingDays(List<Calendar> calendar, Payment payment) {
        if (calendar == null || payment == null) {
            return 0;
        }
        return (int) calendar.stream()
                .filter(day -> day.year() == payment.getCalculationYear() && day.month() == payment.getCalculationMonth())
            .filter(Calendar::isWorkingDay)
            .count();
    }
    
    private boolean isWeekend(String dayOfWeek) {
        return "SAT".equals(dayOfWeek) || "SUN".equals(dayOfWeek);
    }

    public double getTaxFactor(TaxClass taxClass) {
        if (taxClass == null) {
            logger.warn("Tax class is null, using default factor of 1.0");
            return 1.0;
        }
        //net salary = gross salary * (1 - tax_rate)
        double netFactor = 1.0 - taxClass.factor();
        logger.debug("Tax class {}: rate={}, net factor={}", 
                    taxClass.taxClass(), taxClass.factor(), netFactor);
        return netFactor;
    }

}

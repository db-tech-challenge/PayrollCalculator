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
        if (daysWorked == null) {
            return 1;
        }
        return (double) daysWorked / calendar.stream()
            .filter(c -> c.month() == payment.month()-1)
            .filter(c -> c.year() == payment.year())
            .filter(Calendar::isWorkingDay)
            .count();
    }

    public double getTaxFactor(TaxClass taxClass) {
        return 1.f - taxClass.factor();
    }

}

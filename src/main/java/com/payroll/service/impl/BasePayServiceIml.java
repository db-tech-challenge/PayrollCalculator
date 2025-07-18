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

        return workDaysPayFactor * rate.rate() * (1-taxFactor);
    }

    public double getDaysRatio(Integer daysWorked, List<Calendar> calendar, Payment payment) {
        if (daysWorked == null || daysWorked < 0 || calendar == null || payment == null) {
            throw new IllegalArgumentException("Invalid input data");
        }

        int calculationMonth = payment.getCalculationMonth();
        int calculationYear = payment.getCalculationYear();
        long daysTotal = calendar.stream()
                .filter(c -> c.year() == calculationYear &&
                        c.month() == calculationMonth &&
                        c.isWorkingDay())
                .count();

        if (daysTotal == 0) {
            throw new ArithmeticException("No working days in the given month/year");
        }

        return daysWorked / (double) daysTotal;
    }


    public double getTaxFactor(TaxClass taxClass) {
        if (taxClass.taxClass().equalsIgnoreCase("4"))
        {
            return 0.15;
        }
        else if(taxClass.taxClass().equalsIgnoreCase("1"))
        {
            return 0.2;
        }
        else {
            return 0.1;
        }
    }

}

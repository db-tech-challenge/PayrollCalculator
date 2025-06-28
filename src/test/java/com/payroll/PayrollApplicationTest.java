package com.payroll;

import static java.lang.Math.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.payroll.model.PaymentResult;
import com.payroll.service.impl.FileServiceImpl;
import com.payroll.util.DefaultFileReader;
import com.payroll.util.SimpleCsvParser;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PayrollApplicationTest {
    static List<PaymentResult> results;

    @BeforeAll
    static void setUp() {
        SimpleCsvParser parser = new SimpleCsvParser(new DefaultFileReader());
        FileServiceImpl fileService = new FileServiceImpl("data", parser);
        results = fileService.loadResult();
    }

    @Test
    void test_01_01() {
        existOne("24778715");
    }

    @Test
    void test_02_01() {
        existOne("98198992");
    }

    @Test
    void test_04_01() {
        PaymentResult employer = existOne("69819545");
        assertTrue(List.of(5345, 5814).contains((int) round(employer.pay())));
    }

    @Test
    void test_05_01() {
        PaymentResult employer = existOne("69819545");
        assertTrue(List.of(5345, 6288).contains((int) round(employer.pay())));
    }

    @Test
    void test_06_01() {
        existNone("96391050");
    }

    @Test
    void test_08_01() {
        PaymentResult msShenk = existOne("21233182");
        assertTrue(List.of(4174, 4518, 5155, 5585).contains((int) round(msShenk.pay())));
        PaymentResult rmGraf = existOne("20937689");
        assertTrue(List.of(4740, 4367, 3730, 4047).contains((int) round(rmGraf.pay())));

    }

    @Test
    void test_09_01() {
        existOne("17792663");
    }

    private PaymentResult existOne(String id) {
        List<PaymentResult> list = results.stream().filter(x -> x.employeeId().equals(id)).toList();
        assertEquals(1, list.size(), "Employee with ID " + id + " should exist and be unique");
        return list.getFirst();
    }

    private void existNone(String id) {
        boolean notExists = results.stream().noneMatch(x -> x.employeeId().equals(id));
        assertTrue(notExists, "Employee with ID " + id + " should not exist");
    }

}
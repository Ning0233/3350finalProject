package com.company.employee.controller;

import com.company.employee.CombinedPay;
import com.company.employee.DivisionPayTotal;
import com.company.employee.EmployeeDAO;
import com.company.employee.JobTitlePayTotal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final EmployeeDAO dao;

    public ReportController(EmployeeDAO dao) {
        this.dao = dao;
    }

    @GetMapping("/fulltime-pay")
    public ResponseEntity<?> fullTimePayHistory() {
        try {
            List<CombinedPay> list = dao.getFullTimeEmployeePayHistory();
            return ResponseEntity.ok(list);
        } catch (SQLException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @GetMapping("/by-jobtitle")
    public ResponseEntity<?> totalByJobTitle(@RequestParam int year, @RequestParam int month) {
        try {
            List<JobTitlePayTotal> list = dao.getTotalPayByJobTitle(year, month);
            return ResponseEntity.ok(list);
        } catch (SQLException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @GetMapping("/by-division")
    public ResponseEntity<?> totalByDivision(@RequestParam int year, @RequestParam int month) {
        try {
            List<DivisionPayTotal> list = dao.getTotalPayByDivision(year, month);
            return ResponseEntity.ok(list);
        } catch (SQLException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }
}

package com.company.employee.controller;

import com.company.employee.Employee;
import com.company.employee.EmployeeDAO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeDAO dao;

    public EmployeeController(EmployeeDAO dao) {
        this.dao = dao;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        try {
            Employee e = dao.findById(id);
            if (e == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(e);
        } catch (SQLException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @GetMapping("/ssn/{ssn}")
    public ResponseEntity<?> getBySsn(@PathVariable String ssn) {
        try {
            Employee e = dao.findBySSN(ssn);
            if (e == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(e);
        } catch (SQLException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchByName(@RequestParam("q") String q) {
        try {
            List<Employee> list = dao.findByName(q);
            return ResponseEntity.ok(list);
        } catch (SQLException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Employee e) {
        try {
            dao.insertEmployee(e);
            return ResponseEntity.status(HttpStatus.CREATED).body(e);
        } catch (SQLException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody Employee e) {
        try {
            e.setId(id);
            boolean updated = dao.updateEmployee(e);
            if (!updated) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found.");
            return ResponseEntity.ok(e);
        } catch (SQLException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @PostMapping("/salary-range-update")
    public ResponseEntity<?> salaryRangeUpdate(@RequestParam double percent,
                                               @RequestParam double minInclusive,
                                               @RequestParam double maxExclusive) {
        try {
            int updated = dao.updateSalaryByRange(percent, minInclusive, maxExclusive);
            return ResponseEntity.ok("Updated rows: " + updated);
        } catch (SQLException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }
}

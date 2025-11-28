package com.company.employee.controller;

import com.company.employee.Employee;
import com.company.employee.EmployeeDAO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.SQLException;

@Controller
@RequestMapping("/")
public class WebController {
    private final EmployeeDAO dao;

    public WebController(EmployeeDAO dao) {
        this.dao = dao;
    }

    @GetMapping
    public String index(Model model) throws SQLException {
        model.addAttribute("employees", dao.listAll());
        return "index";
    }

    @GetMapping("employees/new")
    public String newEmployee(Model model) {
        model.addAttribute("employee", new Employee());
        return "create";
    }

    @PostMapping("employees")
    public String createEmployee(Employee employee) throws SQLException {
        dao.insertEmployee(employee);
        return "redirect:/";
    }

    @GetMapping("employees/{id}")
    public String viewEmployee(@PathVariable int id, Model model) throws SQLException {
        Employee e = dao.findById(id);
        model.addAttribute("employee", e);
        return "view";
    }
}

package com.company.employee.controller;

import com.company.employee.Employee;
import com.company.employee.EmployeeDAO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.SQLException;
import java.util.List;

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

    @GetMapping("search/id")
    public String searchById(@RequestParam(name = "id", required = false) Integer id,
                             Model model) throws SQLException {
        if (id == null) {
            return "redirect:/";
        }
        com.company.employee.Employee emp = dao.findById(id);
        if (emp == null) {
            model.addAttribute("employees", dao.listAll());
            model.addAttribute("message", "No employee found with id=" + id);
            return "index";
        }
        model.addAttribute("employee", emp);
        return "view";
    }

    @GetMapping("search/ssn")
    public String searchBySsn(@RequestParam(name = "ssn", required = false) String ssn,
                              Model model) throws SQLException {
        if (ssn == null || ssn.isBlank()) {
            return "redirect:/";
        }
        com.company.employee.Employee emp = dao.findBySSN(ssn.trim());
        if (emp == null) {
            model.addAttribute("employees", dao.listAll());
            model.addAttribute("message", "No employee found with SSN=" + ssn);
            return "index";
        }
        model.addAttribute("employee", emp);
        return "view";
    }

    @GetMapping("search/name")
    public String searchByName(@RequestParam(name = "name", required = false) String name,
                               Model model) throws SQLException {
        if (name == null || name.isBlank()) {
            return "redirect:/";
        }
        List<com.company.employee.Employee> results = dao.findByName(name.trim());
        model.addAttribute("employees", results);
        if (results.isEmpty()) {
            model.addAttribute("message", "No employees found for name: " + name);
        }
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

    @GetMapping("employees/{id}/edit")
    public String editEmployeeForm(@PathVariable int id, Model model) throws SQLException {
        Employee e = dao.findById(id);
        if (e == null) {
            model.addAttribute("message", "Employee not found.");
            model.addAttribute("employees", dao.listAll());
            return "index";
        }
        model.addAttribute("employee", e);
        return "edit";
    }

    @PostMapping("employees/{id}/edit")
    public String editEmployeeSubmit(@PathVariable int id, Employee employee, Model model) throws SQLException {
        employee.setId(id);
        boolean ok = dao.updateEmployee(employee);
        if (!ok) {
            model.addAttribute("message", "Employee not found.");
            model.addAttribute("employees", dao.listAll());
            return "index";
        }
        return "redirect:/employees/" + id;
    }

    @GetMapping("salary/update")
    public String salaryUpdateForm(Model model) {
        return "salary_update";
    }

    @PostMapping("salary/update")
    public String salaryUpdateSubmit(@RequestParam(name = "percent") Double percent,
                                     @RequestParam(name = "min") Double min,
                                     @RequestParam(name = "max") Double max,
                                     Model model) throws SQLException {
        if (percent == null || min == null || max == null) {
            model.addAttribute("message", "All fields are required.");
            model.addAttribute("employees", dao.listAll());
            return "index";
        }
        try {
            int affected = dao.updateSalaryByRange(percent, min, max);
            model.addAttribute("message", "Updated salaries for " + affected + " employees.");
            model.addAttribute("employees", dao.listAll());
            return "index";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("message", ex.getMessage());
            model.addAttribute("employees", dao.listAll());
            return "index";
        }
    }
}

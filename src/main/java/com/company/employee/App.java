package com.company.employee;

import java.util.List;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        System.out.println("Employee Management Console (prototype)");
        try (Scanner sc = new Scanner(System.in)) {
            EmployeeDAO dao = new EmployeeDAO();
            boolean run = true;
            while (run) {
                printMenu();
                String choice = sc.nextLine().trim();
                switch (choice) {
                    case "1":
                        insertEmployee(sc, dao);
                        break;
                    case "2":
                        searchEmployee(sc, dao);
                        break;
                    case "3":
                        updateEmployee(sc, dao);
                        break;
                    case "4":
                        updateSalaryRange(sc, dao);
                        break;
                    case "6":
                        showReports(sc, dao);
                        break;
                    case "5":
                        run = false;
                        break;
                    default:
                        System.out.println("Unknown option");
                }
            }
            System.out.println("Exiting.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void printMenu() {
        System.out.println("\nMenu:");
        System.out.println("1) Insert new employee");
        System.out.println("2) Search employee by name / SSN / ID");
        System.out.println("3) Update employee (by ID)");
        System.out.println("4) Update salary by range");
        System.out.println("5) Exit");
        System.out.print("Select: ");
    }

    private static void insertEmployee(Scanner sc, EmployeeDAO dao) {
        try {
            System.out.print("First name: ");
            String fn = sc.nextLine().trim();
            System.out.print("Last name: ");
            String ln = sc.nextLine().trim();
            System.out.print("SSN (no dashes): ");
            String ssn = sc.nextLine().trim();
            System.out.print("Job title: ");
            String job = sc.nextLine().trim();
            System.out.print("Division: ");
            String div = sc.nextLine().trim();
            System.out.print("Salary: ");
            double sal = Double.parseDouble(sc.nextLine().trim());
            Employee e = new Employee(0, fn, ln, ssn, job, div, sal);
            dao.insertEmployee(e);
            System.out.println("Inserted: " + e);
        } catch (Exception ex) {
            System.out.println("Error inserting employee: " + ex.getMessage());
        }
    }

    private static void searchEmployee(Scanner sc, EmployeeDAO dao) {
        try {
            System.out.print("Search by (1) ID (2) SSN (3) Name fragment: ");
            String mode = sc.nextLine().trim();
            switch (mode) {
                case "1":
                    System.out.print("ID: ");
                    int id = Integer.parseInt(sc.nextLine().trim());
                    Employee e = dao.findById(id);
                    System.out.println(e == null ? "Not found" : e);
                    break;
                case "2":
                    System.out.print("SSN: ");
                    String ssn = sc.nextLine().trim();
                    Employee es = dao.findBySSN(ssn);
                    System.out.println(es == null ? "Not found" : es);
                    break;
                case "3":
                    System.out.print("Name fragment: ");
                    String frag = sc.nextLine().trim();
                    List<Employee> list = dao.findByName(frag);
                    if (list.isEmpty()) System.out.println("No results");
                    else list.forEach(System.out::println);
                    break;
                default:
                    System.out.println("Unknown mode");
            }
        } catch (Exception ex) {
            System.out.println("Error searching: " + ex.getMessage());
        }
    }

    private static void updateEmployee(Scanner sc, EmployeeDAO dao) {
        try {
            System.out.print("Employee ID to update: ");
            int id = Integer.parseInt(sc.nextLine().trim());
            Employee e = dao.findById(id);
            if (e == null) {
                System.out.println("Employee not found");
                return;
            }
            System.out.println("Current: " + e);
            System.out.print("New first name (enter to keep): ");
            String fn = sc.nextLine().trim();
            if (!fn.isEmpty()) e.setFirstName(fn);
            System.out.print("New last name (enter to keep): ");
            String ln = sc.nextLine().trim();
            if (!ln.isEmpty()) e.setLastName(ln);
            System.out.print("New SSN (no dashes, enter to keep): ");
            String ssn = sc.nextLine().trim();
            if (!ssn.isEmpty()) e.setSsn(ssn);
            System.out.print("New job title (enter to keep): ");
            String job = sc.nextLine().trim();
            if (!job.isEmpty()) e.setJobTitle(job);
            System.out.print("New division (enter to keep): ");
            String div = sc.nextLine().trim();
            if (!div.isEmpty()) e.setDivision(div);
            System.out.print("New salary (enter to keep): ");
            String salStr = sc.nextLine().trim();
            if (!salStr.isEmpty()) e.setSalary(Double.parseDouble(salStr));
            dao.updateEmployee(e);
            System.out.println("Updated: " + e);
        } catch (Exception ex) {
            System.out.println("Error updating: " + ex.getMessage());
        }
    }

    private static void updateSalaryRange(Scanner sc, EmployeeDAO dao) {
        try {
            System.out.print("Increase percentage (e.g., 3.2): ");
            double pct = Double.parseDouble(sc.nextLine().trim());
            System.out.print("Min salary (inclusive): ");
            double min = Double.parseDouble(sc.nextLine().trim());
            System.out.print("Max salary (exclusive): ");
            double max = Double.parseDouble(sc.nextLine().trim());
            int affected = dao.updateSalaryByRange(pct, min, max);
            System.out.println("Rows updated: " + affected);
        } catch (Exception ex) {
            System.out.println("Error updating salaries: " + ex.getMessage());
        }
    }

    private static void showReports(Scanner sc, EmployeeDAO dao) {
        try {
            System.out.println("\nReports:");
            System.out.println("1) Full-time employees with pay history");
            System.out.println("2) Total pay for month by job title");
            System.out.println("3) Total pay for month by division");
            System.out.print("Select report: ");
            String opt = sc.nextLine().trim();
            switch (opt) {
                case "1":
                    var list = dao.getFullTimeEmployeePayHistory();
                    if (list.isEmpty()) System.out.println("No results");
                    else list.forEach(System.out::println);
                    break;
                case "2":
                    System.out.print("Year (e.g., 2025): ");
                    int y1 = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Month (1-12): ");
                    int m1 = Integer.parseInt(sc.nextLine().trim());
                    var byJob = dao.getTotalPayByJobTitle(y1, m1);
                    if (byJob.isEmpty()) System.out.println("No results");
                    else byJob.forEach(System.out::println);
                    break;
                case "3":
                    System.out.print("Year (e.g., 2025): ");
                    int y2 = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Month (1-12): ");
                    int m2 = Integer.parseInt(sc.nextLine().trim());
                    var byDiv = dao.getTotalPayByDivision(y2, m2);
                    if (byDiv.isEmpty()) System.out.println("No results");
                    else byDiv.forEach(System.out::println);
                    break;
                default:
                    System.out.println("Unknown report");
            }
        } catch (Exception ex) {
            System.out.println("Error running report: " + ex.getMessage());
        }
    }
}

package com.company.employee;

public class Employee {
    private int id;
    private String firstName;
    private String lastName;
    private String ssn; // no dashes
    private String jobTitle;
    private String division;
    private String address;
    private double salary;

    public Employee() {}

    public Employee(int id, String firstName, String lastName, String ssn, String jobTitle, String division, String address, double salary) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.ssn = ssn;
        this.jobTitle = jobTitle;
        this.division = division;
        this.address = address;
        this.salary = salary;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", ssn='" + ssn + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", division='" + division + '\'' +
                ", address='" + address + '\'' +
                ", salary=" + salary +
                '}';
    }
}

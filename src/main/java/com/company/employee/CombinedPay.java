package com.company.employee;

import java.sql.Date;

public class CombinedPay {
    private int employeeId;
    private String firstName;
    private String lastName;
    private String ssn;
    private String jobTitle;
    private String division;
    private double salary;
    private Date payDate;
    private double grossPay;
    private double deductions;
    private double netPay;
    private String notes;

    public CombinedPay(int employeeId, String firstName, String lastName, String ssn, String jobTitle, String division, double salary, Date payDate, double grossPay, double deductions, double netPay, String notes) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.ssn = ssn;
        this.jobTitle = jobTitle;
        this.division = division;
        this.salary = salary;
        this.payDate = payDate;
        this.grossPay = grossPay;
        this.deductions = deductions;
        this.netPay = netPay;
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "CombinedPay{" +
                "employeeId=" + employeeId +
                ", name='" + firstName + ' ' + lastName + '\'' +
                ", ssn='" + ssn + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", division='" + division + '\'' +
                ", salary=" + salary +
                ", payDate=" + payDate +
                ", grossPay=" + grossPay +
                ", deductions=" + deductions +
                ", netPay=" + netPay +
                '}';
    }
}

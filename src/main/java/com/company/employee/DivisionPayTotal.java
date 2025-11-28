package com.company.employee;

public class DivisionPayTotal {
    private String division;
    private double totalGross;
    private double totalNet;

    public DivisionPayTotal(String division, double totalGross, double totalNet) {
        this.division = division;
        this.totalGross = totalGross;
        this.totalNet = totalNet;
    }

    public String getDivision() { return division; }
    public double getTotalGross() { return totalGross; }
    public double getTotalNet() { return totalNet; }

    @Override
    public String toString() {
        return "DivisionPayTotal{" +
                "division='" + division + '\'' +
                ", totalGross=" + totalGross +
                ", totalNet=" + totalNet +
                '}';
    }
}

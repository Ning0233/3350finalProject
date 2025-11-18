package com.company.employee;

public class JobTitlePayTotal {
    private String jobTitle;
    private double totalGross;
    private double totalNet;

    public JobTitlePayTotal(String jobTitle, double totalGross, double totalNet) {
        this.jobTitle = jobTitle;
        this.totalGross = totalGross;
        this.totalNet = totalNet;
    }

    @Override
    public String toString() {
        return "JobTitlePayTotal{" +
                "jobTitle='" + jobTitle + '\'' +
                ", totalGross=" + totalGross +
                ", totalNet=" + totalNet +
                '}';
    }
}

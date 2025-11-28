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

    public String getJobTitle() { return jobTitle; }
    public double getTotalGross() { return totalGross; }
    public double getTotalNet() { return totalNet; }

    @Override
    public String toString() {
        return "JobTitlePayTotal{" +
                "jobTitle='" + jobTitle + '\'' +
                ", totalGross=" + totalGross +
                ", totalNet=" + totalNet +
                '}';
    }
}

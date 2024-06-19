package com.sonar.vishal.emma.entity;

public class FrequencyData {

    private String companyName;
    private String occurrence;
    private String averagePercentage;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(String occurrence) {
        this.occurrence = occurrence;
    }

    public String getAveragePercentage() {
        return averagePercentage;
    }

    public void setAveragePercentage(String averagePercentage) {
        this.averagePercentage = averagePercentage;
    }
}

package com.sonar.vishal.emma.entity;

public class FrequencyData {

    private String companyName;
    private String occurrence;
    private String averagePercentage;
    private String xDot;
    private String xDotDot;

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

    public String getxDot() {
        return xDot;
    }

    public void setxDot(String xDot) {
        this.xDot = xDot;
    }

    public String getxDotDot() {
        return xDotDot;
    }

    public void setxDotDot(String xDotDot) {
        this.xDotDot = xDotDot;
    }
}

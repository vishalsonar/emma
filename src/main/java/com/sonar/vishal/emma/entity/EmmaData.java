package com.sonar.vishal.emma.entity;

public class EmmaData {

    private String companyName;
    private String lastTradePrice;
    private String change;
    private String percentageChange;
    private String volume;
    private String dayLow;
    private String dayHigh;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getLastTradePrice() {
        return lastTradePrice;
    }

    public void setLastTradePrice(String lastTradePrice) {
        this.lastTradePrice = lastTradePrice;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getPercentageChange() {
        return percentageChange;
    }

    public void setPercentageChange(String percentageChange) {
        this.percentageChange = percentageChange;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getDayLow() {
        return dayLow;
    }

    public void setDayLow(String dayLow) {
        this.dayLow = dayLow;
    }

    public String getDayHigh() {
        return dayHigh;
    }

    public void setDayHigh(String dayHigh) {
        this.dayHigh = dayHigh;
    }
}

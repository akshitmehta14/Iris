package com.model;

import java.time.LocalDate;

public class NoosData {
    public double revenue;
    public LocalDate firstSaleDay;
    public LocalDate lastSaleDay;

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    public LocalDate getFirstSaleDay() {
        return firstSaleDay;
    }

    public void setFirstSaleDay(LocalDate firstSaleDay) {
        this.firstSaleDay = firstSaleDay;
    }

    public LocalDate getLastSaleDay() {
        return lastSaleDay;
    }

    public void setLastSaleDay(LocalDate lastSaleDay) {
        this.lastSaleDay = lastSaleDay;
    }
}

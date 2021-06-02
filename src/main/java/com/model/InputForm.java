package com.model;

import java.time.LocalDate;

public class InputForm {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private double liquidationMultiplier;
    private LocalDate date;
    private double goodSize;
    private double badSize;

    public double getLiquidationMultiplier() {
        return liquidationMultiplier;
    }

    public void setLiquidationMultiplier(double liquidationMultiplier) {
        this.liquidationMultiplier = liquidationMultiplier;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(String end) {
        if(end==null || end==""){
            this.date = LocalDate.now();
        }
        else {
            this.date = LocalDate.parse(end);
        }
    }

    public double getGoodSize() {
        return goodSize;
    }

    public void setGoodSize(double goodSize) {
        this.goodSize = goodSize;
    }

    public double getBadSize() {
        return badSize;
    }

    public void setBadSize(double badSize) {
        this.badSize = badSize;
    }
}

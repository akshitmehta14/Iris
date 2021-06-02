package com.pojo;

import com.util.DatatypeConversion;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class AlgoInputPojo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public void setDate(LocalDate date){
        this.date = date;
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

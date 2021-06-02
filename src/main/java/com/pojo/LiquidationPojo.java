package com.pojo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class LiquidationPojo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String category;
    private String subCategory;
    private String priceBucket;
    private double avgDiscount;
    private double AvgDiscountAfterCleanup;
    private double revenueCleanup;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getPriceBucket() {
        return priceBucket;
    }

    public void setPriceBucket(String priceBucket) {
        this.priceBucket = priceBucket;
    }

    public double getAvgDiscount() {
        return avgDiscount;
    }

    public void setAvgDiscount(double avgDiscount) {
        this.avgDiscount = avgDiscount;
    }

    public double getAvgDiscountAfterCleanup() {
        return AvgDiscountAfterCleanup;
    }

    public void setAvgDiscountAfterCleanup(double avgDiscountAfterCleanup) {
        AvgDiscountAfterCleanup = avgDiscountAfterCleanup;
    }

    public double getRevenueCleanup() {
        return revenueCleanup;
    }

    public void setRevenueCleanup(double revenueCleanup) {
        this.revenueCleanup = revenueCleanup;
    }
}

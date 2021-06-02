package com.model;

public class LiquidationData {
    private String category;
    private String subCategory;
    private int quantity;
    private int cleanedQuantity;
    private double revenue;
    private double avgDiscount;
    private double avgCleanedDiscount;
    private double cleanedRevenue;

    public LiquidationData() {
    }

    public LiquidationData(String category, String subCategory) {
        this.category = category;
        this.subCategory = subCategory;
    }

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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getCleanedQuantity() {
        return cleanedQuantity;
    }

    public void setCleanedQuantity(int cleanedQuantity) {
        this.cleanedQuantity = cleanedQuantity;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    public double getAvgDiscount() {
        return avgDiscount;
    }

    public void setAvgDiscount(double avgDiscount) {
        this.avgDiscount = avgDiscount;
    }

    public double getAvgCleanedDiscount() {
        return avgCleanedDiscount;
    }

    public void setAvgCleanedDiscount(double avgCleanedDiscount) {
        this.avgCleanedDiscount = avgCleanedDiscount;
    }

    public double getCleanedRevenue() {
        return cleanedRevenue;
    }

    public void setCleanedRevenue(double cleanedRevenue) {
        this.cleanedRevenue = cleanedRevenue;
    }
}

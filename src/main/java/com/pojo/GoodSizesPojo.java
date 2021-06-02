package com.pojo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class GoodSizesPojo {
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
    private String size;
    private double revContri;
    private String typeOfSizes;

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

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public double getRevContri() {
        return revContri;
    }

    public void setRevContri(double revContri) {
        this.revContri = revContri;
    }

    public String getTypeOfSizes() {
        return typeOfSizes;
    }

    public void setTypeOfSizes(String typeOfSizes) {
        this.typeOfSizes = typeOfSizes;
    }
}


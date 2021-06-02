package com.pojo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
// TODO have table annotation and name it accordingly
public class NoosPojo {

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
    private String styleCode;
    private double styleRos;
    private double styleRevContri;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStyleCode() {
        return styleCode;
    }

    public void setStyleCode(String styleCode) {
        this.styleCode = styleCode;
    }

    public double getStyleRos() {
        return styleRos;
    }

    public void setStyleRos(double styleRos) {
        this.styleRos = styleRos;
    }

    public double getStyleRevContri() {
        return styleRevContri;
    }

    public void setStyleRevContri(double styleRevContri) {
        this.styleRevContri = styleRevContri;
    }
}

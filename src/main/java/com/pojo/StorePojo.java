package com.pojo;

import javax.persistence.*;

@Entity
@Table(name = "store" , uniqueConstraints = @UniqueConstraint(columnNames = {"branch"},name = "uniqueBranch"))
public class StorePojo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String branch;
    private String city;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}

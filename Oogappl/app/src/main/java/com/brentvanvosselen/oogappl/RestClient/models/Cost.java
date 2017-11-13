package com.brentvanvosselen.oogappl.RestClient.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Cost {

    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("amount")
    private double amount;
    @SerializedName("date")
    private Date date;
    @SerializedName("costCategoryid")
    private CostCategory category;

    public Cost(String title, String description, double amount, Date date, CostCategory category) {
        this.title = title;
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.category = category;
    }

    public Cost(String title, String description, double amount, Date date) {
        this.title = title;
        this.description = description;
        this.amount = amount;
        this.date = date;
    }

    public void setCategory(CostCategory cat) {
        this.category = cat;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public Date getDate() {
        return date;
    }

    public CostCategory getCategory() {
        return category;
    }
}

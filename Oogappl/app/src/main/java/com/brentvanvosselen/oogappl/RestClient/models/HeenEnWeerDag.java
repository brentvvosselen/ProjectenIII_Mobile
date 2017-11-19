package com.brentvanvosselen.oogappl.RestClient.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class HeenEnWeerDag implements Serializable {
    @SerializedName("_id")
    private String id;
    @SerializedName("date")
    private Date date;
    @SerializedName("description")
    private String description;
    @SerializedName("items")
    private HeenEnWeerItem[] items;
    @SerializedName("child")
    private Child child;

    public String getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public HeenEnWeerItem[] getItems() {
        return items;
    }

    public Child getChild() {
        return child;
    }
}

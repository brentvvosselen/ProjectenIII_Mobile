package com.brentvanvosselen.oogappl.RestClient.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by brentvanvosselen on 19/11/2017.
 */

public class HeenEnWeerItem implements Serializable {
    @SerializedName("_id")
    private String id;
    @SerializedName("category")
    private Category category;
    @SerializedName("value")
    private String value;

    public HeenEnWeerItem(Category category, String value) {
        this.category = category;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public Category getCategory() {
        return category;
    }

    public String getValue() {
        return value;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

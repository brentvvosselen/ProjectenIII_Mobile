package com.brentvanvosselen.oogappl.RestClient.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by brentvanvosselen on 03/11/2017.
 */

public class Category {

    @SerializedName("_id")
    private String id;
    @SerializedName("type")
    private String type;
    @SerializedName("color")
    private String color;

    public Category(String id, String type, String color) {
        this.id = id;
        this.type = type;
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public String getColor() {
        return color;
    }
}

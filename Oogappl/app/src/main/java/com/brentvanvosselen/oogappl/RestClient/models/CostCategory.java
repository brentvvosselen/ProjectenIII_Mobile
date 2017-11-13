package com.brentvanvosselen.oogappl.RestClient.models;

import com.google.gson.annotations.SerializedName;

public class CostCategory {

    @SerializedName("type")
    private String type;
    @SerializedName("_id")
    private String id;

    public CostCategory(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}

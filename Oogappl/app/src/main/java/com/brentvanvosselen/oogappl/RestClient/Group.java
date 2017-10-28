package com.brentvanvosselen.oogappl.RestClient;

import com.google.gson.annotations.SerializedName;

public class Group {

    @SerializedName("_id")
    private String id;
    @SerializedName("children")
    private Child[] children;

    public Group(String id, Child[] children) {
        this.id = id;
        this.children = children;
    }

    public Child[] getChildren() {
        return this.children;
    }
}
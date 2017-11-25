package com.brentvanvosselen.oogappl.RestClient.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class HeenEnWeerBoek implements Serializable {

    @SerializedName("_id")
    private String id;
    @SerializedName("child")
    private Child child;
    @SerializedName("days")
    private HeenEnWeerDag[] days;

    public HeenEnWeerBoek(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Child getChild() {
        return child;
    }

    public HeenEnWeerDag[] getDays() {
        return days;
    }
}

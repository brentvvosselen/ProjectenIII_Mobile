package com.brentvanvosselen.oogappl.RestClient.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Group implements Serializable {

    @SerializedName("_id")
    private String id;
    @SerializedName("children")
    private Child[] children;
    @SerializedName("fintype")
    @Expose
    private FinInfo finType;

    public Group(String id, Child[] children, FinInfo finType) {
        this.id = id;
        this.children = children;
        this.finType = finType;
    }

    public Child[] getChildren() {
        return this.children;
    }

    public void setChildren(Child[] children) {
        this.children = children;
    }

    public FinInfo getFinType() {
        return this.finType;
    }

    public void setFinType(FinInfo info) {
        this.finType = info;
    }
}

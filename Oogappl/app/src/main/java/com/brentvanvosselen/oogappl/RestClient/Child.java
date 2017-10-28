package com.brentvanvosselen.oogappl.RestClient;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Child implements Serializable {

    @SerializedName("_id")
    private String _id;
    @SerializedName("firstname")
    private String firstname;
    @SerializedName("lastname")
    private String lastname;
    @SerializedName("gender")
    private String gender;
    @SerializedName("age")
    private int age;
    @SerializedName("categories")
    private Category[] category;

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public Category[] getCategory() {
        return this.category;
    }

    public ArrayList<String> getCategoryNames() {
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < this.category.length; i++) {
            names.add(category[i].getName());
        }
        return names;
    }

    public void addInfo(int catIndex, String name, String value) {
        this.category[catIndex].addInfo(name, value);
    }
}

package com.brentvanvosselen.oogappl.RestClient;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Child implements Serializable {

    @SerializedName("_id")
    private String _id;
    @SerializedName("firstname")
    private String firstname;
    @SerializedName("lastname")
    private String lastname;
    @SerializedName("gender")
    private String gender;
    @SerializedName("birthdate")
    private Date birthdate;
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

    public Child(String firstname, String lastname, String gender, Date birthdate) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.gender = gender;
        this.birthdate = birthdate;
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

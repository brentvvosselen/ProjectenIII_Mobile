package com.brentvanvosselen.oogappl.RestClient;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private List<Category> category;

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public List<Category> getCategory() {
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
        for (int i = 0; i < this.category.size(); i++) {
            names.add(category.get(i).getName());
        }
        return names;
    }

    public void addInfo(int catIndex, String name, String value) {
        this.category.get(catIndex).addInfo(name, value);
    }

    public void updateCategory(Category category) {
        for(int i = 0; i < this.category.size(); i++) {
            if(this.category.get(i).getName().equals(category.getName())) {
                this.category.set(i, category);
            }
        }
    }

    public void addCategory(String name) {
        this.category.add(new Category(name));
    }

    public void removeCategory(Category c) {
        this.category.remove(c);
    }
}

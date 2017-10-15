package com.brentvanvosselen.oogappl.RestClient;

import com.google.gson.annotations.SerializedName;

public class Child {

    @SerializedName("firstname")
    private String firstname;
    @SerializedName("lastname")
    private String lastname;
    @SerializedName("info")
    private String info;

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getInfo() {
        return info;
    }
}

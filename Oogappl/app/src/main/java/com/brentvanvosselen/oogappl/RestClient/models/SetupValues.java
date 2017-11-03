package com.brentvanvosselen.oogappl.RestClient.models;

import com.brentvanvosselen.oogappl.RestClient.models.Child;
import com.google.gson.annotations.SerializedName;

/**
 * Created by brentvanvosselen on 28/10/2017.
 */

public class SetupValues {

    @SerializedName("email")
    private String email;
    @SerializedName("currentType")
    private String type;
    @SerializedName("otherEmail")
    private String otherEmail;
    @SerializedName("otherFirstname")
    private String otherFirstname;
    @SerializedName("otherLastname")
    private String otherLastname;
    @SerializedName("children")
    private Child[] children;


    public SetupValues(String email, String type, String otherEmail, String otherFirstname, String otherLastname, Child[] children) {
        this.email = email;
        this.type = type;
        this.otherEmail = otherEmail;
        this.otherFirstname = otherFirstname;
        this.otherLastname = otherLastname;
        this.children = children;
    }
}

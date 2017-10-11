package com.brentvanvosselen.oogappl.RestClient;

import com.google.gson.annotations.SerializedName;

public class Parent {

    /*
    * Object die wordt ontvangen/doorgegeven via API call
    *
    * @SerializedName: naam in JSON-object
    *
    * */


    @SerializedName("_id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("hobby")
    private String hobby;

    public Parent(String name, String hobby) {
        this.name = name;
        this.hobby = hobby;
    }

    public Parent(String name) {
        this(name, "");
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getHobby() {
        return hobby;
    }
}

package com.brentvanvosselen.oogappl.RestClient.models;

import com.brentvanvosselen.oogappl.RestClient.models.Child;
import com.brentvanvosselen.oogappl.RestClient.models.Group;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Parent implements Serializable {

    /*
    * Object die wordt ontvangen/doorgegeven via API call
    *
    * @SerializedName: naam in JSON-object
    *
    * */


    @SerializedName("_id")
    private String id;
    @SerializedName("email")
    private String email;
    @SerializedName("firstname")
    private String firstname;
    @SerializedName("lastname")
    private String lastname;
    @SerializedName("addressCity")
    private String addressCity;
    @SerializedName("addressNumber")
    private String addressNumber;
    @SerializedName("addressStreet")
    private String addressStreet;
    @SerializedName("addressPostalcode")
    private String addressPostalcode;
    @SerializedName("telephoneNumber")
    private String telephoneNumber;
    @SerializedName("workName")
    private String workName;
    @SerializedName("workNumber")
    private String workNumber;
    @SerializedName("type")
    private String type;
    @SerializedName("group")
    private Group group;
    @SerializedName("doneSetup")
    private boolean doneSetup;

    public Parent(String id, String email, String firstname, String lastname) {
        this.id = id;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public void setAddressCity(String addressCity) {
        this.addressCity = addressCity;
    }

    public void setAddressNumber(String addressNumber) {
        this.addressNumber = addressNumber;
    }

    public void setAddressStreet(String addressStreet) {
        this.addressStreet = addressStreet;
    }

    public void setAddressPostalcode(String addressPostalcode) {
        this.addressPostalcode = addressPostalcode;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }

    public void setWorkNumber(String workNumber) {
        this.workNumber = workNumber;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getAddressCity() {
        return addressCity;
    }

    public String getAddressNumber() {
        return addressNumber;
    }

    public String getAddressStreet() {
        return addressStreet;
    }

    public String getAddressPostalcode() {
        return addressPostalcode;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public String getWorkName() {
        return workName;
    }

    public String getWorkNumber() {
        return workNumber;
    }

    public String getType(){ return type; }

    public Child[] getChildren() { return this.group.getChildren(); }

    public Group getGroup() { return this.group; }

    public boolean hasDoneSetup(){ return doneSetup;}
}

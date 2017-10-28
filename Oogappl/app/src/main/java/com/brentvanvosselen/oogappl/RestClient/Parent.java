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
    @SerializedName("email")
    private String email;
    @SerializedName("firstName")
    private String firstname;
    @SerializedName("lastName")
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

    public boolean hasDoneSetup(){ return doneSetup;}

    public Child[] getChildren() { return this.group.getChildren(); }
}

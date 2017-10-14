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
    @SerializedName("firstname")
    private String firstname;
    @SerializedName("lastname")
    private String lastname;
    @SerializedName("address_city")
    private String addressCity;
    @SerializedName("address_number")
    private String addressNumber;
    @SerializedName("address_street")
    private String addressStreet;
    @SerializedName("address_postalcode")
    private String addressPostalcode;
    @SerializedName("number")
    private String telephoneNumber;
    @SerializedName("work_name")
    private String workName;
    @SerializedName("work_number")
    private String workNumber;

    public Parent(String id, String email, String firstname, String lastname, String addressCity, String addressNumber, String addressStreet, String addressPostalcode, String telephoneNumber, String workName, String workNumber) {
        this.id = id;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.addressCity = addressCity;
        this.addressNumber = addressNumber;
        this.addressStreet = addressStreet;
        this.addressPostalcode = addressPostalcode;
        this.telephoneNumber = telephoneNumber;
        this.workName = workName;
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
}

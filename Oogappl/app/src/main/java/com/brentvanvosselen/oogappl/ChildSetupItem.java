package com.brentvanvosselen.oogappl;

/**
 * Created by brentvanvosselen on 28/10/2017.
 */

public class ChildSetupItem {
    private String firstname,lastname,gender;
    private int birthdate;

    public ChildSetupItem(String firstname, String lastname, String gender, int birthdate) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.gender = gender;
        this.birthdate = birthdate;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getGender() {
        return gender;
    }

    public int getBirthdate() {
        return birthdate;
    }
}

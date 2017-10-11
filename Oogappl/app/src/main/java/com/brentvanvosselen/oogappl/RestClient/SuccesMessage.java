package com.brentvanvosselen.oogappl.RestClient;

import com.google.gson.annotations.SerializedName;

/**
 * Created by joshi on 11/10/2017.
 */

public class SuccesMessage {

    @SerializedName("success")
    private Boolean succes;
    @SerializedName("msg")
    private String message;

    public Boolean getSucces() {
        return succes;
    }

    public String getMessage() {
        return message;
    }
}

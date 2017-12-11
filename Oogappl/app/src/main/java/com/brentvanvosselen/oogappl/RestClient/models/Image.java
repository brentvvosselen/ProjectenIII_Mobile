package com.brentvanvosselen.oogappl.RestClient.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by brentvanvosselen on 11/12/2017.
 */

public class Image implements Serializable {
    @SerializedName("filename")
    private String filename;
    @SerializedName("filetype")
    private String filetype;
    @SerializedName("value")
    private String value;

    public Image(String filename, String filetype, String value) {
        this.filename = filename;
        this.filetype = filetype;
        this.value = value;
    }

    public String getFilename() {
        return filename;
    }

    public String getFiletype() {
        return filetype;
    }

    public String getValue() {
        return value;
    }
}

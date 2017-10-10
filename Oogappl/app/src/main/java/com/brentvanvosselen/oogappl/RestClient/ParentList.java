package com.brentvanvosselen.oogappl.RestClient;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ParentList {

    @SerializedName("data")
    public List data = new ArrayList();
}

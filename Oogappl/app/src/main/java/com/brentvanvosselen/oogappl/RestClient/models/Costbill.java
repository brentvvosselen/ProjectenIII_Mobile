package com.brentvanvosselen.oogappl.RestClient.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Costbill {

    @SerializedName("costs")
    private List<Cost> costsMonth;
    @SerializedName("totalCostToPay")
    private double toPay;

    public List<Cost> getCostsMonth() {
        return costsMonth;
    }

    public double getToPay() {
        return toPay;
    }
}

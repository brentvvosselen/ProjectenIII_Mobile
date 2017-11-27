package com.brentvanvosselen.oogappl.RestClient.models;

import com.brentvanvosselen.oogappl.RestClient.models.Category;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by brentvanvosselen on 03/11/2017.
 */

public class Event{

    @SerializedName("_id")
    private String id;
    @SerializedName("title")
    private String title;
    @SerializedName("start")
    private Date start;
    @SerializedName("end")
    private Date end;
    @SerializedName("description")
    private String description;
    @SerializedName("categoryid")
    private Category category;
    @SerializedName("interval")
    @Expose
    private int interval;
    @SerializedName("freq")
    @Expose
    private String freq;
    @SerializedName("until")
    @Expose
    private Date until;
    @SerializedName("children")
    private Child[] children;


    public Event(String title, Date start, Date end, String description, Category category, Child[] myChildren) {
        this.title = title;
        this.start = start;
        this.end = end;
        this.description = description;
        this.category = category;
        this.children = myChildren;
    }

    public Event(String title, Date start, Date end, String description, Category category, int interval, String freq, Date until) {
        this.title = title;
        this.start = start;
        this.end = end;
        this.description = description;
        this.category = category;
        this.interval = interval;
        this.freq = freq;
        this.until = until;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public String getId() {
        return id;
    }

    public Category getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Child[] getchildren(){ return this.children;}
}

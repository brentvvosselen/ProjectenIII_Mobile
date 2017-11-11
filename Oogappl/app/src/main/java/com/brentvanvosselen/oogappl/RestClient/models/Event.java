package com.brentvanvosselen.oogappl.RestClient.models;

import com.brentvanvosselen.oogappl.RestClient.models.Category;
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


    public Event(String title, Date start, Date end, String description, Category category) {
        this.title = title;
        this.start = start;
        this.end = end;
        this.description = description;
        this.category = category;
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
}

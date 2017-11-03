package com.brentvanvosselen.oogappl.RestClient;

import com.brentvanvosselen.oogappl.RestClient.models.Category;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by brentvanvosselen on 03/11/2017.
 */

public class Event {

    @SerializedName("_id")
    private String id;
    @SerializedName("title")
    private String title;
    @SerializedName("datetime")
    private Date datetime;
    @SerializedName("description")
    private String description;
    @SerializedName("category")
    private Category category;

    public Event(String id, String title, Date datetime, String description, Category category) {
        this.id = id;
        this.title = title;
        this.datetime = datetime;
        this.description = description;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public Date getDatetime() {
        return datetime;
    }

    public Category getCategory() {
        return category;
    }
}

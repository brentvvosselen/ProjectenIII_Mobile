package com.brentvanvosselen.oogappl.RestClient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChildinfoCategory implements Serializable {
    private String name;
    private List<Info> info;

    public ChildinfoCategory(String name) {
        this.name = name;
        this.info = new ArrayList<>();
    }

    public void addInfo(String name, String value) {
        info.add(new Info(name, value));
    }

    public String getName() {
        return name;
    }

    public List<Info> getInfo() {
        return info;
    }

    public void changeInfo(String name, String value) {
        for(Info i : this.info) {
            if (i.getName().equals(name)) {
                i.setValue(value);
            }
        }
    }

    public void remove(Info i) {
        info.remove(i);
    }
}

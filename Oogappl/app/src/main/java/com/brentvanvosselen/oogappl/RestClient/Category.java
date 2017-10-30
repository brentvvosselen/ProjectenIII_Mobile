package com.brentvanvosselen.oogappl.RestClient;

import java.io.Serializable;
import java.util.List;

public class Category implements Serializable {
    private String name;
    private List<Info> info;

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

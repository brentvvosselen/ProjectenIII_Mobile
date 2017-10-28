package com.brentvanvosselen.oogappl.RestClient;

import java.io.Serializable;

public class Info implements Serializable {
    private String name;
    private String value;

    public Info(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return name + ": " + value;
    }
}

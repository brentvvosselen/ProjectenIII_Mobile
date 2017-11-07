package com.brentvanvosselen.oogappl.RestClient.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FinInfo implements Serializable{

    @SerializedName("fintype")
    private String type;
    @SerializedName("accepted")
    private String[] accepted;
    @SerializedName("kindrekening")
    @Expose
    private Kindrekening kindrekening;
    @SerializedName("onderhoudsbijdrage")
    @Expose
    private Onderhoudsbijdrage onderhoudsbijdrage;

    private FinInfo(String type, Parent p) {
        this.type = type;
        accepted = new String[2];
        accepted[0] = p.getId();
    }

    public FinInfo(Parent p, int maxBedrag) {
        this("kindrekening", p);
        this.kindrekening = new Kindrekening(maxBedrag);
    }

    public FinInfo(Parent p, boolean gerechtigde, int percentage) {
        this("onderhoudsbijdrage", p);
        this.onderhoudsbijdrage = new Onderhoudsbijdrage(p, gerechtigde, percentage);
    }

    private class Kindrekening implements Serializable {
        @SerializedName("maxBedrag")
        private int maxBedrag;

        public Kindrekening(int maxBedrag) {
            this.maxBedrag = maxBedrag;
        }
    }

    private class Onderhoudsbijdrage implements Serializable {
        @SerializedName("onderhoudsgerechtigde")
        private String onderhoudsgerechtigde;
        @SerializedName("onderhoudsplichtige")
        private String onderhoudsplichtige;
        @SerializedName("percentage")
        private int percentage;

        public Onderhoudsbijdrage(Parent p, boolean gerechtigde, int percentage) {
            if (gerechtigde) {
                this.onderhoudsgerechtigde = p.getId();
            } else {
                this.onderhoudsplichtige = p.getId();
            }

            this.percentage = percentage;
        }
    }
}

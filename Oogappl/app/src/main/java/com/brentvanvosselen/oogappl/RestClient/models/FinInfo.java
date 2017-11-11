package com.brentvanvosselen.oogappl.RestClient.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FinInfo implements Serializable {

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

    public String getType() {
        return this.type;
    }

    public Kindrekening getKindrekening() {
        return this.kindrekening;
    }

    public Onderhoudsbijdrage getOnderhoudsbijdrage() {
        return this.onderhoudsbijdrage;
    }

    public boolean parentHasAccepted(Parent p) {
        for(String id : accepted) {
            if(p.getId().equals(id)) {
                return true;
            }
        }

        return false;
    }

    public boolean bothParentsAccepted() {
        for(String id: accepted) {
            if(id == null) {
                return false;
            }
        }
        return accepted.length == 2;
    }

    public boolean otherParentHasAccepted(Parent p) {
        for(String id: accepted) {
            if(id != null && !p.getId().equals(id)) {
                return true;
            }
        }

        return false;
    }

    public class Kindrekening implements Serializable {
        @SerializedName("maxBedrag")
        private int maxBedrag;

        public Kindrekening(int maxBedrag) {
            this.maxBedrag = maxBedrag;
        }

        public int getMaxBedrag() {
            return this.maxBedrag;
        }
    }

    public class Onderhoudsbijdrage implements Serializable {
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

        public int getPercentage() {
            return this.percentage;
        }
    }
}

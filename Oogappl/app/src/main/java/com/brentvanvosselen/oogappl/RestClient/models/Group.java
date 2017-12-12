package com.brentvanvosselen.oogappl.RestClient.models;

import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Group implements Serializable {

    @SerializedName("_id")
    private String id;
    @SerializedName("children")
    private Child[] children;
    @SerializedName("finance")
    @Expose
    private FinInfo finType;
    @SerializedName("heenEnWeerBoekjes")
    @Expose
    private HeenEnWeerBoek[] books;

    public Group(String id, Child[] children, FinInfo finType) {
        this.id = id;
        this.children = children;
        this.finType = finType;
    }

    public Child[] getChildren() {
        return this.children;
    }

    public void setChildren(Child[] children) {
        this.children = children;
    }

    public FinInfo getFinType() {
        return this.finType;
    }

    public void setFinType(FinInfo info) {
        this.finType = info;
    }


    public boolean parentHasAccepted(Parent p) {
        if(finType == null) {
            Log.i("CURRENT", "FINTYPE NULL");
            return false;
        }

        return finType.parentHasAccepted(p);
    }

    public boolean bothParentsAccepted() {
        if(finType == null) {
            return false;
        }

        return finType.bothParentsAccepted();
    }

    public boolean otherParentHasAccepted(Parent p) {
        if(finType == null) {
            return false;
        }

        return finType.otherParentHasAccepted(p);
    }

    public FinancialType getFinancialType() {
        String type = this.finType.getType();
        if (type == null || type.equals("")) {
            return null;
        } else if (type.equals("onderhoudsbijdrage")) {
            return FinancialType.ONDERHOUDSBIJDRAGE;
        } else {
            return FinancialType.KINDREKENING;
        }
    }

    public String[] getSelectChildnamesCost() {
        String[] names = new String[this.children.length + 1];
        names[0] = "Allemaal";

        for(int i = 0; i < this.children.length; i++) {
            names[i + 1] = this.children[i].getFirstname();
        }

        return names;
    }

    public Child[] getChildByName(String firstname) {
        if(firstname.equals("Allemaal")) {
            return this.children;
        } else {
            Child[] temp = new Child[1];

            for(Child c : this.children) {
                if(c.getFirstname().equals(firstname)) {
                    temp[0] = c;
                    return temp;
                }
            }
        }

        return null;
    }
}

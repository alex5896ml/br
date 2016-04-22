package com.mobmedianet.trackergps.Project.Objects;

import java.io.Serializable;

/**
 * Created by cesargarcia on 14/10/15.
 */
public class POISObject implements Serializable {
    private int iD, category;
    private String name;
    private Double latitude, longitude;


    public POISObject() {

    }

    public void setID(int ID) {
        this.iD = ID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public int getID() {
        return iD;
    }

    public String getName() {
        return name;
    }

    public int getCategory() {
        return category;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}

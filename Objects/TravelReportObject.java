package com.mobmedianet.trackergps.Project.Objects;

import java.io.Serializable;

/**
 * Created by cesargarcia on 27/1/16.
 */
public class TravelReportObject implements Serializable {
    private String location;
    private String date;
    private String locationFin;
    private String dateFin;
    private String stopTime;
    private String avgSpeed;
    private String maxSpeed;
    private String distance;
    private String idleTime;
    private String unitName;
    private String duration;
    public TravelReportObject()
    {

    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocationFin() {
        return locationFin;
    }

    public void setLocationFin(String locationFin) {
        this.locationFin = locationFin;
    }

    public String getDateFin() {
        return dateFin;
    }

    public void setDateFin(String dateFin) {
        this.dateFin = dateFin;
    }

    public String getStopTime() {
        return stopTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

    public String getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(String avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public String getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(String maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(String idleTime) {
        this.idleTime = idleTime;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}

package com.mobmedianet.trackergps.Project.Objects;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Cesar on 8/26/2015.
 */
public class AlertsObject implements Serializable {
    String unit, name, eventstart, locate, eventend;
    int id, status, type;
    double mlong, mlat;
    String url;

    public void setUnit(String Unit) {
        this.unit = Unit;
    }

    public String getUnit() {
        return unit;
    }

    public void setType(int Type) {
        this.type = Type;
    }

    public int getType() {
        return type;
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getID() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setLon(double xLon) {
        this.mlong = xLon;
    }

    public double getLon() {
        return mlong;
    }

    public void setLat(double Lat) {
        this.mlat = Lat;
    }

    public double getLat() {
        return mlat;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setLocate(String locate) {
        this.locate = locate;
    }

    public String getLocate() {
        return locate;
    }

    public void setStartTime(String startTime) {
        this.eventstart = startTime;
    }

    public String getStartTime() {
        return eventstart;
    }

    public void setStopTime(String stopTime) {
        this.eventend = stopTime;
    }

    public String getStopTime() {
        return eventend;
    }

    public void setUrl(String url) {
        url = url;
    }

    public String getUrl() {
        return url;
    }
}

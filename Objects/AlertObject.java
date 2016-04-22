package com.mobmedianet.trackergps.Project.Objects;

import com.mobmedianet.trackergps.R;

import java.io.Serializable;

/**
 * Created by Cesar on 8/26/2015.
 */
public class AlertObject implements Serializable {
    private String unit, name, eventstart, locate, eventend;
    private int id;
    private int status;
    private int type;
    private int objectType;
    private double mlong, mlat;
    private String url;


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
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public int getObjectType() {
        return objectType;
    }

    public void setObjectType(int objectType) {
        this.objectType = objectType;
    }

    public Integer getImageWithObjecType(){
        Integer drawableId;

            switch (getObjectType()) {
                case 0:
                    drawableId=R.drawable.motorcycle;
                    break;
                case 1:
                    drawableId=R.drawable.car;
                    break;
                case 2:
                    drawableId=R.drawable.box;
                    break;
                case 3:
                    drawableId=R.drawable.people;
                    break;
                case 6:
                    drawableId=R.drawable.asset;
                    break;
                default:
                    drawableId=R.drawable.alertas;
                    break;
            }
        return drawableId;
    }
}

package com.mobmedianet.trackergps.Project.Objects;

import java.io.Serializable;

/**
 * Created by Eduardo on 7/4/16.
 */
public class StopReportObject implements Serializable {
    private String unitName;
    private String dateIni;
    private String dateFin;
    private String duration;
    private String location;
    private String longitud;
    private String latitud;

    public StopReportObject()
    {

    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getDateIni() {
        return dateIni;
    }

    public void setDateIni(String dateIni) {
        this.dateIni = dateIni;
    }

    public String getDateFin() {
        return dateFin;
    }

    public void setDateFin(String dateFin) {
        this.dateFin = dateFin;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }
}

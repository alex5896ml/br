package com.mobmedianet.trackergps.Project.Objects;

import java.io.Serializable;

/**
 * Created by Eduardo on 9/3/16.
 */
public class VehicleHistoryObject  implements Serializable {
    private String location;
    private String reportDate;
    private String status;
    private String speed;
    private String heading;
    private Double longitud;
    private Double latitud;


    public VehicleHistoryObject() {

    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public Double getLongitud() { return longitud;}

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }
}


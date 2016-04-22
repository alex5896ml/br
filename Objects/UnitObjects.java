package com.mobmedianet.trackergps.Project.Objects;

import java.io.Serializable;

/**
 * Created by cesargarcia on 2/11/15.
 */
public class UnitObjects implements Serializable {
    private int unitId, objectType;
    private String name, location, lastTime, imageUrl, driver, orientation, driverPhone, driverEmail;
    private double mLat, mLong, Speed;
    private boolean ignition;
    private String dateFilterReport;


    public UnitObjects() {

    }

    public int getUnitId() {
        return unitId;
    }

    public void setUnitId(int unitId) {
        this.unitId = unitId;
    }

    public int getObjectType() {
        return objectType;
    }

    public void setObjectType(int objectType) {
        this.objectType = objectType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setmLat(double mLat) {
        this.mLat = mLat;
    }

    public double getmLat() {
        return mLat;
    }

    public void setmLong(double mLong) {
        this.mLong = mLong;
    }

    public double getmLong() {
        return mLong;
    }

    public double getSpeed() {
        return Speed;
    }

    public void setSpeed(double speed) {
        Speed = speed;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public void setIgnition(boolean ignition) {
        this.ignition = ignition;
    }

    public boolean isIgnition() {
        return ignition;
    }

    public void setDriverEmail(String driverEmail) {
        this.driverEmail = driverEmail;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public String getDriverEmail() {
        return driverEmail;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public String getDateFilterReport() {
        return dateFilterReport;
    }

    public void setDateFilterReport(String dateFilterReport) {
        this.dateFilterReport = dateFilterReport;
    }
}

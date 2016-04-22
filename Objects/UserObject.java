package com.mobmedianet.trackergps.Project.Objects;

import android.content.Context;

/**
 * Created by Cesar on 8/19/2015.
 */
public class UserObject {

    //Singleton class
    private static UserObject instance;

    /*
    Model
     */
    private Context context;
    private int numOfVehicles = 0;
    private int userId = 0;
    private int companyId = 0;
    private int subFleetId = 0;
    private int groupId = 0;
    private String sprovider = "";
    private int vehicleId = 0;
    private int numOfAlerts = 0;
    private int userType = 0;

    public UserObject(Context context) {
        this.context = context;
    }

    public void setNumOfVehicles(int NumOfVehicles) {
        this.numOfVehicles = NumOfVehicles;
    }

    public int getNumOfVehicles() {
        return this.numOfVehicles;
    }

    public void setUserId(int UserId) {
        this.userId = UserId;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setCompanyId(int CompanyId) {
        this.companyId = CompanyId;
    }

    public int getCompanyId() {
        return this.companyId;
    }

    public void setSubFleetId(int SubFleetId) {
        this.subFleetId = SubFleetId;
    }

    public int getSubFleetId() {
        return this.subFleetId;
    }

    public void setGroupId(int GroupId) {
        this.groupId = GroupId;
    }

    public int getGroupId() {
        return this.groupId;
    }

    public void setSprovider(String sprovider) {
        this.sprovider = sprovider;
    }

    public String getSprovider() {
        return this.sprovider;
    }

    public void setVehicleId(int VehicleId) {
        this.vehicleId = VehicleId;
    }

    public int getVehicleId() {
        return this.vehicleId;
    }

    public void setNumOfAlerts(int NumOfReports) {
        this.numOfAlerts = NumOfReports;
    }

    public int getNumOfAlerts() {
        return this.numOfAlerts;
    }

    public int getUserType() {
        return userType;
    }

    /*
     Global = 1
     Company = 2
     SubFleet = 3
     Group = 4
    */


    public void setUserType() {

        if (companyId == 0)
            userType = 1;
        else if (subFleetId == 0)
            userType = 2;
        else if (groupId == 0)
            userType = 3;
        else
            userType = 4;

    }

    public static void initInstance(Context context) {
        if (instance == null) {
            // Create the instance
            instance = new UserObject(context);
        }
    }

    public static UserObject getInstance() {
        // Return the instance
        return instance;
    }

}

package com.mobmedianet.trackergps.Project.Utility;

import android.content.Context;
import android.util.Log;

import com.mobmedianet.trackergps.Project.Objects.AlertCommentObject;
import com.mobmedianet.trackergps.Project.Objects.AlertObject;
import com.mobmedianet.trackergps.Project.Objects.IdNameObject;
import com.mobmedianet.trackergps.Project.Objects.POISObject;
import com.mobmedianet.trackergps.Project.Objects.StopReportObject;
import com.mobmedianet.trackergps.Project.Objects.TravelReportObject;
import com.mobmedianet.trackergps.Project.Objects.UnitObjects;
import com.mobmedianet.trackergps.Project.Objects.UserObject;
import com.mobmedianet.trackergps.Project.Objects.VehicleHistoryObject;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Cesar18 on 11/6/2015.
 */


public class WShelper {

    // Get connection code
    public static ArrayList<NameValuePair> paramsGetConnectionCode(String user) {
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("webServiceUsername", "user1"));
        params.add(new BasicNameValuePair("webServicePassword", "password1"));
        params.add(new BasicNameValuePair("emailAddress", user));
        return params;
    }

    //Authenticate user
    public static ArrayList<NameValuePair> paramsAuthenticateUser(String user, String password, String sProvider, Context context) {
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("webServiceUsername", "user1"));
        params.add(new BasicNameValuePair("webServicePassword", "password1"));
        params.add(new BasicNameValuePair("userName", user));
        params.add(new BasicNameValuePair("userPassword", password));
        params.add(new BasicNameValuePair("companyCode", ""));
        params.add(new BasicNameValuePair("tokenPhone", Utility.getDeviceId(context)));
        params.add(new BasicNameValuePair("PlatformTypeID", "2"));
        params.add(new BasicNameValuePair("ConnectionCode", sProvider));

        return params;
    }

    public static void responseAuthenticateUser(JSONObject post, UserObject userObject, String sProvider) throws JSONException {
        userObject.setNumOfVehicles(post.getInt("NoOfVehicles"));
        userObject.setUserId(post.getInt("UserID"));
        userObject.setCompanyId(post.getInt("CompanyID"));
        userObject.setSubFleetId(post.getInt("SubFleetID"));
        userObject.setNumOfAlerts(post.getInt("NoOfAlerts"));
        userObject.setGroupId(post.getInt("GroupID"));
        userObject.setSprovider(sProvider);
        userObject.setUserType();
    }

    //GetUserAlerts
    public static ArrayList<NameValuePair> paramsGetUserAlerts(UserObject userObject, int pagNumber, int pagSize, String search, int type) {
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("webServiceUsername", "user1"));
        params.add(new BasicNameValuePair("webServicePassword", "password1"));
        params.add(new BasicNameValuePair("userID", String.valueOf(userObject.getUserId())));
        params.add(new BasicNameValuePair("pageIndex", String.valueOf(pagNumber)));
        params.add(new BasicNameValuePair("pageSize", String.valueOf(pagSize)));
        params.add(new BasicNameValuePair("searchText", search));
        params.add(new BasicNameValuePair("ConnectionCode", userObject.getSprovider()));
        if (type != Constants.typeNoFilter)
            params.add(new BasicNameValuePair("eventType", String.valueOf(type)));

        return params;
    }

    public static void responseGetUserAlerts(JSONObject post, ArrayList<AlertObject> dataFiltered, int type) throws JSONException {
        JSONArray alerts_list;
        if (type == Constants.typeNoFilter)
            alerts_list = post.getJSONObject("GetUserAlerts").getJSONArray("DATA");
        else
            alerts_list = post.getJSONObject("GetUserAlertsFiltered").getJSONArray("DATA");

        for (int i = 0; i < alerts_list.length(); i++) {
            AlertObject temp = new AlertObject();
            JSONObject data_temp = alerts_list.getJSONObject(i);
            temp.setID(data_temp.getInt("AlertID"));
            temp.setUnit(data_temp.getString("Unit"));
            temp.setName(data_temp.getString("Alert"));
            temp.setLocate(data_temp.getString("Location"));
            temp.setObjectType(Integer.valueOf(data_temp.getString("ObjectType")));
            temp.setStartTime(Utility.convertDate(data_temp.getString("EventStartTime")));
            if (data_temp.has("EventEndTime"))
                temp.setStopTime(data_temp.getString("EventEndTime"));
            temp.setLon(data_temp.getDouble("Longitude"));
            temp.setLat(data_temp.getDouble("Latitude"));
            temp.setStatus(data_temp.getInt("STATUS"));
            if (data_temp.has("Type"))
                temp.setType(data_temp.getInt("Type"));
            if (data_temp.has("ImageURL"))
                temp.setUrl(data_temp.getString("ImageURL"));
            dataFiltered.add(temp);
        }
    }

    //GetUserAlertComment
    public static ArrayList<NameValuePair> paramsGetUserAlertComment(int alertId, UserObject userObject) {
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("webServiceUsername", "user1"));
        params.add(new BasicNameValuePair("webServicePassword", "password1"));
        params.add(new BasicNameValuePair("alertID", String.valueOf(alertId)));
        params.add(new BasicNameValuePair("ConnectionCode", userObject.getSprovider()));
        return params;
    }

    public static void responseGetUserAlertComment(JSONObject post, ArrayList<AlertCommentObject> data) throws JSONException {

        JSONArray alerts_comments = post.getJSONObject("GetCommentsList").getJSONArray("DATA");

        for (int i = 0; i < alerts_comments.length(); i++) {
            AlertCommentObject temp = new AlertCommentObject();
            JSONObject data_temp = alerts_comments.getJSONObject(i);
            temp.setTitle(data_temp.getString("User"));
            temp.setDate(Utility.convertDate(data_temp.getString("Date")));
            temp.setComment(data_temp.getString("Comment"));
            data.add(temp);
        }
    }

    //SetAlertStatus
    public static ArrayList<NameValuePair> paramsSetAlertStatus(UserObject userObject, AlertObject data, int status) {
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("webServiceUsername", "user1"));
        params.add(new BasicNameValuePair("webServicePassword", "password1"));
        params.add(new BasicNameValuePair("userID", String.valueOf(userObject.getUserId())));
        params.add(new BasicNameValuePair("alertID", String.valueOf(data.getID())));
        params.add(new BasicNameValuePair("status", String.valueOf(status)));
        params.add(new BasicNameValuePair("ConnectionCode", userObject.getSprovider()));
        return params;
    }

    // GetUserPois
    public static ArrayList<NameValuePair> paramsGetUserPois(UserObject userObject, int pagNumber, int Pag_size, String search) {
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("webServiceUsername", "user1"));
        params.add(new BasicNameValuePair("webServicePassword", "password1"));
        params.add(new BasicNameValuePair("userID", String.valueOf(userObject.getUserId())));
        params.add(new BasicNameValuePair("pageIndex", String.valueOf(pagNumber)));
        params.add(new BasicNameValuePair("pageSize", String.valueOf(Pag_size)));
        params.add(new BasicNameValuePair("searchText", search));
        params.add(new BasicNameValuePair("ConnectionCode", userObject.getSprovider()));
        return params;

    }

    public static ArrayList<POISObject> responseGetUserPois(JSONObject post) throws JSONException {
        JSONArray pois_list = post.getJSONObject("GetUserPOIs").getJSONArray("DATA");
        ArrayList<POISObject> data = new ArrayList<>();

        for (int i = 0; i < pois_list.length(); i++) {
            POISObject temp = new POISObject();
            JSONObject data_temp = pois_list.getJSONObject(i);
            temp.setID(data_temp.getInt("ID"));
            temp.setName(data_temp.getString("Name"));
            temp.setCategory(data_temp.getInt("Category"));
            temp.setLatitude(data_temp.getDouble("Latitude"));
            temp.setLongitude(data_temp.getDouble("Longitude"));
            data.add(temp);
        }
        return data;
    }

    // AddPoi
    public static ArrayList<NameValuePair> paramsAddPoi(UserObject userObject, String nametemp, int categoryPosition, int companyID, int subfleeID, int groupID, String mLongtemp, String mLattemp) {
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("webServiceUsername", "user1"));
        params.add(new BasicNameValuePair("webServicePassword", "password1"));
        params.add(new BasicNameValuePair("userID", String.valueOf(userObject.getUserId())));
        params.add(new BasicNameValuePair("Name", nametemp));
        if (categoryPosition != 0)
            params.add(new BasicNameValuePair("CategoryID", String.valueOf(categoryPosition)));
        else
            params.add(new BasicNameValuePair("CategoryID", String.valueOf(-1)));

        params.add(new BasicNameValuePair("companyID", String.valueOf(companyID)));

        params.add(new BasicNameValuePair("subfleetID", String.valueOf(subfleeID)));

        params.add(new BasicNameValuePair("groupID", String.valueOf(groupID)));

        params.add(new BasicNameValuePair("radius", "50"));
        params.add(new BasicNameValuePair("Longitude", mLongtemp));
        params.add(new BasicNameValuePair("Latitude", mLattemp));
        params.add(new BasicNameValuePair("ConnectionCode", userObject.getSprovider()));
        return params;
    }

    //Send Alert Comment
    public static ArrayList<NameValuePair> paramsSendAlertComment(UserObject userObject, int alertId, String comment) {
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("webServiceUsername", "user1"));
        params.add(new BasicNameValuePair("webServicePassword", "password1"));
        params.add(new BasicNameValuePair("userID", String.valueOf(userObject.getUserId())));
        params.add(new BasicNameValuePair("ConnectionCode", userObject.getSprovider()));
        params.add(new BasicNameValuePair("AlertID", String.valueOf(alertId)));
        params.add(new BasicNameValuePair("comment", comment));
        return params;
    }


    //Recover password
    public static ArrayList<NameValuePair> paramsRecoverPassword(String editPassword) {
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("webServiceUsername", "user1"));
        params.add(new BasicNameValuePair("webServicePassword", "password1"));
        params.add(new BasicNameValuePair("userEmail", editPassword));
        return params;
    }

    // Get Companies
    public static ArrayList<NameValuePair> paramsGetCompanies(UserObject userObject,
                                                              int pagNumberCompany, String pageSize) {
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("webServiceUsername", "user1"));
        params.add(new BasicNameValuePair("webServicePassword", "password1"));
        params.add(new BasicNameValuePair("userID", String.valueOf(userObject.getUserId())));
        params.add(new BasicNameValuePair("pageIndex", String.valueOf(pagNumberCompany)));
        params.add(new BasicNameValuePair("pageSize", String.valueOf(pageSize)));
        params.add(new BasicNameValuePair("searchText", ""));
        params.add(new BasicNameValuePair("ConnectionCode", userObject.getSprovider()));
        return params;
    }

    public static void responseGetCompanies(JSONObject post, ArrayList<IdNameObject> companies) throws
            JSONException, IOException {
        JSONArray companies_list;
        companies_list = post.getJSONObject("GetUserCompanies").getJSONArray("DATA");
        for (int i = 0; i < companies_list.length(); i++) {
            IdNameObject temp = new IdNameObject();
            JSONObject data_temp = companies_list.getJSONObject(i);
            temp.setID(data_temp.getInt("ID"));
            temp.setMname(data_temp.getString("Name"));

            companies.add(temp);
        }

    }

    // Get Subfleets
    public static ArrayList<NameValuePair> paramsGetSubfleets(UserObject userObject,
                                                              int pagNumberSubfleet, String pageSize, int companyId) {
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("webServiceUsername", "user1"));
        params.add(new BasicNameValuePair("webServicePassword", "password1"));
        params.add(new BasicNameValuePair("userID", String.valueOf(userObject.getUserId())));
        params.add(new BasicNameValuePair("pageIndex", String.valueOf(pagNumberSubfleet)));
        params.add(new BasicNameValuePair("pageSize", pageSize));
        params.add(new BasicNameValuePair("companyID", String.valueOf(companyId)));
        params.add(new BasicNameValuePair("searchText", ""));
        params.add(new BasicNameValuePair("ConnectionCode", userObject.getSprovider()));
        return params;
    }

    public static void responseGetSubfleets(JSONObject post, ArrayList<IdNameObject> subfleet) throws
            JSONException, IOException {


        JSONArray subfleetList;
        subfleetList = post.getJSONObject("GetUserSubFleets").getJSONArray("DATA");

        for (int i = 0; i < subfleetList.length(); i++) {
            IdNameObject temp = new IdNameObject();
            JSONObject data_temp = subfleetList.getJSONObject(i);
            temp.setID(data_temp.getInt("ID"));
            temp.setMname(data_temp.getString("Name"));

            subfleet.add(temp);
        }


    }

    // getGroups
    public static ArrayList<NameValuePair> paramsGetGroups(UserObject userObject,
                                                           int pagNumberCompany, String pageSize, int subfleetId) {
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("webServiceUsername", "user1"));
        params.add(new BasicNameValuePair("webServicePassword", "password1"));
        params.add(new BasicNameValuePair("userID", String.valueOf(userObject.getUserId())));
        params.add(new BasicNameValuePair("pageIndex", String.valueOf(pagNumberCompany)));
        params.add(new BasicNameValuePair("pageSize", pageSize));
        params.add(new BasicNameValuePair("subfleetID", String.valueOf(subfleetId)));
        params.add(new BasicNameValuePair("searchText", ""));
        params.add(new BasicNameValuePair("ConnectionCode", userObject.getSprovider()));
        return params;
    }

    public static void responseGetGroups(JSONObject post, ArrayList<IdNameObject> groups) throws
            JSONException, IOException {
        JSONArray groupList;
        groupList = post.getJSONObject("GetUserGroups").getJSONArray("DATA");
        for (int i = 0; i < groupList.length(); i++) {
            IdNameObject temp = new IdNameObject();
            JSONObject data_temp = groupList.getJSONObject(i);
            temp.setID(data_temp.getInt("ID"));
            temp.setMname(data_temp.getString("Name"));
            groups.add(temp);
        }


    }

    // getUnitInfo
    public static ArrayList<NameValuePair> paramsGetUnitInfo(UserObject userObject, UnitObjects
            unitData) {
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("webServiceUsername", "user1"));
        params.add(new BasicNameValuePair("webServicePassword", "password1"));
        params.add(new BasicNameValuePair("vehicleID", String.valueOf(unitData.getUnitId())));
        params.add(new BasicNameValuePair("ConnectionCode", userObject.getSprovider()));
        return params;
    }

    public static void responseGetUnitInfo(JSONObject post, UnitObjects unitData) throws JSONException {
        post = post.getJSONObject("GetVehicleData").getJSONObject("DATA");
        unitData.setmLong(post.getDouble("Longitude"));
        unitData.setmLat(post.getDouble("Latitude"));
        unitData.setIgnition(post.getBoolean("Ignition"));
        unitData.setDriver(post.getString("Driver"));
        unitData.setOrientation(post.getString("Heading"));
        unitData.setSpeed(post.getDouble("Speed"));
        unitData.setDriverPhone(post.getString("CellPhone"));
        unitData.setImageUrl(post.getString("imgURL"));
        unitData.setDriverEmail(post.getString("Email"));
        unitData.setObjectType(Integer.valueOf(post.getString("ObjectType")));
    }

    // getUnitList
    public static ArrayList<NameValuePair> paramsGetUnitList(UserObject userObject, int companyId, int subFleetId, int groupId, int unitPageNumber, int pageSize, String search) {
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("webServiceUsername", "user1"));
        params.add(new BasicNameValuePair("webServicePassword", "password1"));
        params.add(new BasicNameValuePair("userID", String.valueOf(userObject.getUserId())));
        params.add(new BasicNameValuePair("companyID", String.valueOf(companyId)));
        params.add(new BasicNameValuePair("subFleetID", String.valueOf(subFleetId)));
        params.add(new BasicNameValuePair("groupID", String.valueOf(groupId)));
        params.add(new BasicNameValuePair("pageIndex", String.valueOf(unitPageNumber)));
        params.add(new BasicNameValuePair("pageSize", String.valueOf(pageSize)));
        params.add(new BasicNameValuePair("searchText", search));
        params.add(new BasicNameValuePair("ConnectionCode", userObject.getSprovider()));
        PrintParams(params);
        return params;
    }

    public static ArrayList<UnitObjects> responseGetUnitList(JSONObject post) throws JSONException {

        JSONArray vehiclesList = post.getJSONObject("GetUserVehicles").getJSONArray("DATA");
        ArrayList<UnitObjects> data = new ArrayList<>();

        for (int i = 0; i < vehiclesList.length(); i++) {
            UnitObjects temp = new UnitObjects();
            JSONObject dataTemp = vehiclesList.getJSONObject(i);
            temp.setUnitId(dataTemp.getInt("ID"));
            temp.setName(dataTemp.getString("Name"));
            if (dataTemp.has("Location"))
                temp.setLocation(dataTemp.getString("Location"));
            temp.setName(dataTemp.getString("Name"));
            temp.setLastTime(Utility.convertDate(dataTemp.getString("LastTime")));
            temp.setObjectType(dataTemp.getInt("ObjectType"));
//            if (dataTemp.has("ImageURL"))
//                temp.setImageUrl(dataTemp.getString("ImageURL"));

            data.add(temp);
        }

        return data;
    }

    // getUserVehiclesNearpoint

    public static ArrayList<NameValuePair> paramsGetUnitListMap(UserObject userObject, String latitude, String longitude, int limit, int distanceRadius) {
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("webServiceUsername", "user1"));
        params.add(new BasicNameValuePair("webServicePassword", "password1"));
        params.add(new BasicNameValuePair("userID", String.valueOf(userObject.getUserId())));
        params.add(new BasicNameValuePair("latitude", latitude));
        params.add(new BasicNameValuePair("longitude", longitude));
        params.add(new BasicNameValuePair("limit", String.valueOf(limit)));
        params.add(new BasicNameValuePair("DistanceRadius", String.valueOf(distanceRadius)));
        params.add(new BasicNameValuePair("ConnectionCode", userObject.getSprovider()));
        PrintParams(params);
        return params;

    }

    public static void responseGetUnitNearPointList(JSONObject post, ArrayList<UnitObjects> data) throws JSONException {

        JSONArray vehiclesList = post.getJSONObject("GetUserVehiclesNearPoint").getJSONArray("DATA");

        for (int i = 0; i < vehiclesList.length(); i++) {
            UnitObjects temp = new UnitObjects();
            JSONObject dataTemp = vehiclesList.getJSONObject(i);
            temp.setUnitId(dataTemp.getInt("ID"));
            temp.setName(dataTemp.getString("Name"));
            temp.setmLong(dataTemp.getDouble("xLong"));
            temp.setmLat(dataTemp.getDouble("yLat"));
//            if (dataTemp.has("ImageURL") && !dataTemp.getString("ImageURL").isEmpty())
//                temp.setImageUrl(dataTemp.getString("ImageURL"));
//            else
//                temp.setImageUrl("");
            data.add(temp);
        }


    }

    // getVehiclesCommands

    public static ArrayList<NameValuePair> paramsGetVehicleCommands(UserObject userObject, int vehicleId) {
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("webServiceUsername", "user1"));
        params.add(new BasicNameValuePair("webServicePassword", "password1"));
        params.add(new BasicNameValuePair("userID", String.valueOf(userObject.getUserId())));
        params.add(new BasicNameValuePair("vehicleID", String.valueOf(vehicleId)));
        params.add(new BasicNameValuePair("ConnectionCode", userObject.getSprovider()));
        return params;
    }

    public static void responseGetVehicleCommands(JSONObject post, ArrayList<IdNameObject> commands) throws
            JSONException, IOException {


        JSONArray commandList;
        commandList = post.getJSONObject("GetVehicleCommands").getJSONArray("DATA");

        for (int i = 0; i < commandList.length(); i++) {
            IdNameObject temp = new IdNameObject();
            JSONObject data_temp = commandList.getJSONObject(i);
            temp.setID(data_temp.getInt("ID"));
            temp.setMname(data_temp.getString("CommandCode"));
            commands.add(temp);
        }


    }

    // sendVehiclesCommands
    public static ArrayList<NameValuePair> paramsSendVehicleCommands(UserObject userObject, int vehicleId, int commandId) {
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("webServiceUsername", "user1"));
        params.add(new BasicNameValuePair("webServicePassword", "password1"));
        params.add(new BasicNameValuePair("userID", String.valueOf(userObject.getUserId())));
        params.add(new BasicNameValuePair("vehicleID", String.valueOf(vehicleId)));
        params.add(new BasicNameValuePair("commandID", String.valueOf(commandId)));
        params.add(new BasicNameValuePair("ConnectionCode", userObject.getSprovider()));
        return params;
    }

    // getTravelReportList
    public static ArrayList<NameValuePair> paramsGetTravelReportList(UserObject userObject, int vehicleID, String day, int pageIndex, int pageSize) {
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("webServiceUsername", "user1"));
        params.add(new BasicNameValuePair("webServicePassword", "password1"));
        params.add(new BasicNameValuePair("vehicleID", String.valueOf(vehicleID)));
        params.add(new BasicNameValuePair("Day", day));
        params.add(new BasicNameValuePair("pageIndex", String.valueOf(pageIndex)));
        params.add(new BasicNameValuePair("pageSize", String.valueOf(pageSize)));
        params.add(new BasicNameValuePair("ConnectionCode", userObject.getSprovider()));
        PrintParams(params);
        return params;
    }

    public static void responseGetTravelReportList(JSONObject post, ArrayList<TravelReportObject> data) throws JSONException {

        JSONArray List = post.getJSONObject("GetDayTripReport").getJSONArray("DATA");

        for (int i = 0; i < List.length(); i++) {
            TravelReportObject temp = new TravelReportObject();
            JSONObject dataTemp = List.getJSONObject(i);

            temp.setLocation(dataTemp.getString("InitialLocation"));
            temp.setLocationFin(dataTemp.getString("FinalLocation"));
            temp.setDate(Utility.convertDate(dataTemp.getString("StartTime")));
            temp.setDateFin(Utility.convertDate(dataTemp.getString("EndTime")));
            temp.setUnitName(dataTemp.getString("Unit"));
            temp.setDuration(dataTemp.getString("Duration"));

            temp.setStopTime((dataTemp.getString("StopTime").isEmpty()) ? "-" : dataTemp.getString("StopTime"));
            temp.setIdleTime(dataTemp.getString("Idle_x0020_Time"));
            temp.setAvgSpeed(dataTemp.getString("AveSpeed"));
            temp.setMaxSpeed(dataTemp.getString("MaxSpeed"));
            temp.setDistance(dataTemp.getString("Distance"));

            data.add(temp);
        }
    }

    //getUserLinks
    public static ArrayList<NameValuePair> paramsGetLink() {
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("webServiceUsername", "user1"));
        params.add(new BasicNameValuePair("webServicePassword", "password1"));
        params.add(new BasicNameValuePair("ConnectionCode", "Sateqmx_QA_PR"));
        return params;
    }

    public static void responseGetLinks(JSONObject post, ArrayList<String> Links) throws JSONException {
        JSONObject List = post.getJSONObject("GetSideMenuUrl").getJSONObject("DATA");
        Links.add(List.getString("News"));
        Links.add(List.getString("Offices"));
        Links.add(List.getString("Promotions"));
        Links.add(List.getString("Contacts"));



    }

    public static void PrintParams(ArrayList<NameValuePair> params) {
        for (int i = 0; i < params.size(); i++) {
            Log.e(params.get(i).getName(), params.get(i).getValue());
        }
    }


    // getVehicleHistory
    public static ArrayList<NameValuePair> paramsGetVehicleHistory(UserObject userObject, int vehicleID, String startDate, String endDate ) {
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("webServiceUsername", "user1"));
        params.add(new BasicNameValuePair("webServicePassword", "password1"));
        params.add(new BasicNameValuePair("vehicleID", String.valueOf(vehicleID)));
        params.add(new BasicNameValuePair("fromDate", Utility.invertDate(startDate)));
        params.add(new BasicNameValuePair("toDate", Utility.invertDate(endDate)));
        params.add(new BasicNameValuePair("ConnectionCode", userObject.getSprovider()));
        PrintParams(params);
        return params;
    }

    public static void responseGetVehicleHistory(JSONObject post, ArrayList<VehicleHistoryObject> data) throws JSONException {

        JSONArray List = post.getJSONObject("GetVehicleHistory").getJSONArray("DATA");

        for (int i = 0; i < List.length(); i++) {
            VehicleHistoryObject temp = new VehicleHistoryObject();
            JSONObject dataTemp = List.getJSONObject(i);

            temp.setLocation(dataTemp.getString("Location"));
            temp.setReportDate(Utility.convertDate(dataTemp.getString("Report_x0020_Time")));
            temp.setStatus(dataTemp.getString("Status"));
            temp.setSpeed(dataTemp.getString("Speed"));
            temp.setHeading(dataTemp.getString("Heading"));
            temp.setLongitud(Double.valueOf(dataTemp.getString("Longitude")));
            temp.setLatitud(Double.valueOf(dataTemp.getString("Latitude")));

            data.add(temp);
        }
    }
    // getStopReportList
    public static ArrayList<NameValuePair> paramsGetStopReportList(UserObject userObject, int vehicleID, String day, int pageIndex, int pageSize) {
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("webServiceUsername", "user1"));
        params.add(new BasicNameValuePair("webServicePassword", "password1"));
        params.add(new BasicNameValuePair("vehicleID", String.valueOf(vehicleID)));
        params.add(new BasicNameValuePair("Day", day));
        params.add(new BasicNameValuePair("pageIndex", String.valueOf(pageIndex)));
        params.add(new BasicNameValuePair("pageSize", String.valueOf(pageSize)));
        params.add(new BasicNameValuePair("ConnectionCode", userObject.getSprovider()));
        PrintParams(params);
        return params;
    }

    public static void responseGetStopReportList(JSONObject post, ArrayList<StopReportObject> data) throws JSONException {

        JSONArray List = post.getJSONObject("GetDayStopReport").getJSONArray("DATA");
        for (int i = 0; i < List.length(); i++) {
            StopReportObject stopReportObject = new StopReportObject();
            JSONObject dataTemp = List.getJSONObject(i);

            stopReportObject.setUnitName(dataTemp.getString("Unit"));
            stopReportObject.setDateIni(Utility.convertDate(dataTemp.getString("Start_Time")));
            stopReportObject.setDateFin(Utility.convertDate(dataTemp.getString("End_Time")));
            stopReportObject.setDuration(dataTemp.getString("Duration"));
            stopReportObject.setLocation(dataTemp.getString("Location"));
            stopReportObject.setLongitud(dataTemp.getString("Longitude"));
            stopReportObject.setLatitud(dataTemp.getString("Latitude"));


            data.add(stopReportObject);
        }
    }

    public static ArrayList<NameValuePair> paramsGetPointsList(UserObject userObject, int vehicleID, String day, int pageSize) {
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("webServiceUsername", "user1"));
        params.add(new BasicNameValuePair("webServicePassword", "password1"));
        params.add(new BasicNameValuePair("vehicleID", String.valueOf(vehicleID)));
        params.add(new BasicNameValuePair("Day", day));
        params.add(new BasicNameValuePair("pageIndex", String.valueOf(1)));
        params.add(new BasicNameValuePair("pageSize", String.valueOf(pageSize)));
        params.add(new BasicNameValuePair("ConnectionCode", userObject.getSprovider()));
        PrintParams(params);
        return params;
    }

    public static void responseGetPointsList(JSONObject post, ArrayList<UnitObjects> data) throws JSONException {

        JSONArray List = post.getJSONObject("GetDayHistory").getJSONArray("DATA");

        for (int i = 0; i < List.length(); i++) {
           UnitObjects temp = new UnitObjects();
            JSONObject dataTemp = List.getJSONObject(i);
            temp.setName(dataTemp.getString("Unit"));
            temp.setmLong(dataTemp.getDouble("Longitude"));
            temp.setmLat(dataTemp.getDouble("Latitude"));
            data.add(temp);
        }


    }


}

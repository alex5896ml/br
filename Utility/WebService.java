package com.mobmedianet.trackergps.Project.Utility;

import android.util.Log;

import org.apache.http.NameValuePair;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

/**
 * Created by Cesar on 7/21/2015.
 */
public class WebService {

    static final int timeOut = 60000;
    public static final String response_SUCCESS = "100";
    static final boolean test = true;
    //static final String URL_Production = "http://wstest.foresightgps.com/SecureTrackServices.asmx";
    static final String URL_Test = "http://mobile2.foresightgps.com/SecureTrackServices1.asmx";
    static final String WS_NAMESPACE = "http://tempuri.org/";

    // WS NAMES
    public static final String login = "AuthenticateUser";
    public static final String provider = "Get_Service_Provider";
    public static final String recover = "RecoverPassword";
    public static final String alerts = "GetUserAlerts";
    public static final String alertsFiltered = "GetUserAlertsFiltered";
    public static final String alertsStatus = "SetAlertStatus";
    public static final String alertsComments = "GetCommentsList";
    public static final String alertsAddComments = "AddComment";
    public static final String pois = "GetUserPOIs";
    public static final String userCompanies = "GetUserCompanies";
    public static final String userSubfleets = "GetUserSubFleets";
    public static final String userGroups = "GetUserGroups";
    public static final String addPOI = "AddPOI";
    public static final String getUnit = "GetUserVehicles";
    public static final String getUnitInfo = "GetVehicleData";
    public static final String getUnitNearPoint = "GetUserVehiclesNearPoint";
    public static final String getVehicleCommands = "GetVehicleCommands";
    public static final String sendVehicleCommands = "SendCommandToVehicle";
    public static final String getTravelReportList = "GetDayTripReport";
    public static final String getStopReportList = "GetDayStopReport";
    public static final String getLinks = "GetSideMenuUrl";
    public static final String getVehicleHistory = "GetVehicleHistory";
    public static final String getPoints = "GetDayHistory";

    public static String Call(String methodName, ArrayList<NameValuePair> params) {

        // PARAMETERS
        SoapObject request = new SoapObject(WS_NAMESPACE, methodName);
        for (int i = 0; i < params.size(); i++) {
            NameValuePair temp = params.get(i);

            PropertyInfo propiedad = new PropertyInfo();
            propiedad.setName(temp.getName());
            propiedad.setValue(temp.getValue());
            propiedad.setType(String.class);
            request.addProperty(propiedad);
        }

        // CALL

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        Log.e("envelope", request.toString());

        String strresult = "";


        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL_Test, timeOut);


        int counter = 0;

        while (counter < 3) {
            try {
                androidHttpTransport.call(WS_NAMESPACE + methodName, envelope);
                counter = 3;
            } catch (Exception e) {

                if (counter < 2) {
                    counter++;
                } else {

                    e.printStackTrace();
                    strresult = "";
                    return strresult;
                }
            }
        }


        // RESPONSE
        try {
            if (envelope.bodyIn instanceof SoapFault) {
                SoapFault error = (SoapFault) envelope.bodyIn;
                Log.e("Error message : ", error.toString());
            }
            SoapObject soResult;
            soResult = (SoapObject) envelope.bodyIn;
            strresult = soResult.toString();
            Log.e(Constants.Tag + "WS", strresult);

        } catch (final ClassCastException e) {
            e.printStackTrace();
            strresult = "";
        } catch (final Exception e) {
            e.printStackTrace();
            strresult = "";
        }
        return strresult;
    }


}
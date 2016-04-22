package com.mobmedianet.trackergps.Project.Fragments;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mobmedianet.trackergps.Project.Objects.AlertObject;
import com.mobmedianet.trackergps.Project.Objects.UserObject;
import com.mobmedianet.trackergps.Project.Utility.Constants;
import com.mobmedianet.trackergps.Project.Utility.Utility;
import com.mobmedianet.trackergps.Project.Utility.WShelper;
import com.mobmedianet.trackergps.Project.Utility.WebService;
import com.mobmedianet.trackergps.R;

import org.apache.http.NameValuePair;

import java.util.ArrayList;


public class AlertDetail extends Fragment implements OnMapReadyCallback {

    /*
    Constants
     */
    private final static String keyData = "data";

    /*
    Views in xml
     */
    private ImageView carImage, alertStatus, alertType;
    private TextView carName, alertText, date, alertStatusText, description, attendAlert, mapTab,commentTab;
    private LinearLayout map, comments;
    private View mapMarker, commentMarker;
    private Marker previousMarker = null;

    /*
    Model
     */
    private AlertObject data;


    public static AlertDetail newInstance(AlertObject data) {
        AlertDetail fragment = new AlertDetail();
        Bundle args = new Bundle();
        args.putSerializable(keyData, data);
        fragment.setArguments(args);
        return fragment;
    }

    public AlertDetail() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            data = (AlertObject) getArguments().getSerializable(keyData);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_alerts_detail, container, false);

        /*
        Finds Views in xml and set listeners
         */
        carImage = (ImageView) v.findViewById(R.id.iv_car);
        alertStatus = (ImageView) v.findViewById(R.id.iv_alertStatus);
        carName = (TextView) v.findViewById(R.id.tv_carName);
        alertText = (TextView) v.findViewById(R.id.tv_alert);
        date = (TextView) v.findViewById(R.id.tv_date);
        alertStatusText = (TextView) v.findViewById(R.id.tv_alert_status);
        description = (TextView) v.findViewById(R.id.tv_alert_description);
        alertType = (ImageView) v.findViewById(R.id.iv_alertType);

        attendAlert = (TextView) v.findViewById(R.id.tv_attend_alert);
        attendAlert.setOnClickListener(new AttendListener());


        map = (LinearLayout) v.findViewById(R.id.ll_map);
        map.setOnClickListener(new MapTabListener());
        comments = (LinearLayout) v.findViewById(R.id.ll_comments);
        comments.setOnClickListener(new CommentTabListener());

        mapTab =(TextView) v.findViewById(R.id.tv_map);
        mapTab.setOnClickListener(new MapTabListener());
        mapTab.setAllCaps(true);




        commentTab=(TextView) v.findViewById(R.id.tv_comments);
        commentTab.setOnClickListener(new CommentTabListener());
        commentTab.setAllCaps(true);

        mapMarker = v.findViewById(R.id.v_maps);
        commentMarker = v.findViewById(R.id.v_comments);

        // set up data
        setupValues();
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        setMap(googleMap);
    }

    // set values for details
    private void setupValues() {

        carImage.setImageResource(data.getImageWithObjecType());
        setImageStatus(data.getStatus());
        alertType.setImageResource(setImageType(data.getType()));
        carName.setText(data.getUnit());
        alertText.setText(data.getName());
        date.setText(data.getStartTime());
        description.setText(data.getLocate());

        if (data.getStatus() == 0) {
            new SetAlertStatus(1).execute();
            alertStatus.setImageDrawable(null);
            alertStatusText.setText(getResources().getString(R.string.alert_read));
        } else if (data.getStatus() == 2) {
            attendAlert.setVisibility(View.GONE);
        }

        // First Fragment
        SupportMapFragment f = new SupportMapFragment();
        f.getMapAsync(AlertDetail.this);
        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame_alerts_detail, f)
                .commit();
        mapMarker.setVisibility(View.VISIBLE);
        commentMarker.setVisibility(View.INVISIBLE);

    }

    // set ImageStatus
    private void setImageStatus(int Status) {
        switch (Status) {
            case 0:
                alertStatus.setImageResource(R.drawable.no_leida);
                alertStatusText.setText(getResources().getString(R.string.alert_unattended));
                break;
            case 1:
                alertStatus.setImageDrawable(null);
                alertStatusText.setText(getResources().getString(R.string.alert_read));
                break;
            case 2:
                alertStatus.setImageResource(R.drawable.recibida);
                alertStatusText.setText(getResources().getString(R.string.alert_attended));
                break;
        }

    }

    // configure map
    private void setMap(final GoogleMap googleMap) {

        if (googleMap != null) {
            Log.e(Constants.Tag + "Setting map", "Map");
            LatLng alert_locate = new LatLng(data.getLat(), data.getLon());
            final MarkerOptions marker = new MarkerOptions().position(alert_locate);
            Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marker_map);

            marker.icon(BitmapDescriptorFactory.fromBitmap(imageBitmap));
            googleMap.addMarker(marker);

            googleMap.setInfoWindowAdapter(new AlertsMarkersInfoAdapter());

//            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//                @Override
//                public boolean onMarkerClick(Marker marker) { //Called when a marker has been clicked or tapped.
//
//                    if(previousMarker!=null){
//                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_map));
//                    }
//                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_pointer));
//                    previousMarker=marker; //Now the clicked marker becomes previousMarker
//                    previousMarker=marker;  //Now the clicked marker becomes previousMarker
//                    return false;
//                }
//            });

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(alert_locate, 15));
        }
    }

    // Attend alert button Listener
    private class AttendListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            new SetAlertStatus(2).execute();


            alertStatusText.setText(getResources().getString(R.string.alert_attended));
            alertStatus.setImageResource(R.drawable.recibida);
            attendAlert.setVisibility(View.GONE);

            int cantAlerts = UserObject.getInstance().getNumOfAlerts();
            UserObject.getInstance().setNumOfAlerts(cantAlerts-1);

            TextView nroAlerts= (TextView)getActivity().findViewById(R.id.tv_nroAlerts);
            ImageView alerts = (ImageView) getActivity().findViewById(R.id.iv_alerts);


            if(UserObject.getInstance().getNumOfAlerts() > 99) {
                nroAlerts.setText("+99");
                nroAlerts.setVisibility(View.VISIBLE);
            }
            else if (UserObject.getInstance().getNumOfAlerts() > 0){
                nroAlerts.setText(String.valueOf(UserObject.getInstance().getNumOfAlerts()));
                nroAlerts.setVisibility(View.VISIBLE);
            }
            else{
                nroAlerts.setVisibility(View.GONE);
                alerts.setImageDrawable(getResources().getDrawable(R.drawable.alert_icon));
                alerts.setPadding(10,10,10,10);
            }

        }
    }

    // MapTabListener
    private class MapTabListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            SupportMapFragment f = new SupportMapFragment();
            f.getMapAsync(AlertDetail.this);
            getFragmentManager().beginTransaction()
                    .replace(R.id.content_frame_alerts_detail, f)
                    .commit();
            mapMarker.setVisibility(View.VISIBLE);
            commentMarker.setVisibility(View.INVISIBLE);

        }
    }

    //CommentTabsListener
    private class CommentTabListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.content_frame_alerts_detail, AlertDetailComments.newInstance(data.getID()))
                    .commit();
            mapMarker.setVisibility(View.INVISIBLE);
            commentMarker.setVisibility(View.VISIBLE);
        }
    }

    //Info window for Marker
    private class AlertsMarkersInfoAdapter implements GoogleMap.InfoWindowAdapter {

        @Override
        public View getInfoWindow(Marker marker) {
            View v = View.inflate(getActivity(), R.layout.marker_info_simple, null);

            TextView lat = (TextView) v.findViewById(R.id.tv_lat);
            lat.setText(getResources().getString(R.string.pois_lat) + "    :   " + data.getLat());
            TextView Long = (TextView) v.findViewById(R.id.tv_long);
            Long.setText(getResources().getString(R.string.pois_long) + "  :   " + data.getLon());
//            ImageView car_image = (ImageView) v.findViewById(R.id.iv_car_image);
//            car_image.setImageDrawable(carImage.getDrawable());
            return v;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }

    // change alert status
    private class SetAlertStatus extends AsyncTask<Void, Void, String> {
        protected Dialog progressDialog;
        protected int status;


        public SetAlertStatus(int status) {
            this.status = status;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = Utility.create_dialog(getActivity(), getActivity().getText(R.string.alerts_Status_dialog).toString());


        }

        @Override
        protected String doInBackground(Void... parametros) {

            String result = "";

            ArrayList<NameValuePair> params = WShelper.paramsSetAlertStatus(UserObject.getInstance(), data, status);
            result = WebService.Call(WebService.alertsStatus, params);


            if (result != null && !result.isEmpty())
                result = result.substring(result.indexOf("=") + 1, result.indexOf(";"));
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {

                progressDialog.dismiss();

            } else {
                Utility.showAlert(getActivity(), getActivity().getString(R.string.conErrorMSG));
            }
        }


    }

    // set Image type
    private int setImageType(int type) {

        switch (type) {
            case -2:
                return R.drawable.panic;
            case 1:
                return R.drawable.zona;
            case 2:
                return R.drawable.area;
            case 3:
                return R.drawable.sensordeentrada;
            case 4:
                return R.drawable.movimiento;
            case 5:
                return R.drawable.excesodevelocidad;
            case 6:
                return R.drawable.reporteretrasado;
            case 7:
                return R.drawable.fueraruta;
            case 8:
                return R.drawable.retraso;
            case 9:
                return R.drawable.mantenimientovehicular;
            case 10:
                return R.drawable.expiraciones;
            case 12:
                return R.drawable.excesodevelocidad;
            case 13:
                return R.drawable.on;
            case 14:
                return R.drawable.off;
            case 15:
                return R.drawable.comienzodeservicio;
            case 16:
                return R.drawable.findeservicio;
            case 17:
                return R.drawable.comienzotardio;
            case 18:
                return R.drawable.mantenimientovehicular;
            case 19:
                return R.drawable.excesoderalenti;
            case 20:
                return R.drawable.odometro;
            case 21:
                return R.drawable.actividadfueradehora;
            case 22:
                return R.drawable.paradalarga;

            default:
                return R.drawable.alertas;
        }

    }


}

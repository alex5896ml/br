package com.mobmedianet.trackergps.Project.Fragments;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.mobmedianet.trackergps.Project.Objects.IdNameObject;
import com.mobmedianet.trackergps.Project.Objects.UnitObjects;
import com.mobmedianet.trackergps.Project.Objects.UserObject;
import com.mobmedianet.trackergps.Project.Utility.Constants;
import com.mobmedianet.trackergps.Project.Utility.GPSTracker;
import com.mobmedianet.trackergps.Project.Utility.LoadingImages;
import com.mobmedianet.trackergps.Project.Utility.Utility;
import com.mobmedianet.trackergps.Project.Utility.WShelper;
import com.mobmedianet.trackergps.Project.Utility.WebService;
import com.mobmedianet.trackergps.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.NonViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Cesar18 on 11/9/2015.
 */
public class UnitSelectorMap extends SupportMapFragment implements OnMapReadyCallback {
    ArrayList<UnitObjects> vehicles = new ArrayList<>();
    private static final int vehiclesLimit = 10;
    private static int vehiclesRadio;
    private GoogleMap map;
    Bitmap imageBitmap;
    String mLat, mLong;
    LatLng firstLocation;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marker_map);
        GPSTracker gps = new GPSTracker(getActivity());

        // check if GPS enabled
        if (gps.canGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            firstLocation = new LatLng(latitude, longitude);
            mLat = String.format(Locale.ENGLISH, "%.5f", latitude);
            mLong = String.format(Locale.ENGLISH, "%.5f", longitude);

            Log.e("prueba localizacion", mLat + " " + mLong + " / " + String.valueOf(latitude) + " " + String.valueOf(longitude));
        } else {
            gps.showSettingsAlert();
        }
        this.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapLongClickListener(new MapClickListener());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 7));
        map.setInfoWindowAdapter(new UnitsMarkersInfoAdapter());
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setAllGesturesEnabled(true);

        getRadio();
        new GetUserVehiclesMap().execute();

    }

    private class GetUserVehiclesMap extends AsyncTask<Void, Void, String> {
        protected Dialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressDialog = Utility.create_dialog(getActivity(), getActivity().getText(R.string.unit_dialog).toString());
        }

        @Override
        protected String doInBackground(Void... parametros) {

            String result = "";
            ArrayList<NameValuePair> params = WShelper.paramsGetUnitListMap(UserObject.getInstance(), mLat, mLong, vehiclesLimit, vehiclesRadio);
            result = WebService.Call(WebService.getUnitNearPoint, params);

            if (result != null && !result.isEmpty())
                result = result.substring(result.indexOf("=") + 1, result.indexOf(";"));
            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);

            if (result != null) {
                JSONObject post;


                try {
                    post = new JSONObject(result);
                    if (post.getString("responseCode").equals(WebService.response_SUCCESS)) {
                        WShelper.responseGetUnitNearPointList(post, vehicles);
                        paintVehicles();

                    } else if (post.getString("responseCode").equals("-900")) {

                        // Utility.showAlert(getActivity(), getActivity().getString(R.string.pois_noCompany));

                    } else {
                        Utility.showAlert(getActivity(), getActivity().getString(R.string.conErrorMSG));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Utility.showAlert(getActivity(), getActivity().getString(R.string.jsonErrorMSG));
                }

                //progressDialog.dismiss();

            }
        }


    }

    private void paintVehicles() {

        for (int i = 0; i < vehicles.size(); i++) {
            Log.e(Constants.Tag + "Setting map", "Map");
            if (!vehicles.get(i).getImageUrl().isEmpty())
                LoadingImages.getInstance().loadImage(vehicles.get(i).getImageUrl(), new ImageListener(new LatLng(vehicles.get(i).getmLat(), vehicles.get(i).getmLong()), i));
            else {
                MarkerOptions marker = new MarkerOptions().position(new LatLng(vehicles.get(i).getmLat(), vehicles.get(i).getmLong())).title(String.valueOf(i));
                Bitmap loadedImage = BitmapFactory.decodeResource(getResources(), R.drawable.nophoto100);
                marker.snippet(String.valueOf(i));
                marker.icon(BitmapDescriptorFactory.fromBitmap(overlay(imageBitmap, loadedImage)));
                Log.e(Constants.Tag + "Setting map", "adding");
                map.addMarker(marker);
            }

        }

    }


    //getRadius
    private void getRadio() {
        VisibleRegion vr = map.getProjection().getVisibleRegion();
        double left = vr.latLngBounds.southwest.longitude;
        double top = vr.latLngBounds.northeast.latitude;
        double right = vr.latLngBounds.northeast.longitude;
        double bottom = vr.latLngBounds.southwest.latitude;

        Location MiddleLeftCornerLocation;
        Location center = new Location("center");
        center.setLatitude(vr.latLngBounds.getCenter().latitude);
        center.setLongitude(vr.latLngBounds.getCenter().longitude);
        MiddleLeftCornerLocation = new Location("left");
        MiddleLeftCornerLocation.setLatitude(center.getLatitude());
        MiddleLeftCornerLocation.setLongitude(left);
        float dis = center.distanceTo(MiddleLeftCornerLocation);
        vehiclesRadio = Math.round(dis);

    }

    // Build custom marker icon
    private static Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Bitmap resizedBitmapmarker = Bitmap.createScaledBitmap(bmp1, 100, 130, false);
        Bitmap resizedBitmapCar = Bitmap.createScaledBitmap(bmp2, 60, 80, false);
        Bitmap bmOverlay = Bitmap.createBitmap(resizedBitmapmarker.getWidth(), resizedBitmapmarker.getHeight(), resizedBitmapmarker.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(resizedBitmapmarker, 0, 0, null);
        canvas.drawBitmap(resizedBitmapCar, canvas.getWidth() / 5, canvas.getHeight() / 10, null);
        return bmOverlay;
    }

    //Listener when image is completed
    private class ImageListener implements ImageLoadingListener {

        LatLng pos;
        int vehiclePosition;

        public ImageListener(LatLng pos, int i) {
            this.pos = pos;
            this.vehiclePosition = i;
        }

        @Override
        public void onLoadingStarted(String imageUri, View view) {

        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            Log.e(Constants.Tag + "Setting map", "failed");
            MarkerOptions marker = new MarkerOptions().position(pos).title(String.valueOf(vehiclePosition));
            marker.icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(imageBitmap, 120, 120, false)));
            map.addMarker(marker);

        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            MarkerOptions marker = new MarkerOptions().position(pos).title(String.valueOf(vehiclePosition));
            marker.icon(BitmapDescriptorFactory.fromBitmap(overlay(imageBitmap, loadedImage)));
            Log.e(Constants.Tag + "Setting map", "adding");
            map.addMarker(marker);
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
            MarkerOptions marker = new MarkerOptions().position(pos).title(String.valueOf(vehiclePosition));
            marker.icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(imageBitmap, 120, 120, false)));
            Log.e(Constants.Tag + "Setting map", "cancelled");
            map.addMarker(marker);
        }
    }

    private class MapClickListener implements GoogleMap.OnMapLongClickListener {

        @Override
        public void onMapLongClick(LatLng latLng) {
            mLat = String.format(Locale.ENGLISH, "%.5f", latLng.latitude);
            mLong = String.format(Locale.ENGLISH, "%.5f", latLng.longitude);
            vehicles.clear();
            map.clear();
            getRadio();
            new GetUserVehiclesMap().execute();
        }
    }

    private class UnitsMarkersInfoAdapter implements GoogleMap.InfoWindowAdapter {

        @Override
        public View getInfoWindow(Marker marker) {
            View v = View.inflate(getActivity(), R.layout.adapter_units_marker_info, null);
            int position = Integer.parseInt(marker.getTitle());
            Log.e("pos", marker.getTitle());
            Log.e("possni", marker.getSnippet());
            TextView title = (TextView) v.findViewById(R.id.tv_title);
            title.setText(vehicles.get(position).getName());
            TextView lat = (TextView) v.findViewById(R.id.tv_lat);
            lat.setText("Latitud" + ": " + vehicles.get(position).getmLat());
            TextView Long = (TextView) v.findViewById(R.id.tv_long);
            Long.setText("Longitud" + ": " + vehicles.get(position).getmLong());

            return v;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }


}

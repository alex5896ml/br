package com.mobmedianet.trackergps.Project.Fragments;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mobmedianet.trackergps.Project.Objects.UnitObjects;
import com.mobmedianet.trackergps.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class UnitsDetailMap extends SupportMapFragment implements OnMapReadyCallback {
    // Constants
    private static final String unitKey = "UnitObject";
    private static final String fragmentTag = "FRAGMENT_VIEWPAGER";

    //model
    private UnitObjects unitData;
    private Bitmap carImage;
    private ArrayList<UnitObjects> data = new ArrayList<>();
    private ArrayList<Marker> markers = new ArrayList<>();
    private Marker initial_marker;


    public static UnitsDetailMap newInstance(UnitObjects object) {
        UnitsDetailMap fragment = new UnitsDetailMap();
        Bundle args = new Bundle();
        args.putSerializable(unitKey, object);
        fragment.setArguments(args);
        return fragment;
    }

    public UnitsDetailMap() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            unitData = (UnitObjects) getArguments().getSerializable(unitKey);
            ImageLoader.getInstance().loadImage(unitData.getImageUrl(), new ImageListener());
        }


    }


    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onPause() {
        super.onPause();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng alert_locate = new LatLng(unitData.getmLat(), unitData.getmLong());
        MarkerOptions marker = new MarkerOptions().position(alert_locate);
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marker_map);
        marker.icon(BitmapDescriptorFactory.fromBitmap(overlay(imageBitmap, carImage)));
        initial_marker = googleMap.addMarker(marker);
        googleMap.setOnMarkerClickListener(new MarkerListener());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(alert_locate, 15));
    }

    //Marker click Listener
    private class MarkerListener implements GoogleMap.OnMarkerClickListener {

        @Override
        public boolean onMarkerClick(Marker marker) {
            UnitsDetail f = (UnitsDetail) getActivity().getSupportFragmentManager().findFragmentByTag(fragmentTag);
            f.changeToDetails();
            return false;
        }
    }

    // Build custom marker icon
    private Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {

        Bitmap resizedBitmapmarker = Bitmap.createScaledBitmap(bmp1, 120, 120, false);
        if (bmp2 == null) {
            bmp2 = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.nophoto100);
        }
        Bitmap resizedBitmapCar = Bitmap.createScaledBitmap(bmp2, 70, 100, false);
        Bitmap bmOverlay = Bitmap.createBitmap(resizedBitmapmarker.getWidth(), resizedBitmapmarker.getHeight(), resizedBitmapmarker.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(resizedBitmapmarker, 0, 0, null);
        canvas.drawBitmap(resizedBitmapCar, canvas.getWidth() / 5, - canvas.getHeight() / 15, null);


        return bmOverlay;
    }

    //Listener when image is completed
    private class ImageListener implements ImageLoadingListener {


        @Override
        public void onLoadingStarted(String imageUri, View view) {

        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            carImage = null;
            UnitsDetailMap.this.getMapAsync(UnitsDetailMap.this);
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            carImage = loadedImage;
            UnitsDetailMap.this.getMapAsync(UnitsDetailMap.this);
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {

        }
    }

    //
    public void createPath(ArrayList<UnitObjects> data) {
        this.data = data;
        markers.clear();
        initial_marker.remove();
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);


        for (int i = 0; i < data.size(); i++) {
            LatLng alert_locate = new LatLng(data.get(i).getmLat(), data.get(i).getmLong());
            options.add(alert_locate);
            MarkerOptions marker = new MarkerOptions().position(alert_locate).title(String.valueOf(i));
            Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marker_map);
            marker.icon(BitmapDescriptorFactory.fromBitmap(overlay(imageBitmap, carImage)));
            marker.snippet(String.valueOf(i));
            Marker markerr = this.getMap().addMarker(marker);
            markers.add(markerr);

        }
        this.getMap().addPolyline(options);
        this.getMap().setInfoWindowAdapter(new UnitsMarkersInfoAdapter());
        changeMarker(0);
    }

    private class UnitsMarkersInfoAdapter implements GoogleMap.InfoWindowAdapter {

        @Override
        public View getInfoWindow(Marker marker) {
            View v = View.inflate(getActivity(), R.layout.adapter_units_marker_info, null);
            int position = Integer.parseInt(marker.getTitle());
            Log.e("pos", marker.getTitle());
            Log.e("possni", marker.getSnippet());
            TextView title = (TextView) v.findViewById(R.id.tv_title);
            title.setText(data.get(position).getName());
            TextView lat = (TextView) v.findViewById(R.id.tv_lat);
            lat.setText("Latitud" + ": " + data.get(position).getmLat());
            TextView Long = (TextView) v.findViewById(R.id.tv_long);
            Long.setText("Longitud" + ": " + data.get(position).getmLong());

            return v;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }

    //
    public void changeMarker(int pos) {
        LatLng alert_locate2 = new LatLng(data.get(pos).getmLat(), data.get(pos).getmLong());
        this.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(alert_locate2, 18));
        markers.get(pos).showInfoWindow();
    }

    //
    public void deleteMarkers() {
        for (int i = 0; i < markers.size(); i++) {
            markers.get(i).remove();
        }
        LatLng alert_locate = new LatLng(unitData.getmLat(), unitData.getmLong());
        MarkerOptions marker = new MarkerOptions().position(alert_locate);
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marker_map);
        marker.icon(BitmapDescriptorFactory.fromBitmap(overlay(imageBitmap, carImage)));
        initial_marker = this.getMap().addMarker(marker);
        this.getMap().setOnMarkerClickListener(new MarkerListener());
        this.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(alert_locate, 15));
    }

    //

}

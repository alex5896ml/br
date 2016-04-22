package com.mobmedianet.trackergps.Project.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobmedianet.trackergps.BuildConfig;
import com.mobmedianet.trackergps.R;

public class About extends Fragment {



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public About() {

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.fragment_about, container, false);
        ImageView facebookButton = (ImageView)v.findViewById(R.id.img_fb);
        ImageView twitterButton = (ImageView)v.findViewById(R.id.img_twitter);
        ImageView instagramButton = (ImageView)v.findViewById(R.id.img_instagram);
        TextView versionCode =(TextView) v.findViewById(R.id.tv_versionCode);


        versionCode.setText(getResources().getString(R.string.about_version) + " " +BuildConfig.VERSION_NAME);


        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/TrackerGPSVe"));
                    startActivity(browserIntent);
            }
        });

        twitterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.twitter.com/TrackerGPS"));
                startActivity(browserIntent);
            }
        });

        instagramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/trackergps"));
                startActivity(browserIntent);
            }
        });








        return v;
    }

}

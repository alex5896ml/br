package com.mobmedianet.trackergps.Project.Utility;

import android.support.v4.widget.SwipeRefreshLayout;

import com.mobmedianet.trackergps.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * Created by Cesar on 9/14/2015.
 */
public class Constants {

    // General
    public static String Tag = "TRACKER GPS ";
    public static DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.nophoto100)
            .showImageOnFail(R.drawable.nophoto100).cacheInMemory(false)
            .cacheOnDisk(false).considerExifParams(true)
            .displayer(new RoundedBitmapDisplayer(1000)).build();

    //Units
    public static final String fragmentTag = "FRAGMENT_VIEWPAGER";
    public static final String UnitKeySource = "Unit";
    public static final String TravelKeySource = "Travel";
    public static final String StopKeySource = "Stop";


    public enum Entity {
        company,
        subfleet,
        group;
    }

    // Alerts
    public final static int typeNoFilter = -4;

    // Testing
    public static final boolean testJson = false;
    public static final boolean endlessList = true;

    // SwipeRefreshLayout

    public static void StyleRefreshLayout(SwipeRefreshLayout mSwipeRefreshLayout) {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.trackerPurple,
                android.R.color.black);
    }
}

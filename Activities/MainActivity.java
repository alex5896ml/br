package com.mobmedianet.trackergps.Project.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ikimuhendis.ldrawer.ActionBarDrawerToggle;
import com.ikimuhendis.ldrawer.DrawerArrowDrawable;
import com.mobmedianet.trackergps.Project.Adapters.NavDrawerListAdapter;
import com.mobmedianet.trackergps.Project.Fragments.About;
import com.mobmedianet.trackergps.Project.Fragments.Alerts;
import com.mobmedianet.trackergps.Project.Fragments.Help;
import com.mobmedianet.trackergps.Project.Fragments.POIS;
import com.mobmedianet.trackergps.Project.Fragments.ReportSelector;
import com.mobmedianet.trackergps.Project.Fragments.UnitsSelector;
import com.mobmedianet.trackergps.Project.Model.NavDrawerItem;
import com.mobmedianet.trackergps.Project.Objects.UserObject;
import com.mobmedianet.trackergps.Project.Utility.Constants;
import com.mobmedianet.trackergps.Project.Utility.Utility;
import com.mobmedianet.trackergps.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;


public class MainActivity extends FragmentActivity {

    private DrawerLayout mDrawerLayout; // Drawer Object
    private ListView mDrawerList; // Right menu
    private ActionBarDrawerToggle mDrawerToggle;
    private ImageView menu, alerts, search; // top buttons
    private Boolean isopen = false; // Boolean to know if Drawer is open or not
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;
    private DrawerArrowDrawable drawerArrow;
    private String urlNews, urlOffice, urlPromotions, urlContacts;
    private UnitsSelector unitsSelector;
    private TextView nroAlerts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        unitsSelector =new UnitsSelector();
        setContentView(R.layout.activity_main);

        urlNews= Utility.get_preferences("URL_News", getBaseContext());
        urlOffice=Utility.get_preferences("URL_Offices", getBaseContext());
        urlPromotions= Utility.get_preferences("URL_Promotions", getBaseContext());
        urlContacts= Utility.get_preferences("URL_Contacts", getBaseContext());

        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        menu = (ImageView) findViewById(R.id.iv_menu);
        alerts = (ImageView) findViewById(R.id.iv_alerts);


        search = (ImageView) findViewById(R.id.iv_search);
        nroAlerts = (TextView) findViewById(R.id.tv_nroAlerts);

        if(UserObject.getInstance().getNumOfAlerts() > 99) {
            nroAlerts.setText("+99");
            nroAlerts.setVisibility(View.VISIBLE);
        }
        else if (UserObject.getInstance().getNumOfAlerts() > 0){

            nroAlerts.setText( String.valueOf(UserObject.getInstance().getNumOfAlerts()));
            nroAlerts.setVisibility(View.VISIBLE);

        }
        else{
            nroAlerts.setVisibility(View.GONE);
            alerts.setPadding(10, 10, 10, 10);
            alerts.setScaleType(ImageView.ScaleType.CENTER_CROP);
            alerts.setImageDrawable(getResources().getDrawable(R.drawable.alert_icon));


    }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        navDrawerItems = new ArrayList<NavDrawerItem>();
        navDrawerItems.add(new NavDrawerItem("",navMenuIcons.getResourceId(0, -1),true,false));
        navDrawerItems.add(new NavDrawerItem(Utility.get_preferences("user",getBaseContext()),navMenuIcons.getResourceId(1, -1),false,true));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(2, -1),false,false));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(3, -1),false,false));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(4, -1),false,false));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(5, -1),false,false));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(6, -1),false,false));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(7, -1),false,false));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(8, -1),false,false));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[7], navMenuIcons.getResourceId(9, -1), false, false));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[8], navMenuIcons.getResourceId(10, -1), false, false));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[9], navMenuIcons.getResourceId(11, -1), false, false));

        navMenuIcons.recycle();
        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        mDrawerLayout.setDrawerListener(new mDrawerListener());
        alerts.setOnClickListener(new AlertsOnClickListener());
        menu.setOnClickListener(new MenuOnClickListener());
//        search.setOnClickListener(new SearchOnClickListener());
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        drawerArrow = new DrawerArrowDrawable(this) {
            @Override
            public boolean isLayoutRtl() {
                return false;
            }
        };

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                drawerArrow, R.string.drawer_open,
                R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        mDrawerToggle.setAnimateEnabled(true);


        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(2);
        }
        // init ImageLoader to use it later in each fragment
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(false).cacheOnDisc(false).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext()).defaultDisplayImageOptions(
                defaultOptions).build();
        ImageLoader.getInstance().init(config);

        firstFragment();

    }

    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
            Log.e("Position", String.valueOf(position));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        Intent browserIntent;
        Boolean isFragment=false;
        switch (position) {
            case 1:
                log_out();
                break;
            case 2:
                fragment = UnitsSelector.newInstance(Constants.UnitKeySource);
                isFragment= true;
                break;
            case 3:
                fragment = new Alerts();
                isFragment= true;
                break;
            case 4:
                fragment = new POIS();
                isFragment= true;
                break;
            case 5:
                fragment = new ReportSelector();
                isFragment= true;
                break;
            case 6:
                openURL(urlNews);
                break;

            case 7:
                openURL(urlOffice);
                break;
            case 8:
                openURL(urlPromotions);
                break;
            case 9:
                openURL(urlContacts);
                break;
            case 10:
                fragment = new Help();
                isFragment= true;
                break;
            case 11:
                fragment = new About();
                isFragment= true;
                break;

            default:
                break;
        }

        if (isFragment) {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();




            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
//            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    private void firstFragment() {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, UnitsSelector.newInstance(Constants.UnitKeySource))
                .commit();

    }

    // Close app
    private void log_out() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.title_logOut);
        alertDialogBuilder.setMessage(getText(R.string.msg_logOut))
                .setCancelable(false);
        alertDialogBuilder.setNegativeButton((R.string.opt_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alertDialogBuilder.setPositiveButton((R.string.opt_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent i = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void openURL(String url){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }
    private void about() {
        Intent i = new Intent(MainActivity.this, About.class);
        startActivity(i);
    }
    // Menu Button listener
    private class MenuOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (isopen) {
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                // Set focus into firts item left menu
                mDrawerList.setSelection(0);

                isopen = false;
            } else {
                mDrawerLayout.openDrawer(Gravity.LEFT);
                mDrawerList.setSelection(0);
//
                isopen = true;
            }
        }
    }

//    public class SearchOnClickListener implements View.OnClickListener {
//
//        @Override
//        public void onClick(View v) {
//           unitsSelector.showBar(getApplicationContext());
//         }
//    }

    // alerts Button listener
    private class AlertsOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new Alerts())
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            super.onBackPressed();
    }

    // Drawer Listener
    private class mDrawerListener implements DrawerLayout.DrawerListener {

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {

        }

        @Override
        public void onDrawerOpened(View drawerView) {
//            menu.setImageResource(R.drawable.menudesplegado2);
        }

        @Override
        public void onDrawerClosed(View drawerView) {

//            menu.setImageResource(R.drawable.menudesplegado2);
        }

        @Override
        public void onDrawerStateChanged(int newState) {

        }
    }

    //get Drawer
    public DrawerLayout getDrawer() {
        return mDrawerLayout;
    }

}

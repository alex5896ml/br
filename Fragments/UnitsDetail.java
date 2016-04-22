package com.mobmedianet.trackergps.Project.Fragments;


import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.mobmedianet.trackergps.Project.Activities.MainActivity;
import com.mobmedianet.trackergps.Project.Objects.UnitObjects;
import com.mobmedianet.trackergps.Project.Objects.UserObject;
import com.mobmedianet.trackergps.Project.Utility.Utility;
import com.mobmedianet.trackergps.Project.Utility.WShelper;
import com.mobmedianet.trackergps.Project.Utility.WebService;
import com.mobmedianet.trackergps.R;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UnitsDetail extends Fragment {

    // Constants
    private static final String unitKey = "unitData";

    //Views

    private ViewPager viewPager;

    // Model

    private MainActivity act;
    private SampleFragmentPagerAdapter adapter;
    private UnitObjects unitData;
    private PagerSlidingTabStrip tabsStrip;


    public static UnitsDetail newInstance(UnitObjects object) {
        UnitsDetail fragment = new UnitsDetail();
        Bundle args = new Bundle();
        args.putSerializable(unitKey, object);
        fragment.setArguments(args);
        return fragment;
    }

    public UnitsDetail() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = (MainActivity) getActivity();
        if (getArguments() != null) {
            unitData = (UnitObjects) getArguments().getSerializable(unitKey);
        } else {
            new GetUnits().execute();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        act.getDrawer().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        // viewPager.invalidate();
        // adapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        act.getDrawer().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //inflate view
        View v = inflater.inflate(R.layout.fragment_unit_info, container, false);
        // find views in xml
        act.getDrawer().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        adapter = new SampleFragmentPagerAdapter(getChildFragmentManager());
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        tabsStrip = (PagerSlidingTabStrip) v.findViewById(R.id.tabs);
        if (unitData != null)
            new GetUnitInfo().execute();
        return v;
    }


    public void changeToDetails() {
        viewPager.setCurrentItem(0);
    }

    public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 3;
        private String tabTitles[] = new String[]{getResources().getString(R.string.tab_unit), getResources().getString(R.string.tab_map), getResources().getString(R.string.tab_reports)};

        public SampleFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return UnitsDetailDetail.newInstance(unitData);

                case 1:
                    return UnitDetailMapParent.newInstance(unitData);

                case 2:
                    return ReportSelector.newInstance(unitData);

                default:
                    return new Fragment();
            }
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }


    }


    private class GetUnitInfo extends AsyncTask<Void, Void, String> {
        protected Dialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = Utility.create_dialog(getActivity(), getActivity().getText(R.string.unit_dialog).toString());


        }

        @Override
        protected String doInBackground(Void... parametros) {

            String result = "";
            ArrayList<NameValuePair> params = WShelper.paramsGetUnitInfo(UserObject.getInstance(), unitData);

            result = WebService.Call(WebService.getUnitInfo, params);

            if (result != null && !result.isEmpty())
                result = result.substring(result.indexOf("=") + 1, result.indexOf(";"));
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            if (result != null) {
                JSONObject post;

                try {
                    post = new JSONObject(result);
                    if (post.getString("responseCode").equals(WebService.response_SUCCESS)) {

                        WShelper.responseGetUnitInfo(post, unitData);


                    } else {
                        Utility.showAlert(getActivity(), getActivity().getString(R.string.conErrorMSG));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Utility.showAlert(getActivity(), getActivity().getString(R.string.jsonErrorMSG));
                }

                progressDialog.dismiss();
                viewPager.setAdapter(adapter);
                viewPager.setCurrentItem(1);
                tabsStrip.setViewPager(viewPager);

            }

        }

    }

    //GetUnitsAsyntask
    private class GetUnits extends AsyncTask<Void, Void, String> {
        protected Dialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = Utility.create_dialog(getActivity(), getActivity().getText(R.string.unit_dialog).toString());


        }

        @Override
        protected String doInBackground(Void... parametros) {

            String result = "";
            ArrayList<NameValuePair> params = WShelper.paramsGetUnitList(UserObject.getInstance(), UserObject.getInstance().getCompanyId(), UserObject.getInstance().getSubFleetId(), UserObject.getInstance().getGroupId(), 1, 10, "");
            result = WebService.Call(WebService.getUnit, params);

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
                        ArrayList<UnitObjects> data = new ArrayList<>();
                        unitData = WShelper.responseGetUnitList(post).get(0);

                        new GetUnitInfo().execute();

                    } else if (post.getString("responseCode").equals("-900")) {


                        Utility.showAlert(getActivity(), getActivity().getString(R.string.unit_nounit));

                    } else {
                        Utility.showAlert(getActivity(), getActivity().getString(R.string.conErrorMSG));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Utility.showAlert(getActivity(), getActivity().getString(R.string.jsonErrorMSG));
                }

                progressDialog.dismiss();


            }

        }
    }
}

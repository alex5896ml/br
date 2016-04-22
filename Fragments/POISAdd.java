package com.mobmedianet.trackergps.Project.Fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.mobmedianet.trackergps.Project.Json.JsonReader;
import com.mobmedianet.trackergps.Project.Objects.IdNameObject;
import com.mobmedianet.trackergps.Project.Objects.POISObject;
import com.mobmedianet.trackergps.Project.Objects.UserObject;
import com.mobmedianet.trackergps.Project.Utility.Constants;
import com.mobmedianet.trackergps.Project.Utility.GPSTracker;
import com.mobmedianet.trackergps.Project.Utility.Utility;
import com.mobmedianet.trackergps.Project.Utility.WShelper;
import com.mobmedianet.trackergps.Project.Utility.WebService;
import com.mobmedianet.trackergps.R;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;


public class POISAdd extends Fragment {

    /*
    Constants
     */

    private static String latLongKey = "LatLong";
    private static String pageSize = "10";

    /*
    Views
     */

    EditText name, mLat, mLong;
    TextView spCategory, spCompany, spSubfleet, spGroup;
    ImageView spCategorySelector, spGroupSelector, spCompanySelector, spSubfleetSelector,selectorPage;
    TextView actualLocal;
    ImageView ok, next;
    LinearLayout ll_coord, ll_category;
    RadioButton page1, page2;
    LinearLayout ll_company, ll_categorypoi, ll_subfleet, ll_group;
    Animation shake;

    /*
    Model
     */

    private LatLng latLong;
    PopupWindow List;
    ListView listView;
    ProgressBar progress;
    int categoryPosition = 0;
    ArrayAdapter<CharSequence> categories;

    ArrayList<IdNameObject> companies = new ArrayList<>();
    IdNameObject companySelected = new IdNameObject();
    ArrayAdapter companyAdapter;
    ArrayList<String> companyNames = new ArrayList<>();
    private int pagNumberCompany = 1;


    ArrayList<IdNameObject> subfleets = new ArrayList<>();
    ArrayList<String> subfleetsNames = new ArrayList<>();
    ArrayAdapter subfleet_adapter;
    IdNameObject subfleetSelected = new IdNameObject();
    private int pagNumberSubfleet = 1;


    ArrayList<IdNameObject> groups = new ArrayList<>();
    IdNameObject groupSelected = new IdNameObject();
    ArrayList<String> groupNames = new ArrayList<>();
    ArrayAdapter group_adapter;
    private int pagNumberGroup = 1;


    public static POISAdd newInstance(LatLng lat) {
        POISAdd fragment = new POISAdd();
        Bundle args = new Bundle();
        args.putParcelable(latLongKey, lat);
        fragment.setArguments(args);
        return fragment;
    }

    public POISAdd() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            latLong = getArguments().getParcelable(latLongKey);
        }
        shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_pois_add, container, false);

        ll_category = (LinearLayout) v.findViewById(R.id.ll_categorias);
        ll_coord = (LinearLayout) v.findViewById(R.id.ll_coordenadas);
        selectorPage = (ImageView) v.findViewById(R.id.imageView14);
        selectorPage.setTag(R.drawable.selector1);
//        selectorPage.setOnClickListener( new selectorPageChangeOnClickListener());
        name = (EditText) v.findViewById(R.id.et_name);
        mLat = (EditText) v.findViewById(R.id.et_lat);
        mLong = (EditText) v.findViewById(R.id.et_long);
        page1 = (RadioButton) v.findViewById(R.id.radio_page1);
        page2 = (RadioButton) v.findViewById(R.id.radio_page2);
        page1.setOnClickListener(new PageSwitcherClickListener());
        page2.setOnClickListener(new PageSwitcherClickListener());

        setLatLongPrev();

        spCategory = (TextView) v.findViewById(R.id.sp_category);
        spCompany = (TextView) v.findViewById(R.id.sp_company);
        spSubfleet = (TextView) v.findViewById(R.id.sp_subFleet);
        spGroup = (TextView) v.findViewById(R.id.sp_group);


        spCategorySelector = (ImageView) v.findViewById(R.id.iv_selector_category);
        spCategorySelector.setOnClickListener(new SelectorListener(0));

        spCompanySelector = (ImageView) v.findViewById(R.id.iv_selector_company);
        spCompanySelector.setOnClickListener(new SelectorListener(1));

        spSubfleetSelector = (ImageView) v.findViewById(R.id.iv_selector_subFleet);
        spSubfleetSelector.setOnClickListener(new SelectorListener(2));

        spGroupSelector = (ImageView) v.findViewById(R.id.iv_selector_group);
        spGroupSelector.setOnClickListener(new SelectorListener(3));

        actualLocal = (TextView) v.findViewById(R.id.tv_Actual_local);
        actualLocal.setOnClickListener(new ActualLocalListener());




        ok = (ImageView) v.findViewById(R.id.go);
        ok.setOnClickListener(new OkListener());
        next = (ImageView) v.findViewById(R.id.next);
        next.setOnClickListener(new PageSwitcherClickListener());
        Log.e("UserType", String.valueOf(UserObject.getInstance().getUserType()));
        ll_categorypoi = (LinearLayout) v.findViewById(R.id.ll_category);
        ll_company = (LinearLayout) v.findViewById(R.id.ll_company);
        ll_subfleet = (LinearLayout) v.findViewById(R.id.ll_subfleet);
        ll_group = (LinearLayout) v.findViewById(R.id.ll_group);




        userLogicVerification();
        initLists();
//        spCategory.setText("Seleccione Categorias");


        return v;
    }


    // user type verification
    void userLogicVerification() {

        spCompanySelector.setClickable(false);
        spCompanySelector.setAlpha((float) 0.5);

        spSubfleetSelector.setClickable(false);
        spSubfleetSelector.setAlpha((float) 0.5);

        spGroupSelector.setClickable(false);
        spGroupSelector.setAlpha((float) 0.5);

        companySelected.setID(UserObject.getInstance().getCompanyId());
        subfleetSelected.setID(UserObject.getInstance().getSubFleetId());
        groupSelected.setID(UserObject.getInstance().getGroupId());

        switch (UserObject.getInstance().getUserType()) {
            case 1:
                spCompanySelector.setClickable(true);
                spCompanySelector.setAlpha((float) 1);

                break;
            case 2:
                spSubfleetSelector.setClickable(true);
                spSubfleetSelector.setAlpha((float) 1);
                ll_company.setVisibility(View.GONE);

                break;
            case 3:
                spGroupSelector.setClickable(true);
                ll_company.setVisibility(View.GONE);
                ll_subfleet.setVisibility(View.GONE);
                spGroupSelector.setAlpha((float) 1);

                break;
            case 4:
                ll_company.setVisibility(View.GONE);
                ll_subfleet.setVisibility(View.GONE);
                ll_group.setVisibility(View.GONE);
                break;
        }

    }

    // set LatLongSelected
    void setLatLongPrev() {
        if (latLong != null) {
            mLat.setText(String.format("%.5f", latLong.latitude));
            mLong.setText(String.format("%.5f", latLong.longitude));
        }
    }

    // init all the list
    void initLists() {

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View v = layoutInflater.inflate(R.layout.adapter_endlesslist, null, false);

        List = new PopupWindow(v,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        listView = (ListView) v.findViewById(R.id.listView1);
        progress = (ProgressBar) v.findViewById(R.id.progressBar);
        List.setOutsideTouchable(true);
        List.setBackgroundDrawable(new BitmapDrawable());
        List.setHeight(400);

    }

    // configure Category dropdown
    void configureCategory() {
        categories = ArrayAdapter.createFromResource(getActivity(), R.array.pois_Categories, R.layout.adapter_alerts_type);
        listView.setAdapter(categories);
        listView.setOnItemClickListener(new PopupListItemClickListener(0));
        List.setOnDismissListener(new PopupListDismissListener(0));
        List.setWidth(spCategory.getWidth());
        List.showAsDropDown(spCategory);
    }

    // configure Company dropdown
    void configureCompanyList() {
        companyNames.clear();
        for (int i = 0; i < companies.size(); i++) {
            companyNames.add(companies.get(i).getMname());
        }
        if (!List.isShowing()) {
            companyAdapter = new ArrayAdapter<>(getActivity(), R.layout.adapter_alerts_type, companyNames);
            listView.setAdapter(companyAdapter);
            listView.setOnItemClickListener(new PopupListItemClickListener(1));
            listView.setOnScrollListener(new EntityEndlessScrollListener(1));

            List.setOnDismissListener(new PopupListDismissListener(1));
            List.setWidth(spCategory.getWidth());
            List.showAsDropDown(spCompany);
        } else {
            if (companyAdapter != null) {
                companyAdapter.notifyDataSetChanged();

            }
        }
    }

    // configure subfleet dropdown
    void configureSubfleetList() {
        subfleetsNames.clear();
        for (int i = 0; i < subfleets.size(); i++) {
            subfleetsNames.add(subfleets.get(i).getMname());
        }
        if (!List.isShowing()) {
            subfleet_adapter = new ArrayAdapter<>(getActivity(), R.layout.adapter_alerts_type, subfleetsNames);
            listView.setAdapter(subfleet_adapter);
            listView.setOnItemClickListener(new PopupListItemClickListener(2));
            listView.setOnScrollListener(new EntityEndlessScrollListener(2));

            List.setOnDismissListener(new PopupListDismissListener(2));
            List.setWidth(spCategory.getWidth());
            List.showAsDropDown(spSubfleet);
        } else {
            if (subfleet_adapter != null) {
                subfleet_adapter.notifyDataSetChanged();

            }
        }
    }

    // configure group dropdown
    void configureGroupList() {
        groupNames.clear();
        for (int i = 0; i < groups.size(); i++) {
            groupNames.add(groups.get(i).getMname());
        }
        if (!List.isShowing()) {
            group_adapter = new ArrayAdapter<>(getActivity(), R.layout.adapter_alerts_type, groupNames);
            listView.setAdapter(group_adapter);
            listView.setOnItemClickListener(new PopupListItemClickListener(3));
            listView.setOnScrollListener(new EntityEndlessScrollListener(3));
            List.setOnDismissListener(new PopupListDismissListener(3));
            List.setWidth(spCategory.getWidth());
            List.showAsDropDown(spGroup);
        } else if (group_adapter != null) {
            group_adapter.notifyDataSetChanged();

        }
    }

    // PopupList ItemClick Listener
    private class PopupListItemClickListener implements AdapterView.OnItemClickListener {
        int mPosition;

        PopupListItemClickListener(int position) {
            this.mPosition = position;

        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (mPosition) {
                case 0:
                    spCategorySelector.setRotation(0);
                    spCategory.setText(categories.getItem(position));
                    categoryPosition = position;

                    break;
                case 1:
                    spCompanySelector.setRotation(0);
                    spCompany.setText(companies.get(position).getMname());
                    companySelected = companies.get(position);
//                    spGroup.setText("-");
                    spSubfleetSelector.setClickable(true);
                    spSubfleetSelector.setAlpha((float) 1);
                    spGroupSelector.setClickable(false);
                    spGroupSelector.setAlpha((float) 0.5);
//                    spSubfleet.setText("Seleccione subflota");
                    pagNumberSubfleet = 1;
                    pagNumberGroup = 1;
                    subfleets.clear();
                    groups.clear();


                    break;
                case 2:
                    spSubfleetSelector.setRotation(0);
                    spSubfleet.setText(subfleets.get(position).getMname());
                    subfleetSelected = subfleets.get(position);
                    pagNumberGroup = 1;
                    spGroupSelector.setClickable(true);
//                    spGroup.setText("Seleccione grupo");
                    spGroupSelector.setAlpha((float) 1);
                    groups.clear();

                    break;
                case 3:
                    spGroupSelector.setRotation(0);
                    spGroup.setText(groups.get(position).getMname());
                    groupSelected = groups.get(position);
                    break;

            }
            List.dismiss();
        }
    }

    // Dismiss Listener
    private class PopupListDismissListener implements PopupWindow.OnDismissListener {
        int position;

        PopupListDismissListener(int position) {
            this.position = position;
        }

        @Override
        public void onDismiss() {
            Runnable mRunnable;
            Handler mHandler = new Handler();
            mRunnable = new Runnable() {

                @Override
                public void run() {
                    switch (position) {
                        case 0:
                            spCategorySelector.setClickable(true);
                            spCategorySelector.setRotation(0);
                            break;
                        case 1:
                            spCompanySelector.setClickable(true);
                            spCompanySelector.setRotation(0);
                            break;
                        case 2:
                            spSubfleetSelector.setClickable(true);
                            spSubfleetSelector.setRotation(0);
                            break;
                        case 3:
                            spGroupSelector.setClickable(true);
                            spGroupSelector.setRotation(0);
                            break;

                    }
                }
            };
            mHandler.postDelayed(mRunnable, 200);//Execute after 0.2 Seconds


        }
    }

    private class  PageSwitcherClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.radio_page1:
                    switchElements(1);
                    break;
                case R.id.radio_page2:
                case R.id.next:
                    if (name.getText().toString().isEmpty())
                        name.startAnimation(shake);
                    else if (mLat.getText().toString().isEmpty())
                        mLat.startAnimation(shake);
                    else if (mLong.getText().toString().isEmpty())
                        mLong.startAnimation(shake);
                    else
                        switchElements(2);
                    break;
            }
        }
    }

    public void switchElements(Integer page){
        switch (page){
            case 1:
                selectorPage.setImageResource(R.drawable.selector1);
                selectorPage.setTag(R.drawable.selector1);
                next.setVisibility(View.VISIBLE);
                ok.setVisibility(View.GONE);
                ll_category.setVisibility(View.GONE);
                ll_coord.setVisibility(View.VISIBLE);
                break;
            case 2:

                selectorPage.setImageResource(R.drawable.selector2);
                selectorPage.setTag(R.drawable.selector2);
                next.setVisibility(View.GONE);
                ok.setVisibility(View.VISIBLE);
                ll_category.setVisibility(View.VISIBLE);
                ll_coord.setVisibility(View.GONE);
                break;
        }
    }



    // ok Button Listener
    private class OkListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            if(spCategory.getText().toString().isEmpty())
                spCategory.startAnimation(shake);
            else if (spCompany.getText().toString().isEmpty() && ll_company.getVisibility() == View.VISIBLE)
                spCompany.startAnimation(shake);
            else if(spSubfleet.getText().toString().isEmpty() && ll_subfleet.getVisibility() ==View.VISIBLE)
                spSubfleet.startAnimation(shake);
            else if(spGroup.getText().toString().isEmpty() && ll_group.getVisibility() ==View.VISIBLE)
                spGroup.startAnimation(shake);
            else
                new Create_POI().execute();
        }
    }

    // Actuallocal Button Listener
    private class ActualLocalListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            GPSTracker gps = new GPSTracker(getActivity());

            // check if GPS enabled
            if (gps.canGetLocation()) {

                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();


                mLat.setText(String.format(Locale.ENGLISH, "%.5f", latitude));
                mLong.setText(String.format(Locale.ENGLISH, "%.5f", longitude));

            } else {
                gps.showSettingsAlert();
            }
        }
    }

    // Selector Button Listener
    private class SelectorListener implements View.OnClickListener {
        private int position;

        SelectorListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            v.setRotation(180);
            v.setClickable(false);

            switch (position) {

                case 0:
                    configureCategory();
                    break;
                case 1:
                    if (companies.isEmpty())
                        new GetUserCompanies().execute();
                    else
                        configureCompanyList();
                    break;
                case 2:
                    if (subfleets.isEmpty())
                        new GetUserSubfleets().execute();
                    else
                        configureSubfleetList();
                    break;
                case 3:
                    if (groups.isEmpty())
                        new GetUserGroups().execute();
                    else
                        configureGroupList();
                    break;

            }

        }
    }

    // Company Asyntask
    private class GetUserCompanies extends AsyncTask<Void, Void, String> {
        protected Dialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = Utility.create_dialog(getActivity(), getActivity().getText(R.string.pois_company_dialog).toString());

        }

        @Override
        protected String doInBackground(Void... parametros) {

            String result;
            ArrayList<NameValuePair> params = WShelper.paramsGetCompanies(UserObject.getInstance(), pagNumberCompany, pageSize);

            result = WebService.Call(WebService.userCompanies, params);

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
                    if (!Constants.testJson)
                        post = new JSONObject(result);
                    else
                        post = new JSONObject(JsonReader.loadJSONFromAsset(getActivity(), R.raw.company));
                    if (post.getString("responseCode").equals(WebService.response_SUCCESS)) {
                        spCompanySelector.setClickable(true);
                        spCompanySelector.setAlpha((float) 1);
                        WShelper.responseGetCompanies(post, companies);

                        if (pagNumberCompany == 1) {
                            if (companies.size() == 1) {
                                companySelected = companies.get(0);
                                spCompany.setText(companySelected.getMname());
                                spCompanySelector.setRotation(0);
                                spCompanySelector.setClickable(false);
                                spCompanySelector.setAlpha((float) 0.5);
                                spSubfleetSelector.setClickable(true);
                                spSubfleetSelector.setAlpha((float) 1);
//                                spSubfleet.setText("seleccione subflota");
                            } else
                                configureCompanyList();
                        } else {
                            configureCompanyList();
                        }


                    } else if (post.getString("responseCode").equals("-900")) {
                        progress.setVisibility(View.GONE);
                        if (pagNumberCompany != 2) {
                            Utility.showAlert(getActivity(), getActivity().getString(R.string.pois_noCompany));
                        }
                        if (pagNumberCompany == 1) {
                            companySelected = new IdNameObject();
                            companySelected.setMname(getString(R.string.filter_noCompany));
                            companySelected.setID(0);
                            spCompany.setText(companySelected.getMname());
                            spCompanySelector.setRotation(0);
                            spCompanySelector.setClickable(false);
                            spCompanySelector.setAlpha((float) 0.5);

                        } else {
                            progress.setVisibility(View.GONE);
                            pagNumberCompany--;
                        }

                    } else {
                        Utility.showAlert(getActivity(), getActivity().getString(R.string.conErrorMSG));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    spCategorySelector.setRotation(0);
                    Utility.showAlert(getActivity(), getActivity().getString(R.string.jsonErrorMSG));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                progressDialog.dismiss();

            }
        }


    }

    // Subfleet Asyntask
    private class GetUserSubfleets extends AsyncTask<Void, Void, String> {
        Dialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            progressDialog = Utility.create_dialog(getActivity(), getActivity().getText(R.string.pois_subfleet_dialog).toString());

        }

        @Override
        protected String doInBackground(Void... parametros) {

            String result;


            ArrayList<NameValuePair> params = WShelper.paramsGetSubfleets(UserObject.getInstance(), pagNumberSubfleet, pageSize, companySelected.getID());

            result = WebService.Call(WebService.userSubfleets, params);

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
                    if (!Constants.testJson)
                        post = new JSONObject(result);
                    else
                        post = new JSONObject(JsonReader.loadJSONFromAsset(getActivity(), R.raw.subfleet));
                    if (post.getString("responseCode").equals(WebService.response_SUCCESS)) {
                        spSubfleetSelector.setClickable(true);
                        spSubfleetSelector.setAlpha((float) 1);
                        WShelper.responseGetSubfleets(post, subfleets);
                        if (pagNumberSubfleet == 1) {


                            if (subfleets.size() == 1) {
                                subfleetSelected = subfleets.get(0);
                                spSubfleet.setText(subfleetSelected.getMname());
                                spSubfleetSelector.setRotation(0);
                                spSubfleetSelector.setClickable(false);
                                spSubfleetSelector.setAlpha((float) 0.5);
                                spGroupSelector.setClickable(true);
                                spGroupSelector.setAlpha((float) 1);
//                                spGroup.setText("Seleccione grupo");
                            } else
                                configureSubfleetList();
                        } else
                            configureSubfleetList();


                    } else if (post.getString("responseCode").equals("-900")) {
                        if (pagNumberSubfleet != 2) {

                            Utility.showAlert(getActivity(), getActivity().getString(R.string.pois_noSubfleet));
                        }
                        if (pagNumberSubfleet == 1) {

                            subfleetSelected = new IdNameObject();
                            subfleetSelected.setMname(getString(R.string.filter_noSubfleet));
                            subfleetSelected.setID(0);
                            spSubfleet.setText(subfleetSelected.getMname());
                            spSubfleetSelector.setRotation(0);
                            spSubfleetSelector.setClickable(false);
                            spSubfleetSelector.setAlpha((float) 0.5);

                        } else {
                            progress.setVisibility(View.GONE);
                            pagNumberSubfleet--;
                        }

                    } else {
                        Utility.showAlert(getActivity(), getActivity().getString(R.string.conErrorMSG));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    spSubfleetSelector.setRotation(0);
                    Utility.showAlert(getActivity(), getActivity().getString(R.string.jsonErrorMSG));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                progressDialog.dismiss();

            }
        }


    }

    // Group Asyntask
    private class GetUserGroups extends AsyncTask<Void, Void, String> {
        protected Dialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = Utility.create_dialog(getActivity(), getActivity().getText(R.string.pois_group_dialog).toString());

        }

        @Override
        protected String doInBackground(Void... parametros) {

            String result;
            ArrayList<NameValuePair> params = WShelper.paramsGetGroups(UserObject.getInstance(), pagNumberGroup, pageSize, subfleetSelected.getID());

            result = WebService.Call(WebService.userGroups, params);

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
                    if (!Constants.testJson)
                        post = new JSONObject(result);
                    else
                        post = new JSONObject(JsonReader.loadJSONFromAsset(getActivity(), R.raw.group));

                    if (post.getString("responseCode").equals(WebService.response_SUCCESS)) {
                        spGroupSelector.setClickable(true);
                        spGroupSelector.setAlpha((float) 1);
                        WShelper.responseGetGroups(post, groups);
                        if (pagNumberGroup == 1) {
                            if (groups.size() == 1) {
                                groupSelected = groups.get(0);
                                spGroup.setText(groupSelected.getMname());
                                spGroupSelector.setRotation(0);
                                spGroupSelector.setClickable(false);
                                spGroupSelector.setAlpha((float) 0.5);
                            } else
                                configureGroupList();
                        } else

                            configureGroupList();


                    } else if (post.getString("responseCode").equals("-900")) {
                        if (pagNumberGroup != 2) {

                            Utility.showAlert(getActivity(), getActivity().getString(R.string.pois_noGroup));
                        }
                        if (pagNumberGroup == 1) {
                            groupSelected = new IdNameObject();
                            groupSelected.setMname(getString(R.string.filter_noGroup));
                            groupSelected.setID(0);
                            spGroup.setText(groupSelected.getMname());
                            spGroupSelector.setRotation(0);
                            spGroupSelector.setClickable(false);
                            spGroupSelector.setAlpha((float) 0.5);
                        } else {
                            progress.setVisibility(View.GONE);
                            pagNumberGroup--;
                        }


                    } else {
                        Utility.showAlert(getActivity(), getActivity().getString(R.string.conErrorMSG));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    spGroupSelector.setRotation(0);
                    Utility.showAlert(getActivity(), getActivity().getString(R.string.jsonErrorMSG));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                progressDialog.dismiss();

            }
        }


    }

    // Create new Poi Asyntask
    private class Create_POI extends AsyncTask<Void, Void, String> {
        protected Dialog progressDialog;

        String nametemp, mLattemp, mLongtemp;

        private void showPoisSuccess() {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle(R.string.app_name);
            alertDialogBuilder.setMessage(getActivity().getString(R.string.pois_add_success))
                    .setCancelable(false);
            alertDialogBuilder.setNegativeButton((R.string.opt_ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            POISObject newPois = new POISObject();
                            newPois.setName(nametemp);
                            newPois.setCategory(categoryPosition);
                            newPois.setLatitude(Double.parseDouble(mLattemp));
                            newPois.setLongitude(Double.parseDouble(mLongtemp));
                            getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            getFragmentManager().beginTransaction()
                                    .replace(R.id.content_frame, POISDetail.newInstance(newPois))
                                    .commit();
                        }
                    });
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = Utility.create_dialog(getActivity(), getActivity().getText(R.string.pois_addPoi_dialog).toString());

            nametemp = name.getText().toString();
            mLattemp = mLat.getText().toString();
            mLongtemp = mLong.getText().toString();
            if (companySelected.getID()==0) companySelected.setID(UserObject.getInstance().getCompanyId());
            if (subfleetSelected.getID()==0) subfleetSelected.setID(UserObject.getInstance().getSubFleetId());
            if (groupSelected.getID()==0) groupSelected.setID(UserObject.getInstance().getGroupId());

        }

        @Override
        protected String doInBackground(Void... parametros) {

            String result;
            ArrayList<NameValuePair> params = WShelper.paramsAddPoi(UserObject.getInstance(), nametemp, categoryPosition, companySelected.getID(), subfleetSelected.getID(), groupSelected.getID(), mLongtemp, mLattemp);
            result = WebService.Call(WebService.addPOI, params);

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

                        showPoisSuccess();
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

    private class EntityEndlessScrollListener implements AbsListView.OnScrollListener {
        private int visibleThreshold = 1;
        public int previousTotal = 0;
        public boolean loading = true;
        public int position;


        public EntityEndlessScrollListener(int position) {

            this.position = position;
        }


        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {

            if (!loading && totalItemCount < previousTotal) {
                previousTotal = 0;
                loading = true;
            }

            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                    progress.setVisibility(View.GONE);

                }
            }
            if (!loading && Constants.endlessList
                    && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {

                switch (position) {
                    case 1:
                        pagNumberCompany++;
                        new GetUserCompanies().execute();
                        break;
                    case 2:
                        pagNumberSubfleet++;
                        new GetUserSubfleets().execute();
                        break;
                    case 3:
                        pagNumberGroup++;
                        new GetUserGroups().execute();
                        break;

                }

                loading = true;
                progress.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
    }

}

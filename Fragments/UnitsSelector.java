package com.mobmedianet.trackergps.Project.Fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.mobmedianet.trackergps.Project.Activities.MainActivity;
import com.mobmedianet.trackergps.Project.Json.JsonReader;
import com.mobmedianet.trackergps.Project.Objects.IdNameObject;
import com.mobmedianet.trackergps.Project.Objects.UserObject;
import com.mobmedianet.trackergps.Project.Utility.Constants;
import com.mobmedianet.trackergps.Project.Utility.Utility;
import com.mobmedianet.trackergps.Project.Utility.WShelper;
import com.mobmedianet.trackergps.Project.Utility.WebService;
import com.mobmedianet.trackergps.R;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


public class UnitsSelector extends Fragment {

    private MainActivity mainActivity;
    //Constants
    private static final String pagSize = "10";
    private static final String paramsNameSource = "source";


    // Views
    private ViewPager viewPager;
    private PagerSlidingTabStrip tabsStrip;
    private ImageView selector,searchButton;
    private TextView spEntity,btnCancel;
    private EditText searchBar;
    private LinearLayout filter, filterSubtitle;
    private RelativeLayout search;
    private TextView filterCompany, filterSubfleet, filterGroup;
    //    private TextView titleCompany, titleSubfleet, titleGroup;
    private LinearLayout ll_titleCompany, ll_titleSubfleet, ll_titleGroup;
    private Button btnClose,btnFilter;
    private ImageView firstDelete, secondDelete, thirdDelete;
    private ImageView filterSwitcher,filterButton;



    // Model
    private int pageNumber = 1;
    private PopupWindow popupList;
    private ProgressBar progress;
    private ArrayAdapter entityAdapter;
    private ArrayList entityString = new ArrayList();
    private SelectionState state;
    private ArrayList<IdNameObject> entityArray = new ArrayList<>();
    private IdNameObject subFleetSelected = new IdNameObject();
    private IdNameObject companySelected = new IdNameObject();
    private IdNameObject groupSelected = new IdNameObject();
    private UnitSelectorList unitList;
    private String source;
    private View v;
    private UserObject mUserObject;


    public static UnitsSelector newInstance(String source) {
        UnitsSelector fragment = new UnitsSelector();
        Bundle b = new Bundle();
        b.putString(paramsNameSource, source);
        fragment.setArguments(b);
        return fragment;
    }

    public UnitsSelector() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        source = getArguments().getString(paramsNameSource);
        unitList = UnitSelectorList.newInstance(source);

        if (source.contentEquals(Constants.UnitKeySource))
            isThereOnlyOneUnit();

    }

    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // find view and set listeners
        v = inflater.inflate(R.layout.fragment_units, container, false);

        search = (RelativeLayout) v.findViewById(R.id.ll_search);
        btnCancel= (TextView) v.findViewById(R.id.textView23);
        btnCancel.setOnClickListener(new CancelOnClickListener());
        searchBar = (EditText) v.findViewById(R.id.et_search);
        searchBar.setOnEditorActionListener(new SearchListener());
        searchBar.addTextChangedListener(textWatcher());
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        tabsStrip = (PagerSlidingTabStrip) v.findViewById(R.id.tabs);
        // unitList = new UnitSelectorList();
        viewPager.setAdapter(new SampleFragmentPagerAdapter(getChildFragmentManager()));
        tabsStrip.setViewPager(viewPager);
        searchButton= (ImageView)getActivity().findViewById(R.id.iv_search);
        searchButton.setVisibility(View.VISIBLE);
        searchButton.setOnClickListener(new SearchOnClickListener());
        filterButton= (ImageView)v.findViewById(R.id.iv_filter);
        filterButton.setOnClickListener(new FilterOnClickListener());
        filterButton.setVisibility((mUserObject.getInstance().getUserType() == 4) ? View.GONE : View.VISIBLE);


        return v;
    }


    private enum SelectionState {
        selectingCompany,
        selectingSubFleet,
        selectingGroup,
        nothingToSelect;
    }
    private class SearchOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            View view = getView();
            if(view != null)
                search.setVisibility((search.getVisibility()==View.GONE) ? View.VISIBLE: View.GONE);
        }
    }
    private class CancelOnClickListener implements View.OnClickListener {

        @Override

        public void onClick(View v) {

            if (searchBar.getText().length()>0){
                searchBar.setText("");
                cleanVariableObject();
                unitList.getUnits(searchBar.getText().toString(), true,  mUserObject.getInstance().getCompanyId(), mUserObject.getInstance().getSubFleetId(),  mUserObject.getInstance().getGroupId());
            }
       }
    }
    private class FilterOnClickListener implements View.OnClickListener {

        @Override

        public void onClick(View v) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

            LayoutInflater inflater = getActivity().getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.popup_filters, null);
            dialogBuilder.setView(dialogView);
            spEntity = (TextView) dialogView.findViewById(R.id.sp_entity);
            filterSubtitle= (LinearLayout)dialogView.findViewById(R.id.ll_filterSubtitle);
            selector = (ImageView) dialogView.findViewById(R.id.iv_selector);
            ll_titleCompany = (LinearLayout) dialogView.findViewById(R.id.ll_titleCompany);
            filterCompany = (TextView) dialogView.findViewById(R.id.company);
            ll_titleSubfleet = (LinearLayout) dialogView.findViewById(R.id.ll_titleSubfleet);
            filterSubfleet = (TextView) dialogView.findViewById(R.id.subfleet);
            ll_titleGroup = (LinearLayout) dialogView.findViewById(R.id.ll_titleGroup);
            filterGroup = (TextView) dialogView.findViewById(R.id.group);
            firstDelete = (ImageView) dialogView.findViewById(R.id.back1);
            secondDelete = (ImageView) dialogView.findViewById(R.id.back2);
            thirdDelete = (ImageView) dialogView.findViewById(R.id.back3);
            btnClose =(Button) dialogView.findViewById(R.id.btn_close);
            btnFilter =(Button) dialogView.findViewById(R.id.btn_filter);

            firstDelete.setOnClickListener(new DeleteListener());
            secondDelete.setOnClickListener(new DeleteListener());
            thirdDelete.setOnClickListener(new DeleteListener());
            selector.setOnClickListener(new SelectorListener());

            final AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.setCanceledOnTouchOutside(false);
//            alertDialog.getWindow().setLayout(getResources().getDimensionPixelSize(R.dimen.dialog_width), 700); //Controlling width and height.


            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    firstDelete.callOnClick();
                    secondDelete.callOnClick();
                    thirdDelete.callOnClick();
                    alertDialog.dismiss();
                }
            });
            btnFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    unitList.cleandata();
                    unitList.getUnits(searchBar.getText().toString(), true, companySelected.getID(), subFleetSelected.getID(), groupSelected.getID());
//                    mUserObject.getInstance().setCompanyId(companySelected.getID());
//                    mUserObject.getInstance().setSubFleetId(subFleetSelected.getID());
//                    mUserObject.getInstance().setGroupId(groupSelected.getID());
                    firstDelete.callOnClick();
                    secondDelete.callOnClick();
                    thirdDelete.callOnClick();
                    alertDialog.dismiss();
                }
            });
            setupFilter();
            alertDialog.show();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        cleanVariableObject();
        searchButton.setVisibility(View.GONE);
    }

    public void cleanVariableObject(){
//        mUserObject.getInstance().setCompanyId();
//        mUserObject.getInstance().setSubFleetId(0);
//        mUserObject.getInstance().setGroupId(0);
    }

    //check if there is only one unit
    private boolean isThereOnlyOneUnit() {
        if (UserObject.getInstance().getNumOfVehicles() == 1) {

            getFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new UnitsDetail(), Constants.fragmentTag)
                    .commit();
            return true;
        } else
            return false;
    }


    //init popuplist

    private void initPopUpList() {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View v = layoutInflater.inflate(R.layout.adapter_endlesslist, null, false);
        popupList = new PopupWindow(v,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

        ListView listView = (ListView) v.findViewById(R.id.listView1);
        entityAdapter = new ArrayAdapter(getActivity(), R.layout.adapter_alerts_type, entityString);
        listView.setAdapter(entityAdapter);
        progress = (ProgressBar) v.findViewById(R.id.progressBar);
        listView.setOnItemClickListener(new EntityListOnClickListener());
        listView.setOnScrollListener(new EntityEndlessScrollListener());
        popupList.setOnDismissListener(new EntityListOnDismissListener());
        popupList.setWidth(spEntity.getWidth());
        popupList.setHeight(700);
        popupList.setBackgroundDrawable(new BitmapDrawable());
        popupList.setOutsideTouchable(true);
        popupList.showAsDropDown(spEntity, 0, 0);
    }

    //Filter Setup
    private void setupFilter() {

        //setup filter position position
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

       /* LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins((int) (metrics.widthPixels / 10), 0, 0, 0);
        filterCompany.setLayoutParams(layoutParams);
        filterSubfleet.setLayoutParams(layoutParams);
        filterGroup.setLayoutParams(layoutParams);
        */

        // show or hide filter
        mUserObject = UserObject.getInstance();
//        int userType = mUserObject.getUserType();

//        filterButton.setVisibility((userType == 4) ?View.GONE:View.VISIBLE);

        companySelected.setID(mUserObject.getCompanyId());
        subFleetSelected.setID(mUserObject.getSubFleetId());
        groupSelected.setID(mUserObject.getGroupId());

        switch (UserObject.getInstance().getUserType()) {
            case 1:
                state = SelectionState.selectingCompany;
                spEntity.setText(getResources().getString(R.string.filter_selectCompany));
                break;
            case 2:
                state = SelectionState.selectingSubFleet;
                spEntity.setText(getResources().getString(R.string.filter_selectSubfleet));
                break;
            case 3:
                state = SelectionState.selectingGroup;
                spEntity.setText(getResources().getString(R.string.filter_selectGroup));
                break;
        }
    }

    //enable selector
    private void enableSelector() {
        selector.setEnabled(true);
        selector.setAlpha((float) 1);
    }

    // set up filter List Popup
    private void configureList(ArrayList<IdNameObject> arr) {
        entityString.clear();
        for (IdNameObject temp : arr) {
            entityString.add(temp.getMname());
        }
        if (popupList == null) {
            initPopUpList();

        } else {
            entityAdapter.notifyDataSetChanged();
            popupList.showAsDropDown(spEntity, 0, 0);
        }
    }

    //One element case
    private void oneElement(Constants.Entity entity) {
        switch (entity) {
            case company:
                filterSubtitle.setVisibility(View.GONE);
                btnFilter.setVisibility(View.GONE);
                ll_titleCompany.setVisibility(View.VISIBLE);
                filterCompany.setVisibility(View.VISIBLE);
                filterCompany.setText((String) entityArray.get(0).getMname());
                state = SelectionState.selectingSubFleet;
                firstDelete.setVisibility(View.VISIBLE);
                companySelected = entityArray.get(0);
                entityArray.clear();
                spEntity.setText(getResources().getString(R.string.filter_selectSubfleet));
                spEntity.setVisibility(View.VISIBLE);
                selector.setVisibility(View.VISIBLE);
                break;
            case subfleet:
                filterSubtitle.setVisibility(View.GONE);
                filterSubfleet.setVisibility(View.VISIBLE);
                btnFilter.setVisibility(View.GONE);
                ll_titleSubfleet.setVisibility(View.VISIBLE);
                filterSubfleet.setText((String) entityArray.get(0).getMname());
                state = SelectionState.selectingGroup;
                firstDelete.setVisibility(View.GONE);
                secondDelete.setVisibility(View.VISIBLE);
                subFleetSelected = entityArray.get(0);
                spEntity.setText(getResources().getString(R.string.filter_selectGroup));
                spEntity.setVisibility(View.VISIBLE);
                selector.setVisibility(View.VISIBLE);
                entityArray.clear();
                selector.setRotation(0);
                break;
            case group:
                filterSubtitle.setVisibility(View.GONE);
                filterGroup.setVisibility(View.VISIBLE);
                ll_titleGroup.setVisibility(View.VISIBLE);
                filterGroup.setText((String) entityArray.get(0).getMname());
                state = SelectionState.nothingToSelect;
                thirdDelete.setVisibility(View.VISIBLE);
                secondDelete.setVisibility(View.GONE);
                groupSelected = entityArray.get(0);
//                spEntity.setText(groupSelected.getMname());
                spEntity.setVisibility(View.GONE);
                selector.setVisibility(View.GONE);
                selector.setRotation(0);
                break;
        }

//        unitList.cleandata();
//        unitList.getUnits(searchBar.getText().toString(), true, companySelected.getID(), subFleetSelected.getID(), groupSelected.getID());

    }

    //No element case
    private void noElement(Constants.Entity entity) {
        switch (entity) {
            case company:
                filterSubtitle.setVisibility(View.GONE);
                ll_titleCompany.setVisibility(View.VISIBLE);
                filterCompany.setVisibility(View.VISIBLE);
                filterCompany.setText(getResources().getString(R.string.filter_noCompany));
                state = SelectionState.selectingSubFleet;
                firstDelete.setVisibility(View.VISIBLE);
                companySelected = new IdNameObject();
                companySelected.setMname(getResources().getString(R.string.filter_noCompany));
                companySelected.setID(0);

                spEntity.setText(companySelected.getMname());
                selector.setRotation(0);
                selector.setEnabled(false);
                selector.setAlpha((float) 0.5);
                break;
            case subfleet:
                ll_titleSubfleet.setVisibility(View.VISIBLE);
                filterSubfleet.setVisibility(View.VISIBLE);
                filterSubfleet.setText(getResources().getString(R.string.filter_noSubfleet));
                state = SelectionState.selectingGroup;
                firstDelete.setVisibility(View.GONE);
                secondDelete.setVisibility(View.VISIBLE);
                subFleetSelected = new IdNameObject();
                subFleetSelected.setMname(getResources().getString(R.string.filter_noSubfleet));
                subFleetSelected.setID(0);
                spEntity.setText(subFleetSelected.getMname());
                selector.setEnabled(false);
                selector.setRotation(0);
                selector.setAlpha((float) 0.51);
                break;
            case group:
                ll_titleGroup.setVisibility(View.VISIBLE);
                filterGroup.setVisibility(View.VISIBLE);
                filterGroup.setText(getResources().getString(R.string.filter_noGroup));
                state = SelectionState.nothingToSelect;
                thirdDelete.setVisibility(View.VISIBLE);
                secondDelete.setVisibility(View.GONE);
                groupSelected = new IdNameObject();
                groupSelected.setID(0);
                groupSelected.setMname(getResources().getString(R.string.filter_noGroup));
                spEntity.setVisibility(View.GONE);
                selector.setVisibility(View.GONE);
                selector.setRotation(0);
                selector.setAlpha((float) 0.5);
                break;
        }
    }

    //Switcher Click listener
    private class SwitcherListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (filter.getVisibility() == View.VISIBLE)
                filter.setVisibility(View.GONE);
            else
                filter.setVisibility(View.VISIBLE);
        }
    }

    //Delete Button listener
    private class DeleteListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            entityArray.clear();
            pageNumber = 1;
            enableSelector();
            switch (state) {
                case selectingSubFleet:
                    filterSubtitle.setVisibility(View.VISIBLE);
                    btnFilter.setVisibility(View.GONE);
                    ll_titleCompany.setVisibility(View.GONE);
                    filterCompany.setVisibility(View.GONE);
                    firstDelete.setVisibility(View.GONE);
                    companySelected = new IdNameObject();
                    companySelected.setID(0);
                    state = SelectionState.selectingCompany;
                    spEntity.setText(getResources().getString(R.string.filter_selectCompany));
                    break;
                case selectingGroup:
                    filterSubtitle.setVisibility(View.GONE);
                    ll_titleSubfleet.setVisibility(View.GONE);
                    filterSubfleet.setVisibility(View.GONE);
                    secondDelete.setVisibility(View.GONE);
                    subFleetSelected = new IdNameObject();
                    subFleetSelected.setID(0);
                    state = SelectionState.selectingSubFleet;
                    spEntity.setText(getResources().getString(R.string.filter_selectSubfleet));

                    if (UserObject.getInstance().getUserType() < 2)
                        firstDelete.setVisibility(View.VISIBLE);

                    break;
                case nothingToSelect:
                    ll_titleGroup.setVisibility(View.GONE);
                    filterGroup.setVisibility(View.GONE);
                    thirdDelete.setVisibility(View.GONE);
                    groupSelected = new IdNameObject();
                    groupSelected.setID(0);
                    state = SelectionState.selectingGroup;
                    spEntity.setText(getResources().getString(R.string.filter_selectGroup));
                    spEntity.setVisibility(View.VISIBLE);
                    selector.setVisibility(View.VISIBLE);
                    if (UserObject.getInstance().getUserType() < 3)
                        secondDelete.setVisibility(View.VISIBLE);
                    break;
            }
//
//            unitList.cleandata();
//            unitList.getUnits(searchBar.getText().toString(), true, companySelected.getID(), subFleetSelected.getID(), groupSelected.getID());
        }
    }

    // selector button listener
    private class SelectorListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            v.setRotation(180);
            if (entityArray.isEmpty()) {
                switch (state) {
                    case selectingCompany:
                        new GetUserCompanies().execute();
                        break;
                    case selectingSubFleet:
                        new GetUserSubfleets().execute();
                        break;
                    case selectingGroup:
                        new GetUserGroups().execute();
                        break;
                    case nothingToSelect:
                        configureList(entityArray);
                        break;

                }
            } else {
                configureList(entityArray);
            }
        }
    }

    // Search Bar Listener
    private class SearchListener implements TextView.OnEditorActionListener {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                unitList.cleandata();
                unitList.getUnits(searchBar.getText().toString(), true, companySelected.getID(), subFleetSelected.getID(), groupSelected.getID());
                Utility.hideKeyboard(v, getActivity());
                return true;
            }
            return false;
        }
    }

    private TextWatcher textWatcher() {
        return new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!searchBar.getText().toString().equals("")) { //if edittext include text
                    btnCancel.setTextColor(getResources().getColor(R.color.trackerWhite));
                } else { //not include text
                    btnCancel.setTextColor(getResources().getColor(R.color.trackerGrey));
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
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

            String result = "";
            ArrayList<NameValuePair> params = WShelper.paramsGetCompanies(UserObject.getInstance(), pageNumber, pagSize);
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
                    if (Constants.testJson == false)
                        post = new JSONObject(result);
                    else
                        post = new JSONObject(JsonReader.loadJSONFromAsset(getActivity(), R.raw.company));


                    if (post.getString("responseCode").equals(WebService.response_SUCCESS)) {

                        WShelper.responseGetCompanies(post, entityArray);
                        if (pageNumber == 1 && entityArray.size() == 1)
                            oneElement(Constants.Entity.company);
                        else
                            configureList(entityArray);

                    } else if (post.getString("responseCode").equals("-900")) {

                        if (pageNumber != 2)
                            Utility.showAlert(getActivity(), getActivity().getString(R.string.pois_noCompany));
                        if (pageNumber == 1)
                            noElement(Constants.Entity.company);
                        else
                            progress.setVisibility(View.GONE);


                    } else {
                        Utility.showAlert(getActivity(), getActivity().getString(R.string.conErrorMSG));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    selector.setRotation(0);
                    Utility.showAlert(getActivity(), getActivity().getString(R.string.jsonErrorMSG));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                progressDialog.dismiss();

            }
        }


    }

    // GetUserSubfleets AsynTask
    private class GetUserSubfleets extends AsyncTask<Void, Void, String> {
        Dialog progressDialog;


        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            progressDialog = Utility.create_dialog(getActivity(), getActivity().getText(R.string.pois_subfleet_dialog).toString());

        }

        @Override
        protected String doInBackground(Void... parametros) {

            String result = "";
            ArrayList<NameValuePair> params = WShelper.paramsGetSubfleets(UserObject.getInstance(), pageNumber, pagSize, companySelected.getID());
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
                    if (Constants.testJson == false)
                        post = new JSONObject(result);
                    else
                        post = new JSONObject(JsonReader.loadJSONFromAsset(getActivity(), R.raw.subfleet));
                    if (post.getString("responseCode").equals(WebService.response_SUCCESS)) {
                        WShelper.responseGetSubfleets(post, entityArray);
                        if (pageNumber == 1 && entityArray.size() == 1)
                            oneElement(Constants.Entity.subfleet);
                        else
                            configureList(entityArray);

                    } else if (post.getString("responseCode").equals("-900")) {
                        if (pageNumber != 2)
                            Utility.showAlert(getActivity(), getActivity().getString(R.string.pois_noSubfleet));

                        if (pageNumber == 1)
                            noElement(Constants.Entity.subfleet);
                        else
                            progress.setVisibility(View.GONE);


                    } else {
                        Utility.showAlert(getActivity(), getActivity().getString(R.string.conErrorMSG));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    selector.setRotation(0);
                    Utility.showAlert(getActivity(), getActivity().getString(R.string.jsonErrorMSG));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                progressDialog.dismiss();


            }
        }


    }

    //GetUserGroups AsynTask
    private class GetUserGroups extends AsyncTask<Void, Void, String> {
        protected Dialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = Utility.create_dialog(getActivity(), getActivity().getText(R.string.pois_group_dialog).toString());

        }

        @Override
        protected String doInBackground(Void... parametros) {

            String result = "";
            ArrayList<NameValuePair> params = WShelper.paramsGetGroups(UserObject.getInstance(), pageNumber, pagSize, subFleetSelected.getID());
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
                    if (Constants.testJson == false)
                        post = new JSONObject(result);
                    else
                        post = new JSONObject(JsonReader.loadJSONFromAsset(getActivity(), R.raw.group));

                    if (post.getString("responseCode").equals(WebService.response_SUCCESS)) {

                        WShelper.responseGetGroups(post, entityArray);

                        if (pageNumber == 1 && entityArray.size() == 1)
                            oneElement(Constants.Entity.group);
                        else
                            configureList(entityArray);

                    } else if (post.getString("responseCode").equals("-900")) {

                        if (pageNumber != 2)
                            Utility.showAlert(getActivity(), getActivity().getString(R.string.pois_noGroup));

                        if (pageNumber == 1)
                            noElement(Constants.Entity.group);
                        else
                            progress.setVisibility(View.GONE);


                    } else {
                        Utility.showAlert(getActivity(), getActivity().getString(R.string.conErrorMSG));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    selector.setRotation(0);
                    Utility.showAlert(getActivity(), getActivity().getString(R.string.jsonErrorMSG));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                progressDialog.dismiss();

            }
        }


    }

    // Entity List listener
    private class EntityListOnClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selector.setRotation(0);
            switch (state) {
                case nothingToSelect:
                    filterGroup.setText((String) entityAdapter.getItem(position));
                    groupSelected = entityArray.get(position);
                    spEntity.setText(groupSelected.getMname());
                    break;

                case selectingGroup:
                    btnFilter.setVisibility(View.VISIBLE);
//                    filterSubtitle.setVisibility(View.GONE);
                    filterGroup.setVisibility(View.VISIBLE);
                    ll_titleGroup.setVisibility(View.VISIBLE);
                    filterGroup.setText((String) entityAdapter.getItem(position));
                    state = SelectionState.nothingToSelect;
                    thirdDelete.setVisibility(View.VISIBLE);
                    secondDelete.setVisibility(View.GONE);
                    groupSelected = entityArray.get(position);
                    spEntity.setText(groupSelected.getMname());
                    spEntity.setVisibility(View.GONE);
                    selector.setVisibility(View.GONE);
                    break;

                case selectingSubFleet:
                    filterSubfleet.setVisibility(View.VISIBLE);
                    ll_titleSubfleet.setVisibility(View.VISIBLE);
                    btnFilter.setVisibility(View.VISIBLE);
                    filterSubfleet.setText((String) entityAdapter.getItem(position));
                    state = SelectionState.selectingGroup;
                    firstDelete.setVisibility(View.GONE);
                    secondDelete.setVisibility(View.VISIBLE);
                    subFleetSelected = entityArray.get(position);
                    spEntity.setText(getResources().getString(R.string.filter_selectGroup));
                    entityArray.clear();
                    pageNumber = 1;
                    break;

                case selectingCompany:
                    filterSubtitle.setVisibility(View.GONE);
                    btnFilter.setVisibility(View.VISIBLE);
                    filterCompany.setVisibility(View.VISIBLE);
                    ll_titleCompany.setVisibility(View.VISIBLE);
                    filterCompany.setText((String) entityAdapter.getItem(position));
                    state = SelectionState.selectingSubFleet;
                    firstDelete.setVisibility(View.VISIBLE);
                    companySelected = entityArray.get(position);
                    spEntity.setText(getResources().getString(R.string.filter_selectSubfleet));
                    entityArray.clear();
                    pageNumber = 1;
                    break;


            }
//            unitList.cleandata();
//            unitList.getUnits(searchBar.getText().toString(), true, companySelected.getID(), subFleetSelected.getID(), groupSelected.getID());

            if (state != SelectionState.nothingToSelect)
                entityArray.clear();
            popupList.dismiss();


        }
    }

    // dismiss Entity List  listener
    private class EntityListOnDismissListener implements PopupWindow.OnDismissListener {
        @Override
        public void onDismiss() {
            selector.setRotation(0);
            Runnable mRunnable;
            Handler mHandler = new Handler();
            mRunnable = new Runnable() {

                @Override
                public void run() {
                    selector.setClickable(true);
                }
            };
            mHandler.postDelayed(mRunnable, 200);//Execute after 0.2 Seconds
        }
    }

    // ViewPager adapter
    public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 2;

        private String tabTitles[] = new String[]{getResources().getString(R.string.tab_list), getResources().getString(R.string.tab_map)};

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
                    return unitList;
                default:
                    return new UnitSelectorMap();
            }
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }


    }

    // class to model Endless List
    private class EntityEndlessScrollListener implements AbsListView.OnScrollListener {
        private int visibleThreshold = 1;
        private int previousTotal = 0;
        private boolean loading = true;


        public EntityEndlessScrollListener() {

        }


        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {

            if (totalItemCount < previousTotal) {
                this.previousTotal = totalItemCount;
                this.loading = true;

            }

            if (loading && (totalItemCount > previousTotal)) {

                loading = false;
                previousTotal = totalItemCount;
                pageNumber++;
                progress.setVisibility(View.GONE);


            }
            if (!loading && Constants.endlessList
                    && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {

                switch (state) {
                    case selectingCompany:
                        new GetUserCompanies().execute();
                        break;
                    case selectingSubFleet:
                        new GetUserSubfleets().execute();
                        break;
                    case selectingGroup:
                        new GetUserGroups().execute();
                        break;
                    case nothingToSelect:
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




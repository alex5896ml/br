package com.mobmedianet.trackergps.Project.Fragments;


import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobmedianet.trackergps.Project.Adapters.PoisArrayAdapter;
import com.mobmedianet.trackergps.Project.Objects.UserObject;
import com.mobmedianet.trackergps.Project.Utility.Constants;
import com.mobmedianet.trackergps.Project.Utility.Utility;
import com.mobmedianet.trackergps.Project.Utility.WShelper;
import com.mobmedianet.trackergps.Project.Utility.WebService;
import com.mobmedianet.trackergps.R;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class POIS extends Fragment {

    //Constants
    private final static int Pag_size = 10;

    //Views
    private SwipeRefreshLayout swipeContainer;
    private ListView poisList;
    private ImageView addPoi, searchButton;
    private EditText search;
    private RelativeLayout searchBar;
    private TextView btnCancel;

    //Model
    private PoisArrayAdapter adapter;
    private String searchText;
    private int pagNumber = 1;


    public POIS() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // init adapter
        adapter = new PoisArrayAdapter(getActivity());
        new GetUserPois().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_poi, container, false);

        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeResfreshListener());
        Constants.StyleRefreshLayout(swipeContainer);

        poisList = (ListView) v.findViewById(R.id.lv_pois_list);
        poisList.setAdapter(adapter);
        poisList.setOnScrollListener(new EndlessScrollListenerPOIS());
        poisList.setOnItemClickListener(new PoiListOnClickListener());

        addPoi = (ImageView) v.findViewById(R.id.iv_addPoi);
        addPoi.setOnClickListener(new AddPoiOnclicklistener());

        search = (EditText) v.findViewById(R.id.et_search);
        search.setOnEditorActionListener(new OnSearchlistener());
        search.addTextChangedListener(textWatcher());
        searchButton= (ImageView)getActivity().findViewById(R.id.iv_search);
        searchButton.setVisibility(View.VISIBLE);
        searchButton.setOnClickListener(new SearchOnClickListener());
        searchBar = (RelativeLayout)v.findViewById(R.id.relativeLayout10);
        btnCancel= (TextView) v.findViewById(R.id.textView34);
        btnCancel.setOnClickListener(new CancelOnClickListener());


        return v;
    }

    @Override
    public void onStop() {
        super.onStop();
        searchButton.setVisibility(View.GONE);
    }

    private class SearchOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            View view = getView();
            if(view != null)
                searchBar.setVisibility((searchBar.getVisibility()==View.GONE) ? View.VISIBLE: View.GONE);
        }
    }
    private class CancelOnClickListener implements View.OnClickListener {

        @Override

        public void onClick(View v) {

            if (search.getText().length()>0){
                 search.setText("");
                searchText=search.getText().toString();
                cleanData();
                new GetUserPois().execute();
                Utility.hideKeyboard(v, getActivity());
            }
        }
    }

    //EditText OnSearch listener
    private class OnSearchlistener implements TextView.OnEditorActionListener {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchText = search.getText().toString();
                cleanData();
                new GetUserPois().execute();
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
                if (!search.getText().toString().equals("")) { //if edittext include text
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

    //clean adapter
    private void cleanData() {
        adapter.clear();
        pagNumber = 1;
    }

    // AddPoi Button listener
    private class AddPoiOnclicklistener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new POISAdd()).addToBackStack(null)
                    .commit();
        }
    }

    // Pois List listener
    private class PoiListOnClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, POISDetail.newInstance(adapter.getItem(position))).addToBackStack(null)
                    .commit();
        }
    }

    // Endless list
    private class EndlessScrollListenerPOIS implements AbsListView.OnScrollListener {
        private int visibleThreshold = 1;
        private int previousTotal = 0;
        private boolean loading = true;

        public EndlessScrollListenerPOIS() {
        }

        public EndlessScrollListenerPOIS(int visibleThreshold) {
            this.visibleThreshold = visibleThreshold;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {

            if (!loading && totalItemCount < previousTotal) {
                loading = true;
                previousTotal = 0;
            }

            if (loading) {
                if (totalItemCount > previousTotal) {
                    pagNumber++;
                    loading = false;
                    previousTotal = totalItemCount;

                }
            }
            if (!loading
                    && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {

                new GetUserPois().execute();
                loading = true;
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
    }

    private class SwipeResfreshListener implements SwipeRefreshLayout.OnRefreshListener {

        @Override
        public void onRefresh() {
            cleanData();
            new GetUserPois().execute();
        }
    }

    private class GetUserPois extends AsyncTask<Void, Void, String> {
        protected Dialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = Utility.create_dialog(getActivity(), getActivity().getText(R.string.pois_dialog).toString());

        }

        @Override
        protected String doInBackground(Void... parametros) {

            String result = "";
            ArrayList<NameValuePair> params = WShelper.paramsGetUserPois(UserObject.getInstance(), pagNumber, Pag_size, searchText);
            result = WebService.Call(WebService.pois, params);

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

                        adapter.addAll(WShelper.responseGetUserPois(post));
                    } else if (post.getString("responseCode").equals("-900")) {
                        if (pagNumber != 2) {

                            Utility.showAlert(getActivity(), getActivity().getString(R.string.pois_noPois));
                        }
                    } else {
                        Utility.showAlert(getActivity(), getActivity().getString(R.string.conErrorMSG));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Utility.showAlert(getActivity(), getActivity().getString(R.string.jsonErrorMSG));
                }

                progressDialog.dismiss();
                if (swipeContainer.isRefreshing())
                    swipeContainer.setRefreshing(false);

            }
        }


    }


}

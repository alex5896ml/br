package com.mobmedianet.trackergps.Project.Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobmedianet.trackergps.Project.Adapters.StopReportAdapter;
import com.mobmedianet.trackergps.Project.Objects.StopReportObject;
import com.mobmedianet.trackergps.Project.Objects.UnitObjects;
import com.mobmedianet.trackergps.Project.Objects.UserObject;
import com.mobmedianet.trackergps.Project.Utility.Constants;
import com.mobmedianet.trackergps.Project.Utility.Utility;
import com.mobmedianet.trackergps.Project.Utility.WShelper;
import com.mobmedianet.trackergps.Project.Utility.WebService;
import com.mobmedianet.trackergps.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Eduardo on 7/4/16.
 */
public class ReportStopReport  extends Fragment {
    private static final String ARG_PARAM1 = "unitData";
    private static final String NoDateFiltering = "2000-01-01";
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private static final String fragmentTag = "FRAGMENT_VIEWPAGER";

    ImageView carImage;
    TextView carName, carLocation, date;
    ListView stopReportList;
    ProgressBar progress;
    ImageView calendarIcon;

    //model
    UnitObjects unitData;
    StopReportAdapter adapter;
    int pageNumber = 1;
    int pageSize = 999999;
    private DatePickerDialog fromDatePickerDialog;
    private String dateSelected;
    Calendar newCalendar;
    private ArrayList<StopReportObject> data = new ArrayList<>();

    public static ReportStopReport newInstance(UnitObjects unitData) {
        ReportStopReport fragment = new ReportStopReport();
        Bundle b = new Bundle();
        b.putSerializable(ARG_PARAM1, unitData);
        fragment.setArguments(b);
        return fragment;
    }
    public ReportStopReport() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dateSelected = NoDateFiltering;
        if (getArguments() != null && getArguments().containsKey(ARG_PARAM1))
            unitData = (UnitObjects) getArguments().getSerializable(ARG_PARAM1);


    }

    @Override
    public void onResume() {
        super.onResume();
        if (unitData.getDateFilterReport() != null ) {
            dateSelected=unitData.getDateFilterReport();
//            adapter.getReports(pageNumber, dateSelected);
            Log.w("FECHA:", unitData.getDateFilterReport());
        }
        else{
//            adapter.getReports(pageNumber, dateSelected);
        }
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_report_stop, container, false);

        carImage = (ImageView) v.findViewById(R.id.imageView11);
        carName = (TextView) v.findViewById(R.id.textView25);
        carLocation = (TextView) v.findViewById(R.id.textView26);
        date = (TextView) v.findViewById(R.id.textView27);
        progress = (ProgressBar) v.findViewById(R.id.progressBar22);

        calendarIcon = (ImageView) v.findViewById(R.id.imageView13);
        calendarIcon.setOnClickListener(new CalendarOnClickListener());

        stopReportList = (ListView) v.findViewById(R.id.listViewStop);

        adapter = new StopReportAdapter(getActivity(),data);
        stopReportList.setAdapter(adapter);
//        stopReportList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                getFragmentManager().beginTransaction()
//                        .replace(R.id.content_frame, Stop.newInstance(adapter.getItemById(position), unitData.getUnitId())).addToBackStack(null)
//                        .commit();
//            }
//        });
        stopReportList.setOnScrollListener(new EndlessScrollListenerTravelReport());
        pageNumber = 1;


        GetReports(dateSelected);


        setupValues();
        return v;
    }

    private void setupValues() {
        if (unitData.getImageUrl() != null)
            ImageLoader.getInstance().displayImage(unitData.getImageUrl(), carImage, Constants.defaultOptions);
        else
            carImage.setImageResource(R.drawable.novedades48);


        carName.setText(unitData.getName());
        date.setText(unitData.getLastTime());
        if (unitData.getLocation() != null && !unitData.getLocation().isEmpty())
            carLocation.setText(unitData.getLocation());
        else
            carLocation.setText(getActivity().getString(R.string.noLocationrMSG));


    }

    private class CalendarOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            configurecalendar();
        }
    }

    // setup Calendar
    private void configurecalendar() {

        if (newCalendar == null)
            newCalendar = Calendar.getInstance();

        else {
            try {
                newCalendar.setTime(dateFormatter.parse(dateSelected));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        fromDatePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                dateSelected = dateFormatter.format(newDate.getTime());
                Log.e(Constants.Tag + "Dateselected", dateSelected);
                pageNumber = 1;
//                adapter.cleanData();
               GetReports(dateSelected);
                unitData.setDateFilterReport(dateSelected);

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        fromDatePickerDialog.show();
    }

    // Endless list
    private class EndlessScrollListenerTravelReport implements AbsListView.OnScrollListener {
        private int visibleThreshold = 1;
        private int previousTotal = 0;
        private boolean loading = true;

        public EndlessScrollListenerTravelReport() {
        }

        public EndlessScrollListenerTravelReport(int visibleThreshold) {
            this.visibleThreshold = visibleThreshold;
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
                    pageNumber++;

                }
            }
            if (!loading
                    && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
//                adapter.getReports(pageNumber, dateSelected);
                loading = true;
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
    }




    private class GetStopReports extends AsyncTask<Void, Void, String> {
        protected Dialog progressDialog;
        private String day;

        public GetStopReports(String day) {
            this.day = day;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            data.clear();
            progressDialog = Utility.create_dialog(getActivity(), getActivity().getText(R.string.unit_dialog).toString());
        }

        @Override
        protected String doInBackground(Void... parametros) {
            String result;
            ArrayList<NameValuePair> params = WShelper.paramsGetStopReportList(UserObject.getInstance(), unitData.getUnitId(),day + "T00:00:00", pageNumber,pageSize);
            result = WebService.Call(WebService.getStopReportList, params);

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

                        WShelper.responseGetStopReportList(post, data);
                        ArrayAdapter adapter = new ArrayAdapter<StopReportObject>(getContext(), R.layout.adapter_vehicle_history,data);
                        stopReportList.setAdapter(adapter);

                    } else {
                        //TODO: Interpretar respuestas del servidor para hacer un mensaje más UserFriendly
//                        Utility.create_dialog(getActivity(),"No se encontraron paradas");
                        Utility.showAlert(getActivity(),"No se encontraron paradas");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Utility.showAlert(getActivity(), getActivity().getString(R.string.jsonErrorMSG));
                }
                progressDialog.dismiss();

//                ArrayList<VehicleHistoryObject> arrayCableado = new ArrayList<VehicleHistoryObject>();
//                VehicleHistoryObject objetoCableado = new VehicleHistoryObject();
//                objetoCableado.setReportDate("2015-12-16T08:17:00");
//                objetoCableado.setLatitud("19.395790");
//                objetoCableado.setLongitud("-102.055500");
//                objetoCableado.setHeading("N");
//                objetoCableado.setStatus("Bateria Baja CAMP");
//                objetoCableado.setLocation("Este es el location cableado");
//                arrayCableado.add(objetoCableado);
//                adapter = new VehicleHistoryAdapter(getActivity(),arrayCableado);


                stopReportList.setAdapter(adapter);
                stopReportList.setOnItemClickListener(new stopReportClickListener(getContext()));

            }
        }
    }

    private class stopReportClickListener implements AdapterView.OnItemClickListener  {
        Context context;
        public stopReportClickListener(Context context){
            super();
            this.context = context;

        }
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            StopReportObject stopReportObject = (StopReportObject) adapter.getStopReportByItemByPosition(position);

            Fragment f = new Fragment();
            f = ReportStopReportDetail.newInstance(stopReportObject, unitData);
//
            Intent myIntent = new Intent(context,VehicleHistoryMap.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("stopReportObject",stopReportObject);
            bundle.putSerializable("unitData", unitData);

            myIntent.putExtras(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, f, fragmentTag).addToBackStack(null).commit();




        }
    }

    private void GetReports(String day ){
        new GetStopReports(day).execute();

    }
}

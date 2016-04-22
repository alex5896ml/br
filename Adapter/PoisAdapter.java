package com.mobmedianet.trackergps.Project.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobmedianet.trackergps.Project.Objects.POISObject;
import com.mobmedianet.trackergps.Project.Objects.UserObject;
import com.mobmedianet.trackergps.Project.Utility.Utility;
import com.mobmedianet.trackergps.Project.Utility.WShelper;
import com.mobmedianet.trackergps.Project.Utility.WebService;
import com.mobmedianet.trackergps.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by cesargarcia on 13/10/15.
 */
public class PoisAdapter extends BaseAdapter {

    //Constants
    private final static int Pag_size = 10;

    //Model
    private int pagNumber = 1;
    private Context context;
    private LayoutInflater mInflater;
    private ArrayList<POISObject> data = new ArrayList<>();  // Array Data
    private String search = "";

    public PoisAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = mInflater.inflate(R.layout.adapter_textview, parent, false);
        TextView textView = (TextView) convertView;

        // find view in xml
        textView.setText(data.get(position).getName());
        return convertView;

    }

    public void setPagNumber(int pagNumber) {
        this.pagNumber = pagNumber;
    }

    public int getPagNumber() {
        return pagNumber;
    }

    // get adapter Array data
    public ArrayList<POISObject> getData() {
        return data;
    }

    //execute getPois AsyncTask
    public void getdata(String search) {

        this.search = search;
        new GetUserPois().execute();
    }

    // clear Pois Data Array
    public void cleanData() {
        data.clear();
        pagNumber = 1;
    }

    // get Pois data Asynctask
    private class GetUserPois extends AsyncTask<Void, Void, String> {
        protected Dialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = Utility.create_dialog(context, context.getText(R.string.pois_dialog).toString());

        }

        @Override
        protected String doInBackground(Void... parametros) {

            String result = "";
            ArrayList<NameValuePair> params = WShelper.paramsGetUserPois(UserObject.getInstance(), pagNumber, Pag_size, search);
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

                        //WShelper.responseGetUserPois(post, data);
                    } else if (post.getString("responseCode").equals("-900")) {
                        if (pagNumber != 2) {

                            Utility.showAlert(context, context.getString(R.string.pois_noPois));
                        }
                    } else {
                        Utility.showAlert(context, context.getString(R.string.conErrorMSG));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Utility.showAlert(context, context.getString(R.string.jsonErrorMSG));
                }
                notifyDataSetChanged();
                progressDialog.dismiss();

            }
        }


    }


}

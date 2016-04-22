package com.mobmedianet.trackergps.Project.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import java.util.ArrayList;

/**
 * Created by cesargarcia on 3/11/15.
 */
public class UnitAdapter extends BaseAdapter {
    // Constants
    private static final int pageSize = 10;

    //Model
    private Context context;
    private LayoutInflater mInflater;
    private int unitPageNumber = 1;
    private ProgressBar progress;

    // Data
    ArrayList<UnitObjects> data = new ArrayList<>();


    public UnitAdapter(Context context, ProgressBar progress) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.progress = progress;
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
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.adapter_unit, parent, false);
            holder = new ViewHolder();

            // find views in xml
            holder.carImage = (ImageView) convertView.findViewById(R.id.iv_car);
            holder.name = (TextView) convertView.findViewById(R.id.tv_carName);
            holder.location = (TextView) convertView.findViewById(R.id.tv_location);
            holder.date = (TextView) convertView.findViewById(R.id.tv_date);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // setting data
        if (data.get(position).getImageUrl() != null)
            ImageLoader.getInstance().displayImage(data.get(position).getImageUrl(), holder.carImage, Constants.defaultOptions);
        else
            holder.carImage.setImageResource(R.drawable.alertas);
            holder.name.setText(data.get(position).getName());
            holder.location.setText(data.get(position).getLocation());
            holder.date.setText(data.get(position).getLastTime());

        return convertView;
    }

    // get Data
    public void getUnits(String search, boolean dialog, int companyId, int subFleetId, int groupId) {

        new GetUnits(search, dialog, companyId, subFleetId, groupId).execute();
    }

    // clear Pois Data Array
    public void cleanData() {
        data.clear();
        unitPageNumber = 1;
    }

    public void setUnitPageNumber(int unitPageNumber) {
        this.unitPageNumber = unitPageNumber;
    }

    public int getUnitPageNumber() {
        return unitPageNumber;
    }

    // model units row
    private class ViewHolder {
        private ImageView carImage;
        private TextView name;
        private TextView location;
        private TextView date;
    }

    // get UnitsArray
    public ArrayList<UnitObjects> getArray() {
        return data;
    }

    //GetUnitsAsyntask
    private class GetUnits extends AsyncTask<Void, Void, String> {
        protected Dialog progressDialog;
        Boolean dialog;
        int companyId, subFleetId, groupId;
        String search;

        GetUnits(String search, boolean dialog, int companyId, int subFleetId, int groupId) {
            this.search = search;
            this.dialog = dialog;
            this.companyId = companyId;
            this.subFleetId = subFleetId;
            this.groupId = groupId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (dialog)
                progressDialog = Utility.create_dialog(context, context.getText(R.string.unit_list_dialog).toString());

        }

        @Override
        protected String doInBackground(Void... parametros) {

            String result = "";
            ArrayList<NameValuePair> params = WShelper.paramsGetUnitList(UserObject.getInstance(), companyId, subFleetId, groupId, unitPageNumber, pageSize, search);
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
                    progress.setVisibility(View.GONE);
                    if (post.getString("responseCode").equals(WebService.response_SUCCESS)) {

                        // WShelper.responseGetUnitList(post, data);

                    } else if (post.getString("responseCode").equals("-900")) {

                        if (unitPageNumber != 2) {
                            Utility.showAlert(context, context.getString(R.string.unit_nounit));
                        }
                    } else {
                        Utility.showAlert(context, context.getString(R.string.conErrorMSG));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Utility.showAlert(context, context.getString(R.string.jsonErrorMSG));
                }
                if (dialog)
                    progressDialog.dismiss();
                notifyDataSetChanged();


            }

        }


    }


}

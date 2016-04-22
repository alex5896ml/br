package com.mobmedianet.trackergps.Project.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobmedianet.trackergps.Project.Json.JsonReader;
import com.mobmedianet.trackergps.Project.Objects.AlertObject;
import com.mobmedianet.trackergps.Project.Objects.UserObject;
import com.mobmedianet.trackergps.Project.Utility.Constants;
import com.mobmedianet.trackergps.Project.Utility.Utility;
import com.mobmedianet.trackergps.Project.Utility.WShelper;
import com.mobmedianet.trackergps.Project.Utility.WebService;
import com.mobmedianet.trackergps.R;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Cesar on 8/27/2015.
 */

// Class to model and handle Alerts List
public class AlertsAdapter extends BaseAdapter {

    /*
     Constants
      */

    private final static int pagSize = 10;
    private final static int sameType = -5;

    /*
    Model
     */

    private int pagNumber = 1;
    private int type = -4;
    private String search = "";
    private Context context;
    private LayoutInflater mInflater;
    private ArrayList<AlertObject> dataFiltered = new ArrayList<>();

    public AlertsAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return dataFiltered.size();
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
            convertView = mInflater.inflate(R.layout.adapter_alertlist_row, parent, false);
            holder = new ViewHolder();
            // find views in xml
            holder.CarImage = (ImageView) convertView.findViewById(R.id.iv_car);
            holder.CarName = (TextView) convertView.findViewById(R.id.tv_carName);
            holder.AlertName = (TextView) convertView.findViewById(R.id.tv_alert);
            holder.Date = (TextView) convertView.findViewById(R.id.tv_date);
            holder.TypeImage = (ImageView) convertView.findViewById(R.id.iv_alertType);
            holder.StatusImage = (ImageView) convertView.findViewById(R.id.iv_alertStatus);
            convertView.setTag(holder);


        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // setting data
        holder.CarName.setText(dataFiltered.get(position).getUnit());
        holder.AlertName.setText(dataFiltered.get(position).getName());
        holder.Date.setText(dataFiltered.get(position).getStartTime());

        holder.CarImage.setImageResource(dataFiltered.get(position).getImageWithObjecType());
        holder.TypeImage.setImageResource(setImageType(dataFiltered.get(position).getType()));
        setImageStatus(dataFiltered.get(position).getStatus(), holder);

        return convertView;
    }

    public void setPagNumber(int pagNumber) {
        this.pagNumber = pagNumber;
    }

    public int getPagNumber() {
        return pagNumber;
    }

    // Set Image for each status
    private void setImageStatus(int Status, ViewHolder holder) {
        switch (Status) {
            case 0:
                holder.StatusImage.setImageResource(R.drawable.no_leida);
                break;
            case 1:
                holder.StatusImage.setImageDrawable(null);
                break;
            case 2:
                holder.StatusImage.setImageResource(R.drawable.recibida);
                break;
        }

    }

    //getArrayList
    public ArrayList<AlertObject> getArrayList() {
        return dataFiltered;
    }

    // get new alerts
    public void getData(int Type, String search) {
        if (Type != sameType)
            this.type = Type;

        this.search = search;
        new GetUserAlerts().execute();
    }

    // clear Alerts Array
    public void cleanData() {
        dataFiltered.clear();
        pagNumber = 1;
    }

    // class to model each Alerts row
    private class ViewHolder {
        private ImageView CarImage;
        private ImageView Circle;
        private TextView CarName;
        private TextView AlertName;
        private TextView Date;
        private ImageView TypeImage;
        private ImageView StatusImage;
    }

    // get Alerts data Asynctask
    private class GetUserAlerts extends AsyncTask<Void, Void, String> {
        protected Dialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = Utility.create_dialog(context, context.getText(R.string.alerts_dialog).toString());

        }

        @Override
        protected String doInBackground(Void... parametros) {

            String result = "";
            ArrayList<NameValuePair> params = WShelper.paramsGetUserAlerts(UserObject.getInstance(), pagNumber, pagSize, search, type);

            if (type == Constants.typeNoFilter)
                result = WebService.Call(WebService.alerts, params);
            else {

                result = WebService.Call(WebService.alertsFiltered, params);

            }
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
                    if (Constants.testJson == false) {
                        post = new JSONObject(result);
                    } else
                        post = new JSONObject(JsonReader.loadJSONFromAsset(context, R.raw.alerts));
                    if (post.getString("responseCode").equals(WebService.response_SUCCESS)) {

                        WShelper.responseGetUserAlerts(post, dataFiltered, type);
                    } else if (post.getString("responseCode").equals("-900")) {
                        if (pagNumber != 2) {

                            Utility.showAlert(context, context.getString(R.string.alerts_noAlerts));
                        }
                    } else {
                        Utility.showAlert(context, context.getString(R.string.conErrorMSG));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Utility.showAlert(context, context.getString(R.string.jsonErrorMSG));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                notifyDataSetChanged();
                progressDialog.dismiss();

            } else {
                Log.e(Constants.Tag + "Dialog", "tarea terminada");
            }
        }


    }

    // animate first image display
    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }

    // set Image type
    private int setImageType(int type) {

        switch (type) {

            // seguros
            case -2:
                return R.drawable.panic;
            case 1:
                return R.drawable.zona;
            case 2:
                return R.drawable.area;
            case 3:
                return R.drawable.sensordeentrada;
            case 4:
                return R.drawable.movimiento;
            case 5:
                return R.drawable.excesodevelocidad;
            case 6:
                return R.drawable.reporteretrasado;
            case 7:
                return R.drawable.fueraruta;
            case 8:
                return R.drawable.retraso;
            case 9:
                return R.drawable.mantenimientovehicular;
            case 10:
                return R.drawable.expiraciones;
            case 12:
                return R.drawable.excesodevelocidad;
            case 13:
                return R.drawable.on;
            case 14:
                return R.drawable.off;
            case 15:
                return R.drawable.comienzodeservicio;
            case 16:
                return R.drawable.findeservicio;
            case 17:
                return R.drawable.comienzotardio;
            case 18:
                return R.drawable.mantenimientovehicular;
            case 19:
                return R.drawable.excesoderalenti;
            case 20:
                return R.drawable.odometro;
            case 21:
                return R.drawable.actividadfueradehora;
            case 22:
                return R.drawable.paradalarga;

            default:
                return R.drawable.alertas;
        }

    }


}

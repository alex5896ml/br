package com.mobmedianet.trackergps.Project.Fragments;


import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobmedianet.trackergps.Project.Objects.IdNameObject;
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

import java.io.IOException;
import java.util.ArrayList;


public class UnitsDetailDetail extends Fragment {

    // Constants
    private static final String unitObjectKey = "UnitObject";

    // Views
    private ImageView carPhoto, carImageCircle;
    private TextView vehicleId, vehicleLocation;
    private TextView tvVehicleIgnition, tvVehicleDriver, tvVehicleTime, tvVehicleOrientation, tvVehicleSpeed, tvVehicleDrivePhone, tvVehicleDriverMail;
    private ImageView ivVehicleIgnition;
    private LinearLayout commandsContainer;
    private ProgressBar commandpDialog;
    private TextView commandtextpDialog;

    // Model
    private UnitObjects unitObject;
    private ArrayList<IdNameObject> commands = new ArrayList<>();
    private boolean firstime = true;


    public static UnitsDetailDetail newInstance(UnitObjects Object) {
        UnitsDetailDetail fragment = new UnitsDetailDetail();
        Bundle args1 = new Bundle();
        args1.putSerializable(unitObjectKey, Object);
        fragment.setArguments(args1);
        return fragment;
    }

    public UnitsDetailDetail() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            unitObject = (UnitObjects) getArguments().getSerializable(unitObjectKey);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_units_detail, container, false);

        // find views in xml and setup info


        carPhoto = (ImageView) v.findViewById(R.id.car_photo);
        ImageLoader.getInstance().displayImage(unitObject.getImageUrl(), carPhoto);

        carImageCircle = (ImageView) v.findViewById(R.id.car_image);
        ImageLoader.getInstance().displayImage(unitObject.getImageUrl(), carImageCircle, Constants.defaultOptions);

        vehicleId = (TextView) v.findViewById(R.id.tv_vehicleId);
        vehicleId.setText(unitObject.getName());

        vehicleLocation = (TextView) v.findViewById(R.id.tv_vehiclelocation);
        vehicleLocation.setText(unitObject.getLocation());

        tvVehicleIgnition = (TextView) v.findViewById(R.id.tv_vehicleIgnition);
        ivVehicleIgnition = (ImageView) v.findViewById(R.id.iv_vehicleIgnition);

        if (unitObject.isIgnition()) {
            tvVehicleIgnition.setText(getString(R.string.unit_vehicleOn));
            ivVehicleIgnition.setImageResource(R.drawable.encendido);
        } else {
            tvVehicleIgnition.setText(getString(R.string.unit_vehicleOff));
            ivVehicleIgnition.setImageResource(R.drawable.apagado);
        }

        tvVehicleDriver = (TextView) v.findViewById(R.id.tv_vehicleDriver);
        tvVehicleDriver.setText(unitObject.getDriver());

        tvVehicleDrivePhone = (TextView) v.findViewById(R.id.tv_vehicleDriverPhone);
        tvVehicleDrivePhone.setText(unitObject.getDriverPhone());
        tvVehicleDrivePhone.setOnClickListener(new DriverPhoneOnClickListener());

        tvVehicleDriverMail = (TextView) v.findViewById(R.id.tv_vehicleDriverMail);
        tvVehicleDriverMail.setText(unitObject.getDriverEmail());
        tvVehicleDriverMail.setOnClickListener(new DriverMailOnClickListener());

        tvVehicleTime = (TextView) v.findViewById(R.id.tv_vehicleTime);
        tvVehicleTime.setText(unitObject.getLastTime().substring(0, 10) + "\n" + unitObject.getLastTime().substring(11));

        tvVehicleOrientation = (TextView) v.findViewById(R.id.tv_vehicleOrientation);
        assignOrientation();

        tvVehicleSpeed = (TextView) v.findViewById(R.id.tv_vehicleSpeed);
        tvVehicleSpeed.setText(String.valueOf(unitObject.getSpeed()) + " Km/h");

        commandsContainer = (LinearLayout) v.findViewById(R.id.ll_commands);
        commandpDialog = (ProgressBar) v.findViewById(R.id.progressBar);
        commandtextpDialog = (TextView) v.findViewById(R.id.tv_dialog_text);

        if (firstime) {
            new GetVehiclesCommands().execute();
            firstime = false;
        } else {
            commandpDialog.setVisibility(View.GONE);
            commandtextpDialog.setVisibility(View.GONE);
        }


        return v;
    }

    private void assignOrientation(){
        switch (unitObject.getOrientation()){
            case "N":
                tvVehicleOrientation.setText(getString(R.string.orientation_n));
                break;
            case "S":
                tvVehicleOrientation.setText(getString(R.string.orientation_s));
                break;
            case "E":
                tvVehicleOrientation.setText(getString(R.string.orientation_e));
                break;
            case "W":
                tvVehicleOrientation.setText(getString(R.string.orientation_w));
                break;
            case "NW":
                tvVehicleOrientation.setText(getString(R.string.orientation_nw));
                break;
            case "NE":
                tvVehicleOrientation.setText(getString(R.string.orientation_ne));
                break;
            case "SE":
                tvVehicleOrientation.setText(getString(R.string.orientation_se));
                break;
            case "SW":
                tvVehicleOrientation.setText(getString(R.string.orientation_sw));
                break;


        }

    }

    private void addCommands() {
        for (int i = 0; i < commands.size(); i++) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View theInflatedView = inflater.inflate(R.layout.adapter_commands, null);
            theInflatedView.setTag(i);
            theInflatedView.setOnClickListener(new CommandOnclickListener());

            TextView commandName = (TextView) theInflatedView.findViewById(R.id.text);
            ImageView commandImage = (ImageView) theInflatedView.findViewById(R.id.icon);

            switch (commands.get(i).getMname()){
                case "LOCATE":
                    commandName.setText(getString(R.string.command_locate));
                    commandImage.setImageResource(R.drawable.commandlocate);
                    break;
                case "VALET":
                    commandName.setText(getString(R.string.command_valet));
                    commandImage.setImageResource(R.drawable.commandvalet);
                    break;
                case "BLOCK":
                    commandName.setText(getString(R.string.command_block));
                    commandImage.setImageResource(R.drawable.commandlock);
                    break;
                case "UNBLOCK":
                    commandName.setText(getString(R.string.command_open));
                    commandImage.setImageResource(R.drawable.commandopen);
                    break;

            }

            commandsContainer.addView(theInflatedView);
        }

    }

    private class CommandOnclickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            new SendVehiclesCommands(commands.get((Integer) v.getTag()).getID()).execute();
        }
    }

    private class DriverMailOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", unitObject.getDriverEmail(), null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Contact");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "");
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        }
    }

    private class DriverPhoneOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", unitObject.getDriverPhone(), null));
            startActivity(intent);
        }
    }

    // Subfleet Asyntask
    private class GetVehiclesCommands extends AsyncTask<Void, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(Void... parametros) {

            String result;
            ArrayList<NameValuePair> params = WShelper.paramsGetVehicleCommands(UserObject.getInstance(), unitObject.getUnitId());
            result = WebService.Call(WebService.getVehicleCommands, params);

            if (result != null && !result.isEmpty())
                result = result.substring(result.indexOf("=") + 1, result.indexOf(";"));
            return result;
        }

        @Override
        protected void onPostExecute(String result) {


            super.onPostExecute(result);
            commandpDialog.setVisibility(View.GONE);
            commandtextpDialog.setVisibility(View.GONE);
            if (result != null) {

                try {
                    JSONObject post = new JSONObject(result);
                    if (post.getString("responseCode").equals(WebService.response_SUCCESS)) {
                        WShelper.responseGetVehicleCommands(post, commands);
                        addCommands();
                    } else if (post.getString("responseCode").equals("-900")) {
                        //     Utility.showAlert(getActivity(), getActivity().getString(R.string.unit_nocommands));

                    } else {
                        Utility.showAlert(getActivity(), getActivity().getString(R.string.conErrorMSG));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Utility.showAlert(getActivity(), getActivity().getString(R.string.jsonErrorMSG));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }


    }

    private class SendVehiclesCommands extends AsyncTask<Void, Void, String> {
        int commandId;
        Dialog pDialog;

        SendVehiclesCommands(int commandId) {
            this.commandId = commandId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = Utility.create_dialog(getActivity(), getActivity().getString(R.string.unit_commands_dialog));

        }

        @Override
        protected String doInBackground(Void... parametros) {

            String result;
            ArrayList<NameValuePair> params = WShelper.paramsSendVehicleCommands(UserObject.getInstance(), unitObject.getUnitId(), commandId);
            result = WebService.Call(WebService.sendVehicleCommands, params);

            if (result != null && !result.isEmpty())
                result = result.substring(result.indexOf("=") + 1, result.indexOf(";"));
            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            pDialog.dismiss();
            if (result != null) {

                try {
                    JSONObject post = new JSONObject(result);
                    if (post.getString("responseCode").equals(WebService.response_SUCCESS)) {
                        Utility.showAlert(getActivity(), getActivity().getString(R.string.unit_commandsent));
                    } else {
                        Utility.showAlert(getActivity(), getActivity().getString(R.string.conErrorMSG));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                    Utility.showAlert(getActivity(), getActivity().getString(R.string.jsonErrorMSG));
                }


            }

        }


    }


}

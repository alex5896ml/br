package com.mobmedianet.trackergps.Project.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobmedianet.trackergps.Project.Objects.UnitObjects;
import com.mobmedianet.trackergps.R;

import java.util.ArrayList;

/**
 * Created by cesargarcia on 16/2/16.
 */
public class UnitArrayAdapter extends ArrayAdapter<UnitObjects> {


    //Model
    private LayoutInflater mInflater;

    public UnitArrayAdapter(Context context, ArrayList<UnitObjects> users) {
        super(context, 0, users);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        UnitObjects unit = getItem(position);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.adapter_unit, parent, false);
            holder = new ViewHolder();

            // find views in xml
            holder.carImage = (ImageView) convertView.findViewById(R.id.iv_car);
            holder.name = (TextView) convertView.findViewById(R.id.tv_carName);
            holder.location = (TextView) convertView.findViewById(R.id.tv_location);
            holder.date = (TextView) convertView.findViewById(R.id.tv_date);
            holder.carImage.setFocusable(false);
            holder.name.setFocusable(false);
            holder.location.setFocusable(false);

            holder.date.setFocusable(false);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        switch (unit.getObjectType()) {
            case 0:
                holder.carImage.setImageResource(R.drawable.motorcycle);
                break;
            case 1:
                holder.carImage.setImageResource(R.drawable.car);

                break;
            case 2:
                holder.carImage.setImageResource(R.drawable.box);
                break;
            case 3:
                holder.carImage.setImageResource(R.drawable.people);
                break;
            case 6:
                holder.carImage.setImageResource(R.drawable.asset);
                break;
            default:
                holder.carImage.setImageResource(R.drawable.alertas);
                break;

        }


//
//        // setting data
//        if (unit.getImageUrl() != null)
//            ImageLoader.getInstance().displayImage(unit.getImageUrl(), holder.carImage, Constants.defaultOptions);
//        else
//            holder.carImage.setImageResource(R.drawable.alertas);



        holder.name.setText(unit.getName());
        holder.location.setText(unit.getLocation());
        holder.date.setText(unit.getLastTime());

        return convertView;
    }

    // model units row
    private class ViewHolder {
        private ImageView carImage;
        private TextView name;
        private TextView location;
        private TextView date;
    }

}

package com.mobmedianet.trackergps.Project.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mobmedianet.trackergps.Project.Objects.POISObject;
import com.mobmedianet.trackergps.R;

/**
 * Created by cesargarcia on 16/2/16.
 */
public class PoisArrayAdapter extends ArrayAdapter<POISObject> {
    private LayoutInflater mInflater;

    public PoisArrayAdapter(Context context) {
        super(context, 0);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.adapter_textview, parent, false);

        TextView textView = (TextView) convertView;
        textView.setText(getItem(position).getName());

        return convertView;
    }
}

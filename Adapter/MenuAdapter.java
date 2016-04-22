package com.mobmedianet.trackergps.Project.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobmedianet.trackergps.R;


/**
 * Created by Cesar on 3/25/2015.
 */

// class to model left menu in app
public class MenuAdapter extends BaseAdapter {
    private String[] data;
    private LayoutInflater mInflater;
    private Context context;

    public MenuAdapter(Context context, String[] data) {
        this.context = context;
        this.data = data;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.length;
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
            convertView = mInflater.inflate(R.layout.drawer_list_item, parent, false);
            holder = new ViewHolder();

            // find views in xml
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            holder.menu = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // setting data
        switch (position) {
            case 0:
                holder.icon.setImageResource(R.drawable.menu_units);
                break;
            case 1:
                holder.icon.setImageResource(R.drawable.menu_poi);
                break;
            case 2:
                holder.icon.setImageResource(R.drawable.menu_alerts);
                break;
            case 3:
                holder.icon.setImageResource(R.drawable.menu_reports);
                break;
            case 4:
                holder.icon.setImageResource(R.drawable.menu_about);
                break;
            case 5:
                holder.icon.setImageResource(R.drawable.menu_help);
                break;
            default:

                break;
        }
        holder.menu.setText(data[position]);
        return convertView;
    }

    // model each row
    private class ViewHolder {
        private ImageView icon;
        private TextView menu;
    }
}



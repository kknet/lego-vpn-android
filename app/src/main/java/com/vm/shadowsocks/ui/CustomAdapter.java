package com.vm.shadowsocks.ui;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.vm.shadowsocks.R;
import android.view.Gravity;

import java.util.Vector;

/**
 * Created by abdalla on 10/29/17.
 */

public class CustomAdapter extends ArrayAdapter<String> {

    String[] spinnerTitles;
    int[] spinnerImages;
    Context mContext;
    private Vector<Integer> rand_nodes  = new Vector<Integer>();

    public CustomAdapter( Context context, String[] titles, int[] images) {
        super(context, R.layout.custom_spinner_row);
        this.spinnerTitles = titles;
        this.spinnerImages = images;
        this.mContext = context;
        for (int i = 0 ; i < 255; ++i) {
            rand_nodes.add((int)(Math.random() * 1000) + 1);
        }
    }

    @Override
    public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public int getCount() {
        return spinnerTitles.length;
    }

    @Override
    public View getView(int position, View convertView,  ViewGroup parent) {
        ViewHolder mViewHolder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.custom_spinner_row, parent, false);
            mViewHolder.mFlag = (ImageView) convertView.findViewById(R.id.ivFlag);
            mViewHolder.mName = (TextView) convertView.findViewById(R.id.tvName);
            mViewHolder.mPopulation = (TextView) convertView.findViewById(R.id.tvPopulation);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        mViewHolder.mFlag.setImageResource(spinnerImages[position]);
        mViewHolder.mName.setText(spinnerTitles[position]);
        String nodes_num = rand_nodes.get(position) + " nodes";
        mViewHolder.mPopulation.setText(nodes_num);
        return convertView;
    }

    private static class ViewHolder {
        ImageView mFlag;
        TextView mName;
        TextView mPopulation;
    }
}

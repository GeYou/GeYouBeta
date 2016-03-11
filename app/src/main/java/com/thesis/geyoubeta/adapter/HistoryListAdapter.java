/*
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Copyright (C) 2015 Ivan Wesley Chua and Jethro Divino
 */

package com.thesis.geyoubeta.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thesis.geyoubeta.entity.History;

import java.util.ArrayList;

/**
 * Created by ivanwesleychua on 25/02/2016.
 */
public class HistoryListAdapter extends BaseAdapter {

    private ArrayList<History> histories;
    private Context context;
    private LayoutInflater layoutInflater;

    public HistoryListAdapter(ArrayList<History> h, Context c) {
        histories = h;
        context = c;

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return histories.size();
    }

    @Override
    public Object getItem(int position) {
        return histories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(android.R.layout.simple_list_item_2, parent, false);
        }
        TextView destination = (TextView) convertView.findViewById(android.R.id.text1);
        destination.setText(histories.get(position).getParty().getName());
        TextView date = (TextView) convertView.findViewById(android.R.id.text2);
        date.setText(histories.get(position).getParty().getStartDateTime().toString());
        return convertView;
    }
}

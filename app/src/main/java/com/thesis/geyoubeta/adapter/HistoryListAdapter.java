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

import com.thesis.geyoubeta.R;
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
            convertView = layoutInflater.inflate(R.layout.history_row, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.textViewHistPartyName);
        name.setText(histories.get(position).getParty().getName());
        TextView destLat = (TextView) convertView.findViewById(R.id.textViewHistPartyLat);
        destLat.setText(histories.get(position).getParty().getDestLat().toString());
        TextView destLong = (TextView) convertView.findViewById(R.id.textViewHistPartyLong);
        destLong.setText(histories.get(position).getParty().getDestLong().toString());
        TextView destLong = (TextView) convertView.findViewById(R.id.textViewHistPartyLong);
        destLong.setText(histories.get(position)..toString());
        return convertView;
    }
}

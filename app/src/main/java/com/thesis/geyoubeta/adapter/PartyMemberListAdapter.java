/*
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Copyright (C) 2015 Ivan Wesley Chua and Jethro Divino
 */

package com.thesis.geyoubeta.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by ivanwesleychua on 25/02/2016.
 */
public class PartyMemberListAdapter extends BaseAdapter {

    private ArrayList<String> emails;
    private Context context;
    private LayoutInflater layoutInflater;

    public PartyMemberListAdapter(ArrayList<String> arr, Context c) {
        emails = arr;
        context = c;
        for (int i = 0; i < emails.size(); i++) {
            Log.d("PMLISTADAPTER", i + ": " + emails.get(i));
        }
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return emails.size();
    }

    @Override
    public Object getItem(int position) {
        return emails.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        TextView email = (TextView) convertView.findViewById(android.R.id.text1);
        email.setText(emails.get(position));
        return convertView;
    }
}

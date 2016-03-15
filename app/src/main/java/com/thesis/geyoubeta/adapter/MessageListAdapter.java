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

import com.thesis.geyoubeta.entity.Message;

import java.util.ArrayList;

/**
 * Created by ivanwesleychua on 15/03/2016.
 */
public class MessageListAdapter extends BaseAdapter {
    private ArrayList<Message> messages;
    private Context context;
    private LayoutInflater layoutInflater;

    public MessageListAdapter(ArrayList<Message> m, Context c) {
        messages = m;
        context = c;

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
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
        TextView message = (TextView) convertView.findViewById(android.R.id.text1);
        message.setText(messages.get(position).getMessage());
        TextView date = (TextView) convertView.findViewById(android.R.id.text2);
        date.setText(messages.get(position).getUser().getEmail() + "/" + messages.get(position).getSentDate().toString());
        return convertView;
    }
}
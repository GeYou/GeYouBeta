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

import com.thesis.geyoubeta.R;
import com.thesis.geyoubeta.SessionManager;
import com.thesis.geyoubeta.entity.History;
import com.thesis.geyoubeta.entity.PartyMember;
import com.thesis.geyoubeta.entity.User;
import com.thesis.geyoubeta.service.GeYouService;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.JacksonConverter;

/**
 * Created by ivanwesleychua on 25/02/2016.
 */
public class HistoryListAdapter extends BaseAdapter {

    private ArrayList<History> histories;
    private Context context;
    private LayoutInflater layoutInflater;
    private List<String> partyMemberList;
    private String partyMemberString;

    private RestAdapter restAdapter;
    private GeYouService geYouService;
    private SessionManager session;

    public HistoryListAdapter(ArrayList<History> h, Context c) {
        histories = h;
        context = c;

        partyMemberList = new ArrayList<String>();

        session = new SessionManager(context);
        initializeRest();

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

        String partyMem = "here ";

        TextView name = (TextView) convertView.findViewById(R.id.textViewHistPartyName);
        name.setText(histories.get(position).getParty().getName());
        TextView destLat = (TextView) convertView.findViewById(R.id.textViewHistPartyLat);
        destLat.setText(histories.get(position).getParty().getDestLat().toString());
        TextView destLong = (TextView) convertView.findViewById(R.id.textViewHistPartyLong);
        destLong.setText(histories.get(position).getParty().getDestLong().toString());
        TextView partyDateTime = (TextView) convertView.findViewById(R.id.textViewHistDateTime);
        partyDateTime.setText(histories.get(position).getUserDate().toString());
        TextView histId = (TextView) convertView.findViewById(R.id.textViewHistId);
        histId.setText(histories.get(position).getId().toString());


        partyMemberString = "HERE ";

        partyMemberList.clear();

        getPartyMembers(histories.get(position).getParty().getId());
//        for (String s : partyMemberList) {
//            partyMem += " ";
//            partyMem += s;
//            Log.e("HEHE", s);
//            Log.e("HEHE", partyMem);
//        }

        TextView partyMemba = (TextView) convertView.findViewById(R.id.textViewHistPartyMem);
        partyMemba.setText(partyMemberList.toString());

        return convertView;
    }

    public void initializeRest() {
        restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(session.getBaseURL())
                .setConverter(new JacksonConverter())
                .build();

        geYouService = restAdapter.create(GeYouService.class);
    }

    public void getPartyMembers(Integer pId) {
        geYouService.getPartyMembers(pId, new Callback<List<User>>() {
            @Override
            public void success(List<User> users, Response response) {
                Log.e("Hello:  " , users.toString());
                setPM(users);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void setPM(List<User> u) {

        Log.e("HEHEHEH: ", "ning sud");
        for (User user : u) {
            partyMemberList.add(user.getEmail());
            Log.e("HEHEHEH: ", partyMemberList.toString());
        }
    }
}

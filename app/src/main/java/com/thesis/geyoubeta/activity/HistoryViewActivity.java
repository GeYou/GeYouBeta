/*
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Copyright (C) 2015 Ivan Wesley Chua and Jethro Divino
 */

package com.thesis.geyoubeta.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.thesis.geyoubeta.R;
import com.thesis.geyoubeta.SessionManager;
import com.thesis.geyoubeta.entity.History;
import com.thesis.geyoubeta.service.GeYouService;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.JacksonConverter;

public class HistoryViewActivity extends ActionBarActivity {

    private RestAdapter restAdapter;
    private GeYouService geYouService;
    private SessionManager session;

    private Integer historyId;

    private static final String DIR_URL1 = "https://maps.googleapis.com/maps/api/directions/json?origin=";
    private static final String DIR_URL2 = "&destination=";

    //use | to divide waypoints

    private static final String DIR_URL3 = "&waypoints=";

    private static final String STATIC_MAP_URL1 = "https://maps.googleapis.com/maps/api/staticmap?size=400x200&path=weight:3%7Ccolor:blue%7Cenc:";
    private static final String STATIC_MAP_URL2 = "&markers=color:red%7Clabel:Start%7C";
    private static final String STATIC_MAP_URL3 = "&markers=color:red%7Clabel:End%7C";

    private ImageView imageView;
    private LatLng start;
    private LatLng end;
    private String waypoints;
    private String overviewPolyline;

    private List<History> historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_view);

        Bundle bundle = getIntent().getExtras();

        Log.e("FROM HISTORY ACTIVITY: ", "id: "+bundle.getInt("historyId", -1));

        historyId = bundle.getInt("historyId", -1);

        session = new SessionManager(getApplicationContext());
        initializeRest();
        initializeCOmponents();
    }

    public void initializeRest() {
        restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(session.getBaseURL())
                .setConverter(new JacksonConverter())
                .build();

        geYouService = restAdapter.create(GeYouService.class);
    }

    public void initializeCOmponents() {
        Toast.makeText(getApplicationContext(), "HELLO" +historyId.toString(), Toast.LENGTH_SHORT).show();
    }

    public String getDirectionURL() {
        return DIR_URL1 + start.toString() + DIR_URL2 + end.toString() + DIR_URL3 +waypoints;
    }

    public String getStaticURL() {
        return STATIC_MAP_URL1 + overviewPolyline + STATIC_MAP_URL2 + start.toString() + STATIC_MAP_URL3 + end.toString();
    }

    public void makeWaypointsParam() {

    }

    public void getStartCoor() {

    }

    public void getEndCoor() {

    }

    public void getHistoryList() {
        geYouService.getHistoryPoints(session.getPartyId(), session.getUserId(), new Callback<List<History>>() {
            @Override
            public void success(List<History> histories, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}

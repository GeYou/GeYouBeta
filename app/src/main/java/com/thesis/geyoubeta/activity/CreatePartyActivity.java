/*
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Copyright (C) 2015 Ivan Wesley Chua and Jethro Divino
 */

package com.thesis.geyoubeta.activity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.thesis.geyoubeta.NavDrawer;
import com.thesis.geyoubeta.R;
import com.thesis.geyoubeta.adapter.NavDrawerAdapter;
import com.thesis.geyoubeta.entity.History;
import com.thesis.geyoubeta.entity.Party;
import com.thesis.geyoubeta.entity.PartyMember;
import com.thesis.geyoubeta.entity.User;
import com.thesis.geyoubeta.service.GeYouService;
import com.thesis.geyoubeta.SessionManager;
import com.thesis.geyoubeta.service.MyService;

import java.text.DateFormat;
import java.util.Date;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.JacksonConverter;

public class CreatePartyActivity extends ActionBarActivity {

    private SessionManager session;

    private Button btnCreate;
    private Button btnCancel;
    private EditText eTxtName;
    private EditText eTxtStartTimeStamp;
    private EditText eTxtEndTimeStamp;
    private EditText eTxtDestinationLong;
    private EditText eTxtDestinationLat;

    private SlideDateTimeListener startDateListener;
    private SlideDateTimeListener endDateListener;
    private Date startDate;
    private Date endDate;

    private RestAdapter restAdapter;
    private GeYouService geYouService;
    private NavDrawer navDrawer;

    String TITLES[] = {"User Info", "Create Party", "Map", "Messages", "Party Info", "History", "IP Settings", "Logout"};

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    DrawerLayout Drawer;
    android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_party);

        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        initializeDrawer();
        initializeRest();
        initializeComponents();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startService(new Intent(getApplicationContext(), MyService.class));
    }

    @Override
    protected void onStop() {
        super.onStop();

        stopService(new Intent(getApplicationContext(), MyService.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public void initializeDrawer() {
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("GeYou");

        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView);

        mRecyclerView.setHasFixedSize(true);

        mAdapter = new NavDrawerAdapter(TITLES);

        mRecyclerView.setAdapter(mAdapter);

        final GestureDetector mGestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if ((child != null) && mGestureDetector.onTouchEvent(motionEvent)) {
                    Drawer.closeDrawers();

                    Intent intent = navDrawer.getDrawerIntent(recyclerView, child);

                    if (intent != null) {
                        startActivity(intent);
                    } else {
                        session.logoutUser();
                    }

                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

            }
        });

        mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(mLayoutManager);

        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);
        mDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle(this, Drawer, toolbar, R.string.app_name, R.string.app_name) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

        };
        Drawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        android.support.v7.app.ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        menu.setDisplayUseLogoEnabled(true);
        menu.setTitle(" ");
    }

    public void initializeRest() {
        restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(session.getBaseURL())
                .setConverter(new JacksonConverter())
                .build();

        geYouService = restAdapter.create(GeYouService.class);
    }

    public void initializeComponents() {
        navDrawer = new NavDrawer(this);

        eTxtName = (EditText) findViewById(R.id.editTextPartyName);
        eTxtStartTimeStamp = (EditText) findViewById(R.id.editTextStartTimeStamp);
        eTxtEndTimeStamp = (EditText) findViewById(R.id.editTextEndTimeStamp);
        eTxtDestinationLong = (EditText) findViewById(R.id.editTextDestinationLong);
        eTxtDestinationLat = (EditText) findViewById(R.id.editTextDestinationLat);

        startDateListener = new SlideDateTimeListener() {

            @Override
            public void onDateTimeSet(Date date)
            {
                startDate = date;
                eTxtStartTimeStamp.setText(DateFormat.getDateTimeInstance().format(date));
            }

            @Override
            public void onDateTimeCancel()
            {
                // Overriding onDateTimeCancel() is optional.
            }
        };
        endDateListener = new SlideDateTimeListener() {

            @Override
            public void onDateTimeSet(Date date)
            {
                endDate = date;
                eTxtEndTimeStamp.setText(DateFormat.getDateTimeInstance().format(date));
            }

            @Override
            public void onDateTimeCancel()
            {
                // Overriding onDateTimeCancel() is optional.
            }
        };

        btnCreate = (Button) findViewById(R.id.btnCreateParty);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (session.getPartyStatus().equals("status") || session.getPartyStatus().equals("I")) {
                    if (!eTxtName.getText().toString().equals("") && !eTxtStartTimeStamp.getText().toString().equals("") && !eTxtEndTimeStamp.getText().toString().equals("") && !eTxtDestinationLong.getText().toString().equals("") && !eTxtDestinationLat.getText().toString().equals("")) {
                        Party nParty = new Party();

                        nParty.setName(eTxtName.getText().toString());
                        nParty.setStartDateTime(startDate);
                        nParty.setEndDateTime(endDate);
                        nParty.setDestLat(Double.parseDouble(eTxtDestinationLat.getText().toString()));
                        nParty.setDestLong(Double.parseDouble(eTxtDestinationLong.getText().toString()));

                        createParty(nParty);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Could not make party: You have an active party.", Toast.LENGTH_SHORT).show();
                    clearInput();
                }
            }
        });
        btnCancel = (Button) findViewById(R.id.btnCancelParty);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearInput();
            }
        });

        eTxtStartTimeStamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(startDateListener)
                        .setIs24HourTime(true)
                        .setInitialDate((eTxtStartTimeStamp.getText().toString().equals("")) ? new Date() : startDate)
                        .build()
                        .show();
            }
        });

        eTxtEndTimeStamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(endDateListener)
                        .setIs24HourTime(true)
                        .setInitialDate((eTxtEndTimeStamp.getText().toString().equals("")) ? new Date() : endDate)
                        .build()
                        .show();
            }
        });
    }

    public void createParty(Party party) {
        geYouService.createParty(party, session.getUserId(), new Callback<Party>() {
            @Override
            public void success(Party party, Response response) {
                session.setActiveParty(party);
                Toast.makeText(CreatePartyActivity.this, "Successfully created party" , Toast.LENGTH_LONG).show();
                addMemberToParty();
                checkIfHistoryExists();
                //checkActiveParty();
                for (int i = 0; i < 20; i++) ;
                updateUserLocation();
                addHistory();
                clearInput();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(CreatePartyActivity.this, "Unsuccessfully created party!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void clearInput() {
        eTxtName.setText("");
        eTxtStartTimeStamp.setText("");
        eTxtEndTimeStamp.setText("");
        eTxtDestinationLong.setText("");
        eTxtDestinationLat.setText("");
    }

    public void checkIfHistoryExists() {
        geYouService.getExistingHistory(session.getPartyId(), session.getUserId(), new Callback<History>() {
            @Override
            public void success(History history, Response response) {
                if (history.getId() == null) {
                    User u = new User();
                    Party p = new Party();
                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                    u.setId(session.getUserId());
                    p.setId(session.getPartyId());

                    History h = new History();
                    h.setUser(u);
                    h.setParty(p);
                    h.setType("R");
                    h.setStartLat(lastKnownLocation.getLatitude());
                    h.setStartLong(lastKnownLocation.getLongitude());

                    geYouService.addHistory(h, new Callback<History>() {
                        @Override
                        public void success(History history, Response response) {
                            Log.i("CREATE PARTY: ", "made history");
                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });
                } else {
                    Log.i("CREATE PARTY: ", "has history");
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void updateUserLocation(){
        Log.i("CREATE PARTY: ", "updating location...");

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Location l = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        User u = new User();
        Party p = new Party();
        PartyMember pm = new PartyMember();
        u.setId(session.getUserId());
        p.setId(session.getPartyId());

        pm.setId(session.getPartyMemberId());
        pm.setStatus("A");
        pm.setUser(u);
        pm.setParty(p);
        pm.setLastLat(l.getLatitude());
        pm.setLastLong(l.getLongitude());

        geYouService.editMember(pm, new Callback<PartyMember>() {
            @Override
            public void success(PartyMember partyMember, Response response) {
                Log.i("CREATE PARTY: ", "Updated location.");
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void addMemberToParty() {
        PartyMember pm = new PartyMember();
        User u = new User();
        Party p = new Party();
        u.setId(session.getUserId());
        p.setId(session.getPartyId());
        pm.setParty(p);
        pm.setUser(u);
        geYouService.addMember(pm, new Callback<PartyMember>() {
            @Override
            public void success(PartyMember partyMember, Response response) {
                Toast.makeText(CreatePartyActivity.this, "Successfully added user to party", Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void addHistory() {

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Location l = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        User u = new User();
        Party p = new Party();

        u.setId(session.getUserId());
        p.setId(session.getPartyId());

        History h = new History();
        h.setUser(u);
        h.setParty(p);
        h.setStartLat(l.getLatitude());
        h.setStartLong(l.getLongitude());

        geYouService.addHistory(h, new Callback<History>() {
            @Override
            public void success(History history, Response response) {
                Log.i("Servicess: ", "made new history");
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
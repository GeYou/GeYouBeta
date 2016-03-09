/*
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Copyright (C) 2015 Ivan Wesley Chua and Jethro Divino
 */

package com.thesis.geyoubeta.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.thesis.geyoubeta.entity.Party;
import com.thesis.geyoubeta.service.GeYouService;
import com.thesis.geyoubeta.SessionManager;

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
    private EditText eTxtDestination;

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
        eTxtDestination = (EditText) findViewById(R.id.editTextDestination);

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
                    Party nParty = new Party();

                    nParty.setName(eTxtName.getText().toString());
                    nParty.setStartDateTime(startDate);
                    nParty.setEndDateTime(endDate);
                    nParty.setDestination(eTxtDestination.getText().toString());

                    createParty(nParty);
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
                Toast.makeText(CreatePartyActivity.this, "Successfully created party: " + party.toString(), Toast.LENGTH_LONG).show();
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
        eTxtDestination.setText("");
    }
}
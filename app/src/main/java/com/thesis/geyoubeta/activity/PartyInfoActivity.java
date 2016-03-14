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

public class PartyInfoActivity extends ActionBarActivity {

    private SessionManager session;

    private EditText eTxtName;
    private EditText eTxtStartTimeStamp;
    private EditText eTxtEndTimeStamp;
    private EditText eTxtDestination;
    private Button btnEdit;
    private Button btnSave;
    private Button btnCancel;
    private Button btnPartyMembers;

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
        setContentView(R.layout.activity_party_info);

        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        initializeDrawer();
        initializeRest();
        initializeComponents();

        setDefaults();
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

        eTxtName = (EditText) findViewById(R.id.editTextPartyNameInfo);
        eTxtStartTimeStamp = (EditText) findViewById(R.id.editTextStartTimeStampInfo);
        eTxtEndTimeStamp = (EditText) findViewById(R.id.editTextEndTimeStampInfo);
        eTxtDestination = (EditText) findViewById(R.id.editTextDestinationInfo);

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

        btnEdit = (Button) findViewById(R.id.btnEditPartyInfo);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeInputsEnabled();
                btnSave.setEnabled(true);
                btnCancel.setEnabled(true);
            }
        });
        btnSave = (Button) findViewById(R.id.btnSavePartyInfo);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Party nParty = new Party();
                nParty.setId(session.getPartyId());
                nParty.setName(eTxtName.getText().toString());
                nParty.setStartDateTime(startDate);
                nParty.setEndDateTime(endDate);
                nParty.setDestination(eTxtDestination.getText().toString());

                updateParty(nParty);
            }
        });
        btnCancel = (Button) findViewById(R.id.btnCancelPartyInfo);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetInputs();
                btnSave.setEnabled(false);
                btnCancel.setEnabled(false);
            }
        });
        btnPartyMembers = (Button) findViewById(R.id.btnPartyMembersInfo);
        btnPartyMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), PartyMembersActivity.class);
                startActivity(i);
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

    public void setDefaults() {
        eTxtName.setText(session.getPartyName());
        eTxtStartTimeStamp.setText(session.getPartyStart());
        eTxtEndTimeStamp.setText(session.getPartyEnd());
        eTxtDestination.setText(session.getPartyDest());
    }

    public void makeInputsEnabled() {
        eTxtName.setEnabled(true);
        eTxtStartTimeStamp.setEnabled(true);
        eTxtEndTimeStamp.setEnabled(true);
        eTxtDestination.setEnabled(true);
    }

    public void resetInputs() {
        setDefaults();
        eTxtName.setEnabled(false);
        eTxtStartTimeStamp.setEnabled(false);
        eTxtEndTimeStamp.setEnabled(false);
        eTxtDestination.setEnabled(false);
    }

    public void updateParty(Party p) {
        geYouService.updateParty(p, new Callback<Party>() {
            @Override
            public void success(Party party, Response response) {
                resetInputs();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}

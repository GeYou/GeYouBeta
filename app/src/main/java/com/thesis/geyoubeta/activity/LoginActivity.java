/*
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Copyright (C) 2015 Ivan Wesley Chua and Jethro Divino
 */

package com.thesis.geyoubeta.activity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
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

import com.thesis.geyoubeta.R;
import com.thesis.geyoubeta.adapter.NavDrawerAdapter;
import com.thesis.geyoubeta.entity.History;
import com.thesis.geyoubeta.entity.Party;
import com.thesis.geyoubeta.entity.PartyMember;
import com.thesis.geyoubeta.entity.User;
import com.thesis.geyoubeta.service.GeYouService;
import com.thesis.geyoubeta.SessionManager;
import com.thesis.geyoubeta.service.MyService;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.JacksonConverter;

public class LoginActivity extends ActionBarActivity {

    private SessionManager session;

    private Button btnLogin;
    private Button btnRegister;
    private Button btnChangeIP;
    private EditText eTxtEmail;
    private EditText eTxtPassword;

    private RestAdapter restAdapter;
    private GeYouService geYouService;

    String TITLES[] = {"IP Settings", "Main"};

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    DrawerLayout Drawer;
    android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new SessionManager(getApplicationContext());

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

    private void initializeDrawer() {
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

                    Intent intent = null;
                    if (recyclerView.getChildPosition(child) == 1) {
                        intent = new Intent(getApplicationContext(), IPSettingsActivity.class);
                    } else if (recyclerView.getChildPosition(child) == 2) {
                        intent = new Intent(getApplicationContext(), MainActivity.class);
                    }

                    if (intent != null) {
                        startActivity(intent);
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
        eTxtEmail = (EditText) findViewById(R.id.editTextEmailLogin);
        eTxtPassword = (EditText) findViewById(R.id.editTextPasswordLogin);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        btnRegister = (Button) findViewById(R.id.btnRegisterLogin);
        btnRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
            }
        });
        btnChangeIP = (Button) findViewById(R.id.btnChangeIPLogin);
        btnChangeIP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), IPSettingsActivity.class));
            }
        });
    }

    public void dummyLogin() {
        User nUser = new User();
        nUser.setId(-1);
        nUser.setfName("fName");
        nUser.setlName("lName");
        nUser.setEmail("email");
        nUser.setPassword("password");

        session.createLoginSession(nUser);
        Intent i = new Intent(getApplicationContext(), MapActivity.class);
        startActivity(i);
    }

    public void login() {
        if (!eTxtEmail.getText().toString().equals("") && !eTxtPassword.getText().toString().equals("")) {
            geYouService.checkCredentials(eTxtEmail.getText().toString(), eTxtPassword.getText().toString(), new Callback<User>() {
                @Override
                public void success(User user, Response response) {
                    if (user.getId() != null) {

                        session.createLoginSession(user);
                        Toast.makeText(getApplicationContext(), "user id: " +session.getUserId(), Toast.LENGTH_SHORT).show();
                        checkActiveParty();

                        for(int i = 0; i < 10; i++);

                        Intent i = new Intent(getApplicationContext(), MapActivity.class);
                        Intent s = new Intent(getApplicationContext(), MyService.class);
                        startService(s);
                        startActivity(i);
                    } else {
                        Toast.makeText(LoginActivity.this, "Not valid credentials.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Username or Password is empty!", Toast.LENGTH_SHORT).show();
        }
    }

    public void checkActiveParty() {
        geYouService.getActiveParty(session.getUserId(), new Callback<PartyMember>() {
            @Override
            public void success(PartyMember partyMember, Response response) {
                if (partyMember.getId() != null) {
                    Toast.makeText(getApplicationContext(), "Party Mem Id: " + partyMember.getId(), Toast.LENGTH_SHORT).show();
                    session.setPartyMemberId(partyMember.getId());
                    session.setActiveParty(partyMember.getParty());
                    checkIfHistoryExists();

                } else {
                    Toast.makeText(getApplicationContext(), "no active party", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void checkIfHistoryExists() {
        geYouService.getExistingHistory(session.getPartyId(), session.getUserId(), new Callback<History>() {
            @Override
            public void success(History history, Response response) {
                if (history.getId() == null) {
                    User u = new User();
                    Party p = new Party();
                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    u.setId(session.getUserId());
                    p.setId(session.getPartyId());

                    History h = new History();
                    h.setUser(u);
                    h.setParty(p);
                    h.setStartLat((float) lastKnownLocation.getLatitude());
                    h.setStartLong(((float) lastKnownLocation.getLongitude()));

                    geYouService.addHistory(h, new Callback<History>() {
                        @Override
                        public void success(History history, Response response) {
                            Toast.makeText(getApplicationContext(), "made history", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "has history", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
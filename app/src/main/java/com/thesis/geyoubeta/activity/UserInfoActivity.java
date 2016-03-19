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
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.thesis.geyoubeta.NavDrawer;
import com.thesis.geyoubeta.R;
import com.thesis.geyoubeta.adapter.NavDrawerAdapter;
import com.thesis.geyoubeta.entity.User;
import com.thesis.geyoubeta.service.GeYouService;
import com.thesis.geyoubeta.SessionManager;
import com.thesis.geyoubeta.service.MyService;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.JacksonConverter;

public class UserInfoActivity extends ActionBarActivity {

    private SessionManager session;

    private EditText eTxtFName;
    private EditText eTxtLName;
    private EditText eTxtEmail;
    private EditText eTxtPassword;
    private EditText eTxtConfPass;
    private Button btnEdit;
    private Button btnSave;
    private Button btnCancel;

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
        setContentView(R.layout.activity_user_info);

        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        initializeDrawer();
        initializeRest();
        initializeComponents();

        setDefaults();
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

        eTxtFName = (EditText) findViewById(R.id.editTextFNameInfo);
        eTxtLName = (EditText) findViewById(R.id.editTextLNameInfo);
        eTxtEmail = (EditText) findViewById(R.id.editTextEmailInfo);
        eTxtPassword = (EditText) findViewById(R.id.editTextPasswordInfo);
        eTxtConfPass = (EditText) findViewById(R.id.editTextConfirmPassInfo);

        eTxtPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eTxtPassword.setText("");
                eTxtConfPass.setText("");
            }
        });

        btnEdit = (Button) findViewById(R.id.btnEditUserInfo);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeInputsEnabled();
                btnSave.setEnabled(true);
                btnCancel.setEnabled(true);
            }
        });
        btnSave = (Button) findViewById(R.id.btnSaveUserInfo);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eTxtConfPass.getText().toString().equals(eTxtPassword.getText().toString())) {
                    Log.i("User Info: ", "Passwords match!");
                    User nUser = new User();

                    nUser.setId(session.getUserId());
                    nUser.setfName(eTxtFName.getText().toString());
                    nUser.setlName(eTxtLName.getText().toString());
                    nUser.setEmail(eTxtEmail.getText().toString());
                    nUser.setPassword(eTxtPassword.getText().toString());
                    updateCredentials(nUser);
                } else {
                    eTxtPassword.setText("");
                    eTxtConfPass.setText("");
                    Toast.makeText(getApplicationContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnCancel = (Button) findViewById(R.id.btnCancelUserInfo);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetInputs();
                btnSave.setEnabled(false);
                btnCancel.setEnabled(false);
            }
        });
    }

    public void setDefaults() {
        eTxtFName.setText(session.getUserFName());
        eTxtLName.setText(session.getUserLName());
        eTxtEmail.setText(session.getUserEmail());
        eTxtPassword.setText(session.getUserPassword());
        eTxtConfPass.setText(session.getUserPassword());
    }

    public void makeInputsEnabled() {
        eTxtFName.setEnabled(true);
        eTxtLName.setEnabled(true);
        //eTxtEmail.setEnabled(true);
        eTxtPassword.setEnabled(true);
        eTxtConfPass.setEnabled(true);
    }

    public void resetInputs() {
        setDefaults();
        eTxtFName.setEnabled(false);
        eTxtLName.setEnabled(false);
        eTxtEmail.setEnabled(false);
        eTxtPassword.setEnabled(false);
        eTxtConfPass.setEnabled(false);
    }

    public void updateCredentials(User u) {
        geYouService.updateUser(u, new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                Toast.makeText(UserInfoActivity.this, "Successfully updated user.", Toast.LENGTH_LONG).show();
                session.updateLoginCredentials(user);
                resetInputs();
                btnSave.setEnabled(false);
                btnCancel.setEnabled(false);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(UserInfoActivity.this, "Unsuccessfully updated user.", Toast.LENGTH_LONG).show();
            }
        });
    }
}

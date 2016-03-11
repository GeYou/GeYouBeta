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

import com.thesis.geyoubeta.R;
import com.thesis.geyoubeta.adapter.NavDrawerAdapter;
import com.thesis.geyoubeta.entity.Party;
import com.thesis.geyoubeta.entity.User;
import com.thesis.geyoubeta.service.GeYouService;
import com.thesis.geyoubeta.SessionManager;

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

    String TITLES[] = {"IP Settings"};

    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    DrawerLayout Drawer;                                  // Declaring DrawerLayout
    android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;                  // Declaring Action Bar Drawer Toggle;
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
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);                   // Setting toolbar as the ActionBar with setSupportActionBar() call
        toolbar.setTitle("GeYou");

        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View

        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size

        mAdapter = new NavDrawerAdapter(TITLES);

        mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView

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

        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager

        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager

        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view
        mDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle(this, Drawer, toolbar, R.string.app_name, R.string.app_name) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                // open I am not going to put anything here)
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
            }

        }; // Drawer Toggle Object Made
        Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State

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
                    if (user != null) {
                        session.createLoginSession(user);
                        geYouService.getActiveParty(session.getUserId(), new Callback<Party>() {
                            @Override
                            public void success(Party party, Response response) {
                                if (party != null) {
                                    session.setActiveParty(party);
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {

                            }
                        });
                        Intent i = new Intent(getApplicationContext(), MapActivity.class);
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
}
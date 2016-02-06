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
import com.thesis.geyoubeta.entity.User;
import com.thesis.geyoubeta.service.GeYouService;
import com.thesis.geyoubeta.service.SessionManager;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.JacksonConverter;

public class RegisterActivity extends ActionBarActivity {

    SessionManager session;

    Button btnRegister;
    Button btnCancel;
    EditText eTxtFName;
    EditText eTxtLName;
    EditText eTxtEmail;
    EditText eTxtPassword;
    EditText eTxtConfirmPass;

    RestAdapter restAdapter;
    GeYouService geYouService;
    private static final String BASE_URL = "http://10.0.3.2:8080/geyou";

    private Toolbar toolbar;
    String TITLES[] = {"User Info", "Create Party", "Map", "Messages", "Party Info", "History", "IP Settings",  "Logout"};

    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    DrawerLayout Drawer;                                  // Declaring DrawerLayout

    android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;                  // Declaring Action Bar Drawer Toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        initializeDrawer();
        initializeRest();
        initializeComponents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inate the menu; this adds items to the action bar if it is present.
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
                    Toast.makeText(getApplicationContext(), "The Item Clicked is: " + recyclerView.getChildPosition(child), Toast.LENGTH_SHORT).show();

                    Intent intent = null;
                    if (recyclerView.getChildPosition(child) == 1) {
                        intent = new Intent(getApplicationContext(), UserInfoActivity.class);
                    } else if (recyclerView.getChildPosition(child) == 2) {
                        intent = new Intent(getApplicationContext(), CreatePartyActivity.class);
                    } else if (recyclerView.getChildPosition(child) == 3) {
                        intent = new Intent(getApplicationContext(), MapActivity.class);
                    } else if (recyclerView.getChildPosition(child) == 4) {
                        intent = new Intent(getApplicationContext(), MessagesActivity.class);
                    } else if (recyclerView.getChildPosition(child) == 5) {
                        intent = new Intent(getApplicationContext(), PartyInfoActivity.class);
                    } else if (recyclerView.getChildPosition(child) == 6) {
                        intent = new Intent(getApplicationContext(), HistoryActivity.class);
                    } else if (recyclerView.getChildPosition(child) == 7) {
                        intent = new Intent(getApplicationContext(), IPSettingsActivity.class);
                    } else if (recyclerView.getChildPosition(child) == 8) {
                        session.logoutUser();
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
                .setEndpoint(BASE_URL)
                .setConverter(new JacksonConverter())
                .build();

        geYouService = restAdapter.create(GeYouService.class);
    }

    public void initializeComponents() {
        eTxtFName = (EditText) findViewById(R.id.editTextFirstName);
        eTxtLName = (EditText) findViewById(R.id.editTextLastName);
        eTxtEmail = (EditText) findViewById(R.id.editTextEmailReg);
        eTxtPassword = (EditText) findViewById(R.id.editTextPasswordReg);
        eTxtConfirmPass = (EditText) findViewById(R.id.editTextConfirmPassReg);

        btnRegister = (Button) findViewById(R.id.btnRegisterReg);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eTxtConfirmPass.getText().toString().equals(eTxtPassword.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Passwords match!", Toast.LENGTH_SHORT).show();
                    User nUser = new User();

                    nUser.setfName(eTxtFName.getText().toString());
                    nUser.setlName(eTxtLName.getText().toString());
                    nUser.setEmail(eTxtEmail.getText().toString());
                    nUser.setPassword(eTxtPassword.getText().toString());
                    registerCredentials(nUser);
                } else {
                    eTxtPassword.setText("");
                    eTxtConfirmPass.setText("");
                    Toast.makeText(getApplicationContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnCancel = (Button) findViewById(R.id.btnCancelRegister);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearInput();
            }
        });

        geYouService.getUserById(1, new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                Toast.makeText(RegisterActivity.this, "data here : " + user.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(RegisterActivity.this, "unsuccessful", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void registerCredentials(User u) {
        geYouService.createUser(u, new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                Toast.makeText(RegisterActivity.this, "Successfully created user:" + user.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(RegisterActivity.this, "Unsuccessfully created user.", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void clearInput() {
        eTxtFName.setText("");
        eTxtLName.setText("");
        eTxtEmail.setText("");
        eTxtPassword.setText("");
        eTxtConfirmPass.setText("");
    }
}

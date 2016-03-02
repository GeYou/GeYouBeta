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
import com.thesis.geyoubeta.SessionManager;

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

    String TITLES[] = {"User Info", "Create Party", "Map", "Messages", "Party Info", "History", "IP Settings", "Logout"};

    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    DrawerLayout Drawer;                                  // Declaring DrawerLayout
    android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;                  // Declaring Action Bar Drawer Toggle;
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
                .setEndpoint(session.getBaseURL())
                .setConverter(new JacksonConverter())
                .build();

        geYouService = restAdapter.create(GeYouService.class);
    }

    public void initializeComponents() {
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
                    Toast.makeText(getApplicationContext(), "Passwords match!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(UserInfoActivity.this, "Successfully updated user: " + user.toString(), Toast.LENGTH_LONG).show();
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

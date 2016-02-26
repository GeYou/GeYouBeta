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
import android.widget.ListView;
import android.widget.Toast;

import com.thesis.geyoubeta.R;
import com.thesis.geyoubeta.adapter.NavDrawerAdapter;
import com.thesis.geyoubeta.adapter.PartyMemberListAdapter;
import com.thesis.geyoubeta.entity.Party;
import com.thesis.geyoubeta.entity.PartyMember;
import com.thesis.geyoubeta.entity.User;
import com.thesis.geyoubeta.service.GeYouService;
import com.thesis.geyoubeta.service.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.JacksonConverter;

public class PartyMembersActivity extends ActionBarActivity {

    private SessionManager session;

    private EditText eTxtPartyMember;
    private Button btnAdd;
    private ListView listView;

    public ArrayList<String> partyMembers;
    private PartyMemberListAdapter partyMembersAdapter;

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
        setContentView(R.layout.activity_party_members);

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
                .setEndpoint(session.getBaseURL())
                .setConverter(new JacksonConverter())
                .build();

        geYouService = restAdapter.create(GeYouService.class);
    }

    public void initializeComponents() {
        partyMembers = new ArrayList<String>();

        getPartyMembers();

        listView = (ListView) findViewById(R.id.listViewPartyMembers);
        partyMembersAdapter = new PartyMemberListAdapter(partyMembers, this);
        listView.setAdapter(partyMembersAdapter);

        eTxtPartyMember = (EditText) findViewById(R.id.editTextPartyMember);
        btnAdd = (Button) findViewById(R.id.btnAddPartyMember);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                geYouService.validateEmail(eTxtPartyMember.getText().toString(), new Callback<Boolean>() {
                    @Override
                    public void success(Boolean aBoolean, Response response) {
                        if (aBoolean) {
                            Toast.makeText(getApplicationContext(), "User exist.", Toast.LENGTH_LONG).show();
                            addPartyMember(eTxtPartyMember.getText().toString());
                        } else {
                            Toast.makeText(getApplicationContext(), "User does not exist.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
            }
        });
    }

    public void getPartyMembers() {
        geYouService.getPartyMembers(session.getPartyId(), new Callback<List<User>>() {
            @Override
            public void success(List<User> users, Response response) {
                if (users != null) {
                    setPartyMembers(users);
                } else {
                    partyMembers.add("No party members yet.");
                }

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void addPartyMember(String email) {
        geYouService.getUserByEmail(email, new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                if (user != null) {
                    final Party p = new Party();
                    p.setId(session.getPartyId());

                    final User u = user;
                    geYouService.checkPartyMembership(p.getId(), user.getId(), new Callback<Boolean>() {
                        @Override
                        public void success(Boolean aBoolean, Response response) {
                            if (aBoolean) {
                                Toast.makeText(getApplicationContext(), "User already in the party!", Toast.LENGTH_LONG).show();
                            } else {
                                PartyMember pm = new PartyMember();
                                pm.setUser(u);
                                pm.setParty(p);
                                geYouService.addMember(pm, new Callback<PartyMember>() {
                                    @Override
                                    public void success(PartyMember partyMember, Response response) {
                                        Toast.makeText(getApplicationContext(), "Successfully add to party.", Toast.LENGTH_LONG).show();
                                        partyMembers.add(partyMember.getUser().getEmail());
                                        //partyMembersAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Not such user found.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void setPartyMembers(List<User> u) {
        for (User user : u) {
            partyMembers.add(user.getEmail());
        }
        for (int i = 0; i < partyMembers.size(); i++) {
            Toast.makeText(getApplicationContext(), i + ": " + partyMembers.get(i), Toast.LENGTH_SHORT).show();
        }
    }
}

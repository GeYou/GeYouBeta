package com.thesis.geyoubeta;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.thesis.geyoubeta.activity.CreatePartyActivity;
import com.thesis.geyoubeta.activity.HistoryActivity;
import com.thesis.geyoubeta.activity.IPSettingsActivity;
import com.thesis.geyoubeta.activity.MapActivity;
import com.thesis.geyoubeta.activity.MessagesActivity;
import com.thesis.geyoubeta.activity.PartyInfoActivity;
import com.thesis.geyoubeta.activity.UserInfoActivity;
import com.thesis.geyoubeta.adapter.NavDrawerAdapter;

/**
 * Created by ivanwchua on 3/2/2016.
 */
public class NavDrawer {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DrawerLayout Drawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;
    private Context ctx;
    private View view;

    private SessionManager session;

    public NavDrawer(Context c ,View v ,RecyclerView recyclerView, RecyclerView.Adapter adapter, RecyclerView.LayoutManager layoutManager, DrawerLayout d, ActionBarDrawerToggle toggle, Toolbar t) {
        this.ctx = c;
        this.view = v;
        this.mRecyclerView = recyclerView;
        this.mAdapter = adapter;
        this.mLayoutManager = layoutManager;
        this.Drawer = d;
        this.mDrawerToggle = toggle;
        toolbar = t;

        session = new SessionManager(ctx);
    }

    public void initialize(String[] TITLES) {
        toolbar = (Toolbar) view.findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);                   // Setting toolbar as the ActionBar with setSupportActionBar() call
        toolbar.setTitle("GeYou");

        mRecyclerView = (RecyclerView) view.findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View
        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size
        mAdapter = new NavDrawerAdapter(TITLES);
        mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView

        final GestureDetector mGestureDetector = new GestureDetector(ctx, new GestureDetector.SimpleOnGestureListener() {

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
                        intent = new Intent(ctx, UserInfoActivity.class);
                    } else if (recyclerView.getChildPosition(child) == 2) {
                        intent = new Intent(ctx, CreatePartyActivity.class);
                    } else if (recyclerView.getChildPosition(child) == 3) {
                        intent = new Intent(ctx, MapActivity.class);
                    } else if (recyclerView.getChildPosition(child) == 4) {
                        intent = new Intent(ctx, MessagesActivity.class);
                    } else if (recyclerView.getChildPosition(child) == 5) {
                        intent = new Intent(ctx, PartyInfoActivity.class);
                    } else if (recyclerView.getChildPosition(child) == 6) {
                        intent = new Intent(ctx, HistoryActivity.class);
                    } else if (recyclerView.getChildPosition(child) == 7) {
                        intent = new Intent(ctx, IPSettingsActivity.class);
                    } else if (recyclerView.getChildPosition(child) == 8) {
                        session.logoutUser();
                    }

                    if (intent != null) {
                        ctx.startActivity(intent);
                    }

                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

            }
        });

        mLayoutManager = new LinearLayoutManager(ctx);                 // Creating a layout Manager
        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager
        Drawer = (DrawerLayout) view.findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view
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
}

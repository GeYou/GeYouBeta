/*
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Copyright (C) 2015 Ivan Wesley Chua and Jethro Divino
 */

package com.thesis.geyoubeta.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.thesis.geyoubeta.NavDrawer;
import com.thesis.geyoubeta.R;
import com.thesis.geyoubeta.adapter.NavDrawerAdapter;
import com.thesis.geyoubeta.SessionManager;
import com.thesis.geyoubeta.adapter.PlaceAutoCompleteAdapter;

public class MainActivity extends ActionBarActivity {

    private SessionManager session;

    private EditText eTxtTest;

    private NavDrawer navDrawer;

    public LatLng latlng = new LatLng(0,0);
    public String loc = new String("");
    private static final int GOOGLE_API_CLIENT_ID = 0;
    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutoCompleteAdapter mAutoCompleteAdapter;
    private AutoCompleteTextView mAutocompleteView;
    private static final String TAG = "MainActivity";
    private static final LatLngBounds BOUNDS_CEBU = new LatLngBounds(
            new LatLng(10.0800, 123.03000), new LatLng(10.6800, 123.09000));

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
        setContentView(R.layout.activity_main);

        //connectGAPi();

        initializeDrawer();
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


        final GestureDetector mGestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });
        mRecyclerView.setAdapter(mAdapter);

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

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

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

    public void initializeComponents() {
        navDrawer = new NavDrawer(this);


        AutoCompleteTextView autocompleteView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTest);
        //autocompleteView.setAdapter(new PlaceAutoCompleteAdapter(getApplicationContext(), R.layout.autocomplete_list_item));

        autocompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get data associated with the specified position
                // in the list (AdapterView)
                String description = (String) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), description, Toast.LENGTH_SHORT).show();
            }
        });



        //eTxtTest = (EditText) findViewById(R.id.editTextTest);

    }

//    public void connectGAPi() {
//        if (isConnectingToInternet(getApplicationContext()) == false) {
//            showdialog();
//        } else if(isConnectingToInternet(getApplicationContext()) == true) {
//            mGoogleApiClient = new GoogleApiClient.Builder(this)
//                    .enableAutoManage(this, GOOGLE_API_CLIENT_ID /* clientId */, this)
//                    .addApi(Places.GEO_DATA_API)
//                    .addApi(Places.PLACE_DETECTION_API)
//                    .build();
//
//            // Retrieve the AutoCompleteTextView that will display Place suggestions.
//            mAutocompleteView = (AutoCompleteTextView)
//                    findViewById(R.id.autoCompleteTest);
//
//            // Register a listener that receives callbacks when a suggestion has been selected
//            mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);
//
//
//            // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
//            // the entire world.
//            mAutoCompleteAdapter = new PlaceAutoCompleteAdapter(this, android.R.layout.simple_list_item_1, mGoogleApiClient, BOUNDS_CEBU, null);
//            mAutocompleteView.setAdapter(mAutoCompleteAdapter);
//
////            Button clearButton = (Button) findViewById(R.id.button_clear);
////            clearButton.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View v) {
////                    mAutocompleteView.setText("");
////                }
////            });
//        }
//    }
//
////    public void onClick_Search(View v) {
////
////        if (latlng.longitude == 0 && latlng.latitude == 0) {
////            Toast.makeText(this, "Please input a complete address", Toast.LENGTH_LONG).show();
////        } else {
////            double[] intentdouble = new double[2];
////
////            intentdouble[1] = latlng.longitude;
////            intentdouble[0] = latlng.latitude;
////            Intent myIntent = new Intent(MainActivity.this, MapsActivity.class);
////            myIntent.putExtra("string", loc); //Optional parameters
////            myIntent.putExtra("key", intentdouble); //Optional parameters
////            latlng = new LatLng(0,0);
////            MainActivity.this.startActivity(myIntent);
////        }
////    }
//
//    /**
//     * Listener that handles selections from suggestions from the AutoCompleteTextView that
//     * displays Place suggestions.
//     * Gets the place id of the selected item and issues a request to the Places Geo Data API
//     * to retrieve more details about the place.
//     *
//     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(com.google.android.gms.common.api.GoogleApiClient,
//     * String...)
//     */
//    private AdapterView.OnItemClickListener mAutocompleteClickListener
//            = new AdapterView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            /*
//             Retrieve the place ID of the selected item from the Adapter.
//             The adapter stores each Place suggestion in a PlaceAutocomplete object from which we
//             read the place ID.
//              */
//            final PlaceAutoCompleteAdapter.PlaceAutocomplete item = mAutoCompleteAdapter.getItem(position);
//            final String placeId = String.valueOf(item.placeId);
//            loc.concat(item.description.toString());
//            Log.i(TAG, "Autocomplete item selected: " + item.description);
//
//            /*
//             Issue a request to the Places Geo Data API to retrieve a Place object with additional
//              details about the place.
//              */
//            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
//                    .getPlaceById(mGoogleApiClient, placeId);
//            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
//            Log.i(TAG, "Called getPlaceById to get Place details for " + item.placeId);
//        }
//    };
//
//    /**
//     * Callback for results from a Places Geo Data API query that shows the first place result in
//     * the details view on screen.
//     */
//    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
//            = new ResultCallback<PlaceBuffer>() {
//        @Override
//        public void onResult(PlaceBuffer places) {
//            if (!places.getStatus().isSuccess()) {
//                // Request did not complete successfully
//                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
//                places.release();
//                return;
//            }
//            // Get the Place object from the buffer.
//            final Place place = places.get(0);
//
//            // Format details of the place for display and show it in a TextView.
//
//            latlng = place.getLatLng();
//
//
//            Log.i(TAG, "Place details received: " + place.getName());
//
//            places.release();
//        }
//    };
//    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
//                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
//        Log.e(TAG, res.getString(R.string.place_details, name, id, address, phoneNumber,
//                websiteUri));
//        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
//                websiteUri));
//
//    }
//
//    /**
//     * Called when the Activity could not connect to Google Play services and the auto manager
//     * could resolve the error automatically.
//     * In this case the API is not available and notify the user.
//     *
//     * @param connectionResult can be inspected to determine the cause of the failure
//     */
//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//
//        // TODO(Developer): Check error code and notify the user of error state and resolution.
//        // Toast.makeText(this,
//        //       "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
//        //     Toast.LENGTH_SHORT).show();
//    }
//
////    public void showdialog(){
////        Dialogfrag df = new Dialogfrag();
////        df.show(getFragmentManager(), "dfrag");
////    }
//
//    public boolean isConnectingToInternet(Context applicationContext){
//        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo ni = cm.getActiveNetworkInfo();
//        if (ni == null) {
//            // There are no active networks.
//            return false;
//        } else
//            return true;
//
//    }
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (isConnectingToInternet(getApplicationContext()) == false) {
//            //showdialog();
//        }
//    }
}
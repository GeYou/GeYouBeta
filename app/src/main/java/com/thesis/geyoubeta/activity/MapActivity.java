/*
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Copyright (C) 2015 Ivan Wesley Chua and Jethro Divino
 */

package com.thesis.geyoubeta.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.thesis.geyoubeta.NavDrawer;
import com.thesis.geyoubeta.R;
import com.thesis.geyoubeta.SessionManager;
import com.thesis.geyoubeta.adapter.NavDrawerAdapter;
import com.thesis.geyoubeta.entity.History;
import com.thesis.geyoubeta.entity.Party;
import com.thesis.geyoubeta.entity.PartyMember;
import com.thesis.geyoubeta.entity.User;
import com.thesis.geyoubeta.parser.DirectionJSONDijkstra;
import com.thesis.geyoubeta.parser.DistanceJSONParser;
import com.thesis.geyoubeta.service.GeYouService;
import com.thesis.geyoubeta.service.MyService;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.JacksonConverter;

public class MapActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener{

    private SessionManager session;

    private RestAdapter restAdapter;
    private GeYouService geYouService;
    private NavDrawer navDrawer;
    private List<PartyMember> partyMemberLocation;
    private Timer timer;

    String TITLES[] = {"User Info", "Create Party", "Map", "Messages", "Party Info", "History", "IP Settings", "Logout"};

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    DrawerLayout Drawer;
    android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;
    public static final String TAG = MapActivity.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;
    private TextView tvDistanceDuration;
    private LatLng origin;
    private LatLng dest = null;
    private ArrayList<LatLng> markerPoints;
    private String url;
    private LatLng currentLoc = null;
    private LatLng pmCurrentLoc =null;
    private String pmName = "null";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        setUpMapIfNeeded();
        initializeDrawer();
        initializeRest();
        initializeComponents();

        mMap.setMyLocationEnabled(true);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        tvDistanceDuration = (TextView) findViewById(R.id.tv_distance_time);
        timer = new Timer();

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

        mMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        final double originLatitude = location.getLatitude();
        final double originLongitude = location.getLongitude();
        final LatLng origin2 = new LatLng(originLatitude, originLongitude);
        origin = origin2;
        currentLoc = origin2;

        Log.d(TAG,"OGIRIN:"+origin.toString());
        // Initializing
        markerPoints = new ArrayList<LatLng>();

        Log.i(TAG, "get party destination");
        getPartyDestination();
    }

    public void drawPath(){
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        // Getting Map for the SupportMapFragment
        mMap = fm.getMap();
        if(dest == null){
            Log.i(TAG, "destination null");
        }
        if (mMap != null) {
            if (dest != null) {
//                mMap.clear();
                Log.i(TAG, "destination:"+dest.toString());
                mMap.addMarker(new MarkerOptions()
                        .position(dest)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
                url = getDirectionsUrl(origin, dest);
                DL downloadTask = new DL();
                // Start downloading json data from Google Directions API
                downloadTask.execute(url);
            } else {
                Log.i(TAG,"No party destination");
            }
        }else{Log.i(TAG,"Map is null");}
    }

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
        return url;
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Error downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {
        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }
        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DistanceJSONParser parser = new DistanceJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }
        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";

            if(result.size()<1){
                Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }
            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList();
                lineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = result.get(i);

                for(int j=0;j <path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    if(j==0){ // Get distance from the list
                        distance = (String)point.get("distance");
                        continue;
                    }else if(j==1){ // Get duration from the list
                        duration = (String)point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(7);
                lineOptions.color(Color.rgb(26, 255, 26));
            }

//            tvDistanceDuration.setText("Distance:"+distance + ", Duration:"+duration);
            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } catch (ClassCastException e){
                Log.e("onConnected error catch",toString());
            }
        }
        else {
            handleNewLocation(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG,"location changed");
        handleNewLocation(location);
        updateUserLocation(location);
        addHistory(location);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            try {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                mGoogleApiClient.disconnect();
            } catch (ClassCastException e){
                Log.e("onPause error",toString());
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        startService(new Intent(getApplicationContext(), MyService.class));
//        if (getPartyMemberLocation()) {
            asyncGetPartyMemberLocation();

    }
    @Override
    protected void onStop() {
        super.onStop();

        stopService(new Intent(getApplicationContext(), MyService.class));

        timer.cancel();
        timer.purge();
    }

    private class DownloadTaskDistance extends AsyncTask<String, Void, String>{
        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTaskDistance parserTask = new ParserTaskDistance();
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTaskDistance extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DistanceJSONParser parser = new DistanceJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }
        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            String distance = "";
            String duration = "";

            if(result.size()<1){
                Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }
            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                List<HashMap<String, String>> path = result.get(i);

                for(int j=0;j <path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    if(j==0){ // Get distance from the list
                        distance = (String)point.get("distance");
                        continue;
                    }else if(j==1){ // Get duration from the list
                        duration = (String)point.get("duration");
                        continue;
                    }
                }
            }
            tvDistanceDuration.setText("Distance:" + distance + ", Duration:" + duration);
        }
    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        mMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        currentLoc = latLng;
        Log.d(TAG,"current latlang:"+currentLoc.toString());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        mMap.animateCamera(yourLocation);

        getPartyMemberLocation();
        if(dest!=null) {
            Log.d(TAG,"location changed, updating distance calculator");
            String url = getDirectionsUrl(latLng, dest);
            DownloadTaskDistance downloadTask = new DownloadTaskDistance();
            downloadTask.execute(url);
        }else{
            Log.i(TAG,"Location not handled");
        }
    }

    public void getPartyDestination(){
        geYouService.getPartyById(session.getPartyId(), new Callback<Party>() {
            @Override
            public void success(Party party, Response response) {
                setDestination(party);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void setDestination(Party party){
        Log.i(TAG, "set party destination");
        if (party != null){
            Log.i(TAG,party.getName().toString());
            dest = new LatLng(party.getDestLat(), party.getDestLong());
            Log.i(TAG,"Destination: "+ dest.toString());
            drawPath();
        }
    }

    public boolean getPartyMemberLocation(){
        final boolean[] retval = {false};
        geYouService.getMembersByParty(session.getPartyId(), new Callback<List<PartyMember>>() {
            @Override
            public void success(List<PartyMember> partyMembers, Response response) {
                setPartyMemberLocation(partyMembers);
                if (!partyMembers.isEmpty()) {
                    retval[0] = true;
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
        return retval[0];
    }

    public void setPartyMemberLocation(List<PartyMember> pm){
        if(pm.isEmpty()) {
            Log.i(TAG, "No party member location");
                Toast.makeText(getApplicationContext(), "The party has no other members.", Toast.LENGTH_SHORT).show();
        }else{
            mMap.clear();
            mMap.addMarker(new MarkerOptions()
                    .position(dest)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);
//            LatLng pos;
            for (PartyMember partyMember : pm) {
                if (partyMember.getLastLat() !=null && partyMember.getLastLong() != null) {
                    if(session.getUserId()!=partyMember.getUser().getId()) {
                        pmCurrentLoc = new LatLng(partyMember.getLastLat(), partyMember.getLastLong());
                        if(currentLoc!=null) {
                            Log.i(TAG,"currentLoc not null");
                            pmName = partyMember.getUser().getfName();
                            String url = getDirectionsUrl(currentLoc, pmCurrentLoc);
                            DownloadTaskDistancePm downloadTaskPm = new DownloadTaskDistancePm();
                            downloadTaskPm.execute(url);
//                            mMap.addMarker(new MarkerOptions()
//                                    .position(pm)
//                                    .title(partyMember.getUser().getfName())
//                                    .snippet(pmDistance)
//                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                        }else{
                            Log.i(TAG,"currentLoc null");
                            mMap.addMarker(new MarkerOptions()
                                    .position(pmCurrentLoc)
                                    .title(partyMember.getUser().getfName())
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                        }
                    }
                }else{
                    //Toast.makeText(getApplicationContext(), partyMember.getUser().getfName() +" is not active.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void asyncGetPartyMemberLocation(){
        new AsyncTask<Void, Void, String>() {

            protected void onPreExecute() {

            }

            @Override
            protected String doInBackground(Void... params) {
                timer.scheduleAtFixedRate(new TimerTask() {
                    public void run() {
                        try {
                            getPartyMemberLocation();
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    }
                }, 0, 8000);
                return "";
            }

            @Override
            protected void onPostExecute(String msg) {

            }
        }.execute(null, null, null);
    }

    private class DownloadTaskDistancePm extends AsyncTask<String, Void, String>{
        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTaskDistancePm parserTask = new ParserTaskDistancePm();
            parserTask.execute(result);
        }
    }

    private class ParserTaskDistancePm extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DistanceJSONParser parser = new DistanceJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }
        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            String distance ="null";
            String duration = "";

            if(result.size()<1){
                Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }
            // Traversing through all the routes
            for(int i=0;i<result.size();i++) {
                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    if (j == 0) { // Get distance from the list
                        distance = (String) point.get("distance");
                        continue;
                    } else if (j == 1) { // Get duration from the list
                        duration = (String) point.get("duration");
                        continue;
                    }
                }
            }
//            pmDistance = distance;
//            Log.i(TAG,"PMDISTANCE:" +pmDistance);

            mMap.addMarker(new MarkerOptions()
                    .position(pmCurrentLoc)
                    .title(pmName)
                    .snippet("Distance:"+distance)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }

    }

    public void updateUserLocation(Location l){
        Log.i("MAPS: ", "updating location...");

        User u = new User();
        Party p = new Party();
        PartyMember pm = new PartyMember();
        u.setId(session.getUserId());
        p.setId(session.getPartyId());

        pm.setId(session.getPartyMemberId());
        pm.setUser(u);
        pm.setParty(p);
        pm.setStatus("A");
        pm.setLastLat(l.getLatitude());
        pm.setLastLong(l.getLongitude());

        geYouService.editMember(pm, new Callback<PartyMember>() {
            @Override
            public void success(PartyMember partyMember, Response response) {
                Log.i("LOGIN: ", "Updated location.");
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void addHistory(Location l) {
        User u = new User();
        Party p = new Party();

        u.setId(session.getUserId());
        p.setId(session.getPartyId());

        History h = new History();
        h.setUser(u);
        h.setParty(p);
        h.setType("P");
        h.setStartLat(l.getLatitude());
        h.setStartLong(l.getLongitude());

        geYouService.addHistory(h, new Callback<History>() {
            @Override
            public void success(History history, Response response) {
                Log.i("Servicess: ", "made new history");
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private class DL extends AsyncTask<String, Void, String>{
        // Downloading data in non-ui thread
        //List<LatLng> retVal;
        @Override
        protected String doInBackground(String... url) {
            Log.i(TAG, "BINOGO DOWNLOAD URL");
            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }
        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.i(TAG, "BINOGO DOWNLOAD URL");
            PT parserTask = new PT();
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    private class PT extends AsyncTask<String, Integer, List<LatLng>> {
        // Parsing the data in non-ui thread
        List<LatLng> retVal;
        @Override
        protected List<LatLng> doInBackground(String... jsonData) {
            Log.i(TAG, "BINOGO DOWNLOAD URL");
            JSONObject jObject;
            List <LatLng> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionJSONDijkstra parser = new DirectionJSONDijkstra();

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("URL","PTeaw");
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }
        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<LatLng> result) {
            Log.i(TAG, "BINOGO POST DOWNLOAD URL");
            Log.d("POST", String.valueOf(result.size()));
            parseRetVal(result);
        }

        public void parseRetVal(List<LatLng> res){
            String waypoint="";
            for(LatLng point:res){
                if(point!=res.get(0)&&point!=res.get(res.size()-1)){
                    if(point!=res.get(res.size()-2)){
                        waypoint = waypoint.concat(String .valueOf(point.latitude)+","+String.valueOf(point.longitude)+"|");
                    }else{
                        waypoint = waypoint.concat(String .valueOf(point.latitude)+","+String.valueOf(point.longitude));
                    }
                }
            }
            Log.d("WAYPOINT", waypoint);
            String url = getDirectionsWayPointsUrl(res.get(res.size()-1), res.get(0), waypoint);
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);
        }
    }

    private String getDirectionsWayPointsUrl(LatLng origin,LatLng dest, String waypoint){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;
        // Sensor enabled
        String waypoints = "waypoints="+waypoint;

        String API = "key=AIzaSyAVFXzFyuw3noxN6_yLviSUW_dipgJJQSo";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+API;
        String parameters2 = str_origin+"&"+str_dest+"&"+waypoints+"&"+API;
        // Output format
        String output = "json";
        // Building the url to the web service
        Log.i(TAG, "BINOGO DIRECTIONS URL");
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
        String url2 = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters2;
        Log.d(TAG,url2);
        return url;
    }
}

/*
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Copyright (C) 2015 Ivan Wesley Chua and Jethro Divino
 */

package com.thesis.geyoubeta.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.thesis.geyoubeta.R;
import com.thesis.geyoubeta.SessionManager;
import com.thesis.geyoubeta.entity.History;
import com.thesis.geyoubeta.service.GeYouService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.JacksonConverter;

public class HistoryViewActivity extends ActionBarActivity {

    private RestAdapter restAdapter;
    private GeYouService geYouService;
    private SessionManager session;

    private Integer partyId;

    private Bitmap bitmap;

    private static final String DIR_URL1 = "https://maps.googleapis.com/maps/api/directions/json?origin=";
    private static final String DIR_URL2 = "&destination=";
    private static final String DIR_URL3 = "&waypoints=";

    private static final String STATIC_MAP_URL1 = "https://maps.googleapis.com/maps/api/staticmap?size=";
    private static final String STATIC_MAP_URL_SIZE = "500x500";
    private static final String STATIC_MAP_URL2 = "&path=weight:3%7Ccolor:blue%7Cenc:";
    private static final String STATIC_MAP_URL3 = "&markers=color:red%7Clabel:S%7C";
    private static final String STATIC_MAP_URL4 = "&markers=color:red%7Clabel:E%7C";

    private String dirurl;
    private String staticurl;

    private ImageView imageView;
    private LatLng start;
    private LatLng end;
    private String waypoints;
    private String overviewPolyline;
    private JSONArray json;

    private List<History> historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_view);

        Bundle bundle = getIntent().getExtras();

        Log.e("FROM HISTORY ACTIVITY: ", "id: "+bundle.getInt("partyId", -1));

        partyId = bundle.getInt("partyId", -1);
        imageView = (ImageView) findViewById(R.id.imageViewStaticMaps);

        session = new SessionManager(getApplicationContext());

        initializeRest();
        initializeCOmponents();
    }

    public void initializeRest() {
        restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(session.getBaseURL())
                .setConverter(new JacksonConverter())
                .build();

        geYouService = restAdapter.create(GeYouService.class);
    }

    public void initializeCOmponents() {
        Log.e("HISTROTY VIEW: ", partyId.toString());
        waypoints = "";
        historyList = new ArrayList<History>();
        getHistoryList();


    }

    public String getDirectionURL() {
        return DIR_URL1 + start.latitude + "," + start.longitude + DIR_URL2 + end.latitude + "," + end.longitude + DIR_URL3 +waypoints;
    }

    public String getStaticURL() {
        return STATIC_MAP_URL1 + STATIC_MAP_URL_SIZE + STATIC_MAP_URL2 + overviewPolyline + STATIC_MAP_URL3 + start.latitude + "," + start.longitude + STATIC_MAP_URL4 + end.latitude + "," + end.longitude;
    }

    public void makeWaypointsParam() {
        for (int i = 1, j = 0; i < historyList.size() - 1 && j < 6; i++, j++) {
            waypoints += historyList.get(i).getStartLat();
            waypoints += ",";
            waypoints += historyList.get(i).getStartLong();
            waypoints += "|";
        }
        waypoints += historyList.get(historyList.size() - 2).getStartLat();
        waypoints += ",";
        waypoints += historyList.get(historyList.size() - 2).getStartLong();
        waypoints += "|";
        waypoints += historyList.get(historyList.size() - 1).getStartLat();
        waypoints += ",";
        waypoints += historyList.get(historyList.size() - 1).getStartLong();
    }

    public void getStartCoor() {
        start = new LatLng(historyList.get(0).getStartLat(), historyList.get(0).getStartLong());
    }

    public void getEndCoor() {
        end = new LatLng(historyList.get(historyList.size() - 1).getStartLat(), historyList.get(historyList.size() - 1).getStartLong());
    }

    public void getHistoryList() {
        geYouService.getHistoryPoints(partyId, session.getUserId(), new Callback<List<History>>() {
            @Override
            public void success(List<History> histories, Response response) {
                Log.e("HISTROTY VIEW: ", "hello" + histories.toString());
                setHistoryList(histories);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void setHistoryList(List<History> h) {
        for (History history : h) {
            historyList.add(history);
        }
        Log.e("HISTROTY VIEW: ", "booooo" + historyList.toString());
        if (historyList.size() > 0 && historyList != null) {
            getStartCoor();
            getEndCoor();

            Log.e("HISTROTY VIEW: ", "HERO" + historyList.toString());
            Log.e("HISTROTY VIEW: ", start.toString());
            Log.e("HISTROTY VIEW: ", end.toString());

            makeWaypointsParam();

            dirurl = getDirectionURL();

            Log.e("HISTROTY VIEW: ", "dirurl: " +dirurl);
            new RetrieveData(dirurl).execute();
            Log.e("HISTROTY VIEW: ", "OP out: " +overviewPolyline);
            Log.e("HISTROTY VIEW: ", "static url: " +staticurl);
        } else {
            Toast.makeText(getApplicationContext(), "No Path.", Toast.LENGTH_SHORT).show();
        }
    }

    public class RetrieveData extends AsyncTask<Void, Void, String> {
        InputStream is = null;
        String result = null;
        String URL = "";

        public RetrieveData(String URL){
            this.URL = URL;
        }
        @Override
        protected String doInBackground(Void... params) {

            JSONObject json_data = null;

            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                java.net.URL url = new URL(URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                iStream = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
                StringBuffer sb = new StringBuffer();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                result = sb.toString();
                br.close();
            }catch(Exception e){
                Log.d("Error downloading url", e.toString());
            }finally{
                try {
                    iStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                urlConnection.disconnect();
            }
            try {
                json_data = new JSONObject(result);
                Log.e("DATAAA", "data: " + json_data.toString());
            } catch (Exception e) {
                Log.e("Fail 3", e.toString());
            }
            try {
                JSONArray routa = json_data.getJSONArray("routes");
                Log.e("DATAAA", "data: " +routa.toString());
                overviewPolyline = routa.getJSONObject(0).getJSONObject("overview_polyline").getString("points");
                Log.e("HISTROTY VIEW: ", "OP: " +overviewPolyline);
                staticurl = getStaticURL();
                Log.e("HISTROTY VIEW: ", "static url: " + staticurl);

            } catch (Exception e) {
                Log.e("JSON", "There was an error parsing the JSON", e);
            }

            return "";
        }

        @Override
        protected void onPostExecute(String msg) {
            new LoadImage().execute(staticurl);
        }
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
        }

        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {

            if (image != null) {
                imageView.setImageBitmap(image);
            } else {
                Toast.makeText(HistoryViewActivity.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

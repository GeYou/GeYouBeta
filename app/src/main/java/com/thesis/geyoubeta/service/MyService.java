/*
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Copyright (C) 2015 Ivan Wesley Chua and Jethro Divino
 */

package com.thesis.geyoubeta.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.thesis.geyoubeta.Listener.GeyouLocationListener;
import com.thesis.geyoubeta.SessionManager;
import com.thesis.geyoubeta.entity.History;
import com.thesis.geyoubeta.entity.Party;
import com.thesis.geyoubeta.entity.PartyMember;
import com.thesis.geyoubeta.entity.User;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.JacksonConverter;

/**
 * Created by ivanwesleychua on 11/03/2016.
 */
public class MyService extends Service {

    private SessionManager session;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private RestAdapter restAdapter;
    private GeYouService geYouService;

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {

        Log.i("Servicess: ", "Started service.");

        initializeComponents();

        if (session.getPartyId() != -1) {
            Log.i("Servicess: ", "updtr");
            updateUserLocation(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
        } else {
            Log.i("Servicess: ", "updt21r");
        }

        final Handler h = new Handler();
        final int delay = 4000;

        h.postDelayed(new Runnable(){
            public void run(){

                if (session.getPartyId() == -1) {
                    checkForActiveParty();
                } else {
                    //check if party has to be deactivated
                    try {
                        if(DateFormat.getDateTimeInstance().parse(session.getPartyEnd()).before(new Date())) {
                            Toast.makeText(getApplicationContext(), "Party is to be deactivated.", Toast.LENGTH_SHORT).show();

                            locationManager.removeUpdates(locationListener);
                            deactivateParty();
                            deletePartySession();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                h.postDelayed(this, delay);
            }
        }, delay);

        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }

    public void initializeComponents() {
        session = new SessionManager(getApplicationContext());

        restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(session.getBaseURL())
                .setConverter(new JacksonConverter())
                .build();

        geYouService = restAdapter.create(GeYouService.class);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new GeyouLocationListener(session, geYouService, getApplicationContext());
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 3, locationListener);
    }

    public void checkForActiveParty() {
        geYouService.getActiveParty(session.getUserId(), new Callback<PartyMember>() {
            @Override
            public void success(PartyMember partyMember, Response response) {
                if (partyMember.getId() != null) {
                    session.setPartyMemberId(partyMember.getId());
                    session.setActiveParty(partyMember.getParty());

                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 3, locationListener);

                    checkIfHistoryExists();
                } else {
                    Log.i("Servicess: ", "no active party");
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
//                    User u = new User();
//                    Party p = new Party();
//
//                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//
//                    u.setId(session.getUserId());
//                    p.setId(session.getPartyId());
//
//                    History h = new History();
//                    h.setUser(u);
//                    h.setParty(p);
//                    h.setStartLat(lastKnownLocation.getLatitude());
//                    h.setStartLong(lastKnownLocation.getLongitude());

                    addHistory();
                } else {
                    Log.i("Servicess: ", "has history");
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void deactivateParty() {
        geYouService.getPartyMemberByUserAndParty(session.getPartyId(), session.getUserId(), new Callback<PartyMember>() {
            @Override
            public void success(PartyMember partyMember, Response response) {
                if (partyMember.getId() != null) {
                    partyMember.setStatus("I");
                    geYouService.editMember(partyMember, new Callback<PartyMember>() {
                        @Override
                        public void success(PartyMember partyMember, Response response) {
                            if (partyMember.getId() != null) {
                                Toast.makeText(getApplicationContext(), "Party is deactivated.", Toast.LENGTH_SHORT).show();

                            }
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
    }

    public void updateUserLocation(Location l){
        Log.i("SERVICESSS", "updating location...");

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
                Log.i("SERVICESSS", "Updated location.");
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void setUserOrigin(Location l){
        Toast.makeText(getApplicationContext(), "Setting user origin.", Toast.LENGTH_SHORT).show();
        User u = new User();
        u.setId(session.getUserId());
    }

    public void addHistory() {
        User u = new User();
        Party p = new Party();

        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        u.setId(session.getUserId());
        p.setId(session.getPartyId());

        History h = new History();
        h.setUser(u);
        h.setParty(p);
        h.setStartLat(lastKnownLocation.getLatitude());
        h.setStartLong(lastKnownLocation.getLongitude());

        geYouService.addHistory(h, new Callback<History>() {
            @Override
            public void success(History history, Response response) {
                Log.i("Servicess: ", "made history");
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    //make add history
    //attach it to on loc change
    //check other updlate loc to also add history

    public void deletePartySession() {
        session.clearActiveParty();
    }
}

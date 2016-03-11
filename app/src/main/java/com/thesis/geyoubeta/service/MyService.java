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
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
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

    private RestAdapter restAdapter;
    private GeYouService geYouService;

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {

        Toast.makeText(getApplicationContext(), "Started service.", Toast.LENGTH_SHORT).show();

        initializeRestAndSession();

        final Handler h = new Handler();
        final int delay = 3000; //milliseconds

        h.postDelayed(new Runnable(){
            public void run(){

                if (session.getPartyId() == -1) {
                    checkForActiveParty();
                } else {
                    //check if party has to be deactivated
                    try {
                        if(DateFormat.getDateTimeInstance().parse(session.getPartyEnd()).before(new Date())) {
                            Toast.makeText(getApplicationContext(), "Party is to be deactivated.", Toast.LENGTH_SHORT).show();

                            deactivateParty();
                            deletePartySession();
                        } else {
                            //track user loc
                            //updateUserLocation();
                            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                            //Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            LocationListener locationListener = new GeyouLocationListener(session, geYouService, getApplicationContext());
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
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

    public void initializeRestAndSession() {
        session = new SessionManager(getApplicationContext());

        restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(session.getBaseURL())
                .setConverter(new JacksonConverter())
                .build();

        geYouService = restAdapter.create(GeYouService.class);
    }

    public void checkForActiveParty() {
        geYouService.getActiveParty(session.getUserId(), new Callback<PartyMember>() {
            @Override
            public void success(PartyMember partyMember, Response response) {
                if (partyMember.getId() != null) {
                    session.setPartyMemberId(partyMember.getId());
                    session.setActiveParty(partyMember.getParty());

                    checkIfHistoryExists();
                } else {
                    Toast.makeText(getApplicationContext(), "no active party", Toast.LENGTH_SHORT).show();
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
                    User u = new User();
                    Party p = new Party();
                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    u.setId(session.getUserId());
                    p.setId(session.getPartyId());

                    History h = new History();
                    h.setUser(u);
                    h.setParty(p);
                    h.setStartLat((float) lastKnownLocation.getLatitude());
                    h.setStartLong(((float) lastKnownLocation.getLongitude()));

                    geYouService.addHistory(h, new Callback<History>() {
                        @Override
                        public void success(History history, Response response) {
                            Toast.makeText(getApplicationContext(), "made history", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "has history", Toast.LENGTH_SHORT).show();
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

    public void updateUserLocation(){
        Toast.makeText(getApplicationContext(), "updating location...", Toast.LENGTH_SHORT).show();

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location l = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        User u = new User();
        Party p = new Party();
        PartyMember pm = new PartyMember();
        u.setId(session.getUserId());
        p.setId(session.getPartyId());

        pm.setId(session.getPartyMemberId());
        pm.setParty(p);
        pm.setUser(u);
        pm.setStatus("A");
        pm.setLastLat((float) l.getLatitude());
        pm.setLastLong((float) l.getLongitude());

        geYouService.editMember(pm, new Callback<PartyMember>() {
            @Override
            public void success(PartyMember partyMember, Response response) {
                Toast.makeText(getApplicationContext(), "Updated location.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void deletePartySession() {
        session.clearActiveParty();
    }
}

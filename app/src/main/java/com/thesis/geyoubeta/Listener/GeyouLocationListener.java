/*
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Copyright (C) 2015 Ivan Wesley Chua and Jethro Divino
 */

package com.thesis.geyoubeta.Listener;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.thesis.geyoubeta.SessionManager;
import com.thesis.geyoubeta.entity.History;
import com.thesis.geyoubeta.entity.Party;
import com.thesis.geyoubeta.entity.PartyMember;
import com.thesis.geyoubeta.entity.User;
import com.thesis.geyoubeta.service.GeYouService;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ivanwesleychua on 11/03/2016.
 */
public class GeyouLocationListener implements LocationListener{

    private SessionManager session;
    private GeYouService geYouService;
    private Context context;

    public GeyouLocationListener(SessionManager s, GeYouService g, Context c) {
        this.session = s;
        this.geYouService = g;
        this.context = c;
    }

    @Override
    public void onLocationChanged(Location location) {
        updateUserLocation(location);
        addHistory(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void updateUserLocation(Location l){
        Log.i("LISTENER", "listener: Updating location...");

        Log.i("LISTENER", "listener: lat: " + l.getLatitude());
        Log.i("LISTENER", "listener: long: " + l.getLongitude());

        User u = new User();
        Party p = new Party();
        PartyMember pm = new PartyMember();
        u.setId(session.getUserId());
        p.setId(session.getPartyId());

        pm.setId(session.getPartyMemberId());
        pm.setParty(p);
        pm.setUser(u);
        pm.setStatus("A");
        pm.setLastLat(l.getLatitude());
        pm.setLastLong(l.getLongitude());

        geYouService.editMember(pm, new Callback<PartyMember>() {
            @Override
            public void success(PartyMember partyMember, Response response) {
                Log.i("LISTENER", "listener: Updated location.");
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("LISTENER", "listener: failrd Updated location.");
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
}

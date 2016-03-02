/*
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Copyright (C) 2015 Ivan Wesley Chua and Jethro Divino
 */

package com.thesis.geyoubeta;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.thesis.geyoubeta.activity.LoginActivity;
import com.thesis.geyoubeta.entity.Party;
import com.thesis.geyoubeta.entity.User;

import java.util.HashMap;

public class SessionManager {
    public static final String KEY_USER_ID = "id";
    public static final String KEY_USER_FNAME = "fName";
    public static final String KEY_USER_LNAME = "lName";
    public static final String KEY_USER_EMAIL = "email";
    public static final String KEY_USER_PASSWORD = "password";
    public static final String KEY_PARTY_ID = "id";
    public static final String KEY_PARTY_NAME = "pName";
    public static final String KEY_PARTY_START = "startDateTime";
    public static final String KEY_PARTY_END = "endDateTime";
    public static final String KEY_PARTY_DEST = "destination";
    public static final String KEY_PARTY_DEST_LONG = "destLong";
    public static final String KEY_PARTY_DEST_LAT = "destLat";
    public static final String KEY_PARTY_STATUS = "status";
    public static final String KEY_PARTY_CDATE = "createdDate";
    public static final String KEY_PARTY_CBY = "createdBy";
    public static final String KEY_BASE_URL = "ipAddress";
    private static final String PREF_NAME = "GeYouPrefs";
    private static final String IS_LOGIN = "IsLoggedIn";
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(User u) {

        editor.putBoolean(IS_LOGIN, true);
        editor.putInt(KEY_USER_ID, u.getId());
        editor.putString(KEY_USER_FNAME, u.getfName());
        editor.putString(KEY_USER_LNAME, u.getlName());
        editor.putString(KEY_USER_EMAIL, u.getEmail());
        editor.putString(KEY_USER_PASSWORD, u.getPassword());

        editor.commit();
    }

    public void setActiveParty(Party p) {
        editor.putInt(KEY_PARTY_ID, p.getId());
        editor.putString(KEY_PARTY_NAME, p.getName());
        editor.putString(KEY_PARTY_START, p.getStartDateTime());
        editor.putString(KEY_PARTY_END, p.getEndDateTime());
        editor.putString(KEY_PARTY_DEST, p.getDestination());
        editor.putFloat(KEY_PARTY_DEST_LONG, p.getDestLong());
        editor.putFloat(KEY_PARTY_DEST_LAT, p.getDestLat());
        editor.putString(KEY_PARTY_STATUS, p.getStatus());
        editor.putString(KEY_PARTY_CDATE, p.getCreatedDate().toString());

        editor.commit();

        Toast.makeText(_context, "Party Name in prefs: " + getPartyName(), Toast.LENGTH_LONG).show();
    }

    public void changeIPAddress(String ip) {
        editor.putString(KEY_BASE_URL, "http://" + ip + ":8080/geyou");

        editor.commit();
        Toast.makeText(_context, "New URL is: " + pref.getString(KEY_BASE_URL, "not changed"), Toast.LENGTH_LONG).show();
    }

    public String getBaseURL() {
        return pref.getString(KEY_BASE_URL, "http://192.168.1.153:8080/geyou");
    }

    public void checkLogin() {
        if (!this.isLoggedIn()) {
            Intent i = new Intent(_context, LoginActivity.class);

            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            _context.startActivity(i);
        }
    }

    public HashMap<String, Object> getUserDetails() {
        HashMap<String, Object> user = new HashMap<>();

        user.put(KEY_USER_ID, (pref.getInt(KEY_USER_ID, -1)));
        user.put(KEY_USER_FNAME, pref.getString(KEY_USER_FNAME, null));
        user.put(KEY_USER_LNAME, pref.getString(KEY_USER_LNAME, null));
        user.put(KEY_USER_EMAIL, pref.getString(KEY_USER_EMAIL, null));
        user.put(KEY_USER_PASSWORD, pref.getString(KEY_USER_PASSWORD, null));

        return user;
    }

    public void updateLoginCredentials(User u) {
        editor.putInt(KEY_USER_ID, u.getId());
        editor.putString(KEY_USER_FNAME, u.getfName());
        editor.putString(KEY_USER_LNAME, u.getlName());
        editor.putString(KEY_USER_EMAIL, u.getEmail());
        editor.putString(KEY_USER_PASSWORD, u.getPassword());

        editor.commit();
    }

    public Integer getUserId() {
        return pref.getInt(KEY_USER_ID, -1);
    }

    public String getUserFName() {
        return pref.getString(KEY_USER_FNAME, "fName");
    }

    public String getUserLName() {
        return pref.getString(KEY_USER_LNAME, "lName");
    }

    public String getUserEmail() {
        return pref.getString(KEY_USER_EMAIL, "email");
    }

    public String getUserPassword() {
        return pref.getString(KEY_USER_PASSWORD, "password");
    }

    public Integer getPartyId() {
        return pref.getInt(KEY_PARTY_ID, -1);
    }

    public String getPartyName() {
        return pref.getString(KEY_PARTY_NAME, "name");
    }

    public String getPartyStart() {
        return pref.getString(KEY_PARTY_START, "startDateTime");
    }

    public String getPartyEnd() {
        return pref.getString(KEY_PARTY_END, "startDateTime");
    }

    public String getPartyDest() {
        return pref.getString(KEY_PARTY_DEST, "destination");
    }

    public Float getPartyDestLong() {
        return pref.getFloat(KEY_PARTY_DEST_LONG, 0);
    }

    public Float getPartyDestLat() {
        return pref.getFloat(KEY_PARTY_DEST_LONG, 0);
    }

    public String getPartyStatus() {
        return pref.getString(KEY_PARTY_STATUS, "status");
    }

    public String getPartyCreatedDate() {
        return pref.getString(KEY_PARTY_CDATE, "createdDate");
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();

        Intent i = new Intent(_context, LoginActivity.class);

        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        _context.startActivity(i);
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}

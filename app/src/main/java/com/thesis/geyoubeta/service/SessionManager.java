/*
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Copyright (C) 2015 Ivan Wesley Chua and Jethro Divino
 */

package com.thesis.geyoubeta.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.thesis.geyoubeta.activity.LoginActivity;
import com.thesis.geyoubeta.entity.User;

import java.util.HashMap;

/**
 * Created by ivanwesleychua on 04/02/2016.
 */
public class SessionManager {
    SharedPreferences pref;

    SharedPreferences.Editor editor;

    Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "GeYouPrefs";

    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_ID = "id";
    public static final String KEY_FNAME = "fName";
    public static final String KEY_LNAME = "lName";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_BASE_URL = "ipAddress";

    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(User u) {

        editor.putBoolean(IS_LOGIN, true);
        editor.putInt(KEY_ID, u.getId());
        editor.putString(KEY_FNAME, u.getfName());
        editor.putString(KEY_LNAME, u.getlName());
        editor.putString(KEY_EMAIL, u.getEmail());
        editor.putString(KEY_PASSWORD, u.getPassword());

        editor.commit();
    }

    public void changeIPAddress(String ip) {
        editor.putString(KEY_BASE_URL, "http://" + ip + ":8080/geyou");

        editor.commit();
        Toast.makeText(_context, "New URL is: " +pref.getString(KEY_BASE_URL, "not changed"), Toast.LENGTH_LONG).show();
    }

    public String getBaseURL() {
        return pref.getString(KEY_BASE_URL, "http://10.0.3.2:8080/geyou");
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

        user.put(KEY_ID, (pref.getInt(KEY_ID, -1)));
        user.put(KEY_FNAME, pref.getString(KEY_FNAME, null));
        user.put(KEY_LNAME, pref.getString(KEY_LNAME, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));

        return user;
    }

    public void updateLoginCredentials(User u) {
        editor.putInt(KEY_ID, u.getId());
        editor.putString(KEY_FNAME, u.getfName());
        editor.putString(KEY_LNAME, u.getlName());
        editor.putString(KEY_EMAIL, u.getEmail());
        editor.putString(KEY_PASSWORD, u.getPassword());

        editor.commit();
    }

    public Integer getId() {
        return pref.getInt(KEY_ID, -1);
    }

    public String getFName() {
        return pref.getString(KEY_FNAME, "fName");
    }

    public String getLName() {
        return pref.getString(KEY_LNAME, "lName");
    }

    public String getEmail() {
        return pref.getString(KEY_EMAIL, "email");
    }

    public String getPassword() {
        return pref.getString(KEY_PASSWORD, "password");
    }

    public void logoutUser(){
        editor.clear();
        editor.commit();

        Intent i = new Intent(_context, LoginActivity.class);

        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        _context.startActivity(i);
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}

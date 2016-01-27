/*
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Copyright (C) 2015 Ivan Wesley Chua and Jethro Divino
 */

package com.thesis.geyoubeta.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.thesis.geyoubeta.R;

public class LoginActivity extends ActionBarActivity {

    Button btnLogin;
    Button btnRegister;
    EditText eTxtEmail;
    EditText eTxtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        eTxtEmail = (EditText) findViewById(R.id.editTextEmailLogin);
        eTxtPassword = (EditText) findViewById(R.id.editTextPasswordLogin);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLoginCredentials(eTxtEmail.getText().toString(), eTxtPassword.getText().toString())) {
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                }
            }
        });
        btnRegister = (Button) findViewById(R.id.btnRegisterLogin);
        btnRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
            }
        });
    }

    public boolean checkLoginCredentials(String email, String password) {

        return true;
    }
}

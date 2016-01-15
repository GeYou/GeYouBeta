package com.thesis.geyoubeta.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.thesis.geyoubeta.R;
import com.thesis.geyoubeta.callback.RestCallback;
import com.thesis.geyoubeta.entity.User;
import com.thesis.geyoubeta.factory.RestServiceFactory;

public class RegisterActivity extends AppCompatActivity implements RestCallback {

    Button btnRegister;
    EditText eTxtFName;
    EditText eTxtLName;
    EditText eTxtEmail;
    EditText eTxtPassword;
    EditText eTxtConfirmPass;
    RestServiceFactory factory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //init factory
        factory = RestServiceFactory.getInstance();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        eTxtFName = (EditText) findViewById(R.id.editTextFirstName);
        eTxtLName = (EditText) findViewById(R.id.editTextLastName);
        eTxtEmail = (EditText) findViewById(R.id.editTextEmailReg);
        eTxtPassword = (EditText) findViewById(R.id.editTextPasswordReg);
        eTxtConfirmPass = (EditText) findViewById(R.id.editTextConfirmPassReg);

        btnRegister = (Button) findViewById(R.id.btnRegisterReg);

        factory.sendGetRequest(this);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eTxtConfirmPass.getText().toString().equals(eTxtPassword.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Passwords match!", Toast.LENGTH_SHORT).show();
                    User nUser = new User();

                    nUser.setfName(eTxtFName.getText().toString());
                    nUser.setfName(eTxtLName.getText().toString());
                    nUser.setfName(eTxtEmail.getText().toString());
                    nUser.setfName(eTxtPassword.getText().toString());
                    registerCredentials(nUser);
                } else {
                    eTxtPassword.setText("");
                    eTxtConfirmPass.setText("");
                    Toast.makeText(getApplicationContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void registerCredentials(User u) {

    }

    @SuppressLint("LongLogTag")
    @Override
    public void onRestFinish(Object data) {
        //Log.v("REGISTER ACITIVTY CALLBACK", data.toString());

        if (data != null) {
            Toast.makeText(getApplicationContext(), data.toString(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(getApplicationContext(), "data is null", Toast.LENGTH_LONG).show();
        }

    }
}

package com.szamani.waitech.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.szamani.waitech.connections.DoRegister;
import com.szamani.waitech.interfaces.OnRegisterTaskCompleted;

import szamani.com.waitech.R;

/**
 * Created by Szamani on 9/23/2016.
 */
public class RegisterActivity extends AppCompatActivity
        implements OnRegisterTaskCompleted {
    private EditText inputEmail, inputPassword;
    private Button registerButton;

    public static void navigate(AppCompatActivity activity) {
        Intent intent = new Intent(activity, RegisterActivity.class);
        ActivityCompat.startActivity(activity, intent, null);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActivityTransitions();
        setContentView(R.layout.activity_register);

//        initLoginToolbar();
        initViews();
        initViewActions();
    }

    @Override
    public void onRegisterCompleted(String response) {

    }

    private void initViews() {
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);
        registerButton = (Button) findViewById(R.id.btn_register);
    }

    private void initViewActions() {
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputEmail.getText() != null &&
                        inputPassword.getText() != null) // and a little Regex condition later
                    new DoRegister().doRegister(RegisterActivity.this,
                            RegisterActivity.this,
                            inputEmail.getText().toString().trim(),
                            inputPassword.getText().toString().trim());
            }
        });
    }

    private void initActivityTransitions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide transition = new Slide();
            getWindow().setEnterTransition(transition);
            getWindow().setReturnTransition(transition);
        }
    }

    private void initLoginToolbar() {
        setSupportActionBar((Toolbar) findViewById(R.id.checkout_toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }


}

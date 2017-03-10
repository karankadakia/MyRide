package com.example.karan.myride1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class SignUp_Option extends AppCompatActivity {

    Button b5;
    Button b6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupoption);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        b5 = (Button) findViewById(R.id.signupUser);
        b6 = (Button) findViewById(R.id.signupDriver);

        b5.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent e = new Intent(SignUp_Option.this,SignUp_User.class);
                startActivity(e);
                //setContentView(R.layout.activity_signup_user);

            }
        });

        b6.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent f = new Intent(SignUp_Option.this, SignUp_Driver.class);
                startActivity(f);
                //setContentView(R.layout.activity_signup_driver);
            }
        });
    }
}
package com.example.karan.myride1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class Login_Option extends AppCompatActivity {
    Button b3;
    Button b4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginoption);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        b3 = (Button) findViewById(R.id.loginUser);
        b4 = (Button) findViewById(R.id.loginDriver);

        b3.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent c = new Intent(Login_Option.this, Login_User.class);
                startActivity(c);
                //setContentView(R.layout.activity_login_user);

            }
        });

        b4.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent d = new Intent(Login_Option.this, Login_Driver.class);
                startActivity(d);
                //setContentView(R.layout.activity_login_driver);

            }
        });

    }
}

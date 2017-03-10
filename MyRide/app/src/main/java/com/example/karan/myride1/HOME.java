package com.example.karan.myride1;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class HOME extends AppCompatActivity {

    final int code = 115;
    Button b1;
    Button b2;
    boolean all_permissions = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ride);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        b1 = (Button) findViewById(R.id.homelogin);
        b2 = (Button) findViewById(R.id.loginUser);

        b1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (getNetworkStatus() && all_permissions && isLocationEnabled()) {
                    Intent a = new Intent(HOME.this, Login_Option.class);
                    startActivity(a);
                    //setContentView(R.layout.activity_loginoption);
                }
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (getNetworkStatus() && all_permissions) {
                    Intent b = new Intent(HOME.this, SignUp_Option.class);
                    startActivity(b);
                    //setContentView(R.layout.activity_signupoption);
                }
            }
        });
        requestPermissions();
    }

    public boolean isLocationEnabled() {
        int locationMode = 0;
        String locationProviders;
        try {
            locationMode = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        boolean r= locationMode != Settings.Secure.LOCATION_MODE_OFF;
        if(!r){
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
            Toast.makeText(this, "Location is OFF.", Toast.LENGTH_LONG).show();
        }

        return r;
    }

    public boolean getNetworkStatus() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            // notify user you are online
            return true;
        } else {
            // notify user you are not online
            Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    void requestPermissions() {
        all_permissions = true;
        ArrayList<String> arr = new ArrayList<>();
        arr.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        arr.add(Manifest.permission.INTERNET);
        arr.add(Manifest.permission.ACCESS_FINE_LOCATION);
        arr.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        arr.add(Manifest.permission.CAMERA);

        ArrayList<String> reqArr = new ArrayList<>();
        for (String x : arr) {
            if (!checkPermission(x)) {
                all_permissions = false;
                reqArr.add(x);
            }
        }

        Object[] tmp1 = reqArr.toArray();
        String[] req = Arrays.copyOf(tmp1, tmp1.length, String[].class);

        if (req.length > 0)
            ActivityCompat.requestPermissions(this, req, code);
    }

    boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case code: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    all_permissions = true;
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    all_permissions = false;
                    requestPermissions();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}

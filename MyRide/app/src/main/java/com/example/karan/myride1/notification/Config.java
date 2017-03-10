package com.example.karan.myride1.notification;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import static com.example.karan.myride1.notification.MyFirebaseInstanceIDService.TAG;

public class Config {
    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "myride_registrationComplete";
    public static final String PUSH_NOTIFICATION = "myride_pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;

    public static final String SHARED_PREF = "myride_firebase";

    final String url = "http://www.klift.16mb.com/update_firebase.php";
    final String checkUrl = "http://www.klift.16mb.com/check.php";

    public void sendRegistrationToServer(Context context,final String type, final String id) {
        // sending gcm token to server
        SharedPreferences pref = context.getSharedPreferences(SHARED_PREF, 0);
        final String token = pref.getString("regId", null);
        Log.e(TAG, "sendRegistrationToServer: " + token);

        RequestQueue queue= Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("id", id);
                map.put("type", type);
                map.put("firekey",token);
                return map;
            }
        };
        stringRequest.setTag(TAG);
        queue.add(stringRequest);
    }

    public void postToCheckPHPPage(WebView webView, String name,String value){
        try {
            String url = checkUrl + "?" + name + "=" + value;
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl(url);
        }catch (Exception e){
            Log.e("check",e.toString());
        }
    }
}

package com.example.karan.myride1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login_User extends AppCompatActivity {
    final String url = "http://www.klift.16mb.com/user_login.php";
    String TAG = "LOGIN",EMAIL_TAG="user_email_my_ride",PASS_TAG="user_pass_my_ride";
    EditText email, pass;
    Button signin;
    ProgressDialog pd;
    RequestQueue queue;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sp= PreferenceManager.getDefaultSharedPreferences(this);

        email=(EditText)findViewById(R.id.login_userid);
        pass=(EditText)findViewById(R.id.userpass);
        pd = new ProgressDialog(this);
        pd.setMessage("Logging in...");

        email.setText(sp.getString(EMAIL_TAG, ""));
        pass.setText(sp.getString(PASS_TAG, ""));

        signin=(Button)findViewById(R.id.login_user);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogin();
            }
        });
    }

    public void onLogin(){
        final String emailId = email.getText().toString().toLowerCase().trim();
        final String password = pass.getText().toString();

        if ((emailId == null || emailId.equals(""))
                && (password == null || password.equals(""))) {
            Toast.makeText(Login_User.this, "Incorrect Credentials", Toast.LENGTH_LONG).show();
        } else {
            pd.show();
            queue = Volley.newRequestQueue(Login_User.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            pd.dismiss();
                            Log.e("Response", response);
                            try {
                                JSONObject js = new JSONObject(response);
                                String msg=js.getString("msg");
                                if(msg.equalsIgnoreCase("success")){
                                    //after login
                                    sp.edit().putString(EMAIL_TAG,emailId)
                                        .putString(PASS_TAG,password)
                                        .commit();
                                    //Toast.makeText(Login_User.this, "Success", Toast.LENGTH_LONG).show();
                                    //intent code
                                    Intent intent=new Intent(Login_User.this,UserMapsActivity.class);
                                    /*intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
                                            |Intent.FLAG_ACTIVITY_CLEAR_TASK);*/
                                    String id=js.getString("id");
                                    intent.putExtra("id",id);
                                    startActivity(intent);
                                }
                                else if(msg.equalsIgnoreCase("confirmation")){
                                    Toast.makeText(Login_User.this, "Account Confirmation pending", Toast.LENGTH_LONG).show();
                                }
                                else{
                                    Toast.makeText(Login_User.this, "Incorrect Credentials", Toast.LENGTH_LONG).show();
                                }
                            }catch (Exception e){
                                //Log.e("login",e.toString());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pd.dismiss();
                    Toast.makeText(Login_User.this, "Error", Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("email", emailId);
                    map.put("pass", password);
                    return map;
                }
            };
            stringRequest.setTag(TAG);
            queue.add(stringRequest);
        }
    }
}

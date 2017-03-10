package com.example.karan.myride1;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

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

public class DisplayDriverInformationActivity extends AppCompatActivity {

    final String url = "http://www.klift.16mb.com/driver_details.php";
    final String TAG = "driverdetails";

    ImageView profilePic, carPic, licensePic;
    TextView username, emailid;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_driver_information);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.show();

        username = (TextView) findViewById(R.id.editTextUserName);
        emailid = (TextView) findViewById(R.id.editTextEmail);
        profilePic = (ImageView) findViewById(R.id.pic);
        carPic = (ImageView) findViewById(R.id.carImage);
        licensePic = (ImageView) findViewById(R.id.drivingImage);

        final String rid = getIntent().getStringExtra("rid");
        final String driver = getIntent().getStringExtra("driver");

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response", response);
                        try {
                            JSONObject js = new JSONObject(response);
                            username.setText(js.getString("name"));
                            emailid.setText(js.getString("email"));
                            profilePic.setImageBitmap(StringToBitMap(js.getString("image")));
                            carPic.setImageBitmap(StringToBitMap(js.getString("car")));
                            licensePic.setImageBitmap(StringToBitMap(js.getString("numberplate")));
                        } catch (Exception e) {
                            //Log.e("login",e.toString());
                        }
                        pd.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("driver", driver);
                return map;
            }
        };
        stringRequest.setTag(TAG);
        queue.add(stringRequest);
    }

    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte=Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }
}

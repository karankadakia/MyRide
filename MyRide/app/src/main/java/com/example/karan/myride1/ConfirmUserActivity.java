package com.example.karan.myride1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ConfirmUserActivity extends AppCompatActivity implements OnMapReadyCallback {

    final String getRideUrl = "http://www.klift.16mb.com/get_ride.php";
    final String getBookUrl = "http://www.klift.16mb.com/book_ride.php";
    final String TAG = "ConfirmUserActivity";

    GoogleApiClient mGoogleApiClient;

    TextView seats, name;
    Button accept, reject;
    private GoogleMap map;
    LatLng pickup, drop;
    Polyline route;
    PolylineOptions routeLines;
    String driver, user, req, ride, routeDetails;
    ProgressDialog pd;

    boolean mready=false,dataready=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_user);
        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {

                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.e("method:", "Connection suspended");
                        mGoogleApiClient.connect();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.e("method:", "Connection failed");
                    }
                })
                .addApi(LocationServices.API)
                .build();
        try {
            Intent i = getIntent();
            String payload = i.getStringExtra("payload");
            JSONObject data = new JSONObject(payload);
            driver = data.getString("driver");
            user = data.getString("user");
            req = data.getString("req");
            ride = data.getString("ride");
            pickup = new LatLng(data.getDouble("fromLat"), data.getDouble("fromLng"));
            drop = new LatLng(data.getDouble("toLat"), data.getDouble("toLng"));

            seats=(TextView)findViewById(R.id.seats);
            seats.setText("Seats Needed:"+data.getString("seats"));

            name=(TextView)findViewById(R.id.name);
            name.setText(data.getString("name"));

            //get the route
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, getRideUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("Response", response);
                            try {
                                JSONObject js = new JSONObject(response);
                                String msg = js.getString("msg");
                                if (msg.equalsIgnoreCase("success")) {
                                    routeDetails = js.getString("route");
                                    new ParserTask().execute(routeDetails);
                                }
                            } catch (Exception e) {
                                //Log.e("login",e.toString());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("rid", ride);
                    return map;
                }
            };
            stringRequest.setTag(TAG);
            queue.add(stringRequest);
        } catch (Exception e) {
            Log.e("error", e.toString());
        }

        accept=(Button)findViewById(R.id.accept);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("inaccept","");
                pd.show();
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                StringRequest stringRequest = new StringRequest(Request.Method.POST, getBookUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.e("Response", response);
                                try {
                                    if (response.contains("success")) {
                                        finish();
                                    }
                                } catch (Exception e) {
                                    Log.e("login",e.toString());
                                }
                                pd.dismiss();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("login",error.toString());
                        pd.dismiss();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<>();
                        map.put("req", req);
                        map.put("ride",ride);
                        return map;
                    }
                };
                stringRequest.setTag(TAG);
                queue.add(stringRequest);
            }
        });
        reject=(Button)findViewById(R.id.reject);
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mready=true;
        map = googleMap;
        map.getUiSettings().setTiltGesturesEnabled(false);
        map.getUiSettings().setCompassEnabled(false);
        map.getUiSettings().setRotateGesturesEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setIndoorLevelPickerEnabled(false);
        //map.getUiSettings().setScrollGesturesEnabled(false);

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return true;
            }
        });
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

            }
        });
        plotData();
    }

    private void plotData(){
        if(mready && dataready){
            route=map.addPolyline(routeLines);

            MarkerOptions src=new MarkerOptions();
            src.position(pickup);
            src.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            map.addMarker(src);

            MarkerOptions dst=new MarkerOptions();
            dst.position(drop);
            dst.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            map.addMarker(dst);

            map.moveCamera(CameraUpdateFactory.newLatLng(pickup));
            map.animateCamera(CameraUpdateFactory.zoomTo(12));
            pd.dismiss();
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DataParser parser = new DataParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;
            ArrayList<String> lineStrings = new ArrayList<>();
            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();
                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);
                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);
            }
            dataready=true;

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                routeLines=lineOptions;
                plotData();
            } else {
                //Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }
}

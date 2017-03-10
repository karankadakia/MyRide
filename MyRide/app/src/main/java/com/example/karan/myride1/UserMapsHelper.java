package com.example.karan.myride1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.karan.myride1.notification.Config;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserMapsHelper implements OnMapReadyCallback {

    final String url = "http://www.klift.16mb.com/add_new_request.php";
    final String urlGetRide = "http://www.klift.16mb.com/get_request.php";
    String TAG = "RIDE";
    RequestQueue queue;
    //private String routeDetails;
    //private String polylineStrings;
    ProgressDialog pd;

    private UserMapsActivity activity;
    private GoogleMap map;

    private boolean firstLoad = false, srcSet = false, destSet = false,rideBooked=false;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private Marker currLocMarker, srcMarker, destMarker;
    private LatLng src, dest;
    private AutoCompleteTextView srcAddrText, destAddrText;
    //private Polyline routeLines;
    private LinearLayout markerLayout, confirmLayout;
    private TextInputLayout srcLayout, destLayout;
    private ImageButton gpsButton;
    private Button confirmButton,driverDetailsButton;
    private TextView seatsText;


    public void setActivity(UserMapsActivity activity1) {
        activity = activity1;

        seatsText = (TextView) activity.findViewById(R.id.seatsText);
        seatsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout linearLayout = new RelativeLayout(activity);
                final NumberPicker aNumberPicker = new NumberPicker(activity);
                aNumberPicker.setMaxValue(10);
                aNumberPicker.setMinValue(1);
                aNumberPicker.setWrapSelectorWheel(false);
                aNumberPicker.setValue(Integer.parseInt(seatsText.getText().toString()));

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50, 50);
                RelativeLayout.LayoutParams numPicerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                numPicerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

                linearLayout.setLayoutParams(params);
                linearLayout.addView(aNumberPicker, numPicerParams);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                alertDialogBuilder.setTitle("Seats");
                alertDialogBuilder.setView(linearLayout);
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("SET",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        seatsText.setText("" + aNumberPicker.getValue());
                                    }
                                })
                        .setNegativeButton("CANCEL",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
        confirmLayout = (LinearLayout) activity.findViewById(R.id.confirmLayout);
        confirmButton = (Button) activity.findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRideData();
            }
        });

        //src and dest input containers
        srcLayout = (TextInputLayout) activity.findViewById(R.id.srcInputLayout);
        srcLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSrcAutocomplete();
            }
        });
        destLayout = (TextInputLayout) activity.findViewById(R.id.destInputLayout);
        destLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDestAutocomplete();
            }
        });

        //src and dest input fields
        srcAddrText = (AutoCompleteTextView) activity.findViewById(R.id.srcAddrText);
        srcAddrText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSrcAutocomplete();
            }
        });
        srcAddrText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        /*srcAddrText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    openDestAutocomplete();
                }
            }
        });*/
        destAddrText = (AutoCompleteTextView) activity.findViewById(R.id.destAddrText);
        destAddrText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDestAutocomplete();
            }
        });
        destAddrText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    openDestAutocomplete();
                }
            }
        });
        destAddrText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        //set pick up location
        markerLayout = (LinearLayout) activity.findViewById(R.id.markerSelect);
        markerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //src fixed
                srcSet = true;
                setupSource();
            }
        });
        setupSource();

        //curr location button
        gpsButton = (ImageButton) activity.findViewById(R.id.gpsButton);
        gpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLastLocation != null) {
                    animateMap(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                }
            }
        });

        driverDetailsButton=(Button)activity.findViewById(R.id.driverDetailsButton);
        driverDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(activity,DisplayDriverInformationActivity.class);
                intent.putExtra("rid",activity.rid);
                intent.putExtra("driver",activity.driver);
                activity.startActivity(intent);
            }
        });
    }

    public void setupSource() {
        if (srcSet) {
            markerLayout.setVisibility(View.GONE);
            setDestAddrText("");
            destAddrText.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_search_black_24dp, 0, 0, 0);
            destAddrText.setVisibility(View.VISIBLE);
            srcAddrText.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.green, 0,
                    R.drawable.ic_clear_black_24dp, 0);
            srcAddrText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    final int DRAWABLE_RIGHT = 2;
                    if (srcAddrText.getCompoundDrawables()[DRAWABLE_RIGHT] != null && event.getAction() == MotionEvent.ACTION_UP) {
                        if (event.getRawX() >= (srcAddrText.getRight() - srcAddrText.getCompoundDrawables()
                                [DRAWABLE_RIGHT].getBounds().width())) {
                            srcSet = false;
                            setupSource();
                            return true;
                        }
                    }
                    return false;
                }
            });
            setSrcMarker();
        } else {
            setDestAddrText("");
            if (destMarker != null) {
                destMarker.remove();
            }
            confirmLayout.setVisibility(View.INVISIBLE);
            destSet = false;
            destAddrText.setVisibility(View.GONE);
            if (srcMarker != null) {
                srcMarker.remove();
            }
            markerLayout.setVisibility(View.VISIBLE);
            srcAddrText.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_search_black_24dp, 0, 0, 0);
            if (mLastLocation != null) {
                LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                animateMap(latLng.latitude, latLng.longitude);
                new ReverseGeocodingTask(false).execute(latLng);
            }
        }
    }

    //for better predictions
    public LatLngBounds toBounds(LatLng center, double radius) {
        LatLng southwest = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 225);
        LatLng northeast = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 45);
        return new LatLngBounds(southwest, northeast);
    }

    //set curr marker to loc
    public void setCurrLocMarker(double lat, double lng) {
        LatLng latLng = new LatLng(lat, lng);
        if (currLocMarker != null) {
            currLocMarker.remove();
        }
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.loc1));
        currLocMarker = map.addMarker(markerOptions);
    }

    //auto complete place recieve Dest
    public void updateDestLocation(Place place) {
        destSet = true;
        LatLng latLng = place.getLatLng();
        if (destMarker != null) {
            destMarker.remove();
        }
        dest = latLng;
        setDestMarker();
        setDestAddrText(place.getAddress().toString());
        destAddrText.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.red, 0, 0, 0);

        //move map camera
        confirmLayout.setVisibility(View.VISIBLE);
        map.moveCamera(CameraUpdateFactory.newLatLng(src));
        map.animateCamera(CameraUpdateFactory.zoomTo(11));
    }

    public void setDestAddrText(String txt) {
        if (txt.length() > 25) {
            txt = txt.substring(0, 22) + "...";
        }
        destAddrText.setText(txt);
    }

    public void openDestAutocomplete() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setBoundsBias(toBounds(src, 200))
                            .build(activity);
            activity.startActivityForResult(intent, activity.PLACE_AUTOCOMPLETE_DEST_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    public void setDestMarker(){
        if(dest!=null) {
            if(destMarker!=null){
                destMarker.remove();
            }
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(dest);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            destMarker = map.addMarker(markerOptions);
        }
    }

    //auto complete place recieve Src
    public void updateSrcLocation(Place place) {
        LatLng latLng = place.getLatLng();
        if (srcSet) {
            srcMarker.remove();
            src = latLng;
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(src);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            srcMarker = map.addMarker(markerOptions);
        }
        setSrcAddrText(place.getAddress().toString());
        animateMap(latLng.latitude, latLng.longitude);
    }

    public void setSrcAddrText(String txt) {
        if (txt.length() > 25) {
            txt = txt.substring(0, 22) + "...";
        }
        srcAddrText.setText(txt);
    }

    public void openSrcAutocomplete() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setBoundsBias(toBounds(src, 200))
                            .build(activity);
            activity.startActivityForResult(intent, activity.PLACE_AUTOCOMPLETE_SRC_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    public void setSrcMarker(){
        if(src!=null) {
            if(srcMarker!=null){
                srcMarker.remove();
            }
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(src);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            srcMarker = map.addMarker(markerOptions);
        }
    }

    //ride booked
    public void setRide(){
        srcAddrText.setEnabled(!rideBooked);
        destAddrText.setEnabled(!rideBooked);
        seatsText.setEnabled(!rideBooked);
        confirmLayout.setVisibility(View.VISIBLE);
        confirmButton.setEnabled(!rideBooked);
        if(rideBooked){
            confirmButton.setText("Ride Booked");
        }
        else{
            confirmButton.setText("Confirm");
        }
    }

    //check if already ride
    public void alreadyRide(final String rid){
        if (pd == null) {
            pd = new ProgressDialog(activity);
        }
        if (queue == null) {
            queue = Volley.newRequestQueue(activity);
        }
        pd.setMessage("Loading...");
        pd.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlGetRide,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        Log.e("Response", response);
                        try {
                            JSONObject js = new JSONObject(response);
                            String msg = js.getString("msg");
                            if (msg.equalsIgnoreCase("success")) {
                                // ride success
                                //Toast.makeText(activity, "Ride added", Toast.LENGTH_LONG).show();
                                srcSet=true;
                                destSet=true;
                                rideBooked=true;
                                seatsText.setText(js.getString("seats"));
                                src=new LatLng(js.getDouble("fromLat"),js.getDouble("fromLng"));
                                dest=new LatLng(js.getDouble("toLat"),js.getDouble("toLng"));
                                new ReverseGeocodingTask(false).execute(src);
                                new ReverseGeocodingTask((true)).execute(dest);
                                if(js.getInt("booked")==1){
                                    driverDetailsButton.setVisibility(View.VISIBLE);
                                    activity.driver=js.getString("ride");
                                }
                                activity.rid=js.getString("rid");
                                setSrcMarker();
                                setDestMarker();
                                setupSource();
                                setRide();
                                map.moveCamera(CameraUpdateFactory.newLatLng(srcMarker.getPosition()));
                                map.animateCamera(CameraUpdateFactory.zoomTo(11));
                            }
                        } catch (Exception e) {
                            //Log.e("login",e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                //Toast.makeText(activity, "Error", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("id", rid);
                return map;
            }
        };
        stringRequest.setTag(TAG);
        queue.add(stringRequest);
    }

    //map
    @Override
    public void onMapReady(GoogleMap map1) {
        this.map = map1;
        map.getUiSettings().setTiltGesturesEnabled(false);
        map.getUiSettings().setCompassEnabled(false);
        map.getUiSettings().setRotateGesturesEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setIndoorLevelPickerEnabled(false);

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return true;
            }
        });

        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                //Log.d("Camera postion change" + "", cameraPosition + "");
                if (!srcSet) {
                    src = cameraPosition.target;
                    Location mLocation = new Location("");
                    mLocation.setLatitude(src.latitude);
                    mLocation.setLongitude(src.longitude);
                    new ReverseGeocodingTask(false).execute(src);
                    //srcText.setText("Lat : " + src.latitude + "," + "Long : " + src.longitude);
                }
            }
        });
        map.setOnMapLongClickListener(null);
    }

    //takes to position smooth transition
    public void animateMap(double lat, double lng) {
        LatLng home = new LatLng(lat, lng);
        CameraPosition target = CameraPosition.builder()
                .target(home)
                .zoom(17)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(target), 1000, null);
    }

    //maps api call back
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(1000); //5 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        try {
            //location updates moves blue marker
            LocationServices.FusedLocationApi.requestLocationUpdates(activity.mGoogleApiClient, mLocationRequest,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            if (location != null) {
                                if (!firstLoad) {
                                    animateMap(location.getLatitude(), location.getLongitude());
                                    firstLoad = true;
                                }
                                setCurrLocMarker(location.getLatitude(), location.getLongitude());
                                mLastLocation = location;
                            }
                        }
                    });
        } catch (SecurityException e) {
            Log.e("ex:", e.toString());
        }
    }

    //hide keyboard issue
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //gets human readable address from lat and lng
    private class ReverseGeocodingTask extends AsyncTask<LatLng, Void, String> {
        double _latitude, _longitude;
        boolean reverseTaskDest;

        ReverseGeocodingTask(boolean reverseTaskDest){
            this.reverseTaskDest=reverseTaskDest;
        }

        @Override
        protected String doInBackground(LatLng... params) {
            Geocoder geocoder = new Geocoder(activity);
            _latitude = params[0].latitude;
            _longitude = params[0].longitude;

            List<Address> addresses = null;
            String addressText = "";

            try {
                addresses = geocoder.getFromLocation(_latitude, _longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses != null && addresses.size() > 0) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                int maxLines = returnedAddress.getMaxAddressLineIndex();
                for (int i = 0; i < maxLines; i++) {
                    if (maxLines - 1 == i) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i));
                    } else {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append(",");
                    }
                }
                addressText = strReturnedAddress.toString();
                //Log.e("My Current loc address", "" + strReturnedAddress.toString());
            }

            return addressText;
        }

        @Override
        protected void onPostExecute(String addressText) {
            final String result = addressText;
            if(reverseTaskDest){
                setDestAddrText(result);
            }
            else {
                setSrcAddrText(result);
            }
        }
    }

    //send data to server
    private void sendRideData() {
        if (destSet) {
            if (pd == null) {
                pd = new ProgressDialog(activity);
                pd.setMessage("Setting up ride...");
            }
            if (queue == null) {
                queue = Volley.newRequestQueue(activity);
            }
            pd.show();
            final String seats = seatsText.getText().toString();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            pd.dismiss();
                            Log.e("Response", response);
                            try {
                                JSONObject js = new JSONObject(response);
                                String msg = js.getString("msg");
                                if (msg.equalsIgnoreCase("success")) {
                                    // ride success
                                    Toast.makeText(activity, "Ride added", Toast.LENGTH_LONG).show();
                                    rideBooked=true;
                                    activity.rid=js.getString("rid");
                                    setRide();
                                    new Config().postToCheckPHPPage((WebView)activity.findViewById(R.id.webview),"id",activity.rid);
                                } else {
                                    Toast.makeText(activity, "Error", Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                //Log.e("login",e.toString());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pd.dismiss();
                    Toast.makeText(activity, "Error", Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("id", activity.id);
                    map.put("fromLat", ""+src.latitude);
                    map.put("fromLng", ""+src.longitude);
                    map.put("toLat", ""+dest.latitude);
                    map.put("toLng", ""+dest.longitude);
                    map.put("seats", seats);
                    return map;
                }
            };
            stringRequest.setTag(TAG);
            queue.add(stringRequest);
        }
    }

    //for route
    /*
    private void plotRoute() {
        if (destSet) {
            if (routeLines != null) {
                routeLines.remove();
            }
            // Getting URL to the Google Directions API
            String url = getUrl(src, dest);
            Log.d("onMapClick", url.toString());
            FetchUrl FetchUrl = new FetchUrl();
            // Start downloading json data from Google Directions API
            FetchUrl.execute(url);
        }
    }

    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        //waypoints
        if (wayPoints.size() > 0) {
            String points = "";
            for (Marker way : wayPoints) {
                LatLng latLng = way.getPosition();
                points += "|" + latLng.latitude + "," + latLng.longitude;
            }
            parameters += "&waypoints=optimize:true" + points;
        }

        //api key
        parameters += "&key=" + activity.getString(R.string.apikey);

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output +
                "?" + parameters;


        return url;
    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            // For storing data from web service
            String data = "";
            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                //Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                //Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            // Invokes the thread for parsing the JSON data
            routeDetails = result;
            parserTask.execute(result);

        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            //Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            //Log.d("Exception", e.toString());
            e.printStackTrace();
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    //read data fetched
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                //Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                //Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                //Log.d("ParserTask","Executing routes");
                //Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                //Log.d("ParserTask",e.toString());
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

                //Log.d("onPostExecute","onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                routeLines = map.addPolyline(lineOptions);
                polylineStrings = PolyUtil.encode(routeLines.getPoints());
            } else {
                //Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }
    */
}

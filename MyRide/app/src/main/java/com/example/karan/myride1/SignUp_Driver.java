package com.example.karan.myride1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SignUp_Driver extends AppCompatActivity {
    EditText username, emailid, password, confirmPassword, license;
    Button createUser, facebook, google;

    ImageView profilePic, carPic, licensePic;
    final int profilePicCode = 11, carPicCode = 12, licensePicCode = 13;
    final String profilePicFile = "liftProf.jpg", carPicFile = "liftcar.jpg", licensePicFile = "liftlicense.jpg";
    String profilePicString, carPicString, licensePicString;
    String imageExt = "jpg";

    ProgressDialog pd;
    RequestQueue queue;
    String TAG = "REG", basePath;
    final String url = "http://www.klift.16mb.com/driver_add.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_driver);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        basePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
                + File.separator + "lift" + File.separator;

        username = (EditText) findViewById(R.id.editTextUserName);
        emailid = (EditText) findViewById(R.id.editTextEmail);
        password = (EditText) findViewById(R.id.editTextPassword);
        confirmPassword = (EditText) findViewById(R.id.editTextConfirmPassword);
        license = (EditText) findViewById(R.id.editTextLicense);

        profilePic = (ImageView) findViewById(R.id.pic);
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                File f = new File(basePath + profilePicFile);
                try {
                    f.getParentFile().mkdirs();
                    f.createNewFile();
                } catch (Exception e) {
                    Log.e("error:", e.toString());
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                startActivityForResult(intent, profilePicCode);
            }
        });

        carPic = (ImageView) findViewById(R.id.carImage);
        carPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                File f = new File(basePath + carPicFile);
                try {
                    f.getParentFile().mkdirs();
                    f.createNewFile();
                } catch (Exception e) {
                    Log.e("error:", e.toString());
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                startActivityForResult(intent, carPicCode);
            }
        });
        licensePic = (ImageView) findViewById(R.id.drivingImage);
        licensePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                File f = new File(basePath + licensePicFile);
                try {
                    f.getParentFile().mkdirs();
                    f.createNewFile();
                } catch (Exception e) {
                    Log.e("error:", e.toString());
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                startActivityForResult(intent, licensePicCode);
            }
        });

        createUser = (Button) findViewById(R.id.createaccount_user);
        createUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onReg();
            }
        });

        google = (Button) findViewById(R.id.buttonGoogle);
        facebook = (Button) findViewById(R.id.buttonfacebook);

        pd = new ProgressDialog(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == profilePicCode) {
                try {
                    Uri u = data.getData();
                    if (u == null) {
                        new EncodeImage(basePath + profilePicFile, profilePicCode).execute();
                    } else {
                        new EncodeImage(u.getPath(), profilePicCode).execute();
                    }
                }catch (NullPointerException n){
                    new EncodeImage(basePath + profilePicFile, profilePicCode).execute();
                }
            } else if (requestCode == carPicCode) {
                try {
                    Uri u = data.getData();
                    if (u == null) {
                        new EncodeImage(basePath + carPicFile, carPicCode).execute();
                    } else {
                        new EncodeImage(u.getPath(), carPicCode).execute();
                    }
                }catch (NullPointerException n){
                    new EncodeImage(basePath + carPicFile, carPicCode).execute();
                }
            } else if (requestCode == licensePicCode) {
                try {
                    Uri u = data.getData();
                    if (u == null) {
                        new EncodeImage(basePath + licensePicFile, licensePicCode).execute();
                    } else {
                        new EncodeImage(u.getPath(), licensePicCode).execute();
                    }
                }catch (NullPointerException n){
                    new EncodeImage(basePath + licensePicFile, licensePicCode).execute();
                }
            }
        }
    }

    public void onReg() {
        final String name = username.getText().toString().trim();
        final String email = emailid.getText().toString().toLowerCase().trim();
        final String drivingLicense = license.getText().toString().trim();
        if ((name == null || name.equals("")) && (email == null || email.equals(""))
                && (drivingLicense == null || drivingLicense.equals("")) && profilePicString == null
                && licensePicString == null && carPicString == null) {
            Toast.makeText(this, "Enter details", Toast.LENGTH_LONG).show();
        } else {
            final String pass1 = password.getText().toString();
            String pass2 = confirmPassword.getText().toString();
            if (pass1.length() == 0 && !pass1.equals(pass2)) {
                Toast.makeText(this, "Passwords dont match", Toast.LENGTH_LONG).show();
            } else {
                pd.setMessage("Registering...");
                pd.show();
                queue = Volley.newRequestQueue(this);
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
                                        //after login
                                        Toast.makeText(SignUp_Driver.this, "Success", Toast.LENGTH_LONG).show();

                                        /*Intent intent=new Intent(SignUp_Driver.this,HOME.class);
                                        startActivity(intent);

                                        SignUp_User.this.finish();*/
                                    } else {
                                        Toast.makeText(SignUp_Driver.this, "Error Logging", Toast.LENGTH_LONG).show();
                                    }
                                } catch (Exception e) {
                                    //Log.e("login", e.toString());
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.dismiss();
                        Log.e("register", error.toString());
                        Toast.makeText(SignUp_Driver.this, "Error", Toast.LENGTH_LONG).show();
                    }
                }) {
                    @Override
                    public String getBodyContentType() {
                        return "application/x-www-form-urlencoded";
                    }

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<>();
                        map.put("email", email);
                        map.put("pass", pass1);
                        map.put("name", name);
                        map.put("license", drivingLicense);
                        map.put("image", profilePicString);
                        map.put("carpic", carPicString);
                        map.put("licensepic", licensePicString);
                        map.put("image_ext", imageExt);
                        //Log.e("regData",map.toString());
                        return map;
                    }
                };
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                stringRequest.setTag(TAG);
                queue.add(stringRequest);
            }
        }
    }


    private class EncodeImage extends AsyncTask<Void, Void, Void> {
        String path;
        int code;
        String encodedImage;
        Bitmap bitmap;

        public EncodeImage(String path, int code) {
            this.path = path;
            this.code = code;
        }

        @Override
        protected Void doInBackground(Void... params) {
            bitmap = BitmapFactory.decodeFile(path);

            float aR = bitmap.getWidth() / (float) bitmap.getHeight();
            int width = 240;
            int height = Math.round(width / aR);
            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (code == profilePicCode) {
                profilePicString = encodedImage;
                profilePic.setImageBitmap(bitmap);
                //Log.e("size", "" + profilePicString.length());
            } else if (code == carPicCode) {
                carPicString = encodedImage;
                carPic.setImageBitmap(bitmap);
            } else if (code == licensePicCode) {
                licensePicString = encodedImage;
                licensePic.setImageBitmap(bitmap);
            }
        }
    }
}

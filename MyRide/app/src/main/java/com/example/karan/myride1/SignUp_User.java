package com.example.karan.myride1;

import android.app.ProgressDialog;
import android.content.Intent;
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

public class SignUp_User extends AppCompatActivity {

    EditText username, emailid, password, confirmPassword;
    Button createUser, facebook, google;
    ProgressDialog pd;

    ImageView profilePic;
    final int profilePicCode = 11;
    final String profilePicFile = "liftUserProf.jpg";
    String profilePicString;
    String imageExt = "jpg", basePath;

    RequestQueue queue;
    String TAG = "REG";
    final String url = "http://www.klift.16mb.com/user_add.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        basePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
                + File.separator + "lift" + File.separator;

        username = (EditText) findViewById(R.id.editTextUserName);
        emailid = (EditText) findViewById(R.id.editTextEmail);
        password = (EditText) findViewById(R.id.editTextPassword);
        confirmPassword = (EditText) findViewById(R.id.editTextConfirmPassword);

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



        createUser = (Button) findViewById(R.id.createaccount_user);
        createUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onReg();
            }
        });

        google = (Button) findViewById(R.id.buttonGoogle);
        facebook = (Button) findViewById(R.id.buttonfacebook);
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
            }
        }
    }

    public void onReg() {
        final String name = username.getText().toString().trim();
        final String email = emailid.getText().toString().toLowerCase().trim();
        if ((name == null || name.equals("")) && (email == null || email.equals(""))) {
            Toast.makeText(this, "Enter details", Toast.LENGTH_LONG).show();
        } else {
            pd = new ProgressDialog(this);
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
                                        Toast.makeText(SignUp_User.this, "Success", Toast.LENGTH_LONG).show();

                                        /*Intent intent=new Intent(SignUp_User.this,HOME.class);
                                        startActivity(intent);

                                        SignUp_User.this.finish();*/
                                    } else {
                                        Toast.makeText(SignUp_User.this, "Error Logging", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(SignUp_User.this, "Error", Toast.LENGTH_LONG).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<>();
                        map.put("email", email);
                        map.put("pass", pass1);
                        map.put("name", name);
                        map.put("image", profilePicString);
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
            }
        }
    }
}

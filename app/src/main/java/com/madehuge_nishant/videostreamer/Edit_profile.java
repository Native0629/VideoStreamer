package com.madehuge_nishant.videostreamer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;
import com.madehuge_nishant.videostreamer.Others.Imageutils;
import com.madehuge_nishant.videostreamer.Others.JSONParserVolley;
import com.madehuge_nishant.videostreamer.Others.SessionManager;
import com.madehuge_nishant.videostreamer.Others.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

import static com.madehuge_nishant.videostreamer.Others.Api_urls.Change_password_url;
import static com.madehuge_nishant.videostreamer.Others.Api_urls.Edit_profile_url;

public class Edit_profile extends AppCompatActivity implements Imageutils.ImageAttachmentListener{
    EditText EtUsername,EtEmail,EtNewPassword,EtConfirmNewPassword,et_mobile;
    Button BtnUpdateProfile,BtnChangePassword;
    String StProfileImg="",StUserId,StUserImg="",StMobile="",StUsername,StEmail,StNewPassword,StConfirmNewPassword;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    SessionManager sessionManager;
    CircleImageView IvProfilePic;
    ImageView IvEditProfile_pic;
    private final int PICK_IMAGE_CAMERA = 1, PICK_IMAGE_GALLERY = 2;
    private Bitmap bitmap;
    private String file_name;
    Imageutils imageutils;
    String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.app_bg));

        sessionManager =new SessionManager(getApplicationContext());

        SharedPreferences sp =getSharedPreferences("User_detail", Context.MODE_PRIVATE);
        StUsername = sp.getString("fullname","");
        StUserId = sp.getString("user_id","");
        StEmail = sp.getString("email","");
        StUserImg =sp.getString("user_image","");
        StMobile =sp.getString("mobile_num","");

        checkPermissions();
        imageutils = new Imageutils(this);

        Log.e("User_id",StUserId);
        EtUsername = findViewById(R.id.et_username);
        EtEmail =findViewById(R.id.et_email);
        et_mobile =findViewById(R.id.et_mobile);

        EtNewPassword = findViewById(R.id.et_password);
        EtConfirmNewPassword =findViewById(R.id.et_cnf_password);
        BtnUpdateProfile = findViewById(R.id.btn_update_profile);
        BtnChangePassword = findViewById(R.id.btn_change_password);

        IvEditProfile_pic =findViewById(R.id.iv_edit_profile_pic);
        IvProfilePic =findViewById(R.id.civ_user);
        EtEmail.setKeyListener(null);
        et_mobile.setKeyListener(null);
        EtUsername.setText(StUsername);
        EtEmail.setText(StEmail);
        et_mobile.setText(StMobile);

//        Glide.with(getApplicationContext())
//                .load(StUserImg)
//                .crossFade()
//                .skipMemoryCache(true)
//                .centerCrop()
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .into(IvProfilePic);


        final ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);
        Log.e("user_img",StUserImg);
        if(!StUserImg.equals("")) {
            Glide.with(getApplicationContext())
                    .load(StUserImg)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(IvProfilePic);
        }

        BtnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StUsername =EtUsername.getText().toString();
                StEmail =EtEmail.getText().toString();

                if(StUsername.equals("")){
                    Toast.makeText(Edit_profile.this, "Username can not be empty", Toast.LENGTH_LONG).show();

                }else if(!StEmail.matches(emailPattern)){

                    Toast.makeText(Edit_profile.this, "Invalid Email id", Toast.LENGTH_LONG).show();
                    EtEmail.setError("Invalid Email id");

                }else{
                    Update_profile();
                }


                StNewPassword =EtNewPassword.getText().toString();
                StConfirmNewPassword =EtConfirmNewPassword.getText().toString();

//                if(StNewPassword.equals("")){
//                    Toast.makeText(Edit_profile.this, "Password Field Can not be Empty", Toast.LENGTH_LONG).show();
//                    EtNewPassword.setError("Password Field Can not be Empty");
//                }
//
//                else if(StConfirmNewPassword.equals("")){
//                    Toast.makeText(Edit_profile.this, "Confirm Password Field Can not be Empty", Toast.LENGTH_LONG).show();
//                    EtConfirmNewPassword.setError("Confirm Password Field Can not be Empty");
//                }
                 if(!StConfirmNewPassword.equals(StNewPassword)){
                    Toast.makeText(Edit_profile.this, "Confirm Password and Password do not matches", Toast.LENGTH_LONG).show();
                }

                else if(StConfirmNewPassword.equals(StNewPassword) && !StNewPassword.equals("")){
//                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    ChangePassword();
                }

            }
        });
        
        BtnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StNewPassword =EtNewPassword.getText().toString();
                StConfirmNewPassword =EtConfirmNewPassword.getText().toString();
                
                if(StNewPassword.equals("")){
                    Toast.makeText(Edit_profile.this, "Password Field Can not be Empty", Toast.LENGTH_LONG).show();
                    EtNewPassword.setError("Password Field Can not be Empty");
                }

                else if(StConfirmNewPassword.equals("")){
                    Toast.makeText(Edit_profile.this, "Confirm Password Field Can not be Empty", Toast.LENGTH_LONG).show();
                    EtConfirmNewPassword.setError("Confirm Password Field Can not be Empty");
                }
                else if(!StConfirmNewPassword.equals(StNewPassword)){
                    Toast.makeText(Edit_profile.this, "Confirm Password and Password do not matches", Toast.LENGTH_LONG).show();
                }

                else{
//                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    ChangePassword();
                }
            }
        });

        IvEditProfile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                imageutils.imagepicker(1);

            }
        });
    }
    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // do something
            }
            return;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        imageutils.onActivityResult(requestCode, resultCode, data);

    }
    @Override
    public void image_attachment(int from, String filename, Bitmap file, Uri uri) {
        this.bitmap = file;
        this.file_name = filename;
        IvProfilePic.setImageBitmap(file);

        String path = Environment.getExternalStorageDirectory() + File.separator + "ImageAttach" + File.separator;
        imageutils.createImage(file, filename, path, false);
        convertBitmapToString(file);
    }

    @Override
    public boolean moveDatabaseFrom(Context sourceContext, String name) {
        return super.moveDatabaseFrom(sourceContext, name);
    }

    private void convertBitmapToString(Bitmap profilePicture) {
        /*
            Base64 encoding requires a byte array, the bitmap image cannot be converted directly into a byte array.
            so first convert the bitmap image into a ByteArrayOutputStream and then convert this stream into a byte array.
        */
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        profilePicture.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] array = byteArrayOutputStream.toByteArray();
        StProfileImg = Base64.encodeToString(array, Base64.DEFAULT);
//        Toast.makeText(getApplicationContext(),profileImage,Toast.LENGTH_LONG).show();
    }


    private void ChangePassword() {
        final SpotsDialog dialog = new SpotsDialog(this);
        dialog.setTitle("Registering,Please Wait..");
        dialog.show();
//        Toast.makeText(getApplicationContext(),stMobnum+"\n"+stOTP+"\n"+stEmailid+"\n"+stAddress,Toast.LENGTH_LONG).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Change_password_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONParserVolley jsonParserVolley = new JSONParserVolley(response);
                        JSONObject json = jsonParserVolley.JSONParseVolley();
                        try {
                            String status,stUserId,stFullname,staddress,stemail,stMobilenum,stlat,stlong;
                            status = json.getString("status");
                            if (status.equals("false")) {
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(),"Password updated Successfully",Toast.LENGTH_LONG).show();

                                Utils.showErrorMessage(Edit_profile.this,"Password updated Successfully");
//                                status = json.getString("success_msg");
//                                Toast.makeText(Edit_profile.this, status, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), Edit_profile.class));
                                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                finish();

                            } else {

                                dialog.dismiss();
                                Toast.makeText(getBaseContext(), status, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Edit_profile.this, String.valueOf(e), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();
                map.put("user_id",StUserId);
                map.put("new_password",StConfirmNewPassword);

                Log.e("param",""+map);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(500000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

    }

    private void Update_profile() {

        final SpotsDialog dialog = new SpotsDialog(this);
        dialog.setTitle("Registering,Please Wait..");
        dialog.show();
//        Toast.makeText(getApplicationContext(),stMobnum+"\n"+stOTP+"\n"+stEmailid+"\n"+stAddress,Toast.LENGTH_LONG).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Edit_profile_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONParserVolley jsonParserVolley = new JSONParserVolley(response);
                        JSONObject json = jsonParserVolley.JSONParseVolley();
                        try {
                            String status,stUserId,stFullname,stUserImage,stemail,stMobilenum,stlat,stlong;
                            status = json.getString("error");
                            if (status.equals("false")) {
                                dialog.dismiss();

                                stUserId =json.getString("userid");
                                stFullname =json.getString("fullname");
                                stUserImage =json.getString("image");

                                StUserImg = stUserImage;
                                SharedPreferences sp =getSharedPreferences("User_detail", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("user_id",stUserId);
                                editor.putString("fullname",stFullname);
                                editor.putString("user_image",stUserImage);
                                editor.commit();
                                editor.apply();

                                status = json.getString("error_msg");
                                Toast.makeText(Edit_profile.this, status, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), Edit_profile.class));
                                overridePendingTransition(R.anim.fadein, R.anim.fadeout);

                                finish();

                            } else {

                                dialog.dismiss();
                                status = json.getString("error_msg");
                                Toast.makeText(getBaseContext(), status, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Edit_profile.this, String.valueOf(e), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();
                map.put("userid",StUserId);
                map.put("user_name",StUsername);
                map.put("user_image",StProfileImg);

                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(500000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
        
    }
}

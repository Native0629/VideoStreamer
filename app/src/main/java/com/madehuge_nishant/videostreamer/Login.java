package com.madehuge_nishant.videostreamer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.madehuge_nishant.videostreamer.Others.JSONParserVolley;
import com.madehuge_nishant.videostreamer.Others.SessionManager;
import com.madehuge_nishant.videostreamer.Others.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

import static com.madehuge_nishant.videostreamer.Others.Api_urls.Forgot_password_url;
import static com.madehuge_nishant.videostreamer.Others.Api_urls.SignIn_url;

public class Login extends AppCompatActivity {

    EditText EtUsername,EtPassword;
    Button BtnSignin;
    TextView TvForgotPassword,TvDonthaveanAccount;
    String StUsername,StPassword;
    SessionManager sessionManager;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        sessionManager =new SessionManager(getApplicationContext());

        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

        EtUsername = findViewById(R.id.et_username);
        EtPassword = findViewById(R.id.et_password);
        BtnSignin = findViewById(R.id.btn_signin);
        TvForgotPassword =findViewById(R.id.tv_forgot_password);
        TvDonthaveanAccount = findViewById(R.id.tv_dont_have_an_account);

        BtnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StUsername = EtUsername.getText().toString();
                StPassword = EtPassword.getText().toString();
//                startActivity(new Intent(getApplicationContext(),MainActivity.class));
//                overridePendingTransition(R.anim.fadein,R.anim.fadeout);

                if(!StUsername.matches(emailPattern)){

                    Toast.makeText(Login.this, "Invalid Email id" , Toast.LENGTH_LONG).show();

                }else if(StPassword.equals("")){
                    Toast.makeText(Login.this, "Password Field Can not be Empty", Toast.LENGTH_LONG).show();
                }
                else{
//                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    User_Login();
                }

            }
        });

        TvDonthaveanAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Register.class));
                overridePendingTransition(R.anim.fadein,R.anim.fadeout);
            }
        });

        TvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(Login.this);
                final View confirmDialog = li.inflate(R.layout.forgot_password, null);
                TextView Forgot_password_title = (TextView) confirmDialog.findViewById(R.id.title_forgot_password);
                TextView Forgot_password_info = (TextView) confirmDialog.findViewById(R.id.tvfrgtpswdinfo);
                Button Cancel = (Button) confirmDialog.findViewById(R.id.btnclose);
                Button Send = (Button) confirmDialog.findViewById(R.id.btnsend);
                final EditText editTextEmail = (EditText) confirmDialog.findViewById(R.id.email_edtext);

                final String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.+[a-z]+";
                final AlertDialog.Builder alert = new AlertDialog.Builder(Login.this);
                alert.setCancelable(false);
                alert.setView(confirmDialog);
                final AlertDialog alertDialog = alert.create();
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                alertDialog.show();

                Send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String Email_id = editTextEmail.getText().toString();
                        if (Email_id.equals("") && !Email_id.matches(emailPattern)) {
                            Toast.makeText(getApplicationContext(), "Please Enter Valid Email id", Toast.LENGTH_LONG).show();
                        } else {

                            Forgot_password(Email_id, alertDialog);
                        }
                    }
                });

                Cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
            }

        });


    }

    public void Forgot_password(final String email_id, final AlertDialog alertDialog) {

        final SpotsDialog dialog = new SpotsDialog(this);
        dialog.setTitle("Registering,Please Wait..");
        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,Forgot_password_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONParserVolley jsonParserVolley = new JSONParserVolley(response);
                        JSONObject json = jsonParserVolley.JSONParseVolley();
                        try {
                            String status;
                            status = json.getString("error");
                            if (status.equalsIgnoreCase("false")) {
                                dialog.dismiss();
                                status = json.getString("error_msg");
                                Toast.makeText(getBaseContext(), status, Toast.LENGTH_LONG).show();
                                Utils.showErrorMessage(Login.this,status);
                                alertDialog.dismiss();

                            } else {
                                dialog.dismiss();

                                alertDialog.dismiss();
                                status = json.getString("error_msg");
                                Utils.showErrorMessage(Login.this,status);
                                Toast.makeText(getBaseContext(), status, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            dialog.dismiss();

                            e.printStackTrace();
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

                Map<String, String> map = new HashMap <String, String>();
                map.put("email",email_id);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    @Override
    public void onBackPressed(){

        finishAffinity();

    }


    private void User_Login() {
        final SpotsDialog dialog = new SpotsDialog(this);
        dialog.setTitle("Registering,Please Wait..");
        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,SignIn_url,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONParserVolley jsonParserVolley = new JSONParserVolley(response);
                        JSONObject json = jsonParserVolley.JSONParseVolley();

                        try {

                            String status,staddress,stUserId,stFullname,stUserImage,stemail,stMobilenum,stState,stGst,stCity,stAccountBalance;
                            status = json.getString("error");
                            if (status.equals("false")) {

                                stUserId =json.getString("userid");
                                stFullname =json.getString("fullname");
                                stemail =json.getString("email");
                                stUserImage =json.getString("image");
                                stMobilenum =json.getString("mobile");

                                SharedPreferences sp =getSharedPreferences("User_detail", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("user_id",stUserId);
                                editor.putString("fullname",stFullname);
                                editor.putString("email",stemail);
                                editor.putString("user_image",stUserImage);
                                editor.putString("mobile_num",stMobilenum);
                                editor.commit();

                                sessionManager.setLogin(true);
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                overridePendingTransition(R.anim.fadein, R.anim.fadeout);

                            } else {

                                dialog.dismiss();
                                status = json.getString("error_msg");
                                Toast.makeText(getBaseContext(), status, Toast.LENGTH_LONG).show();

                            }
                        } catch (JSONException e) {

                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), String.valueOf(e), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.e("verror",""+error);
                        if (error instanceof NetworkError) {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                        } else if (error instanceof NoConnectionError) {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                        } else if (error instanceof TimeoutError) {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                map.put("user_email",StUsername);
                map.put("user_password",StPassword);

                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

    }
}

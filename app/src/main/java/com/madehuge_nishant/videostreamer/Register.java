package com.madehuge_nishant.videostreamer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.madehuge_nishant.videostreamer.Others.JSONParserVolley;
import com.madehuge_nishant.videostreamer.Others.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

import static com.madehuge_nishant.videostreamer.Others.Api_urls.Signup_url;

public class Register extends AppCompatActivity {

    EditText EtUsername,EtEmail,EtMobile,EtPassword,EtConfirmPassword;
    Button BtnSignup;
    TextView TvhaveanAccount;
    String StUsername,StEmail,StMobile,StPassword,StConfirmPassword;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        sessionManager =new SessionManager(getApplicationContext());

        EtUsername = findViewById(R.id.et_username);
        EtPassword = findViewById(R.id.et_password);
        EtEmail =findViewById(R.id.et_email);
        EtPassword = findViewById(R.id.et_password);
        EtMobile = findViewById(R.id.et_mobile);
        EtConfirmPassword =findViewById(R.id.et_cnf_password);
        BtnSignup = findViewById(R.id.btn_signup);
        TvhaveanAccount = findViewById(R.id.tv_have_an_account);

        BtnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StUsername = EtUsername.getText().toString();
                StEmail =EtEmail.getText().toString();
                StPassword = EtPassword.getText().toString();
                StConfirmPassword =EtConfirmPassword.getText().toString();
                StMobile =EtMobile.getText().toString();

                if(StUsername.equals("")){
                    Toast.makeText(Register.this, "Username can not be empty", Toast.LENGTH_LONG).show();
                    
                }else if(!StEmail.matches(emailPattern)){

                    Toast.makeText(Register.this, "Invalid Email id", Toast.LENGTH_LONG).show();
                    EtEmail.setError("Invalid Email id");

                }else if(StMobile.equals("")){
                    Toast.makeText(Register.this, "Enter Mobile number", Toast.LENGTH_LONG).show();
                    EtMobile.setError("Enter Mobile number");
                }

                else if(StPassword.equals("")){
                    Toast.makeText(Register.this, "Password Field Can not be Empty", Toast.LENGTH_LONG).show();
                    EtPassword.setError("Password Field Can not be Empty");
                }

                else if(StConfirmPassword.equals("")){
                    Toast.makeText(Register.this, "Confirm Password Field Can not be Empty", Toast.LENGTH_LONG).show();
                    EtConfirmPassword.setError("Confirm Password Field Can not be Empty");
                }
                else if(!StConfirmPassword.equals(StPassword)){
                    Toast.makeText(Register.this, "Confirm Password and Password do not matches", Toast.LENGTH_LONG).show();
                }
                
                else{
//                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    User_registeration();
                }
                
            }
        });

        TvhaveanAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Login.class));
                overridePendingTransition(R.anim.fadein,R.anim.fadeout);
            }
        });

    }

    private void User_registeration() {

        final SpotsDialog dialog = new SpotsDialog(this);
        dialog.setTitle("Registering,Please Wait..");
        dialog.show();
//        Toast.makeText(getApplicationContext(),stMobnum+"\n"+stOTP+"\n"+stEmailid+"\n"+stAddress,Toast.LENGTH_LONG).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Signup_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONParserVolley jsonParserVolley = new JSONParserVolley(response);
                        JSONObject json = jsonParserVolley.JSONParseVolley();
                        try {
                            String status,stUserId,stFullname,staddress,stemail,stMobilenum,stlat,stlong;
                            status = json.getString("error");
                            if (status.equals("false")) {
                                dialog.dismiss();

                                stUserId =json.getString("userid");
                                stFullname =json.getString("fullname");
                                stemail =json.getString("email");
                                stMobilenum =json.getString("mobile");

                                SharedPreferences sp =getSharedPreferences("User_detail", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("user_id",stUserId);
                                editor.putString("fullname",stFullname);
                                editor.putString("email",stemail);
                                editor.putString("user_image","");
                                editor.putString("mobile_num",stMobilenum);
                                editor.commit();

                                sessionManager.setLogin(true);
                                status = json.getString("error_msg");
                                Toast.makeText(Register.this, status, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                overridePendingTransition(R.anim.fadein, R.anim.fadeout);

                            } else {

                                dialog.dismiss();
                                status = json.getString("error_msg");
                                Toast.makeText(getBaseContext(), status, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Register.this, String.valueOf(e), Toast.LENGTH_SHORT).show();
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
                map.put("user_password", StPassword);
                map.put("user_email", StEmail);
                map.put("user_name", StUsername);
                map.put("user_mobile", StMobile);
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

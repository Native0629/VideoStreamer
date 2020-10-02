package com.madehuge_nishant.videostreamer;

import android.Manifest;
import android.app.ProgressDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.madehuge_nishant.videostreamer.DataModels.category_list;
import com.madehuge_nishant.videostreamer.DataModels.promotion_option_list;
import com.madehuge_nishant.videostreamer.Others.Imageutils;
import com.madehuge_nishant.videostreamer.Others.JSONParserVolley;
import com.madehuge_nishant.videostreamer.Others.Utils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;

import static com.madehuge_nishant.videostreamer.Others.Api_urls.Base_url;
import static com.madehuge_nishant.videostreamer.Others.Api_urls.upload_article;

public class Upload_article extends AppCompatActivity implements Imageutils.ImageAttachmentListener{
    Button BtnAddArticlefile,BtnAddThumbnail,BtnUploadArticle;
    EditText EtArticleTitle,EtArticleDesc,EtCategory;
    String StArticleTitle="",StArticleDesc="",StCategory="",StThumbnailImg="";
    String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private final int PICK_IMAGE_CAMERA = 1, PICK_IMAGE_GALLERY = 2;
    private Bitmap bitmap;
    private String file_name;
    Imageutils imageutils;
    ArrayList<String> options1 =new ArrayList<>();
    ArrayList<category_list> category_lists = new ArrayList<>();
    Spinner spinner,Ssp_promote;
    String StUsername="",StUserId="",StEmail="",StPromotionOptionid="";
    ArrayList<promotion_option_list> promotion_lists = new ArrayList<>();
    ArrayList<String> promotionoptions1 =new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_article);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.app_bg));

        getSupportActionBar().setTitle("Upload Pictures");

        SharedPreferences sp =getSharedPreferences("User_detail", Context.MODE_PRIVATE);
        StUsername = sp.getString("fullname","");
        StUserId = sp.getString("user_id","");
        StEmail = sp.getString("email","");
        checkPermissions();
        imageutils = new Imageutils(this);

        EtArticleDesc =findViewById(R.id.et_article_desc);
        EtArticleTitle =findViewById(R.id.et_article_title);
        spinner = (Spinner) findViewById(R.id.sspCategory);
        Ssp_promote = findViewById(R.id.sspPromotearticle);


        BtnAddArticlefile = findViewById(R.id.btn_add_articlefile);
        BtnAddArticlefile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageutils.imagepicker(1);

            }
        });

        BtnUploadArticle = findViewById(R.id.btn_upload);
        BtnUploadArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StArticleTitle =EtArticleTitle.getText().toString();
                StArticleDesc =EtArticleDesc.getText().toString();
                StCategory =StCategory;

                if(StArticleTitle.equals("")){
                    EtArticleTitle.setError("Enter Pictures title.");
                }
                else if(StArticleDesc.equals("")){
                    EtArticleTitle.setError("Enter Pictures Desc.");
                }
                else if(StCategory.equals("")){
                    Utils.showErrorMessage(Upload_article.this,"Select Pictures category");
                }else if(StThumbnailImg.equals("")){
                    Utils.showErrorMessage(Upload_article.this,"Add Pictures Image file");
                }

                else{
                    upload_articles();
                }

            }
        });


    getCategoryList();
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
//        CivUser.setImageBitmap(file);

        String path = Environment.getExternalStorageDirectory() + File.separator + "ImageAttach" + File.separator;
        BtnAddArticlefile.setText(path);
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
        StThumbnailImg = Base64.encodeToString(array, Base64.DEFAULT);
//        Toast.makeText(getApplicationContext(),profileImage,Toast.LENGTH_LONG).show();
    }

    private void upload_articles() {
        final SpotsDialog dialog = new SpotsDialog(this);
        dialog.setTitle("Registering,Please Wait..");
        dialog.show();
//        Toast.makeText(getApplicationContext(),stMobnum+"\n"+stOTP+"\n"+stEmailid+"\n"+stAddress,Toast.LENGTH_LONG).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,upload_article,
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
//                                Toast.makeText(getApplicationContext(),"Password updated Successfully",Toast.LENGTH_LONG).show();
                                String id =json.getString("id");
                                status = json.getString("error_msg");
                                Toast.makeText(Upload_article.this, status, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                overridePendingTransition(R.anim.fadein,R.anim.fadeout);

                            } else {

                                dialog.dismiss();
                                status = json.getString("error_msg");
                                Toast.makeText(getBaseContext(), status, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Upload_article.this, String.valueOf(e), Toast.LENGTH_SHORT).show();
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
                map.put("title",StArticleTitle);
                map.put("desc",StArticleDesc);
                map.put("category",StCategory);
                map.put("image",StThumbnailImg);
                map.put("parmot_id",StPromotionOptionid);

                Log.e("params",""+map);

                return map;
            }
        };

//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(stringRequest);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

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


    private void getCategoryList() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.show();

        JsonArrayRequest request = new JsonArrayRequest(Base_url+"all-category",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("response",""+response);

                        if (response == null) {
                            dialog.dismiss();
                            return;
                        }
                        Gson gson = new Gson();

                        dialog.dismiss();
                        for (int i = 0; i < response.length(); i++) {

//                            List<category_list> items = new Gson().fromJson(response.toString(), new TypeToken<List<category_list>>() {
//                            }.getType());
                            category_list workOrderList = null;
                            try {
                                workOrderList = gson.fromJson(response.getJSONObject(i).toString(), category_list.class);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            category_lists.add(workOrderList);

                            getPromotionOption();
                            options1.add(category_lists.get(i).getName());
                            ArrayAdapter fimadap = new ArrayAdapter(Upload_article.this, android.R.layout.simple_spinner_item, options1);
                            fimadap.setDropDownViewResource(android.R.layout.simple_spinner_item);
                            spinner.setAdapter(fimadap);

                            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                /**
                                 * Called when a new item is selected (in the Spinner)
                                 */
                                public void onItemSelected(AdapterView<?> parent, View view,
                                                           int pos, long id) {
                                    // An spinnerItem was selected. You can retrieve the selected item using
                                    // parent.getItemAtPosition(pos)

                                    StCategory = category_lists.get(pos).getId();
//                                      Toast.makeText(Upload_video.this, StCategory, Toast.LENGTH_SHORT).show();
                                }

                                public void onNothingSelected(AdapterView<?> parent) {
                                    // Do nothing, just another required interface callback
                                }

                            });
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                // error in getting json
                Log.e("Community", "Error: " + error.getMessage());
//                Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }

    private void getPromotionOption() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.show();

        JsonArrayRequest request = new JsonArrayRequest(Base_url+"all-parmot-type",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("response",""+response);

                        if (response == null) {
                            dialog.dismiss();
                            return;
                        }
                        Gson gson = new Gson();

                        dialog.dismiss();
                        for (int i = 0; i < response.length(); i++) {

//                            List<category_list> items = new Gson().fromJson(response.toString(), new TypeToken<List<category_list>>() {
//                            }.getType());
                            promotion_option_list workOrderList = null;
                            try {
                                workOrderList = gson.fromJson(response.getJSONObject(i).toString(), promotion_option_list.class);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            promotion_lists.add(workOrderList);
                            promotionoptions1.add(promotion_lists.get(i).getData());
                            Ssp_promote.setPrompt("Select your favorite Planet!");
                            ArrayAdapter promAdap = new ArrayAdapter(Upload_article.this, android.R.layout.simple_spinner_item, promotionoptions1);
                            promAdap.setDropDownViewResource(android.R.layout.simple_spinner_item);
                            Ssp_promote.setAdapter(promAdap);

                            Ssp_promote.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                /**
                                 * Called when a new item is selected (in the Spinner)
                                 */
                                public void onItemSelected(AdapterView<?> parent, View view,
                                                           int pos, long id) {
                                    // An spinnerItem was selected. You can retrieve the selected item using
                                    // parent.getItemAtPosition(pos)

                                    StPromotionOptionid = promotion_lists.get(pos).getId();
//                                      Toast.makeText(Upload_video.this, StCategory, Toast.LENGTH_SHORT).show();
                                }

                                public void onNothingSelected(AdapterView<?> parent) {
                                    // Do nothing, just another required interface callback
                                }
                            });
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                // error in getting json
                Log.e("Community", "Error: " + error.getMessage());
//                Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }

}

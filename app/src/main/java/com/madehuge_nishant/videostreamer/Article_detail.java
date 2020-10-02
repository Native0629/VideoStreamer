package com.madehuge_nishant.videostreamer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.madehuge_nishant.videostreamer.Adapters.comments_adapter;
import com.madehuge_nishant.videostreamer.Adapters.recomended_articles;
import com.madehuge_nishant.videostreamer.DataModels.article_list;
import com.madehuge_nishant.videostreamer.DataModels.comments_list;
import com.madehuge_nishant.videostreamer.Others.JSONParserVolley;
import com.madehuge_nishant.videostreamer.Others.Utils;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

import static com.madehuge_nishant.videostreamer.Others.Api_urls.Base_url;

public class Article_detail extends AppCompatActivity {
    ImageView IvArticle;
    TextView TvArticleTitle,TvArticle_desc,TvVideoViews,TvVideosLikes,TvCategoryDateDurr,TvComments,TextView,tv_follow;
    String StUsername="",StUserId="",StEmail="",StUserImg="",StArticleId="",StUploaderId="",StArticleTitle="",StArticleDesc="",StArticleUrl="",StArticleLike="0",StArticleViews="0",StVideoCategory="",StUploadDate="",StUploadBy="",StLikedUserId="",StRecomendedPicturesUrl="",StTotalComment="0";
    recomended_articles recomendedArticles;
    private List<article_list> article_lists = new ArrayList<>();
    RecyclerView Rv_recent_video;

    private List<comments_list> commentList = new ArrayList<>();
    comments_adapter commentsAdapter;
    RecyclerView RvComments;
    EditText EtAddComment;
    ImageView Iv_addComment,IvReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.app_bg));

        SharedPreferences sp1 =getSharedPreferences("User_detail", Context.MODE_PRIVATE);
        StUsername = sp1.getString("fullname","");
        StUserId = sp1.getString("user_id","");
        StEmail = sp1.getString("email","");
        StUserImg =sp1.getString("user_image","");

        TvComments =findViewById(R.id.tv_comments);

        SharedPreferences sp = getSharedPreferences("article_details", Context.MODE_PRIVATE);
        StArticleId =sp.getString("article_id","");
        StUploaderId =sp.getString("article_uploader_id","");
        StArticleTitle =sp.getString("article_title","");
        StArticleDesc =sp.getString("article_desc","");
        StArticleUrl =sp.getString("article_url","");
        StArticleViews =sp.getString("article_views","");
        StArticleLike =sp.getString("article_like","");
        StVideoCategory =sp.getString("category_name","");
        StUploadDate =sp.getString("upload_date","");
        StUploadBy =sp.getString("upload_by","");
        StLikedUserId =sp.getString("liked_user_id","");
        StRecomendedPicturesUrl =sp.getString("recommended_pictures_url","");
        StTotalComment =sp.getString("total_comment","0");


        TvComments.setText("Comments "+StTotalComment);

        commentsAdapter =new comments_adapter(getApplicationContext(),commentList);
        RvComments =(RecyclerView) findViewById(R.id.rv_comments);
        RvComments.setItemAnimator(new DefaultItemAnimator());
        RvComments.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));


        String[] LikedUserIdList = StLikedUserId.split(",");

        TvCategoryDateDurr =findViewById(R.id.tv_category_date_dur);
        TvCategoryDateDurr.setText(StVideoCategory+" | "+StUploadDate+"\nUploaded by : "+StUploadBy);

        getSupportActionBar().setTitle(StArticleTitle);


        StUserImg =sp1.getString("user_image","");

        EtAddComment =findViewById(R.id.et_add_comment);
        Iv_addComment =findViewById(R.id.iv_send);
        CircleImageView civ_user =findViewById(R.id.civ_user);

        tv_follow = findViewById(R.id.tv_follow);
        tv_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                follow_now(StUserId,StUploaderId);
            }
        });
//
//        Glide.with(getApplicationContext())
//                .load(StUserImg)
//                .crossFade()
//                .skipMemoryCache(true)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .into(civ_user);

        if(!StUserImg.equals("")) {
            Picasso.with(getApplicationContext())
                    .load(StUserImg)
                    .error(R.drawable.ic_account_circle)
                    .resize(50, 50)
                    .into(civ_user);
        }

        EtAddComment.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if(s.length()>0) {
                    Iv_addComment.setVisibility(View.VISIBLE);
                }

                if(s.length()<1){
                    Iv_addComment.setVisibility(View.GONE);

                }
//                if(s.equals("") ) {
//                    Iv_addComment.setVisibility(View.GONE);
//                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }
            public void afterTextChanged(Editable s) {

            }
        });


        Iv_addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String UserId =StUserId;
                String VideoId =StArticleId;
                String Comment =EtAddComment.getText().toString();
                add_comment(UserId,VideoId,Comment);
            }
        });

        IvArticle = findViewById(R.id.iv_article);
        TvArticle_desc = findViewById(R.id.tv_article_desc);
        TvArticleTitle =findViewById(R.id.tv_video_title);
        TvVideoViews = findViewById(R.id.tv_total_views);
        TvVideosLikes =findViewById(R.id.tv_total_likes);
        IvReport =findViewById(R.id.iv_report);

        TvArticleTitle.setText(StArticleTitle);
        TvArticle_desc.setText(StArticleDesc);
        TvVideoViews.setText(StArticleViews+" Viewed");
        TvVideosLikes.setText(StArticleLike+" Liked");

       final ImageView IvLike =findViewById(R.id.iv_like);
        ImageView IvShare =findViewById(R.id.iv_share);
        final ImageView IvDropDown =findViewById(R.id.iv_view_more);
        final ImageView IvDropUp =findViewById(R.id.iv_view_less);

        IvLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IvLike.setImageResource(R.drawable.ic_liked);
                like_articles();
            }
        });

        if(Arrays.asList(LikedUserIdList).contains(StUserId)){
            IvLike.setImageResource(R.drawable.ic_liked);
        }

        IvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, String.valueOf(R.string.app_name));
                    String sAux = "\nI am sharing this Article "+StArticleTitle+ "with you.Please watch it by clicking below link:\n";
                    sAux = sAux +StArticleUrl;
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, "Share using"));

                } catch(Exception e) {
                    //e.toString();
                }
            }
        });


        IvDropDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IvDropDown.setVisibility(View.GONE);
                IvDropUp.setVisibility(View.VISIBLE);
                TvArticle_desc.setMaxLines(Integer.MAX_VALUE);
            }
        });

        IvDropUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IvDropDown.setVisibility(View.VISIBLE);
                IvDropUp.setVisibility(View.GONE);
                TvArticle_desc.setMaxLines(2);
            }
        });

        IvReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(Article_detail.this);
                final View confirmDialog = li.inflate(R.layout.report_content_popup, null);
                TextView Forgot_password_title = (TextView) confirmDialog.findViewById(R.id.title_forgot_password);
                TextView Forgot_password_info = (TextView) confirmDialog.findViewById(R.id.tvfrgtpswdinfo);
                Button Cancel = (Button) confirmDialog.findViewById(R.id.btnclose);
                Button Send = (Button) confirmDialog.findViewById(R.id.btnsend);
                final EditText editTextEmail = (EditText) confirmDialog.findViewById(R.id.email_edtext);

                Forgot_password_title.setText("Report this Picture!");
                Forgot_password_info.setText("Are you sure want to report this Picture?");

                final String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.+[a-z]+";
                final AlertDialog.Builder alert = new AlertDialog.Builder(Article_detail.this);
                alert.setCancelable(false);
                alert.setView(confirmDialog);
                final AlertDialog alertDialog = alert.create();
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                alertDialog.show();

                Send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String StComment = editTextEmail.getText().toString();
                        if (StComment.equals("")) {
                            Toast.makeText(getApplicationContext(), "Please Add Comment", Toast.LENGTH_LONG).show();
                        } else {

                            ReportArticle(StUserId,StArticleId,StComment,alertDialog);
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
        
        final ProgressBar  progressBar = (ProgressBar)findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);
        Glide.with(getApplicationContext())
                .load(StArticleUrl)
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
                .centerCrop()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(IvArticle);

        recomendedArticles =new recomended_articles(getApplicationContext(),article_lists);
        Rv_recent_video =(RecyclerView) findViewById(R.id.rv_recomended_article);
        Rv_recent_video.setItemAnimator(new DefaultItemAnimator());
        Rv_recent_video.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        getRecomendedArticle();

        update_article_coin();

        getCommentsList(StArticleId);

        check_follow(StUserId,StUploaderId);
    }

    private void check_follow(final String stUserId, final String stUploaderId) {
        final SpotsDialog dialog = new SpotsDialog(this);
        dialog.setTitle("Registering,Please Wait..");
        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,Base_url+"already-follow",
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
//                                status = json.getString("error_msg");
//                                Toast.makeText(getBaseContext(), status, Toast.LENGTH_LONG).show();
//                                Utils.showErrorMessage(Article_detail.this,status);

                            } else {
                                dialog.dismiss();
                                tv_follow.setText("Following");

//                                status = json.getString("error_msg");
//                                Utils.showErrorMessage(Article_detail.this,status);
//                                Toast.makeText(getBaseContext(), status, Toast.LENGTH_LONG).show();
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
                map.put("follow_by",stUserId);
                map.put("follow_to",stUploaderId);

                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void follow_now(final String stUserId, final String stVideoUploaderid) {
        final SpotsDialog dialog = new SpotsDialog(this);
        dialog.setTitle("Registering,Please Wait..");
        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,Base_url+"followning",
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
                                Utils.showErrorMessage(Article_detail.this,status);
                                check_follow(StUserId,StUploaderId);

                            } else {
                                dialog.dismiss();

                                status = json.getString("error_msg");
                                Utils.showErrorMessage(Article_detail.this,status);
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
                map.put("follow_by",stUserId);
                map.put("follow_to",stVideoUploaderid);

                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }


    private void ReportArticle(final String stUserId, final String stArticleId, final String stComment, final AlertDialog alertDialog) {

        final SpotsDialog dialog = new SpotsDialog(this);
        dialog.setTitle("Registering,Please Wait..");
        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,Base_url+"report-artical",
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
                                Utils.showErrorMessage(Article_detail.this,status);
                                alertDialog.dismiss();

                            } else {
                                dialog.dismiss();

                                alertDialog.dismiss();
                                status = json.getString("error_msg");
                                Utils.showErrorMessage(Article_detail.this,status);
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
                map.put("user_id",stUserId);
                map.put("artical_id",stArticleId);
                map.put("comment",stComment);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getCommentsList(String stArticleId) {
//        final SpotsDialog dialog = new SpotsDialog(this);
//        dialog.show();
        Log.e("articleid",StArticleId);
        JsonArrayRequest request = new JsonArrayRequest(Base_url+"view-articles-comments?article_id="+stArticleId,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("response_comment",""+response);

                        if (response == null) {
//                            dialog.dismiss();
                            return;
                        }
                        Gson gson = new Gson();
                        commentList.clear();
//                        dialog.dismiss();
                        for (int i = 0; i < response.length(); i++) {

                            comments_list video_listss = null;
                            try {
                                video_listss = gson.fromJson(response.getJSONObject(i).toString(), comments_list.class);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            commentList.add(video_listss);
                            commentsAdapter.notifyDataSetChanged();
                            RvComments.setAdapter(commentsAdapter);

                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
//                dialog.dismiss();
                // error in getting json
                Log.e("Community", "Error: " + error.getMessage());
//                Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }

    private void getRecomendedArticle() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.show();

        JsonArrayRequest request = new JsonArrayRequest(Base_url+StRecomendedPicturesUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("response",""+response);

                        if (response == null) {
                            dialog.dismiss();
                            return;
                        }
                        Gson gson = new Gson();
                        article_lists.clear();
                        dialog.dismiss();
                        for (int i = 0; i < response.length(); i++) {

//                            List<category_list> items = new Gson().fromJson(response.toString(), new TypeToken<List<category_list>>() {
//                            }.getType());
                            article_list video_listss = null;
                            try {
                                video_listss = gson.fromJson(response.getJSONObject(i).toString(), article_list.class);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if(!video_listss.getId().equals(StArticleId)) {
                                article_lists.add(video_listss);
                                recomendedArticles.notifyDataSetChanged();
                                Rv_recent_video.setAdapter(recomendedArticles);
                            }

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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                overridePendingTransition(R.anim.fadein,R.anim.fadeout);
                finish();
                break;

        }
        return true;
    }

    private void add_comment(final String userId, final String videoId, final String comment) {

        final SpotsDialog dialog = new SpotsDialog(this);
        dialog.setTitle("Registering,Please Wait..");
        dialog.show();
//        Toast.makeText(getApplicationContext(),stMobnum+"\n"+stOTP+"\n"+stEmailid+"\n"+stAddress,Toast.LENGTH_LONG).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Base_url+"add_article_comments",
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
                                status = json.getString("error_msg");
                                TvComments.setText("Comments "+String.valueOf(Integer.parseInt(StTotalComment)+1));
                                Utils.showErrorMessage(Article_detail.this,status);
                                EtAddComment.setText("");
                                Iv_addComment.setVisibility(View.GONE);
                                getCommentsList(StArticleId);

                            } else {

                                dialog.dismiss();
                                status = json.getString("error_msg");
                                Utils.showErrorMessage(Article_detail.this,status);
                                Toast.makeText(getBaseContext(), status, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Article_detail.this, String.valueOf(e), Toast.LENGTH_SHORT).show();
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
                map.put("user_id",userId);
                map.put("article_id",videoId);
                map.put("comment",comment);

                Log.e("params",""+map);

                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }


    private void like_articles() {
        final SpotsDialog dialog = new SpotsDialog(this);
        dialog.setTitle("Registering,Please Wait..");
        dialog.show();
//        Toast.makeText(getApplicationContext(),stMobnum+"\n"+stOTP+"\n"+stEmailid+"\n"+stAddress,Toast.LENGTH_LONG).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Base_url+"like-artical",
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
                                TvVideosLikes.setText(String.valueOf(Integer.parseInt(StArticleLike)+1)+" Liked");

                            } else {

                                dialog.dismiss();
                                status = json.getString("error_msg");
                                Toast.makeText(getBaseContext(), status, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Article_detail.this, String.valueOf(e), Toast.LENGTH_SHORT).show();
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
                map.put("artical_id",StArticleId);

                Log.e("params",""+map);

                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void update_article_coin() {
        final SpotsDialog dialog = new SpotsDialog(this);
        dialog.setTitle("Registering,Please Wait..");
        dialog.show();
//        Toast.makeText(getApplicationContext(),stMobnum+"\n"+stOTP+"\n"+stEmailid+"\n"+stAddress,Toast.LENGTH_LONG).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Base_url+"view-artical-coin-give",
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
                                TvVideoViews.setText(String.valueOf(Integer.parseInt(StArticleViews)+1)+" Viewed");

                            } else {

                                dialog.dismiss();
                                status = json.getString("error_msg");
                                Toast.makeText(getBaseContext(), status, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Article_detail.this, String.valueOf(e), Toast.LENGTH_SHORT).show();
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
                map.put("artical_id",StArticleId);

                Log.e("params",""+map);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

    }
    @Override
    public void onBackPressed() {

        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        finish();
    }
}

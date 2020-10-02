package com.madehuge_nishant.videostreamer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.madehuge_nishant.videostreamer.Adapters.comments_adapter;
import com.madehuge_nishant.videostreamer.Adapters.recent_video_list_adapter;
import com.madehuge_nishant.videostreamer.DataModels.comments_list;
import com.madehuge_nishant.videostreamer.DataModels.video_list;
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

public class Video_detail extends AppCompatActivity {

    private Button btnonce, btncontinuously, btnstop, btnplay;
    private VideoView vv;
    private MediaController mediacontroller;
    private Uri uri;
    private boolean isContinuously = false;
    private ProgressBar progressBar;
    recent_video_list_adapter videoListAdapter;
    private List<video_list> video_lists = new ArrayList<>();
    RecyclerView Rv_recent_video;
    String StVideoid="",StVideoUploaderid="",StVideoTitle="",StVideoDesc="",StVideoUrl="",StVideoThumb="",StVideoLikes="",StVideoView="",StVideoCategory="",StUploadDate="",StUploadBy="",StLikedUserId="";
    TextView TvVideoTitle,TvVideoDesc,TvVideoViews,TvVideosLikes,TvCategoryDateDurr,TvComments,tv_follow;
    ImageView IvLike,IvShare,IvDropDown,IvDropUp,IvReport;
    String StUsername="",StUserId="",StEmail="",StUserImg="",StRecommendedVideoUrl="",StTotalComment="0";

    private List<comments_list> commentList = new ArrayList<>();
    comments_adapter commentsAdapter;
    RecyclerView RvComments;
    EditText EtAddComment;
    ImageView Iv_addComment;
    CircleImageView civ_user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.app_bg));

        TvVideoDesc = findViewById(R.id.tv_video_desc);
        TvVideoTitle =findViewById(R.id.tv_video_title);
        TvVideoViews = findViewById(R.id.tv_total_views);
        TvVideosLikes =findViewById(R.id.tv_total_likes);
        TvCategoryDateDurr =findViewById(R.id.tv_category_date_dur);
        TvComments =findViewById(R.id.tv_comments);
        IvReport =findViewById(R.id.iv_report);
        tv_follow =findViewById(R.id.tv_follow);

        commentsAdapter =new comments_adapter(getApplicationContext(),commentList);
        RvComments =(RecyclerView) findViewById(R.id.rv_comments);
        RvComments.setItemAnimator(new DefaultItemAnimator());
        RvComments.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        SharedPreferences sp1 =getSharedPreferences("User_detail", Context.MODE_PRIVATE);
        StUsername = sp1.getString("fullname","");
        StUserId = sp1.getString("user_id","");
        StEmail = sp1.getString("email","");
        StUserImg =sp1.getString("user_image","");

        EtAddComment =findViewById(R.id.et_add_comment);
        Iv_addComment =findViewById(R.id.iv_send);
        civ_user =findViewById(R.id.civ_user);

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
                if(!s.equals("") ) {
                    Iv_addComment.setVisibility(View.VISIBLE);
                }

                if(s.equals("") ) {
                    Iv_addComment.setVisibility(View.GONE);
                }
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
                String VideoId =StVideoid;
                String Comment =EtAddComment.getText().toString();

                add_comment(UserId,VideoId,Comment);

            }
        });


        SharedPreferences sp = getSharedPreferences("video_details", Context.MODE_PRIVATE);
        StVideoid = sp.getString("video_id","");
        StVideoUploaderid = sp.getString("video_uploader_id","");
        StVideoTitle =sp.getString("video_title","");
        StVideoDesc = sp.getString("video_desc","");
        StVideoUrl =sp.getString("video_url","");
        StVideoThumb =sp.getString("video_thumb","");
        StVideoLikes =sp.getString("video_like","");
        StVideoView =sp.getString("video_views","0");
        StVideoCategory =sp.getString("category_name","");
        StUploadDate =sp.getString("upload_date","");
        StUploadBy =sp.getString("upload_by","");
        StLikedUserId =sp.getString("liked_user_id","");
        StRecommendedVideoUrl =sp.getString("recommended_video_url","");
        StTotalComment =sp.getString("total_comment","0");

        TvComments.setText("Comments "+StTotalComment);
        Log.e("StVideoid",StVideoid);
        getCommentsList(StVideoid);


        getSupportActionBar().setTitle(StVideoTitle);
        String[] LikedUserIdList = StLikedUserId.split(",");

        TvCategoryDateDurr.setText(StVideoCategory+" | "+StUploadDate+"\nUploaded by : "+StUploadBy);

        TvVideoDesc.setText(StVideoDesc);
        TvVideoTitle.setText(StVideoTitle);
        TvVideoViews.setText(StVideoView+" Viewed");
        TvVideosLikes.setText(StVideoLikes+" Liked");

        tv_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                follow_now(StUserId,StVideoUploaderid);
            }
        });


        videoListAdapter =new recent_video_list_adapter(getApplicationContext(),video_lists);
        Rv_recent_video =(RecyclerView) findViewById(R.id.rv_recent_video);
        Rv_recent_video.setItemAnimator(new DefaultItemAnimator());
        Rv_recent_video.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        progressBar = (ProgressBar) findViewById(R.id.progrss);
        btnonce = (Button) findViewById(R.id.btnonce);
        btncontinuously = (Button) findViewById(R.id.btnconti);
        btnstop = (Button) findViewById(R.id.btnstop);
        btnplay = (Button) findViewById(R.id.btnplay);
        vv = (VideoView) findViewById(R.id.vv);


        IvLike =findViewById(R.id.iv_like);
        IvShare =findViewById(R.id.iv_share);
        IvDropDown =findViewById(R.id.iv_view_more);
        IvDropUp =findViewById(R.id.iv_view_less);

        IvLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                IvLike.setImageResource(R.drawable.ic_liked);
                like_video();

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
                    String sAux = "\nI am sharing this video "+StVideoTitle+ "with you.Please watch it by clicking below link:\n";
                    sAux = sAux +StVideoUrl;
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
                TvVideoDesc.setMaxLines(Integer.MAX_VALUE);



            }
        });

        IvDropUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                IvDropDown.setVisibility(View.VISIBLE);
                IvDropUp.setVisibility(View.GONE);

                TvVideoDesc.setMaxLines(2);
            }
        });

        IvReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater li = LayoutInflater.from(Video_detail.this);
                final View confirmDialog = li.inflate(R.layout.report_content_popup, null);
                TextView Forgot_password_title = (TextView) confirmDialog.findViewById(R.id.title_forgot_password);
                TextView Forgot_password_info = (TextView) confirmDialog.findViewById(R.id.tvfrgtpswdinfo);
                Button Cancel = (Button) confirmDialog.findViewById(R.id.btnclose);
                Button Send = (Button) confirmDialog.findViewById(R.id.btnsend);
                final EditText editTextEmail = (EditText) confirmDialog.findViewById(R.id.email_edtext);

                Forgot_password_title.setText("Report this Video!");
                Forgot_password_info.setText("Are you sure want to report this video?");

                final String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.+[a-z]+";
                final AlertDialog.Builder alert = new AlertDialog.Builder(Video_detail.this);
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

                            ReportVideo(StUserId,StVideoid,StComment,alertDialog);
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
        
        
        getVideoLists();
        update_video_coin();
        mediacontroller = new MediaController(this);
        mediacontroller.setAnchorView(vv);
        String uriPath = StVideoUrl; //update package name
        uri = Uri.parse(uriPath);

        vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(isContinuously){
                    vv.start();
                }
            }
        });

        btnstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vv.pause();
            }
        });

        btnplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                vv.setMediaController(mediacontroller);
                vv.setVideoURI(uri);
                vv.requestFocus();
                vv.start();
            }
        });

        btnonce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isContinuously = false;
                progressBar.setVisibility(View.VISIBLE);
                vv.setMediaController(mediacontroller);
                vv.setVideoURI(uri);
                vv.requestFocus();
                vv.start();
            }
        });

        btncontinuously.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isContinuously = true;
                progressBar.setVisibility(View.VISIBLE);
                vv.setMediaController(mediacontroller);
                vv.setVideoURI(uri);
                vv.requestFocus();
                vv.start();
            }
        });

        progressBar.setVisibility(View.VISIBLE);
        vv.setMediaController(mediacontroller);
        vv.setVideoURI(uri);
        vv.requestFocus();
        vv.start();

        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {


                int videoWidth = mp.getVideoWidth();
                int videoHeight = mp.getVideoHeight();

                //Get VideoView's current width and height
                int videoViewWidth = vv.getWidth();
                int videoViewHeight = vv.getHeight();

                float xScale = (float) videoViewWidth / videoWidth;
                float yScale = (float) videoViewHeight / videoHeight;

                //For Center Crop use the Math.max to calculate the scale
                //float scale = Math.max(xScale, yScale);
                //For Center Inside use the Math.min scale.
                //I prefer Center Inside so I am using Math.min
                float scale = Math.max(xScale, yScale);
                float scaledWidth = scale * videoWidth;
                float scaledHeight = scale * videoHeight;

                //Set the new size for the VideoView based on the dimensions of the video
                ViewGroup.LayoutParams layoutParams = vv.getLayoutParams();
                layoutParams.width = (int)scaledWidth;
                layoutParams.height = (int)scaledHeight;
                vv.setLayoutParams(layoutParams);
                progressBar.setVisibility(View.GONE);
            }
        });


        vv.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                progressBar.setVisibility(View.GONE);
                return false;
            }
        });

        check_follow(StUserId,StVideoUploaderid);
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
                                status = json.getString("error_msg");
//                                Toast.makeText(getBaseContext(), status, Toast.LENGTH_LONG).show();
//                                Utils.showErrorMessage(Article_detail.this,status);

                            } else {
                                dialog.dismiss();
                                tv_follow.setText("Following");

                                status = json.getString("error_msg");
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
                                Utils.showErrorMessage(Video_detail.this,status);
                                check_follow(StUserId,StVideoUploaderid);


                            } else {
                                dialog.dismiss();

                                status = json.getString("error_msg");
                                Utils.showErrorMessage(Video_detail.this,status);
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

    private void ReportVideo(final String stUserId, final String stVideoid, final String stComment, final AlertDialog alertDialog) {

        final SpotsDialog dialog = new SpotsDialog(this);
        dialog.setTitle("Registering,Please Wait..");
        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,Base_url+"report-video",
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
                                Utils.showErrorMessage(Video_detail.this,status);
                                alertDialog.dismiss();

                            } else {
                                dialog.dismiss();

                                alertDialog.dismiss();
                                status = json.getString("error_msg");
                                Utils.showErrorMessage(Video_detail.this,status);
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
                map.put("video_id",stVideoid);
                map.put("comment",stComment);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getCommentsList(String stVideoid) {
        final SpotsDialog dialog = new SpotsDialog(this);
        dialog.show();

        JsonArrayRequest request = new JsonArrayRequest(Base_url+"view-videos-comments?video_id="+stVideoid,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("response",""+response);

                        if (response == null) {
                            dialog.dismiss();
                            return;
                        }
                        Gson gson = new Gson();
                        commentList.clear();
                        dialog.dismiss();
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
                dialog.dismiss();
                // error in getting json
                Log.e("Community", "Error: " + error.getMessage());
//                Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }



    private void add_comment(final String userId, final String videoId, final String comment) {

        final SpotsDialog dialog = new SpotsDialog(this);
        dialog.setTitle("Registering,Please Wait..");
        dialog.show();
//        Toast.makeText(getApplicationContext(),stMobnum+"\n"+stOTP+"\n"+stEmailid+"\n"+stAddress,Toast.LENGTH_LONG).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Base_url+"add_video_comments",
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
                                Utils.showErrorMessage(Video_detail.this,status);
                                TvComments.setText("Comments "+String.valueOf(Integer.parseInt(StTotalComment)+1));

                                EtAddComment.setText("");
                                Iv_addComment.setVisibility(View.GONE);
                                getCommentsList(StVideoid);
                            } else {

                                dialog.dismiss();
                                status = json.getString("error_msg");
                                Utils.showErrorMessage(Video_detail.this,status);
                                Toast.makeText(getBaseContext(), status, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Video_detail.this, String.valueOf(e), Toast.LENGTH_SHORT).show();
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
                map.put("video_id",videoId);
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

    private void like_video() {
        final SpotsDialog dialog = new SpotsDialog(this);
        dialog.setTitle("Registering,Please Wait..");
        dialog.show();
//        Toast.makeText(getApplicationContext(),stMobnum+"\n"+stOTP+"\n"+stEmailid+"\n"+stAddress,Toast.LENGTH_LONG).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Base_url+"like-video",
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

                                TvVideosLikes.setText(String.valueOf(Integer.parseInt(StVideoLikes)+1)+" Liked");
                            } else {

                                dialog.dismiss();
                                status = json.getString("error_msg");
                                Toast.makeText(getBaseContext(), status, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Video_detail.this, String.valueOf(e), Toast.LENGTH_SHORT).show();
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
                map.put("video_id",StVideoid);

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

    private void getVideoLists() {
        Log.e("vidurl",StRecommendedVideoUrl);
        final SpotsDialog dialog = new SpotsDialog(this);
        dialog.show();

        JsonArrayRequest request = new JsonArrayRequest(Base_url+StRecommendedVideoUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("response_recommend",""+response);

                        if (response == null) {
                            dialog.dismiss();
                            return;
                        }
                        Gson gson = new Gson();
                        video_lists.clear();
                        dialog.dismiss();
                        for (int i = 0; i < response.length(); i++) {

                            video_list video_listss = null;
                            try {
                                video_listss = gson.fromJson(response.getJSONObject(i).toString(), video_list.class);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if(!video_listss.getId().equals(StVideoid)) {
                                video_lists.add(video_listss);
                                videoListAdapter.notifyDataSetChanged();
                                Rv_recent_video.setAdapter(videoListAdapter);
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

    private void update_video_coin() {
        final SpotsDialog dialog = new SpotsDialog(this);
        dialog.setTitle("Registering,Please Wait..");
        dialog.show();
//        Toast.makeText(getApplicationContext(),stMobnum+"\n"+stOTP+"\n"+stEmailid+"\n"+stAddress,Toast.LENGTH_LONG).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Base_url+"view-video-coin-give",
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
                                TvVideoViews.setText(String.valueOf(Integer.parseInt(StVideoView)+1)+" Viewed");

                            } else {

                                dialog.dismiss();
                                status = json.getString("error_msg");
                                Toast.makeText(getBaseContext(), status, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Video_detail.this, String.valueOf(e), Toast.LENGTH_SHORT).show();
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
                map.put("video_id",StVideoid);

                Log.e("params",""+map);

                return map;
            }
        };
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(stringRequest);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

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
    @Override
    public void onBackPressed() {

       startActivity(new Intent(getApplicationContext(),MainActivity.class));
       overridePendingTransition(R.anim.fadein,R.anim.fadeout);
    }

}

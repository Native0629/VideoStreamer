package com.madehuge_nishant.videostreamer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.madehuge_nishant.videostreamer.Adapters.article_categories_adapter;
import com.madehuge_nishant.videostreamer.Adapters.article_list_adapter;
import com.madehuge_nishant.videostreamer.Adapters.categories_adapter;
import com.madehuge_nishant.videostreamer.Adapters.most_viewed_pictures_adpater;
import com.madehuge_nishant.videostreamer.Adapters.most_viewed_videos_adapter;
import com.madehuge_nishant.videostreamer.Adapters.promoted_pictures_adapter;
import com.madehuge_nishant.videostreamer.Adapters.promoted_videos_adapter;
import com.madehuge_nishant.videostreamer.Adapters.recent_pictures_adapter;
import com.madehuge_nishant.videostreamer.Adapters.recent_videos_adpater;
import com.madehuge_nishant.videostreamer.Adapters.top_videos_adapter;
import com.madehuge_nishant.videostreamer.Adapters.video_list_adapter;
import com.madehuge_nishant.videostreamer.DataModels.article_list;
import com.madehuge_nishant.videostreamer.DataModels.category_list;
import com.madehuge_nishant.videostreamer.DataModels.most_viewed_pictures_list;
import com.madehuge_nishant.videostreamer.DataModels.most_viewed_videos_list;
import com.madehuge_nishant.videostreamer.DataModels.promoted_pictures_list;
import com.madehuge_nishant.videostreamer.DataModels.promoted_videos_list;
import com.madehuge_nishant.videostreamer.DataModels.recent_pictures_list;
import com.madehuge_nishant.videostreamer.DataModels.recent_videos_list;
import com.madehuge_nishant.videostreamer.DataModels.video_list;
import com.madehuge_nishant.videostreamer.Others.CenterZoomLayoutManager;
import com.madehuge_nishant.videostreamer.Others.Global;
import com.madehuge_nishant.videostreamer.Others.JSONParserVolley;
import com.madehuge_nishant.videostreamer.Others.SessionManager;
import com.madehuge_nishant.videostreamer.Others.Utils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import static com.madehuge_nishant.videostreamer.Others.Api_urls.Base_url;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SessionManager sessionManager;
    RecyclerView Rv_video,RvPromoted_videos,Rv_promoted_pictures,Rv_articles,Rv_videos,RvMostViewedVideos,Rv_mostViewed_pictures,RvRecentVideos,RvRecentPictures,RvCategories,RvCategoriesPicture;
    video_list_adapter videoListAdapter;
    top_videos_adapter topVideosAdapter;

    promoted_videos_adapter promotedVideosAdapter;
    private List<promoted_videos_list> promotedVideosLists = new ArrayList<>();

    private List<video_list> video_lists = new ArrayList<>();

    article_list_adapter articleListAdapter;
    private List<article_list> article_lists = new ArrayList<>();

    most_viewed_videos_adapter mostViewedVideosAdapter;
    private List<most_viewed_videos_list> mostViewedVideosLists = new ArrayList<>();

    promoted_pictures_adapter promotedPicturesAdapter;
    private List<promoted_pictures_list> promotedPicturesLists = new ArrayList<>();

    most_viewed_pictures_adpater mostViewedPicturesAdapter;
    private List<most_viewed_pictures_list> mostViewedPicturesLists = new ArrayList<>();

    recent_videos_adpater recentVideosAdpater;
    private List<recent_videos_list> recentVideosLists = new ArrayList<>();

    recent_pictures_adapter recentPicturesAdapter;
    private List<recent_pictures_list> recentPicturesLists = new ArrayList<>();

//    private List<recent_pictures_list> recentPicturesLists = new ArrayList<>();

    SpotsDialog dialog;
    String StUsername,StUserId,StEmail,StUserImg,StMobile;
    TextView TvWalletbalance,TvWithdraw,Tvupgrade;
    String StTotal_credit="",StTotal_debit="",StTotal_balance="";
    TextView TvNovideos,TvNoArticles,TvnoTopVideos;
    categories_adapter categoriesAdapter;
    article_categories_adapter articleCategoriesAdapter;
    List<category_list> category_lists = new ArrayList<>();
    Realm realm;
    String StCategory="0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");
        sessionManager =new SessionManager(getApplicationContext());

        realm.init(getApplicationContext());
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("mydb.realm")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();

        realm.setDefaultConfiguration(config);

//        realm.init(getApplicationContext());
        realm = Realm.getDefaultInstance();

        SharedPreferences sp1 = getSharedPreferences("Category",Context.MODE_PRIVATE);
        StCategory = sp1.getString("categoryid","0");

        SharedPreferences sp =getSharedPreferences("User_detail", Context.MODE_PRIVATE);
        StUsername = sp.getString("fullname","");
        StUserId = sp.getString("user_id","");
        StEmail = sp.getString("email","");
        StUserImg =sp.getString("user_image","");
        StMobile =sp.getString("mobile_num","");

        TvnoTopVideos =findViewById(R.id.tv_no_top_videos);
        TvNovideos =findViewById(R.id.tv_no_videos);
        TvNoArticles =findViewById(R.id.tv_no_article);

        videoListAdapter =new video_list_adapter(getApplicationContext(),video_lists);
        promotedVideosAdapter =new promoted_videos_adapter(getApplicationContext(),promotedVideosLists);
        promotedPicturesAdapter =new promoted_pictures_adapter(getApplicationContext(),promotedPicturesLists);
        topVideosAdapter =new top_videos_adapter(getApplicationContext(),video_lists);
        articleListAdapter =new article_list_adapter(getApplicationContext(),article_lists);
        mostViewedVideosAdapter =new most_viewed_videos_adapter(getApplicationContext(),mostViewedVideosLists);
        mostViewedPicturesAdapter =new most_viewed_pictures_adpater(getApplicationContext(),mostViewedPicturesLists);
        recentVideosAdpater =new recent_videos_adpater(getApplicationContext(),recentVideosLists);
        recentPicturesAdapter =new recent_pictures_adapter(getApplicationContext(),recentPicturesLists);
        categoriesAdapter =new categories_adapter(getApplicationContext(),category_lists);
        articleCategoriesAdapter =new article_categories_adapter(getApplicationContext(),category_lists);

        RvCategories = findViewById(R.id.Rv_videos_cat);
        RvCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        SnapHelper snapHelper6 = new LinearSnapHelper();
        snapHelper6.attachToRecyclerView(RvCategories);

        RvCategoriesPicture = findViewById(R.id.Rv_article_cat);
        RvCategoriesPicture.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        SnapHelper snapHelper8 = new LinearSnapHelper();
        snapHelper8.attachToRecyclerView(RvCategoriesPicture);

        Rv_video =(RecyclerView) findViewById(R.id.Rv_top_videos);
        Rv_video.smoothScrollToPosition(2);
        Rv_video.setLayoutManager(new CenterZoomLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(Rv_video);

        RvPromoted_videos = findViewById(R.id.Rv_promoted_videos);
        RvPromoted_videos.smoothScrollToPosition(2);
        RvPromoted_videos.setLayoutManager(new CenterZoomLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        SnapHelper snapHelper1 = new LinearSnapHelper();
        snapHelper1.attachToRecyclerView(RvPromoted_videos);

        RvMostViewedVideos = findViewById(R.id.Rv_mostviewed_videos);
        RvMostViewedVideos.smoothScrollToPosition(2);
        RvMostViewedVideos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        SnapHelper snapHelper5 = new LinearSnapHelper();
        snapHelper5.attachToRecyclerView(RvMostViewedVideos);

        Rv_mostViewed_pictures = findViewById(R.id.Rv_mostviewed_pictures);
        Rv_mostViewed_pictures.smoothScrollToPosition(2);
        Rv_mostViewed_pictures.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        SnapHelper snapHelper4 = new LinearSnapHelper();
        snapHelper4.attachToRecyclerView(Rv_mostViewed_pictures);

        RvRecentVideos = findViewById(R.id.Rv_recent_videos);
        RvRecentVideos.smoothScrollToPosition(2);
        RvRecentVideos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        SnapHelper snapHelper7 = new LinearSnapHelper();
        snapHelper7.attachToRecyclerView(RvRecentVideos);

        Rv_videos =(RecyclerView) findViewById(R.id.Rv_videos);
        Rv_videos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        Rv_promoted_pictures =(RecyclerView) findViewById(R.id.Rv_promoted_pictures);
        Rv_promoted_pictures.setItemAnimator(new DefaultItemAnimator());
        Rv_promoted_pictures.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        RvRecentPictures =(RecyclerView) findViewById(R.id.Rv_recent_pictures);
        RvRecentPictures.setItemAnimator(new DefaultItemAnimator());
        RvRecentPictures.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        Rv_articles =(RecyclerView) findViewById(R.id.Rv_articles);
        Rv_articles.setItemAnimator(new DefaultItemAnimator());
        Rv_articles.setLayoutManager( new GridLayoutManager(getApplicationContext(), 2));

//        getFromCacheAndDisplay();
        getCategoryList();
        getVideoLists();
        getPromotedVideosList();
        getPromotedPicturesList();
        getArticleLists();
        getMostViewedVideosList();
        getMostViewedPicturesList();
        get_coin_detail();
        getRecentVideos();
        getRecentPictures();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        TvWalletbalance =(TextView) header.findViewById(R.id.tv_wallet_balance);
        TvWithdraw =(TextView) header.findViewById(R.id.tv_withdraw);
        Tvupgrade =(TextView) header.findViewById(R.id.tv_upgrade);
        TvWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater li = LayoutInflater.from(MainActivity.this);
                final View confirmDialog = li.inflate(R.layout.withdrawl_popup, null);
                TextView TvTotal_coin = confirmDialog.findViewById(R.id.tv_total_coins);
                Button Withdrawl = confirmDialog.findViewById(R.id.btn_withdraw_now);
                ImageView IvClosePopup = confirmDialog.findViewById(R.id.iv_close_popup);
                TvTotal_coin.setText(StTotal_balance+" Coins");
                final android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
                alert.setCancelable(false);
                alert.setView(confirmDialog);
                final AlertDialog alertDialog = alert.create();
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                alertDialog.show();

                Withdrawl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StringRequest stringRequest = new StringRequest(Request.Method.POST,Base_url+"withdrawl_requet",
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                        JSONParserVolley jsonParserVolley = new JSONParserVolley(response);
                                        JSONObject json = jsonParserVolley.JSONParseVolley();
                                        try {
                                            String status,stUserId,stFullname,staddress,stemail,stMobilenum,stlat,stlong;
                                            status = json.getString("error");
                                            if (status.equals("false")) {

                                                alertDialog.dismiss();
                                                status = json.getString("error_msg");
                                                Utils.showErrorMessage(MainActivity.this,status);
                                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                                overridePendingTransition(R.anim.fadein,R.anim.fadeout);

                                            } else {

                                                status = json.getString("error_msg");
                                                Toast.makeText(getBaseContext(), status, Toast.LENGTH_LONG).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Toast.makeText(MainActivity.this, String.valueOf(e), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_LONG).show();
                                    }
                                }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {

                                Map<String, String> map = new HashMap<String, String>();
                                map.put("user_id",StUserId);
                                map.put("amount",StTotal_balance);

                                Log.e("params",""+map);

                                return map;
                            }
                        };

                        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                50000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        requestQueue.add(stringRequest);
                    }
                });

                IvClosePopup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        alertDialog.dismiss();

                    }
                });
            }
        });

        Tvupgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(MainActivity.this);
                final View confirmDialog = li.inflate(R.layout.withdrawl_popup, null);
                TextView TvTotal_coin = confirmDialog.findViewById(R.id.tv_total_coins);
                TextView Title =confirmDialog.findViewById(R.id.title);
                Title.setText("You have insufficient coins in Your wallet.");
                Button Withdrawl = confirmDialog.findViewById(R.id.btn_withdraw_now);
                Withdrawl.setText("Upgrade now");
                ImageView IvClosePopup = confirmDialog.findViewById(R.id.iv_close_popup);

                TvTotal_coin.setText(StTotal_balance+" Coins");

                final android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
                alert.setCancelable(false);
                alert.setView(confirmDialog);
                final AlertDialog alertDialog = alert.create();
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                alertDialog.show();

                Withdrawl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StringRequest stringRequest = new StringRequest(Request.Method.POST,Base_url+"request_purhase_coin",
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        JSONParserVolley jsonParserVolley = new JSONParserVolley(response);
                                        JSONObject json = jsonParserVolley.JSONParseVolley();
                                        try {
                                            String status,stUserId,stFullname,staddress,stemail,stMobilenum,stlat,stlong;
                                            status = json.getString("error");
                                            if (status.equals("false")) {

                                              alertDialog.dismiss();
                                              status = json.getString("error_msg");
                                              Utils.showErrorMessage(MainActivity.this,status);
                                                get_coin_detail();

                                            } else {

                                                status = json.getString("error_msg");
                                                Toast.makeText(getBaseContext(), status, Toast.LENGTH_LONG).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Toast.makeText(MainActivity.this, String.valueOf(e), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_LONG).show();
                                    }
                                }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {

                                Map<String, String> map = new HashMap<String, String>();
                                map.put("user_id",StUserId);
                                Log.e("params",""+map);

                                return map;
                            }
                        };

                        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                50000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        requestQueue.add(stringRequest);
                    }
                });

                IvClosePopup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
            }
        });

    }

    private void getRecentPictures() {
//        final SpotsDialog dialog = new SpotsDialog(this);
//        dialog.show();

        JsonArrayRequest request = new JsonArrayRequest(Base_url+"resend-articals?category="+StCategory+"&user_id="+StUserId,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("response",""+response);

                        if (response == null) {
                            TvNoArticles.setVisibility(View.VISIBLE);

//                            dialog.dismiss();
                            return;
                        }
                        Gson gson = new Gson();
                        recentPicturesLists.clear();
//                        dialog.dismiss();
                        for (int i = 0; i < response.length(); i++) {

//                            List<category_list> items = new Gson().fromJson(response.toString(), new TypeToken<List<category_list>>() {
//                            }.getType());
                            recent_pictures_list promoted_pictures_listss = null;
                            try {
                                promoted_pictures_listss = gson.fromJson(response.getJSONObject(i).toString(), recent_pictures_list.class);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            recentPicturesLists.add(promoted_pictures_listss);
//                            recentPicturesAdapter.notifyDataSetChanged();
                            RvRecentPictures.setAdapter(recentPicturesAdapter);

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

    private void getRecentVideos() {
//        final SpotsDialog dialog = new SpotsDialog(this);
//        dialog.show();

        JsonArrayRequest request = new JsonArrayRequest(Base_url+"resend-videos?category="+StCategory+"&user_id="+StUserId,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("response",""+response);

                        if (response == null) {
//                            dialog.dismiss();
                            return;
                        }
                        Gson gson = new Gson();
                        recentVideosLists.clear();
//                        dialog.dismiss();
                        for (int i = 0; i < response.length(); i++) {

//                            List<category_list> items = new Gson().fromJson(response.toString(), new TypeToken<List<category_list>>() {
//                            }.getType());
                            recent_videos_list video_listss = null;
                            try {
                                video_listss = gson.fromJson(response.getJSONObject(i).toString(), recent_videos_list.class);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            recentVideosLists.add(video_listss);
                            recentVideosAdpater.notifyDataSetChanged();
                            RvRecentVideos.setAdapter(recentVideosAdpater);

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

    private void getMostViewedVideosList() {

//        final SpotsDialog dialog = new SpotsDialog(this);
//        dialog.show();

        JsonArrayRequest request = new JsonArrayRequest(Base_url+"most-viewed-videos?category="+StCategory+"&user_id="+StUserId,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("response",""+response);

                        if (response == null) {
//                            dialog.dismiss();
                            return;
                        }
                        Gson gson = new Gson();
                        mostViewedVideosLists.clear();
//                        dialog.dismiss();
                        for (int i = 0; i < response.length(); i++) {

//                            List<category_list> items = new Gson().fromJson(response.toString(), new TypeToken<List<category_list>>() {
//                            }.getType());
                            most_viewed_videos_list video_listss = null;
                            try {
                                video_listss = gson.fromJson(response.getJSONObject(i).toString(), most_viewed_videos_list.class);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mostViewedVideosLists.add(video_listss);
                            mostViewedVideosAdapter.notifyDataSetChanged();
                            RvMostViewedVideos.setAdapter(mostViewedVideosAdapter);

//                            if(mostViewedVideosLists.size()<1){
//                                TvnoTopVideos.setVisibility(View.VISIBLE);
//                                TvNovideos.setVisibility(View.VISIBLE);
//                            }
//
//                            if(mostViewedVideosLists.size()>0){
//                                TvnoTopVideos.setVisibility(View.GONE);
//                                TvNovideos.setVisibility(View.GONE);
//                            }

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

    private void getFromCacheAndDisplay() {
        RealmResults<video_list> tasksVideoList = realm.where(video_list.class).findAll();
        RealmResults<article_list> tasksArticalList = realm.where(article_list.class).findAll();
//        RealmResults<most_viewed_pictures_list> tasksMostViewedpictureslList = realm.where(most_viewed_pictures_list.class).findAll();


        Global.printLog("tasksVideoList==", "---" + tasksVideoList);
        Global.printLog("tasksArticalList==", "---" + tasksArticalList);
//        Global.printLog("tasksArticalList==", "---" + tasksMostViewedpictureslList);

        video_lists.clear();
        article_lists.clear();


        Rv_video.smoothScrollToPosition(2);
        Rv_video.setLayoutManager(new CenterZoomLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        Rv_video.setAdapter(topVideosAdapter);

        Rv_videos.setLayoutManager(new CenterZoomLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        Rv_videos.setAdapter(videoListAdapter);


        if (tasksVideoList != null && tasksVideoList.size() > 0) {
            Global.printLog("tasksVideoList==", "---" + tasksVideoList.size());

            video_lists.addAll(tasksVideoList);
            videoListAdapter.notifyDataSetChanged();
            topVideosAdapter.notifyDataSetChanged();

            Rv_video.setVisibility(View.VISIBLE);
            TvNovideos.setVisibility(View.GONE);

            Rv_videos.setVisibility(View.VISIBLE);
            TvnoTopVideos.setVisibility(View.GONE);
        } else {
            Rv_video.setVisibility(View.GONE);
            TvNovideos.setVisibility(View.VISIBLE);

            Rv_videos.setVisibility(View.GONE);
            TvnoTopVideos.setVisibility(View.VISIBLE);
        }

        Rv_articles.smoothScrollToPosition(2);
        Rv_articles.setItemAnimator(new DefaultItemAnimator());
        Rv_articles.setLayoutManager( new GridLayoutManager(getApplicationContext(), 2));
        Rv_articles.setAdapter(articleListAdapter);

        if (tasksArticalList != null && tasksArticalList.size() > 0) {
            Global.printLog("tasksArticalList==", "---" + tasksArticalList.size());

            article_lists.addAll(tasksArticalList);
            articleListAdapter.notifyDataSetChanged();
            Rv_articles.setVisibility(View.VISIBLE);
            TvNoArticles.setVisibility(View.GONE);
        } else {
            Rv_articles.setVisibility(View.GONE);
            TvNoArticles.setVisibility(View.VISIBLE);
        }

//        Rv_mostViewed_pictures.smoothScrollToPosition(2);
//        Rv_mostViewed_pictures.setLayoutManager(new CenterZoomLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//        Rv_mostViewed_pictures.setAdapter(mostViewedPicturesAdapter);
//
//        if (tasksMostViewedpictureslList != null && tasksMostViewedpictureslList.size() > 0) {
//            Global.printLog("tasksArticalList==", "---" + tasksArticalList.size());
//
//            mostViewedPicturesLists.addAll(tasksMostViewedpictureslList);
//            mostViewedPicturesAdapter.notifyDataSetChanged();
//            Rv_mostViewed_pictures.setVisibility(View.VISIBLE);
//            Rv_mostViewed_pictures.setVisibility(View.GONE);
//        } else {
//            Rv_mostViewed_pictures.setVisibility(View.GONE);
////            TvNoArticles.setVisibility(View.VISIBLE);
//        }

    }


    private void getPromotedPicturesList() {

//        final SpotsDialog dialog = new SpotsDialog(this);
//        dialog.show();

        JsonArrayRequest request = new JsonArrayRequest(Base_url+"view-upload-artocals-recommended?category="+StCategory+"&user_id="+StUserId,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("response",""+response);
                        if (response == null) {
                            TvNoArticles.setVisibility(View.VISIBLE);
//                            dialog.dismiss();
                            return;
                        }

                        Gson gson = new Gson();
                        promotedPicturesLists.clear();
//                        dialog.dismiss();
                        for (int i = 0; i < response.length(); i++) {

//                            List<category_list> items = new Gson().fromJson(response.toString(), new TypeToken<List<category_list>>() {
//                            }.getType());
                            promoted_pictures_list promoted_pictures_listss = null;
                            try {
                                promoted_pictures_listss = gson.fromJson(response.getJSONObject(i).toString(), promoted_pictures_list.class);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            promotedPicturesLists.add(promoted_pictures_listss);
                            Rv_promoted_pictures.setAdapter(promotedPicturesAdapter);

                            if(promotedPicturesLists.size()==0){
                                TvNoArticles.setVisibility(View.VISIBLE);
                            }

                            if(promotedPicturesLists.size()>0){
                                TvNoArticles.setVisibility(View.GONE);
                            }
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

    private void getPromotedVideosList() {
//        final SpotsDialog dialog = new SpotsDialog(this);
//        dialog.show();

        JsonArrayRequest request = new JsonArrayRequest(Base_url+"view-upload-videos-recommended?category="+StCategory+"&user_id="+StUserId,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("response",""+response);

                        if (response == null) {
//                            dialog.dismiss();
                            return;
                        }

                        Gson gson = new Gson();
                        promotedVideosLists.clear();
//                        dialog.dismiss();
                        for (int i = 0; i < response.length(); i++) {
//                            List<category_list> items = new Gson().fromJson(response.toString(), new TypeToken<List<category_list>>() {
//                            }.getType());
                            promoted_videos_list promoted_video_listss = null;
                            try {
                                promoted_video_listss = gson.fromJson(response.getJSONObject(i).toString(), promoted_videos_list.class);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            promotedVideosLists.add(promoted_video_listss);
                            promotedVideosAdapter.notifyDataSetChanged();

                            Rv_video.smoothScrollToPosition(2);

                            RvPromoted_videos.setLayoutManager(new CenterZoomLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                            RvPromoted_videos.setAdapter(promotedVideosAdapter);

                            if(promotedVideosLists.size()<1){
                                TvnoTopVideos.setVisibility(View.VISIBLE);
                                TvNovideos.setVisibility(View.VISIBLE);
                            }

                            if(promotedVideosLists.size()>0){
                                TvnoTopVideos.setVisibility(View.GONE);
                                TvNovideos.setVisibility(View.GONE);

                            }

                            if(promotedVideosLists.size()>2) {
                                final int speedScroll = 2200;
                                final Handler handler = new Handler();
                                final Runnable runnable = new Runnable() {
                                    int count = 0;
                                    boolean flag = true;

                                    @Override
                                    public void run() {
                                        if (count < promotedVideosAdapter.getItemCount()) {
                                            if (count == promotedVideosAdapter.getItemCount() - 1) {
                                                flag = false;
                                            } else if (count == 0) {
                                                flag = true;
                                            }
                                            if (flag) count++;
                                            else count--;

                                            RvPromoted_videos.smoothScrollToPosition(count);
                                            handler.postDelayed(this, speedScroll);
                                        }
                                    }
                                };
                                handler.postDelayed(runnable, speedScroll);
                            }
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

    private void get_coin_detail() {

//        Toast.makeText(getApplicationContext(),stMobnum+"\n"+stOTP+"\n"+stEmailid+"\n"+stAddress,Toast.LENGTH_LONG).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Base_url+"check_user_balance",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONParserVolley jsonParserVolley = new JSONParserVolley(response);
                        JSONObject json = jsonParserVolley.JSONParseVolley();
                        try {
                            String status,stUserId,stFullname,staddress,stemail,stMobilenum,stlat,stlong;
                            status = json.getString("error");
                            if (status.equals("false")) {

                                StTotal_credit =json.getString("total_credit");
                                StTotal_debit=json.getString("total_debit");
                                StTotal_balance =json.getString("balance");

                                TvWalletbalance.setText("Wallet Balance \n "+StTotal_balance);

                                SharedPreferences sp =getSharedPreferences("Coins_detail",MODE_PRIVATE);
                                SharedPreferences.Editor editor =sp.edit();
                                editor.putString("total_credit",StTotal_credit);
                                editor.putString("total_debit",StTotal_debit);
                                editor.putString("balance",StTotal_balance);
                                editor.commit();
                                editor.apply();


                                if(Integer.parseInt(StTotal_balance)>0){
                                    Tvupgrade.setVisibility(View.GONE);
                                    TvWithdraw.setVisibility(View.VISIBLE);
                                }

                                if(Integer.parseInt(StTotal_balance)==0){
                                    Tvupgrade.setVisibility(View.VISIBLE);
                                    TvWithdraw.setVisibility(View.GONE);
                                }


                            } else {

                                status = json.getString("error_msg");
                                Toast.makeText(getBaseContext(), status, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, String.valueOf(e), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();
                map.put("user_id",StUserId);
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
    public void onResume(){
        super.onResume();
        // put your code here...
        if(video_lists.isEmpty()) {
            getVideoLists();
        }
////        getArticleLists();
////        get_coin_detail();
//        getFromCacheAndDisplay();
//        getCategoryList();
////        getVideoLists();
//        getPromotedVideosList();
//        getPromotedPicturesList();
//        getArticleLists();
//        getMostViewedVideosList();
//        getMostViewedPicturesList();
//        get_coin_detail();
//        getRecentVideos();
//        getRecentPictures();
    }

    private void getArticleLists() {
//        final ProgressDialog dialog = new ProgressDialog(this);
//        dialog.setMessage("Please wait...");
//        dialog.show();

        JsonArrayRequest request = new JsonArrayRequest(Base_url+"most-viewed-articals?category="+StCategory+"&user_id="+StUserId,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("response",""+response);

                        if (response == null) {
                            TvNoArticles.setVisibility(View.VISIBLE);

//                            dialog.dismiss();
                            return;
                        }
                        Gson gson = new Gson();
                        article_lists.clear();
//                        dialog.dismiss();
                        for (int i = 0; i < response.length(); i++) {
//                            List<category_list> items = new Gson().fromJson(response.toString(), new TypeToken<List<category_list>>() {
//                            }.getType());
                            article_list article_listss = null;
                            try {
                                article_listss = gson.fromJson(response.getJSONObject(i).toString(), article_list.class);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            article_lists.add(article_listss);
                            articleListAdapter.notifyDataSetChanged();
                            Rv_articles.setAdapter(articleListAdapter);

                            if(article_lists.size()==0){
                                TvNoArticles.setVisibility(View.VISIBLE);
                            }

//                            if (article_lists.size() > 0) {
//                                TvNoArticles.setVisibility(View.GONE);
//
//                                if (article_lists != null && article_lists.size() > 0) {
//                                    realm.executeTransactionAsync(new Realm.Transaction() {
//                                        @Override
//                                        public void execute(Realm realm) {
////                                realm.beginTransaction();
//                                            RealmResults<article_list> result = realm.where(article_list.class).findAll();
//                                            result.deleteAllFromRealm();
//
//                                            RealmList<article_list> _OrderList = new RealmList<>();
//                                            _OrderList.removeAll(article_lists);
//                                            _OrderList.addAll(article_lists);
//                                            realm.insertOrUpdate(_OrderList);
//                                            // <-- insert unmanaged to Realm
////                                realm.commitTransaction();
////                                realm.close();
//                                        }
//                                    });
//                                }
//                            }
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

    private  void getMostViewedPicturesList(){
//        final ProgressDialog dialog = new ProgressDialog(this);
//        dialog.setMessage("Please wait...");
//        dialog.show();

        JsonArrayRequest request = new JsonArrayRequest(Base_url+"most-viewed-articals?category="+StCategory+"&user_id="+StUserId,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("response",""+response);

                        if (response == null) {
                            TvNoArticles.setVisibility(View.VISIBLE);
//                            dialog.dismiss();
                            return;
                        }

                        Gson gson = new Gson();
                        mostViewedPicturesLists.clear();
//                        dialog.dismiss();
                        for (int i = 0; i < response.length(); i++) {

//                            List<category_list> items = new Gson().fromJson(response.toString(), new TypeToken<List<category_list>>() {
//                            }.getType());
                            most_viewed_pictures_list article_listss = null;
                            try {
                                article_listss = gson.fromJson(response.getJSONObject(i).toString(), most_viewed_pictures_list.class);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            mostViewedPicturesLists.add(article_listss);

                        }
                            mostViewedPicturesAdapter.notifyDataSetChanged();
                            Rv_mostViewed_pictures.setAdapter(mostViewedPicturesAdapter);



//                            if (mostViewedPicturesLists.size() > 0) {
//                                if (mostViewedPicturesLists != null && mostViewedPicturesLists.size() > 0) {
//                                    realm.executeTransactionAsync(new Realm.Transaction() {
//                                        @Override
//                                        public void execute(Realm realm) {
//
////                                realm.beginTransaction();
//                                            RealmResults<most_viewed_pictures_list> result = realm.where(most_viewed_pictures_list.class).findAll();
//                                            result.deleteAllFromRealm();
//
//                                            RealmList<most_viewed_pictures_list> _OrderList = new RealmList<>();
//                                            _OrderList.addAll(mostViewedPicturesLists);
//                                            realm.insertOrUpdate(_OrderList); // <-- insert unmanaged to Realm
//
////                                realm.commitTransaction();
////                                realm.close();
//                                        }
//                                    });
//                                }
//                            }
//                            if(mostViewedPicturesLists.size()==0){
//                                TvNoArticles.setVisibility(View.VISIBLE);
//                            }
//
//                            if(article_lists.size()>0){
//                                TvNoArticles.setVisibility(View.GONE);
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

    private void getVideoLists() {

//        if (video_lists.isEmpty()) {
//        SpotsDialog    dialog = new SpotsDialog(this);
//            if (!dialog.isShowing())
//                dialog.show();
//        }
//
//        final ProgressDialog dialog = new ProgressDialog(this);
//        dialog.setMessage("Please wait...");
//        dialog.show();

        JsonArrayRequest request = new JsonArrayRequest(Base_url+"view-upload-videos?category="+StCategory+"&user_id="+StUserId+"&user_id="+StUserId,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("response",""+response);
                        if (response == null) {
//                            dialog.dismiss();
                            return;
                        }
                        Gson gson = new Gson();
                        video_lists.clear();
//                        dialog.dismiss();
                        for (int i = 0; i < response.length(); i++) {
//                            List<category_list> items = new Gson().fromJson(response.toString(), new TypeToken<List<category_list>>() {
//                            }.getType());
                            video_list video_listss = null;
                            try {
                                video_listss = gson.fromJson(response.getJSONObject(i).toString(), video_list.class);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            video_lists.add(video_listss);
                            videoListAdapter.notifyDataSetChanged();
                            topVideosAdapter.notifyDataSetChanged();

                            Rv_video.smoothScrollToPosition(2);

                            Rv_video.setLayoutManager(new CenterZoomLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                            Rv_video.setAdapter(topVideosAdapter);
                            Rv_videos.setAdapter(videoListAdapter);

//                            if (video_lists != null && video_lists.size() > 0) {
//                                realm.executeTransactionAsync(new Realm.Transaction() {
//                                    @Override
//                                    public void execute(Realm realm) {
//
////                                realm.beginTransaction();
//                                        RealmResults<video_list> result = realm.where(video_list.class).findAll();
//                                        result.deleteAllFromRealm();
//
//                                        RealmList<video_list> _OrderList = new RealmList<>();
//                                        _OrderList.removeAll(video_lists);
//                                        _OrderList.addAll(video_lists);
//                                        realm.insertOrUpdate(_OrderList); // <-- insert unmanaged to Realm
//
////                                realm.commitTransaction();
////                                realm.close();
//                                    }
//                                });
//                            }

                            if(video_lists.size()<1){
                                TvnoTopVideos.setVisibility(View.VISIBLE);
                                TvNovideos.setVisibility(View.VISIBLE);
                            }

                            if(article_lists.size()>0){
                                TvnoTopVideos.setVisibility(View.GONE);
                                TvNovideos.setVisibility(View.GONE);
                            }

                            if(video_lists.size()>2) {
                                final int speedScroll = 2200;
                                final Handler handler = new Handler();
                                final Runnable runnable = new Runnable() {
                                    int count = 0;
                                    boolean flag = true;

                                    @Override
                                    public void run() {
                                        if (count < topVideosAdapter.getItemCount()) {
                                            if (count == topVideosAdapter.getItemCount() - 1) {
                                                flag = false;
                                            } else if (count == 0) {
                                                flag = true;
                                            }
                                            if (flag) count++;
                                            else count--;

                                            Rv_video.smoothScrollToPosition(count);
                                            handler.postDelayed(this, speedScroll);
                                        }
                                    }
                                };
                                handler.postDelayed(runnable, speedScroll);
                            }
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

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed(){
        if (doubleBackToExitPressedOnce) {
            finishAffinity();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);

            // Handle the camera action
        } else if (id == R.id.nav_history) {
            startActivity(new Intent(getApplicationContext(), History.class));
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            // Handle the camera action
        }else if (id == R.id.nav_videos) {
//            startActivity(new Intent(getApplicationContext(), Video_detail.class));
            if(Integer.parseInt(StTotal_balance)>0) {

                startActivity(new Intent(getApplicationContext(), Video_detail.class));
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            } else{
                Utils.showErrorMessage(MainActivity.this,"You have not sufficient coins to watch videos");
            }

        } else if (id == R.id.nav_articles) {
            if(Integer.parseInt(StTotal_balance)>0) {
                startActivity(new Intent(getApplicationContext(),Article_detail.class));
                overridePendingTransition(R.anim.fadein,R.anim.fadeout);
            } else{
                Utils.showErrorMessage(MainActivity.this,"You have not sufficient coins to view articles");
            }
        }else if (id == R.id.nav_about_us) {
           startActivity(new Intent(getApplicationContext(),About_us.class));
           overridePendingTransition(R.anim.fadein,R.anim.fadeout);

        } else if (id == R.id.nav_edit_profile) {
            startActivity(new Intent(getApplicationContext(),Edit_profile.class));
            overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        } else if (id == R.id.nav_upload_video) {

                startActivity(new Intent(getApplicationContext(), Upload_video.class));
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        } else if (id == R.id.nav_upload_article) {
            startActivity(new Intent(getApplicationContext(),Upload_article.class));
            overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        }else if(id == R.id.nav_logout){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Are You Sure Want to Logout?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(getApplicationContext(), Login.class));
                            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                            sessionManager.setLogin(false);
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.setTitle("Logout");
            alert.show();
            alert.getButton(alert.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            alert.getButton(alert.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        }

        else if (id == R.id.nav_share) {

            try {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, String.valueOf(R.string.app_name));
                String sAux = "\nLet me recommend you this application\n";
                sAux = sAux + "https://play.google.com/store/apps/details?id="+getPackageName();
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, "choose one"));
            } catch(Exception e) {
                //e.toString();
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_filter) {
            LayoutInflater li = LayoutInflater.from(MainActivity.this);
            final View confirmDialog = li.inflate(R.layout.filter_popup, null);

            final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setCancelable(true);
            alert.setView(confirmDialog);
            final AlertDialog alertDialog = alert.create();
            alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            alertDialog.show();
//            String[] array = new String[]{"Apple", "Google"};

//            String[] array = category_lists.get(i).getName()new String[category_lists.size()]);
//            ViewGroup checkboxContainer = (ViewGroup) confirmDialog.findViewById(R.id.checkbox_container);
//            for (int i = 0; i < category_lists.size(); i++) {
//                CheckBox checkBox = new CheckBox(this);
//                checkBox.setText(category_lists.get(i).getName());
//                checkboxContainer.addView(checkBox);
//            }
            RadioGroup rgp= (RadioGroup) confirmDialog.findViewById(R.id.rgp_cat);
            RadioGroup.LayoutParams rprms;

            for(int i=0;i<category_lists.size();i++){
                RadioButton radioButton = new RadioButton(this);
                radioButton.setText(category_lists.get(i).getName());
                radioButton.setId(View.generateViewId());
//                rprms= new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                rgp.addView(radioButton);
            }

            rgp.check(category_lists.indexOf(StCategory));
            rgp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    // find which radio button is selected
                    String CatId="";
//                    CatId = category_lists.get(checkedId - 2).getId();

                    if(checkedId == R.id.rb_all){
                        CatId="0";
//                        Toast.makeText(getApplicationContext(), String.valueOf(CatId),
//                                Toast.LENGTH_SHORT).show();

                        SharedPreferences sp1 = getSharedPreferences("Category",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor =sp1.edit();
                        editor.putString("categoryid",String.valueOf(CatId));
                        editor.apply();
                        editor.commit();
                    }else {
                        CatId = category_lists.get(checkedId - 1).getId();
//                        Toast.makeText(getApplicationContext(), String.valueOf(checkedId-1), Toast.LENGTH_SHORT).show();
                        SharedPreferences sp1 = getSharedPreferences("Category",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor =sp1.edit();
                        editor.putString("categoryid",String.valueOf(CatId));
                        editor.apply();
                        editor.commit();

                        StCategory = sp1.getString("categoryid","0");
                    }
                }
            });

            Button BtnSubmit= confirmDialog.findViewById(R.id.btn_submit);
            BtnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    overridePendingTransition(R.anim.fadein,R.anim.fadeout);
                    alertDialog.dismiss();
                }
            });
            // show the popup window
            // which view you pass in doesn't matter, it is only used for the window tolken
//            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getCategoryList() {

        final SpotsDialog dialog = new SpotsDialog(this);
        dialog.show();

        JsonArrayRequest request = new JsonArrayRequest(Base_url+"all-category",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("response",""+response);

                        if (response == null) {
                            TvNoArticles.setVisibility(View.VISIBLE);

                            dialog.dismiss();
                            return;
                        }
                        Gson gson = new Gson();
                        category_lists.clear();
                        dialog.dismiss();
                        for (int i = 0; i < response.length(); i++) {
//                            List<category_list> items = new Gson().fromJson(response.toString(), new TypeToken<List<category_list>>() {
//                            }.getType());
                            category_list promoted_pictures_listss = null;
                            try {
                                promoted_pictures_listss = gson.fromJson(response.getJSONObject(i).toString(), category_list.class);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            category_lists.add(promoted_pictures_listss);
                            categoriesAdapter.notifyDataSetChanged();
                            articleCategoriesAdapter.notifyDataSetChanged();
                            RvCategories.setAdapter(categoriesAdapter);
                            RvCategoriesPicture.setAdapter(articleCategoriesAdapter);
//                            if(promotedPicturesLists.size()==0){
//                                TvNoArticles.setVisibility(View.VISIBLE);
//                            }
//
//                            if(promotedPicturesLists.size()>0){
//                                TvNoArticles.setVisibility(View.GONE);
//                            }
                        }
                    }
                },new Response.ErrorListener() {
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

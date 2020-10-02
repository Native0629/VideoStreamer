package com.madehuge_nishant.videostreamer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.madehuge_nishant.videostreamer.Adapters.videos_by_cat_adapter;
import com.madehuge_nishant.videostreamer.DataModels.video_list;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import static com.madehuge_nishant.videostreamer.Others.Api_urls.Base_url;

public class VideosByCat extends AppCompatActivity {

    private List<video_list> video_lists = new ArrayList<>();
    videos_by_cat_adapter videoListAdapter;
    RecyclerView RvVideos;
    String StCategory="",StCatname="";
    TextView tv_no_videos;
    String StUsername,StUserId,StEmail,StUserImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos_by_cat);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.app_bg));

        SharedPreferences Sp =getSharedPreferences("Catid",Context.MODE_PRIVATE);
        StCategory =Sp.getString("cat_id","");
        StCatname =Sp.getString("cat_name","");
        getSupportActionBar().setTitle(StCatname+" Videos");

        SharedPreferences sp1 =getSharedPreferences("User_detail", Context.MODE_PRIVATE);
        StUsername= sp1.getString("fullname","");
        StUserId = sp1.getString("user_id","");
        StEmail = sp1.getString("email","");
        StUserImg =sp1.getString("user_image","");

        tv_no_videos = findViewById(R.id.tv_no_videos);
        videoListAdapter =new videos_by_cat_adapter(getApplicationContext(),video_lists);
        RvVideos =findViewById(R.id.rv_videos);
        RvVideos.setItemAnimator(new DefaultItemAnimator());
        RvVideos.setLayoutManager( new GridLayoutManager(getApplicationContext(), 2));

        getVideosList();
    }

    private void getVideosList() {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.show();

        JsonArrayRequest request = new JsonArrayRequest(Base_url+"view-upload-videos?category="+StCategory+"&user_id="+StUserId,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("response",""+response);

                        if (response == null) {
                            dialog.dismiss();
                            tv_no_videos.setVisibility(View.VISIBLE);
                            return;
                        }
                        Gson gson = new Gson();
                        video_lists.clear();
                        dialog.dismiss();
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
                            RvVideos.setAdapter(videoListAdapter);
                        }

                        if(video_lists.size()>0){
                            tv_no_videos.setVisibility(View.GONE);
                        }

                        if(video_lists.size()==0){
                            tv_no_videos.setVisibility(View.VISIBLE);
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

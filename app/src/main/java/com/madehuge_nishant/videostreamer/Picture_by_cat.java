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
import com.madehuge_nishant.videostreamer.Adapters.article_list_adapter;
import com.madehuge_nishant.videostreamer.DataModels.article_list;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import static com.madehuge_nishant.videostreamer.Others.Api_urls.Base_url;

public class Picture_by_cat extends AppCompatActivity {
    article_list_adapter articleListAdapter;
    private List<article_list> article_lists = new ArrayList<>();
    RecyclerView RvPictures;
    TextView tv_no_pictures;
    String StCategory="",StCatname="",StUsername="",StUserId="",StEmail="",StUserImg="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_by_cat);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.app_bg));

        SharedPreferences Sp =getSharedPreferences("Catid",Context.MODE_PRIVATE);
        StCategory =Sp.getString("cat_id","");
        StCatname =Sp.getString("cat_name","");

        SharedPreferences sp1 =getSharedPreferences("User_detail", Context.MODE_PRIVATE);
        StUsername= sp1.getString("fullname","");
        StUserId = sp1.getString("user_id","");
        StEmail = sp1.getString("email","");
        StUserImg =sp1.getString("user_image","");



        getSupportActionBar().setTitle(StCatname+" Pictures");

        articleListAdapter = new article_list_adapter(getApplicationContext(),article_lists);

        tv_no_pictures = findViewById(R.id.tv_no_pictures);
        RvPictures =(RecyclerView) findViewById(R.id.Rv_articles);
        RvPictures.setItemAnimator(new DefaultItemAnimator());
        RvPictures.setLayoutManager( new GridLayoutManager(getApplicationContext(), 2));

        getPicturesList();
    }

    private void getPicturesList() {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.show();

        JsonArrayRequest request = new JsonArrayRequest(Base_url+"most-viewed-articals?category="+StCategory+"&user_id="+StUserId,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("response",""+response);

                        if (response == null) {
                            tv_no_pictures.setVisibility(View.VISIBLE);

                            dialog.dismiss();
                            return;
                        }
                        Gson gson = new Gson();
                        article_lists.clear();
                        dialog.dismiss();
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
                            RvPictures.setAdapter(articleListAdapter);

                            if(article_lists.size()==0){
                                tv_no_pictures.setVisibility(View.VISIBLE);
                            }

                            if (article_lists.size() > 0) {
                                tv_no_pictures.setVisibility(View.GONE);
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


}

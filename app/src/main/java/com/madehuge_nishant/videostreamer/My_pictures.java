package com.madehuge_nishant.videostreamer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.madehuge_nishant.videostreamer.Adapters.my_pictures_adapter;
import com.madehuge_nishant.videostreamer.DataModels.article_list;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

import static com.madehuge_nishant.videostreamer.Others.Api_urls.Base_url;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link My_pictures.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link My_pictures#newInstance} factory method to
 * create an instance of this fragment.
 */
public class My_pictures extends Fragment {

    my_pictures_adapter recomendedArticles;
    private List<article_list> article_lists = new ArrayList<>();
    RecyclerView Rv_recent_video;
    String StUsername="",StUserId="";
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.fragment_my_pictures, container, false);

        SharedPreferences sp1 =getActivity().getSharedPreferences("User_detail", Context.MODE_PRIVATE);
        StUsername = sp1.getString("fullname","");
        StUserId = sp1.getString("user_id","");

        recomendedArticles =new my_pictures_adapter(getActivity(),article_lists);
        Rv_recent_video =(RecyclerView) view.findViewById(R.id.rv_recomended_article);
        Rv_recent_video.setItemAnimator(new DefaultItemAnimator());
        Rv_recent_video.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        getRecomendedArticle();
        // Inflate the layout for this fragment
        return view;
    }
    private void getRecomendedArticle() {
        final SpotsDialog dialog = new SpotsDialog(getActivity());
        dialog.show();

        JsonArrayRequest request = new JsonArrayRequest(Base_url + "view-history-articals?user_id="+StUserId,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("response", "" + response);

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

                                article_lists.add(video_listss);
                                recomendedArticles.notifyDataSetChanged();
                                Rv_recent_video.setAdapter(recomendedArticles);

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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(request);
    }
}

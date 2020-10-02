package com.madehuge_nishant.videostreamer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.madehuge_nishant.videostreamer.Adapters.my_videos_adapter;
import com.madehuge_nishant.videostreamer.DataModels.video_list;
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
 * {@link My_videos.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link My_videos#newInstance} factory method to
 * create an instance of this fragment.
 */
public class My_videos extends Fragment {
   View view;
    RecyclerView Rv_recent_video;
    my_videos_adapter videoListAdapter;
    private List<video_list> video_lists = new ArrayList<>();

    String StUserId="",StUsername="";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.fragment_my_videos, container, false);

        SharedPreferences sp1 =getActivity().getSharedPreferences("User_detail", Context.MODE_PRIVATE);
        StUsername = sp1.getString("fullname","");
        StUserId = sp1.getString("user_id","");

        videoListAdapter =new my_videos_adapter(getActivity(),video_lists);
        Rv_recent_video =(RecyclerView) view.findViewById(R.id.rv_my_videos);
        Rv_recent_video.setItemAnimator(new DefaultItemAnimator());
        Rv_recent_video.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
//        touchHelper.attachToRecyclerView(Rv_recent_video); // Attaching with RecyclerView

        getVideoLists();
        // Inflate the layout for this fragment
        return view;
    }
    ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            if (i == ItemTouchHelper.LEFT) {
                Toast.makeText(getContext(),"Swipe left",Toast.LENGTH_SHORT).show();
            } else if (i == ItemTouchHelper.RIGHT) {
                Toast.makeText(getContext(),"Swipe right",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                float alpha = 1 - (Math.abs(dX) / recyclerView.getWidth());
                viewHolder.itemView.setAlpha(alpha);
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    });


    private void getVideoLists() {
//        Log.e("vidurl",StRecommendedVideoUrl);
        final SpotsDialog dialog = new SpotsDialog(getActivity());
        dialog.show();

        JsonArrayRequest request = new JsonArrayRequest(Base_url+"view-history-videos?user_id="+StUserId,
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

                                video_lists.add(video_listss);
                                videoListAdapter.notifyDataSetChanged();
                                Rv_recent_video.setAdapter(videoListAdapter);

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

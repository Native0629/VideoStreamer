package com.madehuge_nishant.videostreamer.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.madehuge_nishant.videostreamer.DataModels.video_list;
import com.madehuge_nishant.videostreamer.Edit_video;
import com.madehuge_nishant.videostreamer.History;
import com.madehuge_nishant.videostreamer.Others.ItemClickListener;
import com.madehuge_nishant.videostreamer.Others.JSONParserVolley;
import com.madehuge_nishant.videostreamer.Others.Utils;
import com.madehuge_nishant.videostreamer.R;
import com.madehuge_nishant.videostreamer.Video_detail;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;

import static android.content.Context.MODE_PRIVATE;
import static com.madehuge_nishant.videostreamer.Others.Api_urls.Base_url;

public class my_videos_adapter extends RecyclerView.Adapter<my_videos_adapter.MyViewHolder> {

    private Context mContext;
    private List<video_list> videoList;
    private List<video_list> videoListFiltered;

    String DogStatus;
    View itemView;
    video_list row, row1;

    public my_videos_adapter(Context context, List<video_list> videoList) {
        super();
        // this.parentActivity = parentActivity;
        this.mContext = context;
        this.videoListFiltered = videoList;
        this.videoList = videoList;
    }

    @Override
    public int getItemCount() {

//        return videoList.size();

        return Math.min(videoList.size(), 7);
    }

    @Override
    public my_videos_adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_view1, parent, false);
        final my_videos_adapter.MyViewHolder viewHolder = new my_videos_adapter.MyViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final my_videos_adapter.MyViewHolder holder, final int i) {
        row = videoList.get(i);

//        DateFormat df = new SimpleDateFormat("dd/MM/yyyy,HH:mm");
//        String date = df.format(row.getExam_date().toString());

        holder.TvTitle.setText(row.getTitle());
        holder.TvDesc.setText(row.getDescription());
        holder.TvTotalLikes.setText(row.getLike() + " Liked");
        holder.TvTotalViews.setText(row.getViewcount() + " Viewed");
        holder.TvCategory.setText(row.getCategoryName() + " | " + row.getUploadedDate() + "\nUploaded by : " + row.getUploadedBy());

        holder.TvMenuOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                row1 = videoList.get(i);
                //creating a popup menu
                PopupMenu popup = new PopupMenu(mContext, holder.TvMenuOption);
                //inflating menu from xml resource
                popup.inflate(R.menu.menu_my_content);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_share:

                                try {
                                    Intent i = new Intent(Intent.ACTION_SEND);
                                    i.setType("text/plain");
                                    i.putExtra(Intent.EXTRA_SUBJECT, String.valueOf(R.string.app_name));
                                    String sAux = "\nI am sharing this video " + row1.getTitle() + " with you.Please watch it by clicking below link:\n";
                                    sAux = sAux + row1.getVideo();
                                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    mContext.startActivity(Intent.createChooser(i, "Share using"));

                                } catch (Exception e) {
                                    //e.toString();
                                }
                                //handle menu1 click
                                return true;

                            case R.id.action_edit:
                                row1 =videoListFiltered.get(i);
                                SharedPreferences sp =mContext.getSharedPreferences("edit_videos_detail",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("Video_id",row1.getId());
                                editor.putString("Video_title",row1.getTitle());
                                editor.putString("Video_desc",row1.getDescription());
                                editor.putString("Video_category",row1.getCategoryName());
                                editor.putString("Video_category_id",row1.getCategory());
                                editor.putString("Video_promotion_id",row1.getParmot_id());
                                editor.putString("Video_promotion_name",row1.getParmot_name());
//                                editor.putString("Video_promote",row1.get());

                                editor.apply();
                                editor.commit();
                                Intent in=new Intent(mContext,Edit_video.class);
                                in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mContext.startActivity(in);
                                return true;

                            case R.id.action_delete:

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                                alertDialogBuilder.setMessage("Are you sure you want to delete this Picture?");
                                alertDialogBuilder.setPositiveButton("Yes,Delete",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                row1 =videoListFiltered.get(i);
                                                String StArticleId = row1.getId();

                                                deletePictures(StArticleId);

                                            }
                                        });

                                alertDialogBuilder.setNegativeButton("cancel",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {

                                            }
                                        });

                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();


                                return true;

                            default:
                                return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();

            }
        });

        Glide.with(mContext)
                .load(row.getImage())
                .crossFade()
                .skipMemoryCache(true)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.IvVideo);

        holder.Ll_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp1 = mContext.getSharedPreferences("Coins_detail", MODE_PRIVATE);
                String StBalance = sp1.getString("balance", "");
                SharedPreferences sp2 =mContext.getSharedPreferences("User_detail", Context.MODE_PRIVATE);
                String StUserId = sp2.getString("user_id","");

                row1 = videoList.get(i);
                if (Integer.parseInt(StBalance) > 0) {
                    SharedPreferences sp = mContext.getSharedPreferences("video_details", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("video_id", row1.getId());
                    editor.putString("video_uploader_id", row1.getUserId());
                    editor.putString("video_title", row1.getTitle());
                    editor.putString("video_desc", row1.getDescription());
                    editor.putString("video_url", row1.getVideo());
                    editor.putString("video_thumb", row1.getImage());
                    editor.putString("video_like", row1.getLike());
                    editor.putString("video_views", row1.getViewcount());
                    editor.putString("upload_date", row1.getUploadedDate());
                    editor.putString("upload_by", row1.getUploadedBy());
                    editor.putString("category_name", row1.getCategoryName());
                    editor.putString("total_comment", row1.getTotalComment());
                    editor.putString("liked_user_id", row1.getLikeUserId());
                    editor.putString("recommended_pictures_url","view-history-videos?user_id="+StUserId);

                    editor.commit();

                    Intent in = new Intent(mContext, Video_detail.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(in);
                } else {
                    Toast.makeText(mContext, "You have insufficient Coin Balance\nYou Can not watch this Video", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private void deletePictures(final String stArticleId) {

        final SpotsDialog dialog = new SpotsDialog(mContext);
        dialog.setTitle("Registering,Please Wait..");
        dialog.show();
//        Toast.makeText(getApplicationContext(),stMobnum+"\n"+stOTP+"\n"+stEmailid+"\n"+stAddress,Toast.LENGTH_LONG).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Base_url+"delete-video",
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

                                Utils.showErrorMessage(mContext,"Article Deleted Successfully");
//                                status = json.getString("success_msg");
//                                Toast.makeText(Edit_profile.this, status, Toast.LENGTH_SHORT).show();
                                mContext.startActivity(new Intent(mContext, History.class));

                            } else {
                                dialog.dismiss();
                                Toast.makeText(mContext, status, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(mContext, String.valueOf(e), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        Toast.makeText(mContext, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();
                map.put("id",stArticleId);

                Log.e("param",""+map);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(500000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public ItemClickListener itemClickListener;

        TextView TvTitle, TvDesc, TvTotalLikes, TvTotalViews, TvMenuOption, TvCategory;
        ImageView IvVideo;
        LinearLayout Ll_video;

        public MyViewHolder(View parent) {
            super(parent);

            TvTitle = parent.findViewById(R.id.tv_title);
            TvDesc = parent.findViewById(R.id.tv_desc);
            IvVideo = parent.findViewById(R.id.iv_thumbnail);
            Ll_video = parent.findViewById(R.id.ll_video);
            TvTotalLikes = parent.findViewById(R.id.tv_total_likes);
            TvTotalViews = parent.findViewById(R.id.tv_total_views);
            TvMenuOption = parent.findViewById(R.id.textViewOptions);
            TvCategory = parent.findViewById(R.id.tv_category_date_dur);
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getPosition(), false);

        }

        @Override
        public boolean onLongClick(View v) {
            itemClickListener.onClick(v, getPosition(), true);
            return true;
        }
    }
}
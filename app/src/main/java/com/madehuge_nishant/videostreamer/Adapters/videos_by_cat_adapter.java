package com.madehuge_nishant.videostreamer.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.madehuge_nishant.videostreamer.DataModels.video_list;
import com.madehuge_nishant.videostreamer.Others.ItemClickListener;
import com.madehuge_nishant.videostreamer.R;
import com.madehuge_nishant.videostreamer.Video_detail;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class videos_by_cat_adapter extends RecyclerView.Adapter<videos_by_cat_adapter.MyViewHolder> {

    private Context mContext;
    private List<video_list> videoList;
    private List<video_list> videoListFiltered;

    String DogStatus;
    View itemView;
    video_list row, row1;

    public videos_by_cat_adapter(Context context, List<video_list> videoList) {
        super();
        // this.parentActivity = parentActivity;
        this.mContext = context;
        this.videoListFiltered = videoList;
        this.videoList = videoList;
    }

    @Override
    public int getItemCount() {

        return videoList.size();
    }

    @Override
    public videos_by_cat_adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_view6, parent, false);
        final videos_by_cat_adapter.MyViewHolder viewHolder = new videos_by_cat_adapter.MyViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final videos_by_cat_adapter.MyViewHolder holder, final int i) {
        row = videoList.get(i);

        holder.TvTitle.setText(row.getTitle() + "\nUploaded by :  " + row.getUploadedBy());

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
                    editor.putString("liked_user_id", row1.getLikeUserId());
                    editor.putString("total_comment", row1.getTotalComment());
                    editor.putString("recommended_video_url", "view-upload-videos");

                    editor.commit();

                    Intent in = new Intent(mContext, Video_detail.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(in);

                } else {
                    Toast.makeText(mContext, "You have insufficient Coin Balance\nYou Can not watch this Video", Toast.LENGTH_LONG).show();
                }
            }
        });

//        holder.Rl_video_list.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                row1 = videoList.get(i);
//                String stExamId = row1.getExam_id();
//                SharedPreferences sp = mContext.getSharedPreferences("Exams_id", Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = sp.edit();
//                editor.putString("examId", stExamId);
//                editor.commit();
//
//                Intent in = new Intent(mContext, Upcoming_exams_detail.class);
//                in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                mContext.startActivity(in);
//            }
//        });

    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public ItemClickListener itemClickListener;

        TextView TvTitle;
        ImageView IvVideo;
        LinearLayout Ll_video;

        public MyViewHolder(View parent) {
            super(parent);

            TvTitle = parent.findViewById(R.id.tv_title);
            IvVideo = parent.findViewById(R.id.iv_thumbnail);
            Ll_video = parent.findViewById(R.id.ll_video);

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

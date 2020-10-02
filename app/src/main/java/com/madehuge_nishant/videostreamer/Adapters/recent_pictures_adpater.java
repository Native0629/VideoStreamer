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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.madehuge_nishant.videostreamer.Article_detail;
import com.madehuge_nishant.videostreamer.DataModels.article_list;
import com.madehuge_nishant.videostreamer.Others.ItemClickListener;
import com.madehuge_nishant.videostreamer.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.BlurTransformation;

import static android.content.Context.MODE_PRIVATE;

public class recent_pictures_adpater extends RecyclerView.Adapter<recent_pictures_adpater.MyViewHolder> {

    private Context mContext;
    private List<article_list> articleList;
    private List<article_list> articleListFiltered;

    String DogStatus;
    View itemView;
    article_list row, row1;

    public recent_pictures_adpater(Context context, List<article_list> articleList) {
        super();
        // this.parentActivity = parentActivity;
        this.mContext = context;
        this.articleListFiltered = articleList;
        this.articleList = articleList;
    }

    @Override
    public int getItemCount() {

        return articleList.size();
    }

    @Override
    public recent_pictures_adpater.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_view3, parent, false);
        final recent_pictures_adpater.MyViewHolder viewHolder = new recent_pictures_adpater.MyViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final recent_pictures_adpater.MyViewHolder holder, final int i) {
        row = articleList.get(i);

//        DateFormat df = new SimpleDateFormat("dd/MM/yyyy,HH:mm");
//        String date = df.format(row.getExam_date().toString());

        holder.TvTitle.setText(row.getTitle() + "\nUploaded by :  " + row.getUploadedBy());

//           Glide.with(mContext)
//                .load(row.getImage())
//                .listener(new RequestListener<String, GlideDrawable>() {
//                    @Override
//                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                        holder.progressBar.setVisibility(View.GONE);
//                        return false;
//                    }
//                })
//                   .override(35, 45)
//                   .centerCrop()
//
//                   .skipMemoryCache(true)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .into(holder.IvVideo);

        Picasso.with(mContext)
                .load(row.getImage())
                .centerCrop()
                .resize(240, 140)
                .transform(new BlurTransformation(mContext, 5, 2))
                .into(holder.IvVideo);

        holder.Ll_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sp1 = mContext.getSharedPreferences("Coins_detail", MODE_PRIVATE);
                String StBalance = sp1.getString("balance", "");
                row1 = articleList.get(i);

                if (Integer.parseInt(StBalance) > 0) {

                    SharedPreferences sp = mContext.getSharedPreferences("article_details", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("article_id", row1.getId());
                    editor.putString("article_uploader_id", row1.getUserId());
                    editor.putString("article_title", row1.getTitle());
                    editor.putString("article_desc", row1.getDescription());
                    editor.putString("article_url", row1.getImage());
                    editor.putString("article_like", row1.getLike());
                    editor.putString("upload_date", row1.getUploadedDate());
                    editor.putString("upload_by", row1.getUploadedBy());
                    editor.putString("category_name", row1.getCategoryName());
                    editor.putString("liked_user_id", row1.getLikeUserId());
                    editor.putString("article_views", row1.getViewcount());
                    editor.putString("total_comment", row1.getTotalComment());
                    editor.putString("recommended_pictures_url", "view-upload-artocals");

                    editor.commit();

                    Intent in = new Intent(mContext, Article_detail.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(in);

                } else {
//                    Utils.showErrorMessage(mContext,"You have insufficient Coin Balance\nYou Can not View this Article");

                    Toast.makeText(mContext, "You have insufficient Coin Balance\nYou Can not View this Article", Toast.LENGTH_LONG).show();
                }
            }
        });

//        holder.Rl_article_list.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                row1 = articleList.get(i);
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
        ProgressBar progressBar;

        public MyViewHolder(View parent) {
            super(parent);
            progressBar = parent.findViewById(R.id.progress);
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

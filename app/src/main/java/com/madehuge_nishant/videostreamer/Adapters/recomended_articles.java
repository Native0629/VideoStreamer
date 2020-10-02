package com.madehuge_nishant.videostreamer.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.madehuge_nishant.videostreamer.Article_detail;
import com.madehuge_nishant.videostreamer.DataModels.article_list;
import com.madehuge_nishant.videostreamer.Others.ItemClickListener;
import com.madehuge_nishant.videostreamer.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.BlurTransformation;

import static android.content.Context.MODE_PRIVATE;

public class recomended_articles extends RecyclerView.Adapter<recomended_articles.MyViewHolder> {

    private Context mContext;
    private List<article_list> articleList;
    private List<article_list> articleListFiltered;

    String DogStatus;
    View itemView;
    article_list row, row1;

    public recomended_articles(Context context, List<article_list> articleList) {
        super();
        // this.parentActivity = parentActivity;
        this.mContext = context;
        this.articleListFiltered = articleList;
        this.articleList = articleList;
    }

    @Override
    public int getItemCount() {

//        return articleList.size();
        return Math.min(articleList.size(), 7);

    }

    @Override
    public recomended_articles.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_view1, parent, false);
        final recomended_articles.MyViewHolder viewHolder = new recomended_articles.MyViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final recomended_articles.MyViewHolder holder, final int i) {
        row = articleList.get(i);

//        DateFormat df = new SimpleDateFormat("dd/MM/yyyy,HH:mm");
//        String date = df.format(row.getExam_date().toString());

        holder.TvTitle.setText(row.getTitle());

//        Glide.with(mContext)
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
//
//                })
//                .centerCrop()
//                .skipMemoryCache(true)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .into(holder.IvVideo);

        if(!row.getImage().equals("")) {

            Picasso.with(mContext)
                    .load(row.getImage())
                    .centerCrop()
                    .resize(200, 140)
                    .transform(new BlurTransformation(mContext, 3, 2))
                    .into(holder.IvVideo);
        }
        holder.TvDesc.setText(row.getDescription());
        holder.IvPlay.setVisibility(View.GONE);

        holder.TvTotalLikes.setText(row.getLike()+" Liked");
        holder.TvTotalViews.setText(row.getViewcount()+" Viewed");
        holder.TvCategory.setText(row.getCategoryName()+" | "+row.getUploadedDate() +"\nUploaded by : "+row.getUploadedBy());

        holder.TvMenuOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                row1 = articleList.get(i);
                //creating a popup menu
                PopupMenu popup = new PopupMenu(mContext, holder.TvMenuOption);
                //inflating menu from xml resource
                popup.inflate(R.menu.main);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_filter:

                                try {
                                    Intent i = new Intent(Intent.ACTION_SEND);
                                    i.setType("text/plain");
                                    i.putExtra(Intent.EXTRA_SUBJECT, String.valueOf(R.string.app_name));
                                    String sAux = "\nI am sharing this Article "+row1.getTitle()+ " with you.Please view it by clicking below link:\n";
                                    sAux = sAux +row1.getImage()+"\n "+row1.getDescription();
                                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    mContext.startActivity(Intent.createChooser(i, "Share using"));

                                } catch(Exception e) {
                                    //e.toString();
                                }

                                //handle menu1 click
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
                    editor.putString("article_views", row1.getViewcount());
                    editor.putString("upload_date",row1.getUploadedDate());
                    editor.putString("upload_by",row1.getUploadedBy());
                    editor.putString("category_name",row1.getCategoryName());
                    editor.putString("total_comment",row1.getTotalComment());
                    editor.putString("liked_user_id",row1.getLikeUserId());
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

        TextView TvTitle,TvDesc,TvTotalLikes,TvTotalViews,TvMenuOption,TvCategory;
        ImageView IvVideo,IvPlay;
        LinearLayout Ll_video;
        ProgressBar progressBar;

        public MyViewHolder(View parent) {
            super(parent);
            progressBar = parent.findViewById(R.id.progress);

            TvTitle = parent.findViewById(R.id.tv_title);
            TvDesc =parent.findViewById(R.id.tv_desc);
            IvVideo = parent.findViewById(R.id.iv_thumbnail);
            Ll_video = parent.findViewById(R.id.ll_video);
            TvTotalLikes =parent.findViewById(R.id.tv_total_likes);
            TvTotalViews =parent.findViewById(R.id.tv_total_views);
            TvMenuOption =parent.findViewById(R.id.textViewOptions);
            TvCategory =parent.findViewById(R.id.tv_category_date_dur);
            IvPlay=parent.findViewById(R.id.iv_play);

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

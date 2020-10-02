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
import com.madehuge_nishant.videostreamer.DataModels.promoted_pictures_list;
import com.madehuge_nishant.videostreamer.Others.ItemClickListener;
import com.madehuge_nishant.videostreamer.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.BlurTransformation;

import static android.content.Context.MODE_PRIVATE;

public class promoted_pictures_adapter extends RecyclerView.Adapter<promoted_pictures_adapter.MyViewHolder> {

    private Context mContext;
    private List<promoted_pictures_list> promotedpicturesList;
    private List<promoted_pictures_list> promotedpicturesListFiltered;

    String DogStatus;
    View itemView;
    promoted_pictures_list row, row1;

    public promoted_pictures_adapter(Context context, List<promoted_pictures_list> promotedpicturesList) {
        super();
        // this.parentActivity = parentActivity;
        this.mContext = context;
        this.promotedpicturesListFiltered = promotedpicturesList;
        this.promotedpicturesList = promotedpicturesList;
    }

    @Override
    public int getItemCount() {

        return promotedpicturesList.size();
    }

    @Override
    public promoted_pictures_adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_view5, parent, false);
        final promoted_pictures_adapter.MyViewHolder viewHolder = new promoted_pictures_adapter.MyViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final promoted_pictures_adapter.MyViewHolder holder, final int i) {
        row = promotedpicturesList.get(i);

//        DateFormat df = new SimpleDateFormat("dd/MM/yyyy,HH:mm");
//        String date = df.format(row.getExam_date().toString());

        holder.TvTitle.setText(row.getTitle()+"\nUploaded by :  "+row.getUploadedBy());

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
                .resize(1200, 600)
                .transform(new BlurTransformation(mContext, 20, 2))
                .into(holder.IvVideo);

        holder.Ll_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp1 =mContext.getSharedPreferences("Coins_detail",MODE_PRIVATE);
                String StBalance = sp1.getString("balance","");
                row1 = promotedpicturesList.get(i);

                if(Integer.parseInt(StBalance)>0) {

                    SharedPreferences sp = mContext.getSharedPreferences("article_details", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("article_id", row1.getId());
                    editor.putString("article_uploader_id", row1.getUserId());
                    editor.putString("article_title", row1.getTitle());
                    editor.putString("article_desc", row1.getDescription());
                    editor.putString("article_url", row1.getImage());
                    editor.putString("article_like", row1.getLike());
                    editor.putString("upload_date",row1.getUploadedDate());
                    editor.putString("upload_by",row1.getUploadedBy());
                    editor.putString("category_name",row1.getCategoryName());
                    editor.putString("liked_user_id",row1.getLikeUserId());
                    editor.putString("article_views", row1.getViewcount());
                    editor.putString("total_comment",row1.getTotalComment());
                    editor.putString("recommended_pictures_url","view-upload-artocals-recommended");

                    editor.commit();

                    Intent in = new Intent(mContext, Article_detail.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(in);

                }else{
//                    Utils.showErrorMessage(mContext,"You have insufficient Coin Balance\nYou Can not View this Article");

                    Toast.makeText(mContext,"You have insufficient Coin Balance\nYou Can not View this Article", Toast.LENGTH_LONG).show();
                }
            }
        });

//        holder.Rl_promoted_pictures_list.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                row1 = promotedpicturesList.get(i);
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
            TvTitle =parent.findViewById(R.id.tv_title);
            IvVideo =parent.findViewById(R.id.iv_thumbnail);
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

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

public class recent_video_list_adapter  extends RecyclerView.Adapter<recent_video_list_adapter.MyViewHolder> {

    private Context mContext;
    private List<video_list> videoList;
    private List<video_list> videoListFiltered;

    String DogStatus;
    View itemView;
    video_list row, row1;

    public recent_video_list_adapter(Context context, List<video_list> videoList) {
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
    public recent_video_list_adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_view1, parent, false);
        final recent_video_list_adapter.MyViewHolder viewHolder = new recent_video_list_adapter.MyViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final recent_video_list_adapter.MyViewHolder holder, final int i) {
        row = videoList.get(i);

//        DateFormat df = new SimpleDateFormat("dd/MM/yyyy,HH:mm");
//        String date = df.format(row.getExam_date().toString());

        holder.TvTitle.setText(row.getTitle());
        holder.TvDesc.setText(row.getDescription());
        holder.TvTotalLikes.setText(row.getLike()+" Liked");
        holder.TvTotalViews.setText(row.getViewcount()+" Viewed");
        holder.TvCategory.setText(row.getCategoryName()+" | "+row.getUploadedDate() +"\nUploaded by : "+row.getUploadedBy());

        holder.TvMenuOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                row1 = videoList.get(i);
                //creating a popup menu
                PopupMenu popup = new PopupMenu(mContext, holder.TvMenuOption);
                //inflating menu from xml resource
                popup.inflate(R.menu.main);
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
                                    String sAux = "\nI am sharing this video "+row1.getTitle()+ " with you.Please watch it by clicking below link:\n";
                                    sAux = sAux +row1.getVideo();
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

        Glide.with(mContext)
                .load(row.getImage())
                .crossFade()
                .skipMemoryCache(true)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.IvVideo);

        holder.Ll_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                SharedPreferences sp1 =mContext.getSharedPreferences("Coins_detail",MODE_PRIVATE);
                String StBalance = sp1.getString("balance","");

                row1 = videoList.get(i);
                if(Integer.parseInt(StBalance)>0) {
                    SharedPreferences sp = mContext.getSharedPreferences("video_details", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("video_id",row1.getId());
                editor.putString("video_uploader_id",row1.getUserId());
                editor.putString("video_title",row1.getTitle());
                editor.putString("video_desc",row1.getDescription());
                editor.putString("video_url",row1.getVideo());
                editor.putString("video_thumb",row1.getImage());
                editor.putString("video_like",row1.getLike());
                editor.putString("video_views",row1.getViewcount());
                editor.putString("upload_date",row1.getUploadedDate());
                editor.putString("upload_by",row1.getUploadedBy());
                editor.putString("category_name",row1.getCategoryName());
                editor.putString("total_comment",row1.getTotalComment());
                editor.putString("liked_user_id",row1.getLikeUserId());
                editor.commit();

                Intent in = new Intent(mContext, Video_detail.class);
                in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(in);
                }else{
                    Toast.makeText(mContext,"You have insufficient Coin Balance\nYou Can not watch this Video", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public ItemClickListener itemClickListener;

        TextView TvTitle,TvDesc,TvTotalLikes,TvTotalViews,TvMenuOption,TvCategory;
        ImageView IvVideo;
        LinearLayout Ll_video;

        public MyViewHolder(View parent) {
            super(parent);

            TvTitle = parent.findViewById(R.id.tv_title);
            TvDesc =parent.findViewById(R.id.tv_desc);
            IvVideo = parent.findViewById(R.id.iv_thumbnail);
            Ll_video = parent.findViewById(R.id.ll_video);
            TvTotalLikes =parent.findViewById(R.id.tv_total_likes);
            TvTotalViews =parent.findViewById(R.id.tv_total_views);
            TvMenuOption =parent.findViewById(R.id.textViewOptions);
            TvCategory =parent.findViewById(R.id.tv_category_date_dur);
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

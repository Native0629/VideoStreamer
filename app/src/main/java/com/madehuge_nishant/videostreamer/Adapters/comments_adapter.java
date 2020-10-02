package com.madehuge_nishant.videostreamer.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.madehuge_nishant.videostreamer.DataModels.comments_list;
import com.madehuge_nishant.videostreamer.Others.ItemClickListener;
import com.madehuge_nishant.videostreamer.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class comments_adapter extends RecyclerView.Adapter<comments_adapter.MyViewHolder> {

    private Context mContext;
    private List<comments_list> commentsList;
    private List<comments_list> commentsListFiltered;

    String DogStatus;
    View itemView;
    comments_list row, row1;

    public comments_adapter(Context context, List<comments_list> commentsList) {
        super();
        // this.parentActivity = parentActivity;
        this.mContext = context;
        this.commentsListFiltered = commentsList;
        this.commentsList = commentsList;
    }

    @Override
    public int getItemCount() {

        return commentsList.size();
    }

    @Override
    public comments_adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_view, parent, false);
        final comments_adapter.MyViewHolder viewHolder = new comments_adapter.MyViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final comments_adapter.MyViewHolder holder, final int i) {
        row = commentsList.get(i);
        if(row.getError().equals("false")) {
            holder.LlComment.setVisibility(View.VISIBLE);
            holder.LlComment.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            holder.TvUsernameDate.setText(row.getUserName() + " - " + row.getDate());
            holder.TvComment.setText(row.getComment());

            if (!row.getImage().equals("")) {
                Picasso.with(mContext)
                        .load(row.getImage())
                        .centerCrop()
                        .resize(200, 120)
//                    .transform(new BlurTransformation(mContext, 10, 1))
                        .into(holder.civUser);
            }
        }
        else{
            holder.LlComment.setVisibility(View.GONE);
            holder.LlComment.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public ItemClickListener itemClickListener;
        TextView TvUsernameDate,TvComment;
        CircleImageView civUser;
        LinearLayout LlComment;

        public MyViewHolder(final View parent) {
            super(parent);

            parent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        // run scale animation and make it bigger
                        Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.scale_in_tv);
                        parent.startAnimation(anim);
                        anim.setFillAfter(true);
                    } else {
                        // run scale animation and make it smaller
                        Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.scale_out_tv);
                        parent.startAnimation(anim);
                        anim.setFillAfter(true);
                    }
                }
            });

            TvUsernameDate = parent.findViewById(R.id.tv_username);
            TvComment = parent.findViewById(R.id.tv_comment);
            civUser = parent.findViewById(R.id.civ_user);
            LlComment =parent.findViewById(R.id.ll_comment_view);


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

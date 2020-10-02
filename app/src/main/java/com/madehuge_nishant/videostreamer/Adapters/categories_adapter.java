package com.madehuge_nishant.videostreamer.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.madehuge_nishant.videostreamer.DataModels.category_list;
import com.madehuge_nishant.videostreamer.Others.ItemClickListener;
import com.madehuge_nishant.videostreamer.R;
import com.madehuge_nishant.videostreamer.VideosByCat;

import java.util.List;

public class categories_adapter extends RecyclerView.Adapter<categories_adapter.MyViewHolder> {

    private Context mContext;
    private List<category_list> commentsList;
    private List<category_list> commentsListFiltered;

    String DogStatus;
    View itemView;
    category_list row, row1;

    public categories_adapter(Context context, List<category_list> commentsList) {
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
    public categories_adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_view, parent, false);
        final categories_adapter.MyViewHolder viewHolder = new categories_adapter.MyViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final categories_adapter.MyViewHolder holder, final int i) {

            row = commentsList.get(i);

        Log.e("name",row.getName());
        String catName ="";
        catName =row.getName().toString();
            holder.TvCatname.setText(catName);
            holder.CvCat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    row1 = commentsList.get(i);
                    String StCatid = row1.getId();
                    SharedPreferences Sp =mContext.getSharedPreferences("Catid",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor =Sp.edit();
                    editor.putString("cat_id",StCatid);
                    editor.putString("cat_name",row1.getName());
                    editor.commit();
                    editor.apply();
//                    Toast.makeText(mContext, StCatid, Toast.LENGTH_SHORT).show();
                    Intent in =new Intent(mContext,VideosByCat.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(in);
                }
            });
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public ItemClickListener itemClickListener;
        TextView TvCatname,TvComment;

        LinearLayout LlCat;
        CardView CvCat;
        public MyViewHolder(final View parent) {
            super(parent);
            TvCatname = parent.findViewById(R.id.tv_catName);
            CvCat =parent.findViewById(R.id.cv_category);
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

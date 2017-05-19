package com.example.a700_15isk.redhomework.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.a700_15isk.redhomework.Classes.Res_body;
import com.example.a700_15isk.redhomework.R;

import java.util.ArrayList;

/**
 * Created by 700-15isk on 2017/5/19.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoHolder> {
    ArrayList<Res_body> res_bodies = new ArrayList<>();
    Context context;

    public VideoAdapter(ArrayList<Res_body> arrayList, Context context) {
        this.res_bodies = arrayList;
        this.context=context;
    }



    @Override
    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VideoHolder(LayoutInflater.from(context).inflate(R.layout.rec_item, parent, false));
    }



    @Override
    public void onBindViewHolder(VideoHolder holder, int position) {
            holder.textView.setText(res_bodies.get(position).text);
    }


    @Override
    public int getItemCount() {
        return res_bodies.size();
    }

    public class VideoHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public VideoHolder(View itemView) {
            super(itemView);
            textView=(TextView)itemView.findViewById(R.id.userContent);

        }
    }
}

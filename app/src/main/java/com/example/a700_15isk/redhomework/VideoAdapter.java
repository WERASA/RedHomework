package com.example.a700_15isk.redhomework;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.a700_15isk.redhomework.Classes.Res_body;
import com.example.a700_15isk.redhomework.Tools.CircleImageView;
import com.example.a700_15isk.redhomework.Tools.BitmapTool.MyBitmapUtils;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Thread.sleep;

/**
 * Created by 700-15isk on 2017/5/19.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoHolder>
         {
    ArrayList<Res_body> res_bodies = new ArrayList<>();
    Context context;
    SurfaceHolder mHolder;
    MediaPlayer mediaPlayer=new MediaPlayer();
    String uri;
    private boolean isPlaying;
    SeekBar seekBar;




    public VideoAdapter(ArrayList<Res_body> arrayList, Context context) {
        this.res_bodies = arrayList;
        this.context = context;
    }


    @Override
    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VideoHolder(LayoutInflater.from(context).inflate(R.layout.rec_item, parent, false));
    }


    @Override
    public void onBindViewHolder(final VideoHolder holder, final int position) {
        holder.textView.setText(res_bodies.get(position).text);
        holder.creat_time.setText(res_bodies.get(position).create_time);
        holder.userName.setText(res_bodies.get(position).userName);

        uri = res_bodies.get(position).videoUrl;
        final MyBitmapUtils myBitmapUtils = new MyBitmapUtils();
        myBitmapUtils.disPlay(holder.user_Icon, res_bodies.get(position).user_icon);
        MediaMetadataRetriever media=new MediaMetadataRetriever();
        media.setDataSource(uri, new HashMap<String, String>());
        holder.first.setImageBitmap(media.getFrameAtTime());
        media.release();
        final ArrayList<String>urls=new ArrayList<>();
        for (Res_body r:res_bodies){
            urls.add(r.videoUrl);

        }
        holder.start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data=uri;
                Intent i=new Intent(context,VideoPlayer.class);
                i.putExtra("uri",urls);
                i.putExtra("position",position);
                context.startActivity(i);
            }
        });
    }









    @Override
    public int getItemCount() {
       return res_bodies.size();
    }

     public class VideoHolder extends RecyclerView.ViewHolder {
        TextView textView;
        TextView userName;
        TextView creat_time;

        ImageView start;

        SeekBar seekBar;
       ImageView user_Icon;
         ImageView first;




        public VideoHolder(View itemView) {
            super(itemView);

            user_Icon = (ImageView) itemView.findViewById(R.id.author_icon);
            textView = (TextView) itemView.findViewById(R.id.user_content);
            userName = (TextView) itemView.findViewById(R.id.user_name);
            creat_time = (TextView) itemView.findViewById(R.id.creat_time);

            start=(ImageView)itemView.findViewById(R.id.start) ;
            first=(ImageView)itemView.findViewById(R.id.first);
        }
    }



         }





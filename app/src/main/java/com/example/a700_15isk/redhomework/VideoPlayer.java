package com.example.a700_15isk.redhomework;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.a700_15isk.redhomework.Tools.ActivityCollector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class VideoPlayer extends AppCompatActivity implements
        MediaPlayer.OnPreparedListener,  MediaPlayer.OnBufferingUpdateListener,
        View.OnClickListener {
ArrayList<String>urls=new ArrayList<>();
    ProgressBar progressBar;
    SeekBar seekBar;
    SurfaceView surfaceView;
    Button stopButton;
    Button replayButton;
    TextView vedioTiemTextView;
    Button next;
    Button back;
    int position;
    MediaPlayer mediaPlayer;
    String path;
    private boolean seekBarAutoFlag;
    private int videoTimeLong;
    private String videoTimeString;
    private SurfaceHolder surfaceHolder;
    String fileEx,fileNa,fileName;
    int downLoadFileSize,fileSize;
    Notification notification;
    NotificationManager notificationManager;
    private RemoteViews notificationViews;
    Button download;
    Timer timer;
    TimerTask task;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    notificationManager=(NotificationManager)VideoPlayer.this.getSystemService(Context.NOTIFICATION_SERVICE);
//                    notification.tickerText="少女祈祷中。。。。";
                    notificationViews=new RemoteViews(VideoPlayer.this.getPackageName(),R.layout.notifition_view);
                    break;
                case 1:
                    float size = (float) downLoadFileSize * 100 / (float) fileSize;
                    DecimalFormat format = new DecimalFormat("0.00");
                    String progress=format.format(size);
                    notificationViews.setTextViewText(R.id.progressBar, "已下载" + progress + "%");
                    notificationViews.setProgressBar(R.id.progressBar, 100, (int) size, false);
                    notification.contentView = notificationViews;
                    notificationManager.notify(1, notification);
                    break;
                case 2:
                    notificationViews.setTextViewText(R.id.progressBar, "下载完成");
                    notificationViews.setProgressBar(R.id.progressBar, 100, 100, false);
                    notification.contentView = notificationViews;
                    notification.tickerText = "下载完成";
                    notificationManager.notify(1, notification);
                    if (timer != null && task != null) {
                        timer.cancel();
                        task.cancel();
                        timer = null;
                        task = null;
                    }


            }
        }
    };
    private void handlerTask() {
        timer = new Timer();
        task = new TimerTask() {

            @Override
            public void run() {
                handler.sendEmptyMessage(1);
            }
        };
        timer.schedule(task, 500, 500);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        ActivityCollector.addActivity(this);
        Intent i=getIntent();
        position =i.getIntExtra("position",0);
        urls=i.getStringArrayListExtra("uri");
        initView();
    }
    public  void initView(){
        path =urls.get(position);
        surfaceView = (SurfaceView) findViewById(R.id.video_player);
        surfaceHolder=surfaceView.getHolder();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        stopButton = (Button) findViewById(R.id.button_stop);
        replayButton = (Button) findViewById(R.id.button_replay);
        vedioTiemTextView = (TextView) findViewById(R.id.textView_showTime);
        next = (Button) findViewById(R.id.next);
        back = (Button) findViewById(R.id.back);
        download=(Button)findViewById(R.id.download) ;
        surfaceHolder.addCallback(new SurfaceCallback());
        progressBar=(ProgressBar)findViewById(R.id.progressBar);




    }
    public void playVideo() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.reset();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        try {
            mediaPlayer.setDataSource(this, Uri.parse(path));
            mediaPlayer.prepareAsync();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }




    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SimpleDateFormat")
    public String getShowTime(long milliseconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        SimpleDateFormat dateFormat = null;
        if (milliseconds / 60000 > 60) {
            dateFormat = new SimpleDateFormat("hh:mm:ss");
        } else {
            dateFormat = new SimpleDateFormat("mm:ss");
        }
        return dateFormat.format(calendar.getTime());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onPrepared(MediaPlayer mp) {
        progressBar.setVisibility(View.GONE);
        seekBarAutoFlag = true;
        seekBar.setMax(mediaPlayer.getDuration());
        videoTimeLong = mediaPlayer.getDuration();
        videoTimeString = getShowTime(videoTimeLong);
        vedioTiemTextView.setText("00:00:00/" + videoTimeString);

        seekBar.setOnSeekBarChangeListener(new SeekBarChangeListener());

        replayButton.setOnClickListener(this);

        stopButton.setOnClickListener(this);
        next.setOnClickListener(this);
        back.setOnClickListener(this);
       download.setOnClickListener(this);
        mediaPlayer.start();

        mediaPlayer.setDisplay(surfaceHolder);

        new Thread(runnable).start();

        mediaPlayer.setScreenOnWhilePlaying(true);
        surfaceHolder.setKeepScreenOn(true);

    }
    private Runnable runnable = new Runnable() {

        public void run() {
            // TODO Auto-generated method stub

            try {
                while (seekBarAutoFlag) {


                    if (null != VideoPlayer.this.mediaPlayer
                            && VideoPlayer.this.mediaPlayer.isPlaying()) {
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    @Override
    public void onClick(View v) {
      switch (v.getId())
      {
          case R.id.download:
              Log.d("tag", String.valueOf(Environment.getExternalStorageDirectory()));
          new Thread(new Runnable() {
              @Override
              public void run() {

                 downLoad(path, "/storage/emulated/0/video");

              }
          }).start();
          break;
          case R.id.button_stop:
              if (mediaPlayer.isPlaying()){
                  mediaPlayer.pause();
                  stopButton.setText("play");
              }
              else{
                  mediaPlayer.start();
                  stopButton.setText("stop");}
              break;
          case  R.id.button_replay:
              if (mediaPlayer!=null){
                  mediaPlayer.seekTo(0);
                  seekBar.setProgress(0);
              }
              else

                  playVideo();

              break;
          case R.id.back:
              mediaPlayer.release();
              mediaPlayer=null;


              if (position>0){
                  position=position-1;
                  path=urls.get(position);
              playVideo();}
              break;
          case R.id.next:
              mediaPlayer.release();
              mediaPlayer=null;
              if (position<urls.size()){

                position=position+1;
                  path=urls.get(position);
                  playVideo();
              }

      }
    }


    private class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @RequiresApi(api = Build.VERSION_CODES.N)
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // TODO Auto-generated method stub
            if (progress >= 0) {

                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }

                vedioTiemTextView.setText(getShowTime(progress) + "/" + videoTimeString);
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }

    }






    public class SurfaceCallback implements SurfaceHolder.Callback{

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            playVideo();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    }
    public void downLoad(String url,String path){
        fileName=url.substring(url.lastIndexOf("/")+1);
        HttpURLConnection httpURLConnection = null;
        try {
            URL mUrl = new URL(url);
            httpURLConnection = (HttpURLConnection) mUrl.openConnection();
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setReadTimeout(10000);


            InputStream inputStream = httpURLConnection.getInputStream();
            this.fileSize=httpURLConnection.getContentLength();
            if (this.fileSize <= 0) throw new RuntimeException("无法获知文件大小 ");
            if (inputStream==null){throw  new RuntimeException("Stream is null");}
            File filePath = new File(path);
            File file = new File(path+fileName);
            if(!filePath.exists()){
                filePath.mkdirs();
            }
            if(!file.exists()){
                file.createNewFile();
            }
            FileOutputStream fileOutputStream=new FileOutputStream(path+fileName);
            byte buf[]=new byte[1024];
            downLoadFileSize=0;
            Message message=new Message();
            message.what=0;
            handler.sendMessage(message);
            do{

                int numread = inputStream.read(buf);
                if (numread == -1)
                {
                    break;
                }
                fileOutputStream.write(buf, 0, numread);
                downLoadFileSize += numread;

            } while (true);


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

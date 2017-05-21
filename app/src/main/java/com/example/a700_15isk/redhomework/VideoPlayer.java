package com.example.a700_15isk.redhomework;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.icu.text.SimpleDateFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.a700_15isk.redhomework.Tools.ActivityCollector;

import java.io.IOException;
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
     Button returnB;
    Button download;

   private  DownLoadService.DownloadBinder downloadBinder;

private ServiceConnection connection=new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
     downloadBinder=(DownLoadService.DownloadBinder)service;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
};


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
        returnB=(Button)findViewById(R.id.returnB);
        returnB.setOnClickListener(this);
        surfaceHolder.addCallback(new SurfaceCallback());
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        Intent intent=new Intent(this, DownLoadService.class);
        startService(intent);
        bindService(intent,connection,BIND_AUTO_CREATE);





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
              downloadBinder.startDownload(path);

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
              break;
          case R.id.returnB:
              Intent i=new Intent(VideoPlayer.this,MainActivity.class);
              ActivityCollector.removeActivity(this);
              startActivity(i);
              break;
      }
    }


    private class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @RequiresApi(api = Build.VERSION_CODES.N)
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            if (progress >= 0) {

                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }

                vedioTiemTextView.setText(getShowTime(progress) + "/" + videoTimeString);
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {


        }

        public void onStopTrackingTouch(SeekBar seekBar) {


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

}

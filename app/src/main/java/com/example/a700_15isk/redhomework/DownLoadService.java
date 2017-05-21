package com.example.a700_15isk.redhomework;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.a700_15isk.redhomework.Tools.BitmapTool.DownloadListener;
import com.example.a700_15isk.redhomework.Tools.BitmapTool.DownloadTask;

import java.io.File;

public class DownLoadService extends Service {
private DownloadTask downloadTask;

    private  String downloadUrl;
    private DownloadListener downloadListener=new DownloadListener() {
        @Override
        public void onProgress(int progress) {
             getNotificationManager().notify(1,getNotification("Download....",progress));
        }

        @Override
        public void onSuccess() {
          downloadTask=null;
            stopForeground(true);
            getNotificationManager().notify(1,getNotification("Download Success",-1));
            Toast.makeText(DownLoadService.this,"success",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFail() {
            downloadTask=null;
            stopForeground(true);
            getNotificationManager().notify(1,getNotification("Download Failed",-1));
            Toast.makeText(DownLoadService.this,"failed",Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onPaused() {
            downloadTask=null;
            Toast.makeText(DownLoadService.this,"pause",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCanceled() {
            downloadTask=null;
            stopForeground(true);

            Toast.makeText(DownLoadService.this,"canceled",Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }
    private DownloadBinder mBinder=new DownloadBinder();
    class DownloadBinder extends Binder{
        public void startDownload(String url){
            if (downloadTask==null){
                downloadUrl=url;
                downloadTask=new DownloadTask(downloadListener);
                downloadTask.execute(downloadUrl);
                startForeground(1,getNotification("Downloading..",0));
            }
        }
        public void pauseDownload(){
            if (downloadTask!=null){
                downloadTask.pauseDownload();
            }
        }
        public void cancelDownload(){
            if (downloadTask!=null){
                downloadTask.cancelDown();
                String fileName=downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                String directory= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                File file=new File(directory+fileName);
                if (file.exists()){
                    file.delete();
                }
                getNotificationManager().cancel(1);
                stopForeground(true);
                Toast.makeText(DownLoadService.this,"canceled",Toast.LENGTH_SHORT).show();
            }
        }
    }


    private NotificationManager getNotificationManager(){
        return (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }
    private Notification getNotification(String title,int progress){

        Intent intent=new Intent(this, VideoPlayer.class);
      //  PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,0);
        NotificationCompat.Builder builder=new  NotificationCompat.Builder(this);
       // builder.setContentIntent(pendingIntent);
        builder.setContentTitle(title);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));
        if (progress>0){
            builder.setContentText(progress+"%");
            builder.setProgress(100,progress,false);
        }
        return builder.build();

    }
}

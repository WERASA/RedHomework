package com.example.a700_15isk.redhomework.Tools.BitmapTool;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by 700-15isk on 2017/5/21.
 */

public class DownloadTask extends AsyncTask<String,Integer,Integer> {
    public final int TYPE_SUCCESS=0;
    public final int TYPE_FAILED=1;
    public final int TYPE_PAUSED=2;
    public final int TYPE_CANCELED=3;
    DownloadListener downloadListener;
    private boolean isCanceled=false;
    private boolean isPaused=false;
    private  int lastProgress;
    public DownloadTask(DownloadListener downloadListener){
        this.downloadListener=downloadListener;
    }

    @Override
    protected Integer doInBackground(String... params) {
        InputStream inputStream=null;
        RandomAccessFile savedFile=null;
        File file=null;

            long downloadedlenth=0;
            String downloadUrl=params[0];
            String fileName=downloadUrl.substring(downloadUrl.lastIndexOf("/"));
            String directory= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            file=new File(directory+fileName);
            long contentLenth=getContentLenth(downloadUrl);
            if (file.exists()){
                downloadedlenth=file.length();
            }
            HttpURLConnection httpURLConnection = null;
            try {
                URL mUrl = new URL(downloadUrl);
                httpURLConnection = (HttpURLConnection) mUrl.openConnection();
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setReadTimeout(10000);
                inputStream = httpURLConnection.getInputStream();
                savedFile=new RandomAccessFile(file,"rw");
                byte b[]=new byte[1024];
                int total=0;
                int len=0;
                while ((len=inputStream.read(b))!=-1){
                    if (isCanceled){
                        return TYPE_CANCELED;
                    }else if (isPaused){
                        return TYPE_PAUSED;
                    }else {
                        total+=len;
                        savedFile.write(b,0,len);
                        int progress=(int)((total+downloadedlenth)*100/contentLenth);
                        publishProgress(progress);
                    }
                }
                inputStream.close();
                return TYPE_SUCCESS;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {

                    try {if (inputStream!=null){
                        inputStream.close();}
                        if (savedFile!=null){
                            savedFile.close();
                        }
                        if (isCanceled&&file!=null){
                            file.delete();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();

                }
            }
            return TYPE_FAILED;
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        int progress=values[0];
        if (progress>lastProgress){
            downloadListener.onProgress(progress);
            lastProgress=progress;
        }
    }


    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        switch (integer){
            case TYPE_SUCCESS:
                downloadListener.onSuccess();
                break;
            case TYPE_CANCELED:
                downloadListener.onCanceled();
                break;
            case TYPE_PAUSED:
                downloadListener.onPaused();
                break;
            case TYPE_FAILED:
                downloadListener.onFail();
                break;
            default:
                break;
        }
    }
    public void pauseDownload(){
        isPaused=true;
    }
    public void cancelDown(){
        isCanceled=true;
    }

    private long getContentLenth(String downloadUrl){
        long fileSize;
        HttpURLConnection httpURLConnection = null;
        try {
            URL mUrl = new URL(downloadUrl);
            httpURLConnection = (HttpURLConnection) mUrl.openConnection();
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setReadTimeout(10000);


            InputStream inputStream = httpURLConnection.getInputStream();
            fileSize=httpURLConnection.getContentLength();
            return fileSize;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  0;
    }

}

package com.example.a700_15isk.redhomework.Tools;

import android.os.Handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by 700-15isk on 2017/3/7.
 */

public class NetWork {
    public static void get(final String url, final Callback callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = NetWork.getFormUrl(url);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onRespond(response);
                    }
                });


            }
        }).start();
    }

    public static void post(final String url, final Callback callback, final String content) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    final String response = NetWork.postToUrl(url, content);

                    @Override
                    public void run() {
                        callback.onRespond(response);

                    }
                });
            }
        }).start();


    }

    public static String postToUrl(final String url, final String content) {
        String response;
        HttpURLConnection httpURLConnection = null;
        try {
            URL mUrl = new URL(url);
            httpURLConnection = (HttpURLConnection) mUrl.openConnection();
            httpURLConnection.setReadTimeout(5000);
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setDoOutput(true);
            String data = content;
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(data.getBytes());
            outputStream.flush();
            outputStream.close();
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == 200) {
                InputStream inputStream = httpURLConnection.getInputStream();
                response = getStringFromInputStream(inputStream);
                return response;


            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return null;
    }

    public static String getFormUrl(final String url) {
        String response;

        HttpURLConnection httpURLConnection = null;
        try {
            URL mUrl = new URL(url);
            httpURLConnection = (HttpURLConnection) mUrl.openConnection();
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setReadTimeout(10000);
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == 200) {
                InputStream inputStream = httpURLConnection.getInputStream();
                response = getStringFromInputStream(inputStream);
                return response;

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }


        return null;
    }

    public static String getStringFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        inputStream.close();
        String state = outputStream.toString();
        outputStream.close();
        return state;
    }


    public interface Callback {
        void onRespond(String respond);

    }

}

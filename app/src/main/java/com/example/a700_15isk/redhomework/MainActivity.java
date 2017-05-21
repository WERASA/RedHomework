package com.example.a700_15isk.redhomework;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.a700_15isk.redhomework.Classes.Res_body;
import com.example.a700_15isk.redhomework.Tools.NetWork;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    Context context;
    ArrayList<Res_body> res_bodies = new ArrayList<>();
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getVideo();
        context=this;
        verifyPermission(MainActivity.this);
        recyclerView=(RecyclerView)findViewById(R.id.videoRv);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(this,"用户拒绝了权限",Toast.LENGTH_SHORT).show();
        }
    }



    public static void verifyPermission(Activity activity){
        int permission= ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity,PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
        }

    }




    public void getVideo(){
        String url="http://route.showapi.com/255-1?showapi_appid=38549&showapi_sign=0245d802ee3c4a8e85e01df59d442e3f&type=41&title=&page=&";

        NetWork.get(url, new NetWork.Callback() {
            @Override
            public void onRespond(String respond) {
                try {
                    JSONObject returnData=new JSONObject(respond);

                    JSONObject showapi_res_body=new JSONObject(returnData.getString("showapi_res_body"));
                    JSONObject pagebean=new JSONObject(showapi_res_body.getString("pagebean"));
                    JSONArray jsonArray = new JSONArray(pagebean.getString("contentlist"));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                         Res_body res_body=new Res_body();
                       // Log.d("Test", jsonObject.toString());
                        res_body.create_time=jsonObject.getString("create_time");
                        res_body.hate=jsonObject.getInt("hate");
                        res_body.love=jsonObject.getInt("love");
                        res_body.text=jsonObject.getString("text");
                        res_body.videoUrl=jsonObject.getString("video_uri");
                        res_body.userName=jsonObject.getString("name");
                        res_body.user_icon=jsonObject.getString("profile_image");
                        res_bodies.add(res_body);
                        Log.d("Test", res_bodies.get(i).text);
                    }
                    VideoAdapter videoAdapter=new VideoAdapter(res_bodies,context);
                    recyclerView.setAdapter(videoAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public boolean isNetWorkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null) {

                return info.isAvailable();
            }
        }


        return false;
    }


}


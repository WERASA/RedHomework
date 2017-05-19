package com.example.a700_15isk.redhomework;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.a700_15isk.redhomework.Adapters.VideoAdapter;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getVideo();
        context=this;

        recyclerView=(RecyclerView)findViewById(R.id.videoRv);

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


}


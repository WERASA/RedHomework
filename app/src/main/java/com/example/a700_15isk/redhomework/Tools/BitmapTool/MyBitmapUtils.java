package com.example.a700_15isk.redhomework.Tools.BitmapTool;

import android.graphics.Bitmap;
import android.widget.ImageView;


/**
 * Created by 700-15isk on 2017/3/11.
 */

public class MyBitmapUtils {
    private NetCacheUtils mNetCacheUtils;
    private LocalCacheUtils mLocalCacheUtils;
    private MemoryCacheUtils mMemoryCacheUtils;

    public MyBitmapUtils(){
        mMemoryCacheUtils=new MemoryCacheUtils();
        mLocalCacheUtils=new LocalCacheUtils();
        mNetCacheUtils=new NetCacheUtils(mLocalCacheUtils,mMemoryCacheUtils);
    }

    public void disPlay(ImageView ivPic, String url) {
     //   ivPic.setImageResource(R.mipmap.ic_default);
        Bitmap bitmap;

        bitmap=mMemoryCacheUtils.getBitmapFromMemory(url);
        if (bitmap!=null){
            ivPic.setImageBitmap(bitmap);

            return;
        }


        bitmap = mLocalCacheUtils.getBitmapFromLocal(url);
        if(bitmap !=null){
            ivPic.setImageBitmap(bitmap);
            mMemoryCacheUtils.setBitmapToMemory(url,bitmap);
            return;
        }

        mNetCacheUtils.getBitmapFromNet(ivPic,url);
    }
}

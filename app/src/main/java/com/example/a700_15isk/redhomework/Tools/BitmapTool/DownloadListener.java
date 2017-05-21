package com.example.a700_15isk.redhomework.Tools.BitmapTool;

/**
 * Created by 700-15isk on 2017/5/21.
 */

public interface DownloadListener {
    void onProgress(int progress);
    void onSuccess();
    void onFail();
    void onPaused();
    void onCanceled();

}

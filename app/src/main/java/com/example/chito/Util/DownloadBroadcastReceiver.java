package com.example.chito.Util;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.chito.activities.WebActivity;

public class DownloadBroadcastReceiver extends BroadcastReceiver {
    final String TAG = "DownloadBroadcastReceiver";
    @SuppressLint("LongLogTag")
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
            if(WebActivity.booklist_isDonwloaded){
                Log.d(TAG,WebActivity.booklist_isDonwloaded+"");
                WebActivity.loadBrowseUrl();
                WebActivity.booklist_isDonwloaded = false;
            }
            else if(WebInterface.playbook_isDonwloaded){
                Log.d(TAG,WebInterface.playbook_isDonwloaded+"");
                WebInterface.playbook_isDonwloaded_n++;
                if(WebInterface.playbook_isDonwloaded_n == WebInterface.playbook_isDonwloaded_max) {
                    WebInterface.progressDialog.dismiss();
//                WebActivity.loadBrowseUrl();
                    WebInterface.playbook_isDonwloaded = false;
                }
                Log.d(TAG,WebInterface.playbook_isDonwloaded_n+"");
            }
        }
    }
}

package com.example.chito.view;


import android.bluetooth.BluetoothAdapter;

import com.example.chito.Util.WebInterface;

import org.json.JSONArray;


public interface HtmlView {

    void setContentView();
    void showToast(String text);

    void reques_permission();
    void file_downloader(String thumbnail_url,String assets_id);
}

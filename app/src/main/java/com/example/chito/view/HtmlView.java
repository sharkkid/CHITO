package com.example.chito.view;


import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;

import com.example.chito.Util.WebInterface;

import org.json.JSONArray;


public interface HtmlView {

    void setContentView();
    void showToast(String text);

    void reques_permission();
    void file_downloader(String thumbnail_url,String assets_id);
    void dialog_show(String[] notificationClick, AlertDialog.Builder dialog, Context context);
    void dialog_dismiss(AlertDialog dialog);
}

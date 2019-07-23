package com.example.chito.presenter;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.view.Window;

import com.example.chito.model.MainModel;
import com.example.chito.view.HtmlView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;


public class WebPresenter {
    private HtmlView htmlView;
    private MainModel mainModel;

    //權限
    public static final int ACCESS_FINE_LOCATION_CODE = 1;
    public static final int REQUEST_ENABLE_BT_CODE = 2;
    public static final int  OVERLAY_PERMISSION_REQ_CODE = 3;

    public WebPresenter(HtmlView htmlView, MainModel mainModel){
        this.htmlView = htmlView;
        this.mainModel = mainModel;
    }
    public void onCreate(){
        htmlView.setContentView();
    }
    public void reques_permission(){
        htmlView.reques_permission();
    }

    public boolean checkStoredPermission(Activity activity){
        return mainModel.haveStoragePermission(activity);
    }

    public boolean isFileExists(String dirname,String filename){
        return mainModel.isFileExists(dirname,filename);
    }

    public void deleteFile(String dirname,String filename){
        mainModel.deleteFile(dirname,filename);
    }

    public boolean checkNetworkState(Context context, ConnectivityManager manager){
        return mainModel.checkNetworkState(context, manager);
    }

    public void showToast(String text){
        htmlView.showToast(text);
    }

    public void file_downloader(String thumbnail_url,String assets_id){
        htmlView.file_downloader(thumbnail_url,assets_id);
    }

    public String getFileText(String path, String filename) throws IOException{
        return mainModel.getFileText(path, filename);
    }

    //json格式化
    public String toPrettyFormat(String jsonString)
    {
        return mainModel.toPrettyFormat(jsonString);
    }

    //取得Scene ID
    public JSONObject getJSONObjectById(String id, ArrayList<JSONObject> jsonArray){
        return mainModel.getJSONObjectById(id, jsonArray);
    }

    //播放聲音檔
    public MediaPlayer playSound(final Context context, final String fileName, boolean loop) {
        return mainModel.playSound(context, fileName, loop);
    }

    //JSON處理
    public Map<String,String> InitialParser(JSONObject jsonObject) {
        return mainModel.InitialParser(jsonObject);
    }
}

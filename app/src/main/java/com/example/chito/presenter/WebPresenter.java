package com.example.chito.presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.view.Window;

import com.example.chito.model.MainModel;
import com.example.chito.view.HtmlView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class WebPresenter {
    public String FindSceneById;
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
    public void playSound(Context context,String book_id,String fileName,AudioManager audioManager,int timer_max) {
        mainModel.playSound(context,book_id,fileName,audioManager,timer_max);
    }

    //播放聲音檔
    public MediaPlayer playSound(final Context context, final String book_id, final String fileName, boolean loop, final AudioManager audioManager, final int fadeIn_sec, int fadeOut_sec, String[] audio_finish_flag) {
        return mainModel.playSound(context,book_id, fileName, loop,audioManager,fadeIn_sec,fadeOut_sec,audio_finish_flag);
    }

    //JSON處理
    public Map<String,String> JsonParser(JSONObject jsonObject) {
        return mainModel.JsonParser(jsonObject);
    }

    //利用劇情編號搜尋劇情腳本
    public Map<String,String> FindSceneById(List<Map<String,String>> map, String SceneId){
        return mainModel.FindSceneById(map,SceneId);
    }

    //判斷key value是否空值
    public String IsMapNull(Map<String,String> story_map,String key){
        return mainModel.IsMapNull(story_map,key);
    }

    //確認GPS狀態
    public boolean checkGpsStatus(Context context){
        return mainModel.checkGpsStatus(context);
    }

    //啟動GPS判斷
    public void startGPS(final Context context, float distance, String book_id, int next_sceneId,Map<String,String> gps_map) {
        mainModel.startGPS(context,distance, book_id, next_sceneId,gps_map);
    }

    //啟動藍芽判斷
    public void startBLE(Context context , final String book_id, final String[] ble_data) {
        mainModel.startBLE(context,book_id,ble_data);
    }

    public void dialog_show(String[] notificationClick, AlertDialog.Builder dialog, Context context){
        htmlView.dialog_show(notificationClick,dialog,context);
    }
    public void dialog_dismiss(AlertDialog dialog){
        htmlView.dialog_dismiss(dialog);
    }

    public void wakeUpAndUnlock(Context context){
        mainModel.wakeUpAndUnlock(context);
    }
}

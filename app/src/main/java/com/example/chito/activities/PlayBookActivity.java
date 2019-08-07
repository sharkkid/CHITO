package com.example.chito.activities;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.chito.R;
import com.example.chito.Util.CurrentLocation;
import com.example.chito.Util.GPSListner;
import com.example.chito.Util.PlayBookPojo;
import com.example.chito.Util.WebInterface;
import com.example.chito.model.MainModel;
import com.example.chito.presenter.WebPresenter;
import com.example.chito.view.HtmlView;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION;


public class PlayBookActivity extends AppCompatActivity implements HtmlView {
    public static WebPresenter webPresenter;
    public static WebView webView;

    public static String book_id = "";
    public static int current_sceneId = 0;
    public static int next_sceneId = 0;

    public JSONObject json;
    public JSONArray scenes;

    public String result = "";
    public static List<Map<String, String>> scenes_list;

    //判斷Triggers type
    public boolean IsQR = false;
    public boolean IsGPS = false;
    public boolean IsBLE = false;
    public boolean IsWIFI = false;

    public static int timer = 0;

    //音樂播放
    public static MediaPlayer mp;

    //進度條
    private static ProgressDialog progressDialog;
    public static boolean booklist_isDonwloaded = false;

    //GPS
    CurrentLocation currentLocation;

    //訊息顯示
    public static AlertDialog.Builder diglog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @SuppressLint("MissingPermission")
    public void init() {
        //主要調配器宣告
        webPresenter = new WebPresenter(this , new MainModel());
        webPresenter.onCreate();

        webView = findViewById(R.id.PlayBookWebView);

        WebSettings webSettings = webView.getSettings();
        // 设置与Js交互的权限
        webSettings.setJavaScriptEnabled(true);
        // 通过addJavascriptInterface()将Java对象映射到JS对象
        //参数1：Javascript对象名
        //参数2：Java对象名
        webView.addJavascriptInterface(new WebInterface(PlayBookActivity.this , webPresenter) , "test");//AndroidtoJS类对象映射到js的test对象


        Intent intent = this.getIntent();
        //取得傳遞過來的資料
        book_id = intent.getStringExtra("book_id");
        book_id = "1";
        //解析劇本劇情
        scenes_list = new ArrayList<Map<String, String>>();
        try {
            result = webPresenter.getFileText(Environment.getExternalStorageDirectory().getPath() + "/story_assets/s1" , "1.json");
            json = new JSONObject(result);
            scenes = json.getJSONArray("scenes");
            for (int i = 0; i < scenes.length(); i++) {
                Map<String, String> mapping_story = webPresenter.JsonParser(scenes.getJSONObject(i));
                scenes_list.add(mapping_story);
                Log.d("mapping_story " + i , String.valueOf(mapping_story));
            }
//            Log.d("mapping_story", String.valueOf(scenes_list));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } catch (IllegalStateException | JsonSyntaxException exception) {
            exception.printStackTrace();
        }
        //取得GPS
        if (!webPresenter.checkGpsStatus(PlayBookActivity.this)) {
            currentLocation = new CurrentLocation(this);
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1,
                    1, new CurrentLocation(this));
        }

        startPlayBook(this,scenes_list.get(0),scenes_list);
    }

    public static void loadHtmlUrl(final String book_id , final String html_id , final String next_sceneId , final String flag) {
        webView.loadUrl("file://"+Environment.getExternalStorageDirectory()+"/story_assets/s"+book_id+"/"+html_id+".html");
        webView.setWebViewClient(new WebViewClient()
            {
            @Override
            public void onPageFinished(WebView view, String url)
            {
                super.onPageFinished(view, url);
                switch (flag) {
                    case "0":
                    webView.loadUrl("javascript:function loadPage(href)\n" +
                            "            {\n" +
                            "                var xmlhttp = new XMLHttpRequest();\n" +
                            "                xmlhttp.open(\"GET\", href, false);\n" +
                            "                xmlhttp.send();\n" +
                            "                return xmlhttp.responseText;\n" +
                            "            }" +
                            "var oDiv = document.getElementById(\"go-next\");\n" +
                            "oDiv.addEventListener(\"click\", function(){\n" +
                            "    test.loadHtmlUrl(\"" + book_id + "\",\"" + next_sceneId + "\");" +
                            "});");
                    break;
            }
//                webView.loadUrl("javascript:callJS(\"測試測試\")");
            }

                @Override
                public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
//                    Log.d("shouldInterceptRequest", "shouldInterceptRequest : " + url);
                    if (url.toLowerCase().endsWith("")) {
                        try {
                            String picName = url.substring(url.lastIndexOf("/"));
                            File file = new File(Environment.getExternalStorageDirectory()+"/story_assets/s"+book_id+"/"+picName+".jpeg");
                            FileInputStream fis = new FileInputStream(file);
                            WebResourceResponse response = new WebResourceResponse(
                                    MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url))
                                    , "UTF-8", fis);
                            return response;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return super.shouldInterceptRequest(view, url);
                }
        });
    }

    public static void loadHtmlUrl(final String book_id , final String html_id) {
        webView.loadUrl("file://"+Environment.getExternalStorageDirectory()+"/story_assets/s"+book_id+"/"+html_id+".html");

    }

    public static void FakeCall(Context context, String ring_id, String call_id,String name,String next_sceneId) {
//        Log.d("audio_finish_flag[1]",ring_id+"");
        Intent fakecall = new Intent(context, FakeCallActivity.class);
        fakecall.putExtra("book_id",book_id);
        fakecall.putExtra("ring_id",ring_id);
        fakecall.putExtra("call_id",call_id);
        fakecall.putExtra("name",name);
        fakecall.putExtra("next_sceneId",next_sceneId);
        context.startActivity(fakecall);
    }

    //劇本流程通用執行邏輯
    public void startPlayBook(Context context,Map<String, String> story_map, List<Map<String, String>> all_map){
        try {
            int trigger_total = Integer.parseInt(story_map.get("triggers_total"));
            current_sceneId = Integer.parseInt(story_map.get("sceneId"));
            String display_type = webPresenter.IsMapNull(story_map, "display_type");
            String audio_method = webPresenter.IsMapNull(story_map, "audio_method");
            String current_html = "";

            //initial
            if (!display_type.equals("")) {
                switch (display_type) {
                    case "webview":
                        current_html = webPresenter.IsMapNull(story_map, "display_assetsId");
                        Log.d("current_htmlidddd", current_html + "");
                        loadHtmlUrl(book_id, current_html);
                        for (int i = 0; i < trigger_total; i++) {
                            switch (story_map.get("trigger_type" + i)) {
                                case "webviewClick":
                                    next_sceneId = Integer.parseInt(story_map.get("trigger_action_sceneId" + i));
                                    loadHtmlUrl(book_id, current_html, next_sceneId + "", "0");
                                    break;
                            }
                        }
                        break;
                    case "ar":

                        break;
                }
            }
            //triggers
            if (!audio_method.equals("")) {
                for (int i = 0; i < trigger_total; i++) {
                    String[] audio_finish_flag = {"flag","ring_asset_id","call_assetid","name","0"};//flag , ring_asset_id, call_assetid;
                    String audio_assetId = story_map.get("audio_assetId" + i);
                    if(webPresenter.IsMapNull(story_map, "trigger_type"+i).toString().equals("audioFinish")){
                        audio_finish_flag[0] = "1";
                        audio_finish_flag[1] = webPresenter.IsMapNull(story_map, "trigger_action_ring_assetId"+i);
                        audio_finish_flag[2] = webPresenter.IsMapNull(story_map, "trigger_action_call_assetId"+i);
                        audio_finish_flag[3] = webPresenter.IsMapNull(story_map, "trigger_action_callerName"+i);
                        audio_finish_flag[4] = webPresenter.IsMapNull(story_map, "trigger_action_sceneId"+i);
                    }
                    AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
                    mp = webPresenter.playSound(context,"1",audio_assetId,true,audioManager,5,0,audio_finish_flag);
                    switch (story_map.get("trigger_type" + i)) {
                        case "gps":
                            next_sceneId = Integer.parseInt(story_map.get("trigger_action_sceneId" + i));
                            Log.d("next_sceneId",next_sceneId+"");
                            webPresenter.startGPS(this,30,book_id,next_sceneId);
//                            WebInterface.loadHtmlUrl(book_id, next_sceneId+"");
                            break;
                        case "notificationClick":
                            Log.d("notificationClick","進入"+story_map.get("trigger_type" + i));
                            String[] notificationClick = {"message","next_sceneId"};//內容   下個場景id
                            audio_finish_flag[0] = webPresenter.IsMapNull(story_map, "notification_title");
                            audio_finish_flag[1] = webPresenter.IsMapNull(story_map, "trigger_action_sceneId"+i);
                            webPresenter.dialog_show(notificationClick,diglog,context);
                            break;
                    }
                }

            }
        }
        catch (Exception e){
            Log.d("Exception",e.toString());
        }
    }

    @Override
    public void setContentView() {
        //Remove title bar
        setContentView(R.layout.web_playbook);
        try
        {
            //隱藏Action Bar
            this.getSupportActionBar().hide();
            //隱藏Status Bar
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            ActionBar actionBar = getActionBar();
            actionBar.hide();
        }
        catch (NullPointerException e){}
    }

    @Override
    public void showToast(String text) {
        Toast.makeText(this,text,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void reques_permission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //没有 ACCESS_FINE_LOCATION 权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, webPresenter.ACCESS_FINE_LOCATION_CODE);
        }
        if (!Settings.canDrawOverlays(PlayBookActivity.this)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(PlayBookActivity.this);
            dialog.setTitle("權限請求!");
            dialog.setMessage("請允許本程式可懸浮權限!");
            dialog.setPositiveButton("前往", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // TODO Auto-generated method stub
                    Intent enableIntent = new Intent(ACTION_MANAGE_OVERLAY_PERMISSION);
                    startActivityForResult(enableIntent, webPresenter.OVERLAY_PERMISSION_REQ_CODE);
                }
            });
            dialog.show();
        }
    }

    @Override
    public void file_downloader(String thumbnail_url, String assets_id) {
        Uri uri = Uri.parse(thumbnail_url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationInExternalPublicDir("/story_assets/playbook_list", "icon_" + assets_id + ".png");
        DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    @Override
    public void dialog_show(final String[] notificationClick, AlertDialog.Builder dialog, Context context) {
        Log.d("使用dialog_show","使用dialog_show");
        dialog = new AlertDialog.Builder(context);
        dialog.setTitle("提示訊息！");
        dialog.setMessage(notificationClick[0]);
        dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog , int which) {
                WebInterface.loadHtmlUrl(book_id,notificationClick[1]);
            }
        });
        dialog.show();
    }

    @Override
    public void dialog_dismiss(AlertDialog dialog) {
        dialog.dismiss();
    }


}

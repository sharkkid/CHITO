package com.example.chito.activities;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.chito.R;
import com.example.chito.Util.WebInterface;
import com.example.chito.model.MainModel;
import com.example.chito.presenter.MainPresenter;
import com.example.chito.presenter.WebPresenter;
import com.example.chito.view.HtmlView;
import com.example.chito.view.MainView;
import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION;


public class WebActivity extends AppCompatActivity implements HtmlView {
    private static WebPresenter webPresenter;
    private HtmlView htmlView;

    public  static WebView webView;

    private static ProgressDialog progressDialog;
    public static boolean booklist_isDonwloaded = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        //主要調配器宣告
        webPresenter = new WebPresenter(this, new MainModel());
        webPresenter.onCreate();

        webView = findViewById(R.id.MainWebView);
        progressDialog = ProgressDialog.show(WebActivity.this,
                "劇本清單下載中", "請等待...",true);
        //判斷下載後的call back
        booklist_isDonwloaded = true;
        //權限索取
        webPresenter.reques_permission();

        //下載劇本清單
        new PlayBookList_Downloader().execute("http://chito-test.nya.tw:3000/api/v1/playbooks/");
        WebSettings webSettings = webView.getSettings();

        // 设置与Js交互的权限
        webSettings.setJavaScriptEnabled(true);
        // 通过addJavascriptInterface()将Java对象映射到JS对象
        //参数1：Javascript对象名
        //参数2：Java对象名
        webView.addJavascriptInterface(new WebInterface(WebActivity.this,webPresenter), "test");//AndroidtoJS类对象映射到js的test对象
        // 加载JS代码
        // 格式规定为:file:///android_asset/文件名.html
//        webView.loadUrl("file:///android_asset/www/browse.html");
//        webView.setWebViewClient(new WebViewClient()
//        {
//            @Override
//            public void onPageFinished(WebView view, String url)
//            {
//                super.onPageFinished(view, url);
//                webView.loadUrl("javascript:callJS(\"測試測試\")");
//            }
//        });
    }

    public static void loadBrowseUrl(){
        webView.loadUrl("file:///android_asset/www/browse.html");
        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onPageFinished(WebView view, String url)
            {
                super.onPageFinished(view, url);
                webView.loadUrl("javascript:callJS(\"測試測試\")");
            }
        });
        progressDialog.dismiss();
    }

    @Override
    public void setContentView() {
        //Remove title bar
        setContentView(R.layout.web_main);
        try
        {
            this.getSupportActionBar().hide();
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
        if (!Settings.canDrawOverlays(WebActivity.this)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(WebActivity.this);
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

    class PlayBookList_Downloader extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            File direct = new File(Environment.getExternalStorageDirectory()
                    + "/story_assets");
            if (!direct.exists()) {
                direct.mkdirs();
            }
            File direct_id = new File(Environment.getExternalStorageDirectory()
                    + "/story_assets/playbook_list");
            if (!direct_id.exists()) {
                direct_id.mkdirs();
            }

            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {
                final JSONObject json = new JSONObject(result);
                String str = new Gson().toJson(json);
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getPath()+"/story_assets/playbook_list/playbook.json"));
                    fos.write(str.getBytes());
                }
                catch (Exception e){

                }
                finally{
                    if(fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                final JSONArray payloads = json.getJSONArray("payloads");
                for(int i = 0;i < json.getJSONArray("payloads").length() ; i++) {
                    final String assets_id = payloads.getJSONObject(i).getString("id");
                    final String thumbnail_url = payloads.getJSONObject(i).getString("thumbnail_url");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //確認是否擁有權限下載
                            if (webPresenter.checkStoredPermission(WebActivity.this)) {
                                //確認是否以前下載過,有的話先移除在下載
                                if(webPresenter.isFileExists("/story_assets/playbook_list","icon_" + assets_id + ".png")){
                                    //移除以前下載的檔案
                                    if(webPresenter.deleteFile("/story_assets/playbook_list","icon_" + assets_id + ".png")){
                                        Uri uri = Uri.parse(thumbnail_url);
                                        DownloadManager.Request request = new DownloadManager.Request(uri);
                                        request.setDestinationInExternalPublicDir("/story_assets/playbook_list", "icon_" + assets_id + ".png");
                                        DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                        manager.enqueue(request);
                                    }
                                }
                                else{
                                    Uri uri = Uri.parse(thumbnail_url);
                                    DownloadManager.Request request = new DownloadManager.Request(uri);
                                    request.setDestinationInExternalPublicDir("/story_assets/playbook_list", "icon_" + assets_id + ".png");
                                    DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                    manager.enqueue(request);
                                }

                            }
                        }
                    }).start();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private  String GET(String url){
            InputStream inputStream = null;
            String result = "";
            try {
                // create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // make GET request to the given URL
                HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
                // receive response as inputStream
                inputStream = httpResponse.getEntity().getContent();
                // convert inputstream to string
                if(inputStream != null)
                    result = convertInputStreamToString(inputStream);
                else
                    result = "Did not work!";
            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
            }
            return result;
        }

        private  String convertInputStreamToString(InputStream inputStream) throws IOException {
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            while((line = bufferedReader.readLine()) != null)
                result += line;
            inputStream.close();
            return result;
        }
    }
    public class PlayBook_Downloader extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            File direct = new File(Environment.getExternalStorageDirectory()
                    + "/story_assets");
            if (!direct.exists()) {
                direct.mkdirs();
            }
            File direct_id = new File(Environment.getExternalStorageDirectory()
                    + "/story_assets/s"+urls[1]);
            if (!direct_id.exists()) {
                direct_id.mkdirs();
            }

            return GET(urls[0],urls[1]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {
                final JSONObject json = new JSONObject(result);
                final String story_id = json.getString("backgroundSceneId");
                String str = new Gson().toJson(json);
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getPath()+"/story_assets/s"+story_id+"/"+story_id+".json"));
                    fos.write(str.getBytes());
                }
                catch (Exception e){

                }
                finally{
                    if(fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                final JSONArray articles = json.getJSONArray("assets");
                for(int i = 0;i < json.getJSONArray("assets").length() ; i++) {
                    final String assets_id = articles.getJSONObject(i).getString("id");
                    final int finalI = i;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (webPresenter.checkStoredPermission(WebActivity.this)) {
                                Uri uri = Uri.parse("http://chito-test.nya.tw:3000/api/v1/assets/"+assets_id);
                                DownloadManager.Request request = new DownloadManager.Request(uri);
                                try {
                                    request.setDestinationInExternalPublicDir("/story_assets/s"+story_id, fileNameConverter(articles.getJSONObject(finalI).getString("contentType"),assets_id));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                manager.enqueue(request);
                            }
                        }
                    }).start();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private  String GET(String url,String id){
            InputStream inputStream = null;
            String result = "";
            try {
                // create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // make GET request to the given URL
                HttpResponse httpResponse = httpclient.execute(new HttpGet(url+id));
                // receive response as inputStream
                inputStream = httpResponse.getEntity().getContent();
                // convert inputstream to string
                if(inputStream != null)
                    result = convertInputStreamToString(inputStream);
                else
                    result = "Did not work!";
            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
            }
            return result;
        }

        private  String convertInputStreamToString(InputStream inputStream) throws IOException {
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            while((line = bufferedReader.readLine()) != null)
                result += line;

            inputStream.close();
            return result;
        }

        public String fileNameConverter(String contentType,String file_id){
            String file_name = "";
            switch (contentType.split("/")[0]){
                case "audio":
                    file_name = file_id+".mp3";
                    break;
                case "image":
                    file_name = file_id+".jpeg";
                    break;
                case "text":
                    file_name = file_id+".html";
                    break;
            }
            return file_name;
        }
    }
}

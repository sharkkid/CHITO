package com.example.chito.activities;


import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.chito.R;
import com.example.chito.Util.WebInterface;
import com.example.chito.model.MainModel;
import com.example.chito.presenter.WebPresenter;
import com.example.chito.view.HtmlView;
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

        //確認網路權限
        ConnectivityManager connectivityManager =(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(webPresenter.checkNetworkState(WebActivity.this,connectivityManager)) {

            //權限索取
            webPresenter.reques_permission();

            progressDialog = ProgressDialog.show(WebActivity.this,
                    "劇本清單下載中", "請等待...", true);
            //判斷下載後的call back
            booklist_isDonwloaded = true;


            //下載劇本清單
            new PlayBookList_Downloader().execute("http://chito-test.nya.tw:3000/api/v1/playbooks/");
            WebSettings webSettings = webView.getSettings();

            // 设置与Js交互的权限
            webSettings.setJavaScriptEnabled(true);
            // 通过addJavascriptInterface()将Java对象映射到JS对象
            //参数1：Javascript对象名
            //参数2：Java对象名
            webView.addJavascriptInterface(new WebInterface(WebActivity.this, webPresenter), "test");//AndroidtoJS类对象映射到js的test对象
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
        else{
//            webPresenter.showToast("請開啟網路連線,再重新啟動App！");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.ic_launcher_background);
            builder.setTitle("網路開啟提示資訊");
            builder.setMessage("您的手機目前處於無網路狀態，\n如果繼續，請先設定網路！");
            builder.setPositiveButton("設定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = null;
            /**
             * 判斷手機系統的版本！如果API大於10 就是3.0
             * 因為3.0以上的版本的設定和3.0以下的設定不一樣，呼叫的方法不同
             */
                    if (android.os.Build.VERSION.SDK_INT > 10) {
                        intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
                    } else {
                        intent = new Intent();
                        ComponentName component = new ComponentName(
                                "com.android.settings",
                                "com.android.settings.WirelessSettings");
                        intent.setComponent(component);
                        intent.setAction("android.intent.action.VIEW");
                    }
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.create();
            builder.show();
        }
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

    @Override
    public void file_downloader(String thumbnail_url, String assets_id) {
        Uri uri = Uri.parse(thumbnail_url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationInExternalPublicDir("/story_assets/playbook_list", "icon_" + assets_id + ".png");
        DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        manager.enqueue(request);
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
                String str = webPresenter.toPrettyFormat(result);
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
                                    webPresenter.deleteFile("/story_assets/playbook_list","icon_" + assets_id + ".png");
                                    webPresenter.file_downloader(thumbnail_url,assets_id);
                                }
                                else{
                                    webPresenter.file_downloader(thumbnail_url,assets_id);
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
}

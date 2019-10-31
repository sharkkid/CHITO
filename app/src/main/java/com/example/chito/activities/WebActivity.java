package com.example.chito.activities;


import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Environment;
import android.os.Handler;
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
import com.example.chito.Util.GlobalValue;
import com.example.chito.Util.WebInterface;
import com.example.chito.model.MainModel;
import com.example.chito.presenter.MainPresenter;
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
import java.util.HashMap;
import java.util.Map;

import static android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION;
import static com.example.chito.Util.GlobalValue.book_id;


public class WebActivity extends AppCompatActivity implements HtmlView {
    private static WebPresenter webPresenter;
    public  static WebView webView;

    public static ProgressDialog progressDialog;
    public static boolean booklist_isDonwloaded = false;

    public static String result="";
    private static String book_content;

    private static int book_total = 0;

    public static JSONObject json;

    public static WebActivity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main();

    }

    @Override
    protected void onResume() {
        super.onResume();
        act = WebActivity.this;
        ConnectivityManager connectivityManager =(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(webPresenter.checkNetworkState(WebActivity.this,connectivityManager)) {
            webPresenter.checkStoredPermission(WebActivity.this);
            progressDialog = ProgressDialog.show(WebActivity.this ,
                    "劇本清單下載中" , "請等待..." , true);
//            progressDialog.setMessage("Test");
            //判斷下載後的call back
            booklist_isDonwloaded = true;
            //下載劇本清單
            new PlayBookList_Downloader().execute("http://" + GlobalValue.url + "/api/v1/playbooks/");
            main();
        }
    }

    public static void reload_story_json(){
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new WebInterface(act, webPresenter), "Chito");//AndroidtoJS类对象映射到js的test对象
        try {
            result = webPresenter.getFileText(Environment.getExternalStorageDirectory().getPath() + "/story_assets/playbook_list" , "playbook.json");
            json = new JSONObject(result);
            book_total = json.getJSONArray("payloads").length();
            String readytosend = "";
            for(int i = 0;i<json.getJSONArray("payloads").length();i++){
                String id = json.getJSONArray("payloads").getJSONObject(i).getString("id");
                String title = json.getJSONArray("payloads").getJSONObject(i).getString("title");
                String description = json.getJSONArray("payloads").getJSONObject(i).getString("description");
                if(description == null){
                    Log.d("description","description=null");
                }
                String categories = json.getJSONArray("payloads").getJSONObject(i).getString("categories");
                int IsDownloaded = 0;
                if(webPresenter.isFileExists("/story_assets/s"+id,id+".json"))
                    IsDownloaded = 1;
                else
                    IsDownloaded = 0;
                readytosend += id+","+IsDownloaded+","+title+","+description+","+"0"+"|";
            }
            readytosend = readytosend.substring(0, readytosend.length()-1);
            book_content = readytosend;
            Log.d("list", readytosend);
//                Log.d("list", json.getJSONArray("payloads").getJSONObject(0).getString("id"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void main() {
        //主要調配器宣告
        webPresenter = new WebPresenter(this, new MainModel());
        webPresenter.onCreate();

        webView = findViewById(R.id.MainWebView);

        //更新進度用的Context
        act = WebActivity.this;

        //權限索取
        webPresenter.reques_permission();
        webPresenter.checkStoredPermission(WebActivity.this);
        //確認網路權限
        ConnectivityManager connectivityManager =(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(webPresenter.checkNetworkState(WebActivity.this,connectivityManager)) {

//            progressDialog = ProgressDialog.show(WebActivity.this,
//                    "劇本清單下載中", "請等待...", true);
////            progressDialog.setMessage("Test");
//            //判斷下載後的call back
//            booklist_isDonwloaded = true;
//
//            //下載劇本清單
//            new PlayBookList_Downloader().execute("http://"+ GlobalValue.url +"/api/v1/playbooks/");

//            new PlayBookList_Downloader().execute("http://chito-test.nya.tw:3000/CHITO/playbooks_test.php");

            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webView.addJavascriptInterface(new WebInterface(WebActivity.this, webPresenter), "Chito");//AndroidtoJS类对象映射到js的test对象
            try {
                result = webPresenter.getFileText(Environment.getExternalStorageDirectory().getPath() + "/story_assets/playbook_list" , "playbook.json");
                json = new JSONObject(result);
                book_total = json.getJSONArray("payloads").length();
                String readytosend = "";
                for(int i = 0;i<json.getJSONArray("payloads").length();i++){
                    String id = json.getJSONArray("payloads").getJSONObject(i).getString("id");
                    String title = json.getJSONArray("payloads").getJSONObject(i).getString("title");
                    String description = json.getJSONArray("payloads").getJSONObject(i).getString("description");
                    if(description == null){
                        Log.d("description","description=null");
                    }
                    String categories = json.getJSONArray("payloads").getJSONObject(i).getString("categories");
                    int IsDownloaded = 0;
                    if(webPresenter.isFileExists("/story_assets/s"+id,id+".json"))
                        IsDownloaded = 1;
                    else
                        IsDownloaded = 0;
                    readytosend += id+","+IsDownloaded+","+title+","+description+","+"0"+"|";
                }
                readytosend = readytosend.substring(0, readytosend.length()-1);
                book_content = readytosend;
                Log.d("list", readytosend);
//                Log.d("list", json.getJSONArray("payloads").getJSONObject(0).getString("id"));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

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
                webView.loadUrl("javascript:send_playbook_list_total("+book_total+",\""+book_content+"\")");//調用Webview上的js處理劇本清單
//                webView.loadUrl("javascript:callJS(\"測試測試\")");
            }
        });
        progressDialog.dismiss();
    }

    public static void updateUI_download(){
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                double downloaded_percentage = (WebInterface.playbook_isDonwloaded_n*100) / WebInterface.playbook_isDonwloaded_max;
                Log.d("進度",downloaded_percentage+",最大值="+WebInterface.playbook_isDonwloaded_max);
                WebInterface.progressDialog.setMessage("已下載:"+downloaded_percentage+"%");
            }
        });
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
        webPresenter.checkStoredPermission(WebActivity.this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //没有 ACCESS_FINE_LOCATION 权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, webPresenter.ACCESS_FINE_LOCATION_CODE);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!Settings.canDrawOverlays(WebActivity.this)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(WebActivity.this);
                    dialog.setTitle("權限請求!");
                    dialog.setMessage("請允許本程式可懸浮權限!");
                    dialog.setPositiveButton("前往",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // TODO Auto-generated method stub
                            Intent enableIntent = new Intent( ACTION_MANAGE_OVERLAY_PERMISSION );
                            startActivityForResult( enableIntent, webPresenter.OVERLAY_PERMISSION_REQ_CODE );
                        }
                    });
                    dialog.show();
                }
            };
        },1000);

        if(!webPresenter.checkGpsStatus(this)){
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("GPS定位權限請求!");
            dialog.setMessage("請允許本程式GPS定位權限!");
            dialog.setPositiveButton("前往",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // TODO Auto-generated method stub
                    Intent enableIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS );
                    startActivityForResult( enableIntent, MainPresenter.ACCESS_COARSE_LOCATION );
                }
            });
            dialog.setNegativeButton("拒絕",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // TODO Auto-generated method stub
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
    public void dialog_show(String[] notificationClick , AlertDialog.Builder dialog , Context context) {

    }

    @Override
    public void dialog_dismiss(AlertDialog dialog) {

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
                    Log.d("thumbnail","url="+payloads.getJSONObject(i).getString("thumbnailUrl"));
                    if (payloads.getJSONObject(i).has("thumbnailUrl") && !payloads.getJSONObject(i).isNull("thumbnailUrl")) {
                        final String thumbnail_url = payloads.getJSONObject(i).getString("thumbnailUrl");
                        Log.d("thumbnail","thumbnail_url="+thumbnail_url);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //確認是否擁有權限下載
                                if (webPresenter.checkStoredPermission(WebActivity.this)) {
                                    //確認是否以前下載過,有的話先移除在下載
                                    if (webPresenter.isFileExists("/story_assets/playbook_list" , "icon_" + assets_id + ".png")) {
                                        //移除以前下載的檔案
                                        webPresenter.deleteFile("/story_assets/playbook_list" , "icon_" + assets_id + ".png");
                                        webPresenter.file_downloader(thumbnail_url , assets_id);
                                    } else {
                                        webPresenter.file_downloader(thumbnail_url , assets_id);
                                    }

                                }
                            }
                        }).start();
                    }
                    if(i == json.getJSONArray("payloads").length()-1) {
                        Log.d("i","i="+i);
                        loadBrowseUrl();
                    }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MainPresenter.WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 申请同意
                } else {
                    //申请拒绝
//                    Toast.makeText(this, "您已拒絕位置權限，將無法搜尋Beacon!", Toast.LENGTH_SHORT).show();
                }
                break;
            case MainPresenter.ACCESS_FINE_LOCATION_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 申请同意
                } else {
                    //申请拒绝
//                    Toast.makeText(this, "您已拒絕位置權限，將無法搜尋Beacon!", Toast.LENGTH_SHORT).show();
                }
                break;
            case MainPresenter.REQUEST_ENABLE_BT_CODE:
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

                ) {
                    // 申请同意
                } else {
                    //申请拒绝
                    webPresenter.showToast("位置權限已拒絕!");
                }
                break;
            case MainPresenter.ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 申请同意
                } else {
                    //申请拒绝
//                    Toast.makeText(this, "您已拒絕GPS定位權限，將無法搜尋GPS!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}

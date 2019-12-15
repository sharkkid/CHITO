package com.example.chito.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.chito.activities.MainActivity;
import com.example.chito.activities.PlayBookActivity;
import com.example.chito.activities.QrScanner;
import com.example.chito.activities.WebActivity;
import com.example.chito.presenter.WebPresenter;
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
import java.util.Map;

import static android.content.Context.DOWNLOAD_SERVICE;
import static com.example.chito.Util.GlobalValue.IsBleStart;
import static com.example.chito.Util.GlobalValue.flag_map;
import static com.example.chito.Util.GlobalValue.flag_status;
import static com.example.chito.activities.PlayBookActivity.qr_flag;
import static com.example.chito.activities.WebActivity.reload_story_json;

public class WebInterface extends Object{
    public static String TAG = "WebInterface";
    public static Context context;
    public static WebPresenter webPresenter;

    public static boolean playbook_isDonwloaded = false;
    public static int playbook_isDonwloaded_n = 0;
    public static int playbook_isDonwloaded_max = 0;
    public static PlayBookActivity playBookActivity;

    public static ProgressDialog progressDialog;

    public WebInterface(Context context, WebPresenter webPresenter){
        this.context = context;
        this.webPresenter = webPresenter;
    }

    public static Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 1:
                    if(playbook_isDonwloaded_max != 0) {
                        double downloaded_percentage = (playbook_isDonwloaded_n * 100) / playbook_isDonwloaded_max;
//                        progressDialog.setMessage("已下載:" + downloaded_percentage + "%");
                        if(downloaded_percentage == 100){
                            WebActivity.reload_story_json();
                        }
                    }
                    break;
            }
        }
    };

    @JavascriptInterface
    public void ShowToast(String str){
        Toast.makeText(context,str,Toast.LENGTH_SHORT).show();
    }
    @JavascriptInterface
    public void Playbook_downloader(String book_id){
        progressDialog = ProgressDialog.show(context,
                "劇本下載中", "請等待...", true);
        Log.d("playbook_url","url=http://"+GlobalValue.url+"/api/v1/playbooks/"+ book_id);
        new PlayBook_Downloader().execute("http://"+GlobalValue.url+"/api/v1/playbooks/" + book_id,book_id);
    }
    @JavascriptInterface
    public void Playbook_start(String book_id){
        Intent goweb = new Intent(context, PlayBookActivity.class);
        goweb .putExtra("book_id",book_id);//傳遞劇本編號
        ((Activity)context).startActivity(goweb);
    }
    @JavascriptInterface
    public void openQrScanner(String retryMessage){
        if(qr_flag.equals("0")) {
            Intent goqr = new Intent(context , QrScanner.class);
            ((Activity) context).startActivityForResult(goqr , 1);
        }
        else{
            ShowToast(retryMessage);
        }
    }
    @JavascriptInterface
    public void trigger(String trigger_name){
        if(flag_map.containsKey(trigger_name)){
            Log.d("Yes",flag_status.get(trigger_name));
            flag_status.put(trigger_name,"V");
            Log.d("Yes",flag_status.get(trigger_name)+","+GlobalValue.book_id+","+GlobalValue.flag_sceneId);
            final Handler gps_sesor = new Handler();
            gps_sesor.postDelayed(new Runnable() {
                @Override
                public void run() {
                    PlayBookActivity.trigger_start();
                }
            }, 1000); // 1 second delay (takes millis)
        }

    }
    @JavascriptInterface
    public static void loadHtmlUrl(final String book_id, final String next_sceneId){
        PlayBookActivity.webView.post(new Runnable() {
            @Override
            public void run() {
                Log.d("loadHtmlUrl被呼叫","loadHtmlUrl被呼叫,next_sceneId="+next_sceneId);
                playBookActivity = new PlayBookActivity();
                Map<String,String> story_map = webPresenter.FindSceneById(PlayBookActivity.scenes_list,next_sceneId);
                playBookActivity.startPlayBook(context,story_map,PlayBookActivity.scenes_list);
            }
        });
    }

    @JavascriptInterface
    public static void loadFakeCall(String ring_id, String call_id, String name, String next_sceneId, String message, String caller_number){
       playBookActivity.FakeCall(context,ring_id,call_id,name,next_sceneId,message,caller_number);
    }

    public class PlayBook_Downloader extends AsyncTask<String, Void, String> {
        String book_id = "0";
        @Override
        protected String doInBackground(String... urls) {
            playbook_isDonwloaded = true;
            book_id = urls[1];
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
                Log.d("result","result="+result);
                final JSONObject json = new JSONObject(result);
                final String story_id = json.getString("backgroundSceneId");
                String str = webPresenter.toPrettyFormat(result);
                FileOutputStream fos = null;
                try {
                    if (webPresenter.isFileExists("/story_assets/s"+ book_id, book_id+".json")) {
                        //移除以前下載的檔案
                        Log.d("WebInterface","移除="+"/story_assets/s"+ book_id);
                        webPresenter.deleteFile("/story_assets/s" + book_id, book_id+".json");
                        fos = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getPath()+"/story_assets/s"+book_id+"/"+book_id+".json"));
                        fos.write(str.getBytes());
                    }
                    else{
                        fos = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getPath()+"/story_assets/s"+book_id+"/"+book_id+".json"));
                        fos.write(str.getBytes());
                    }
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
                playbook_isDonwloaded_max = json.getJSONArray("assets").length()-1;
                for(int i = 0;i < json.getJSONArray("assets").length() ; i++) {
                    final String assets_id = articles.getJSONObject(i).getString("id");
                    final int finalI = i;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (webPresenter.checkStoredPermission((Activity) context)) {
                                try {
                                    if (webPresenter.isFileExists("/story_assets/s" + story_id, fileNameConverter(articles.getJSONObject(finalI).getString("contentType"), assets_id))) {
                                        //移除以前下載的檔案
                                        webPresenter.deleteFile("/story_assets/s" + story_id, fileNameConverter(articles.getJSONObject(finalI).getString("contentType"), assets_id));
                                        file_downloader(book_id,assets_id,articles,finalI);
                                    }
                                    else{
                                        file_downloader(book_id,assets_id,articles,finalI);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                }
                reload_story_json();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private  String GET(String url,String id){
            InputStream inputStream = null;
            String result = "";
            try {
//                // create HttpClient
//                HttpClient httpclient = new DefaultHttpClient();
//                // make GET request to the given URL
//                HttpResponse httpResponse = httpclient.execute(new HttpGet(url+id));
//                // receive response as inputStream
//                inputStream = httpResponse.getEntity().getContent();
//                // convert inputstream to string
//                if(inputStream != null)
//                    result = convertInputStreamToString(inputStream);
//                else
//                    result = "Did not work!";
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(url);

                HttpResponse response = client.execute(request);


                BufferedReader rd = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent()));

                StringBuffer result2 = new StringBuffer();
                String line ="";
                while ((line = rd.readLine())!= null) {
                    result2.append(line);
                }
                result = result2.toString()
                ;
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

        public void file_downloader(String story_id,String assets_id,JSONArray articles,int finalI){
            Uri uri = Uri.parse("http://"+GlobalValue.url+"/api/v1/assets/" + assets_id);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            try {
                request.setDestinationInExternalPublicDir("/story_assets/s" + story_id, fileNameConverter(articles.getJSONObject(finalI).getString("contentType"), assets_id));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            DownloadManager manager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
            manager.enqueue(request);
        }


    }
}



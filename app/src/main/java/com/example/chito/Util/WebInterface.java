package com.example.chito.Util;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.example.chito.activities.WebActivity;
import com.example.chito.presenter.WebPresenter;
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

import static android.content.Context.DOWNLOAD_SERVICE;

public class WebInterface extends Object{
    public Context context;
    public WebPresenter webPresenter;
    public static boolean playbook_isDonwloaded = false;
    public static ProgressDialog progressDialog;

    public WebInterface(Context context, WebPresenter webPresenter){
        this.context = context;
        this.webPresenter = webPresenter;
    }
    @JavascriptInterface
    public void ShowToast(String str){
        Toast.makeText(context,str,Toast.LENGTH_SHORT).show();
        progressDialog = ProgressDialog.show(context,
                "劇本下載中", "請等待...",true);
    }
    public void Playbook_downloader(String book_id){
        progressDialog = ProgressDialog.show(context,
                "劇本下載中", "請等待...",true);
        new PlayBook_Downloader().execute("http://chito-test.nya.tw:3000/api/v1/playbooks/"+book_id);

    }
    public class PlayBook_Downloader extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            playbook_isDonwloaded = true;
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
                            if (webPresenter.checkStoredPermission((Activity) context)) {
                                Uri uri = Uri.parse("http://chito-test.nya.tw:3000/api/v1/assets/"+assets_id);
                                DownloadManager.Request request = new DownloadManager.Request(uri);
                                try {
                                    request.setDestinationInExternalPublicDir("/story_assets/s"+story_id, fileNameConverter(articles.getJSONObject(finalI).getString("contentType"),assets_id));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                DownloadManager manager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
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



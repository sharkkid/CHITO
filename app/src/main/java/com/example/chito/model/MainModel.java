package com.example.chito.model;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainModel {

    public int  IsBlueToothOpen(BluetoothAdapter mBtAdapter) {// 0:無藍芽裝置 1:有,但未開啟 2:有,已開啟
        //檢查裝置是否支援藍芽
        int flag = 0;
        if (mBtAdapter != null) {
            // 檢查藍牙是否開啟
            if (!mBtAdapter.isEnabled()) {
                flag = 1;
            }
            else{
                flag = 2;
            }
        }
        else{
            flag = 0;
        }
        return flag;
    }
    public String bytesToHex(byte[] bytes) {

        char[] hexArray = "0123456789ABCDEF".toCharArray();

        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public boolean checkNetworkState(Context context,ConnectivityManager manager) {
        boolean flag = false;
        //得到網路連線資訊
        manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //去進行判斷網路是否連線
        if (manager.getActiveNetworkInfo() != null) {
            flag = true;
        }
        else{
            flag = false;
        }

        return flag;
    }

    public  boolean haveStoragePermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.e("Permission error","You have permission");
                return true;
            } else {

                Log.e("Permission error","You have asked for permission");
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //you dont need to worry about these stuff below api level 23
            Log.e("Permission error","You already have the permission");
            return true;
        }
    }

    public boolean isFileExists(String dirname,String filename){
        File folder1 = new File(Environment.getExternalStorageDirectory().getPath()+dirname+"/"+filename);
        Log.d(filename+"=isFileExists",Environment.getExternalStorageDirectory().getPath()+dirname+"/"+filename);
        Log.d(filename+"=isFileExists",folder1.exists()+"");
        return folder1.exists();
    }

    public void deleteFile(String dirname,String filename){
        File folder1 = new File(Environment.getExternalStorageDirectory().getPath()+dirname+"/"+filename);
//        Log.d(filename+"=deleteFile",folder1.delete()+"");
        folder1.delete();
    }

    public String getFileText(String path, String filename) throws IOException {
        String result = "";
        File file = new File(path, filename);
        BufferedReader br = new BufferedReader(new FileReader(file));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            result = sb.toString();

        } finally {
            br.close();
        }
        return result;
    }

    public String toPrettyFormat(String jsonString)
    {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(jsonString).getAsJsonObject();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson.toJson(json);

        return prettyJson;
    }

    public JSONObject getJSONObjectById(String id, ArrayList<JSONObject> jsonArray){
        JSONObject result = null;
        for(int i=0;i<jsonArray.size();i++){
            try {
                if(jsonArray.get(i).getString("id").equals(id)){
                    result = jsonArray.get(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public MediaPlayer playSound(final Context context, final String fileName, boolean loop) {
        MediaPlayer mp = MediaPlayer.create(context, Uri.parse("file:///"+Environment.getExternalStorageDirectory()+"/story_assets/s"+1+"/8.mp3"));
        mp.setLooping(true);
        mp.start();
        return mp;
    }

    public boolean Isempty(JSONObject jsonObject,String sort) throws JSONException {
        boolean flag = false;
        Log.d("initial",new JSONObject(jsonObject.getString("initial")).toString());
        if(new JSONObject(jsonObject.getString("initial")).has(sort)){
            flag = true;
        }else{
            flag = false;
        }
        return flag;
    }

    public String IsStringNull(String input){
        String output = "";
        if(input == null)
            output = "";
        else
            output = input;

        return output;
    }

    /*
    * 以下為劇本處理邏輯
    * */
    public Map<String,String> JsonParser(JSONObject jsonObject) {
        Map<String, String> map = new HashMap<>();

        try {
            Log.d("json_all", String.valueOf(jsonObject));
            Log.d("json_initial", jsonObject.getString("initial"));

//            Log.d("json_trigger", new JSONObject(jsonObject.getString("triggers")).getString("type"));
            /*
            {
            "id": 2,
            "initial": {
              "display": {
                "type": "webview",
                "assetId": 1
              }
            }
            */
            // Display
            if(Isempty(jsonObject,"display")) {
                String type = new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("display")).getString("type");
                Log.d("type",type);
                switch (type) {
                    case "webview":
                        String assetId = new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("display")).getString("assetId");
                        map.put("display_type",type);
                        map.put("display_assetsId",assetId);
                        break;
                    case "ar":

                        break;
                }
            }
            /*
            {
            "id": 5,
            "initial": {
              "notification": {
                "instanceId": 1,
                "title": "你現在去前往立法院",
                "text": null
              }
            }
            */
            // notification
            if(Isempty(jsonObject,"notification")) {
                if (new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("notification")).has("instanceId")) {
                    String instanceId = new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("notification")).getString("instanceId");
                    map.put("notification_instanceId",instanceId);
                }
                if (new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("notification")).has("title")) {
                    String title = new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("notification")).getString("title");
                    map.put("notification_title",title);
                }
                if (new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("notification")).has("text")) {
                    String text = new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("notification")).getString("text");
                    map.put("notification_text",text);
                }

            }

            /*
            initial": {
            "audio": {
              "method": "update",
              "tracks": [
                {
                  "instanceId": 1,
                  "pause": null,
                  "currentTime": null,
                  "fadeInSeconds": null,
                  "fadeOutSeconds": 5,
                  "volume": {
                    "type": "gps-distance-linear",
                    "latitude": 25.044090901990533,
                    "longtitude": 121.51927395877044,
                    "distanceVolumes": [
                      [
                        30,
                        0.3
                      ],
                      [
                        5,
                        0.9
                      ]
                    ]
                  },
                  "playMode": "mix",
                  "repeat": null,
                  "assetId": 3
                }
                */
            // Audio
            if(Isempty(jsonObject,"audio")) {

                JSONArray jsonArray_audio = new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("audio")).getJSONArray("tracks");
                String method = new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("audio")).getString("method");
                map.put("audio_method", method);
                //從這開始
                Log.d("jsonArray_audio", jsonArray_audio.length() + "");
                if (new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("audio")).has("tracks")) {
                    for (int i = 0; i < jsonArray_audio.length(); i++) {
                        Log.d("迴圈=" + i, i + "");
                        String instanceId = "";
                        String pause ="";
                        String currentTime ="";
                        String assetId ="";
                        String fadeOutSeconds ="";
                        String fadeInSeconds ="";
                        String volume_type ="";
                        String latitude ="";
                        String longtitude ="";
                        if (new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("audio")).getJSONArray("tracks").getJSONObject(i).has("instanceId")) {
                            instanceId = new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("audio")).getJSONArray("tracks").getJSONObject(i).getString("instanceId");
                        }
                        if (new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("audio")).getJSONArray("tracks").getJSONObject(i).has("pause")) {
                            pause = new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("audio")).getJSONArray("tracks").getJSONObject(i).getString("pause");
                        }
                        if (new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("audio")).getJSONArray("tracks").getJSONObject(i).has("currentTime")) {
                            currentTime = new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("audio")).getJSONArray("tracks").getJSONObject(i).getString("currentTime");
                        }
                        if (new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("audio")).getJSONArray("tracks").getJSONObject(i).has("assetId")) {
                            assetId = new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("audio")).getJSONArray("tracks").getJSONObject(i).getString("assetId");
                        }

                        if (new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("audio")).getJSONArray("tracks").getJSONObject(i).has("fadeOutSeconds")) {
                            fadeOutSeconds = new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("audio")).getJSONArray("tracks").getJSONObject(i).getString("fadeOutSeconds");
                        }
                        if (new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("audio")).getJSONArray("tracks").getJSONObject(i).has("fadeInSeconds")) {
                            fadeInSeconds = new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("audio")).getJSONArray("tracks").getJSONObject(i).getString("fadeInSeconds");
                        }
                        if (new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("audio")).getJSONArray("tracks").getJSONObject(i).has("volume")) {
                            volume_type = new JSONObject(new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("audio")).getJSONArray("tracks").getJSONObject(i).getString("volume")).getString("type");
                            latitude =  new JSONObject(new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("audio")).getJSONArray("tracks").getJSONObject(i).getString("volume")).getString("latitude");
                            longtitude =  new JSONObject(new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("audio")).getJSONArray("tracks").getJSONObject(i).getString("volume")).getString("longtitude");
                            JSONArray jsonArray_distanceVolumes = new JSONObject(new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("audio")).getJSONArray("tracks").getJSONObject(i).getString("volume")).getJSONArray("distanceVolumes");

                            int distance_length = new JSONObject(new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("audio")).getJSONArray("tracks").getJSONObject(i).getString("volume")).getJSONArray("distanceVolumes").length();
                            for(int x=0;x<distance_length;x++){
                                String unparesed_value = new JSONObject(new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("audio")).getJSONArray("tracks").getJSONObject(i).getString("volume")).getJSONArray("distanceVolumes").get(i)+"";
                                String[] distance_value = unparesed_value.substring(1,unparesed_value.length()-1).split(",");
                                map.put("distanceVolumes_distance"+x+i, distance_value[0]);
                                map.put("distanceVolumes_volume"+x+i, distance_value[1]);
                            }
                        }
                        map.put("audio_instanceId"+i, instanceId);
                        map.put("pause"+i, pause);
                        map.put("audio_assetId"+i, assetId);
                        map.put("currentTime"+i, currentTime);
                        map.put("audio_fadeOutSeconds"+i, fadeOutSeconds);
                        map.put("audio_fadeInSeconds"+i, fadeInSeconds);

                        map.put("audio_volume_type"+i, volume_type);
                        map.put("audio_latitude"+i, latitude);
                        map.put("audio_longtitude"+i, longtitude);
                        Log.d("audio_longtitude",longtitude);

                    }
                }
            }

            //Trigger

            if(new JSONObject(jsonObject.getString("triggers")).has("type")) {
                int n = new JSONObject(jsonObject.getString("triggers")).getJSONArray("type").length();
                for(int i=0;i<n;i++){
                    map.put("trigger_id",new JSONObject(jsonObject.getJSONArray("triggers").get(i)).getString("type")+"");
                }
            }
            if(new JSONObject(jsonObject.getString("triggers")).has("id")) {
                map.put("trigger_id",new JSONObject(jsonObject.getString("triggers")).getString("id")+"");
            }
            if(new JSONObject(jsonObject.getString("triggers")).has("action")) {

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

//    public String[] TriggerParser(JSONObject jsonObject) {
//        String[] result = new String[2];
//        try {
//            String webviewClick = new JSONObject(new JSONObject(jsonObject.getString("triggers")).getString("webviewClick")).getString("webviewClick");
//            switch (type){
//                case "webview":
//                    String assetId = new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("display")).getString("assetId");
//                    result[0] = type;
//                    result[1] = assetId;
//                    break;
//                case "ar":
//
//                    break;
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }
}

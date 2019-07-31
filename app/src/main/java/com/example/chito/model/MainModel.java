package com.example.chito.model;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
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
import java.util.List;
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

    public MediaPlayer playSound(final Context context, final String book_id, final String fileName, boolean loop, final AudioManager audioManager, final int fadeIn_sec, int fadeOut_sec) {
        final MediaPlayer mp = MediaPlayer.create(context, Uri.parse("file:///"+Environment.getExternalStorageDirectory()+"/story_assets/s"+book_id+"/"+fileName+".mp3"));
        Log.d("Audio_path",Uri.parse("file:///"+Environment.getExternalStorageDirectory()+"/story_assets/s"+book_id+"/"+fileName+".mp3")+"");
//        fadeOut(mp,fadeOut_sec,audioManager);
        mp.setLooping(true);
        mp.start();
        //20190731
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                int timer = 0;
                float volume = 1f;
                float speed = 0.05f;
                volume = FadeIn(mp,fadeIn_sec,volume,speed);
                Log.d("FadeInvolume",volume+"");
                if (timer < 5) {
                    h.postDelayed(this , 1000);
                    timer++;
                }
            }
        }, 100); // 1 second delay (takes millis)

        return mp;
    }
    public float FadeOut(MediaPlayer mp,float deltaTime, float volume, float speed)
    {
        mp.setVolume(volume, volume);
        volume -= speed* deltaTime;
        return volume;
    }
    public float FadeIn(MediaPlayer mp,float deltaTime, float volume, float speed)
    {
        mp.setVolume(volume, volume);
        volume += speed* deltaTime;
        Log.d("FadeIn",volume+"");
        return volume;
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

    public Map<String,String> FindSceneById(List<Map<String,String>> map,String SceneId){
        Map<String,String> temp = null;
        for(int i=0;i<map.size();i++){
            String id = map.get(i).get("sceneId");
            if(id.equals(SceneId))
                temp = map.get(i);
        }

        return temp;
    }

    public String IsMapNull(Map<String,String> story_map,String key){
        String tmp = "";
        if(!story_map.get(key).isEmpty()){
            tmp = story_map.get(key);
        }
        return tmp;
    }

    /*
    * 以下為劇本JSON處理
    *
    * */
    public Map<String,String> JsonParser(JSONObject jsonObject) {
        Map<String, String> map = new HashMap<>();

        try {
            Log.d("json_all", String.valueOf(jsonObject));
            Log.d("json_initial", jsonObject.getString("initial"));
            map.put("sceneId",jsonObject.getString("id"));
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
                map.put("audio_tracks_total", jsonArray_audio.length()+"");
                //從這開始
                Log.d("jsonArray_audio", jsonArray_audio.length() + "");
                if (new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("audio")).has("tracks")) {
                    for (int i = 0; i < jsonArray_audio.length(); i++) {
//                        Log.d("迴圈=" + i, i + "");
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
//            Log.d("json_triggers", new JSONArray (jsonObject.getString("triggers")).getJSONObject(0).getString("type"));
            int n = new JSONArray (jsonObject.getString("triggers")).length();
            map.put("triggers_total", n+"");
            Log.d("Triiger長度=",n+"");
            for(int i=0;i<n;i++) {
                if (new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).has("type")) {
                    map.put("trigger_type"+i, new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("type"));
                }
                if (new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).has("id")) {
                    map.put("trigger_id"+i, new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("id"));
                }
                if (new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).has("latitude")) {
                    map.put("trigger_latitude"+i, new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("latitude"));
                }
                if (new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).has("longitude")) {
                    map.put("trigger_longitude"+i, new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("longitude"));
                }
                if (new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).has("distance")) {
                    map.put("trigger_distance"+i, new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("distance"));
                }
                if (new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).has("operator")) {
                    map.put("trigger_operator"+i, new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("operator"));
                }
                if (new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).has("uuid")) {
                    map.put("trigger_beacon_uuid"+i, new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("uuid"));
                }
                if (new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).has("major")) {
                    map.put("trigger_beacon_major"+i, new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("major"));
                }
                if (new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).has("minor")) {
                    map.put("trigger_beacon_minor"+i, new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("minor"));
                }
                if (new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).has("seconds")) {
                    map.put("trigger_timer_seconds"+i, new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("seconds"));
                }
                if (new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).has("condition")) {
                    map.put("trigger_flag_condition"+i, new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("condition"));
                }
                if (new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).has("audioId")) {
                    map.put("trigger_audioId"+i, new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("audioId"));
                }
                if (new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).has("match")) {
                    map.put("trigger_,match"+i, new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("match"));
                }
                //20190730
                if (new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).has("actions")) {
                    if(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).has("gotoScene")) {
                        map.put("trigger_action_sceneId" + i, new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("gotoScene")).getString("sceneId"));
                        map.put("trigger_action_reuse" + i, new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("gotoScene")).getString("reuse"));
                        map.put("trigger_action_target" + i, new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("gotoScene")).getString("target"));
                    }
                    if (new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).has("flag")) {
                        map.put("trigger_flag_names" + i, new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("flag")).getString("names"));
                    }
                    if(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).has("audio")) {
                        map.put("trigger_audio_method" + i, new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("audio")).getString("method"));

                    }
                    if(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).has("changePlaybookThumbnail")) {
                        map.put("trigger_audio_disablePlaybook_assetId" + i,new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("changePlaybookThumbnail")).getString("assetId"));
                    }
                    if(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).has("disablePlaybook")) {
                        map.put("trigger_audio_disablePlaybook_retryMessage" + i,new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("disablePlaybook")).getString("message"));
                        map.put("trigger_audio_disablePlaybook_message" + i,new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("disablePlaybook")).getString("retryMessage"));
                    }
                    if(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).has("finishPlaybook")) {
                        map.put("trigger_finishPlaybook" + i, new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("finishPlaybook"));
                    }
                    if (new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).has("fakeCall")) {
                        map.put("trigger_action_callerName" + i, new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("callerName"));
                        map.put("trigger_action_callerNumber" + i, new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("callerNumber"));
                        map.put("trigger_action_instanceId" + i, new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("instanceId"));
                        if (new JSONObject(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("ring")).has("instanceId")) {
                            map.put("trigger_action_ring_instanceId" + i, new JSONObject(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("ring")).getString("instanceId"));
                        }
                        if (new JSONObject(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("ring")).has("pause")) {
                            map.put("trigger_action_ring_pause" + i, new JSONObject(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("ring")).getString("pause"));
                        }
                        if (new JSONObject(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("ring")).has("currentTime")) {
                            map.put("trigger_action_ring_currentTime" + i, new JSONObject(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("ring")).getString("currentTime"));
                        }
                        if (new JSONObject(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("ring")).has("fadeInSeconds")) {
                            map.put("trigger_action_ring_fadeInSeconds" + i, new JSONObject(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("ring")).getString("fadeInSeconds"));
                        }
                        if (new JSONObject(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("ring")).has("fadeOutSeconds")) {
                            map.put("trigger_action_ring_fadeOutSeconds" + i, new JSONObject(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("ring")).getString("fadeOutSeconds"));
                        }
                        if (new JSONObject(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("ring")).has("volume")) {
                            map.put("trigger_action_ring_volume" + i, new JSONObject(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("ring")).getString("volume"));
                        }
                        if (new JSONObject(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("ring")).has("playMode")) {
                            map.put("trigger_action_ring_playMode" + i, new JSONObject(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("ring")).getString("playMode"));
                        }
                        if (new JSONObject(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("ring")).has("repeat")) {
                            map.put("trigger_action_ring_repeat" + i, new JSONObject(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("ring")).getString("repeat"));
                        }
                        if (new JSONObject(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("ring")).has("assetId")) {
                            map.put("trigger_action_ring_assetId" + i, new JSONObject(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("ring")).getString("assetId"));
                        }
                        if (new JSONObject(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("call")).has("assetId")) {
                            map.put("trigger_action_call_assetId" + i, new JSONObject(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("call")).getString("assetId"));
                        }
                        if (new JSONObject(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("call")).has("instanceId")) {
                            map.put("trigger_action_call_instanceId" + i, new JSONObject(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("call")).getString("instanceId"));
                        }
                        if (new JSONObject(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("call")).has("pause")) {
                            map.put("trigger_action_call_pause" + i, new JSONObject(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("call")).getString("pause"));
                        }
                        if (new JSONObject(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("call")).has("currentTime")) {
                            map.put("trigger_action_call_currentTime" + i, new JSONObject(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("call")).getString("currentTime"));
                        }
                        if (new JSONObject(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("call")).has("fadeInSeconds")) {
                            map.put("trigger_action_call_fadeInSeconds" + i, new JSONObject(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("call")).getString("fadeInSeconds"));
                        }
                        if (new JSONObject(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("call")).has("fadeOutSeconds")) {
                            map.put("trigger_action_call_fadeOutSeconds" + i, new JSONObject(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("call")).getString("fadeOutSeconds"));
                        }
                        if (new JSONObject(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("call")).has("volume")) {
                            map.put("trigger_action_call_volume" + i, new JSONObject(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("call")).getString("volume"));
                        }
                        if (new JSONObject(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("call")).has("playMode")) {
                            map.put("trigger_action_call_playMode" + i, new JSONObject(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("call")).getString("playMode"));
                        }
                        if (new JSONObject(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("call")).has("repeat")) {
                            map.put("trigger_action_call_repeat" + i, new JSONObject(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("call")).getString("repeat"));
                        }
                        if (new JSONObject(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("call")).has("assetId")) {
                            map.put("trigger_action_call_assetId" + i, new JSONObject(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("call")).getString("assetId"));
                        }
                    }

                }

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

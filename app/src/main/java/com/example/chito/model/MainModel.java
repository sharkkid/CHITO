package com.example.chito.model;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.bluetooth.BluetoothAdapter;
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
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.chito.Util.BeaconScannerService;
import com.example.chito.Util.GPSListner;
import com.example.chito.Util.GlobalValue;
import com.example.chito.Util.WebInterface;
import com.example.chito.activities.FakeCallActivity;
import com.example.chito.activities.MainActivity;
import com.example.chito.activities.PlayBookActivity;
import com.example.chito.presenter.MainPresenter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static android.content.Context.LOCATION_SERVICE;
import static android.support.v4.app.ActivityCompat.startActivityForResult;
import static com.example.chito.Util.GlobalValue.IsBleStart;
import static com.example.chito.Util.GlobalValue.IsGpsStart;
import static com.example.chito.activities.PlayBookActivity.audio_timer_handler;
import static com.example.chito.activities.PlayBookActivity.webPresenter;

public class MainModel {

    public int IsBlueToothOpen(BluetoothAdapter mBtAdapter) {// 0:無藍芽裝置 1:有,但未開啟 2:有,已開啟
        //檢查裝置是否支援藍芽
        int flag = 0;
        if (mBtAdapter != null) {
            // 檢查藍牙是否開啟
            if (!mBtAdapter.isEnabled()) {
                flag = 1;
            } else {
                flag = 2;
            }
        } else {
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

    public boolean checkNetworkState(Context context, ConnectivityManager manager) {
        boolean flag = false;
        //得到網路連線資訊
        manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //去進行判斷網路是否連線
        if (manager.getActiveNetworkInfo() != null) {
            flag = true;
        } else {
            flag = false;
        }

        return flag;
    }

    public static boolean checkGpsStatus(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        //詢問是否存取位置資訊
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Log.d("checkGpsStatus", "Yes");
            return true;
        } else {
            Log.d("checkGpsStatus", "No");
            return false;
        }
    }

    public boolean haveStoragePermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.e("Permission error", "You have permission");
                return true;
            } else {

                Log.e("Permission error", "You have asked for permission");
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //you dont need to worry about these stuff below api level 23
            Log.e("Permission error", "You already have the permission");
            return true;
        }
    }

    public boolean isFileExists(String dirname, String filename) {
        File folder1 = new File(Environment.getExternalStorageDirectory().getPath() + dirname + "/" + filename);
        Log.d(filename + "=isFileExists", Environment.getExternalStorageDirectory().getPath() + dirname + "/" + filename);
        Log.d(filename + "=isFileExists", folder1.exists() + "");
        return folder1.exists();
    }

    public void deleteFile(String dirname, String filename) {
        File folder1 = new File(Environment.getExternalStorageDirectory().getPath() + dirname + "/" + filename);
//        Log.d(filename+"=deleteFile",folder1.delete()+"");
        folder1.delete();
    }

    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
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

    public String toPrettyFormat(String jsonString) {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(jsonString).getAsJsonObject();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson.toJson(json);

        return prettyJson;
    }

    public JSONObject getJSONObjectById(String id, ArrayList<JSONObject> jsonArray) {
        JSONObject result = null;
        for (int i = 0; i < jsonArray.size(); i++) {
            try {
                if (jsonArray.get(i).getString("id").equals(id)) {
                    result = jsonArray.get(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public void playSound(final Context context, String book_id, String fileName, final AudioManager audioManager, int timer_max){
        final MediaPlayer mp = MediaPlayer.create(context, Uri.parse("file:///"+Environment.getExternalStorageDirectory()+"/story_assets/s"+book_id+"/"+fileName+".mp3"));
        Log.d("Audio_path",Uri.parse("file:///"+Environment.getExternalStorageDirectory()+"/story_assets/s"+book_id+"/"+fileName+".mp3")+"");
        if(mp != null) {
            mp.setLooping(true);
            mp.start();
//            final Handler audio_finish = new Handler();
//            audio_finish.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    mp.stop();
//
////                    }
//                }
//            }, 5000); // 1 second delay (takes millis)
        }
    }

    public MediaPlayer playSound(final Context context, final String book_id, final String fileName, boolean loop, final AudioManager audioManager, final int fadeIn_sec, final int fadeOut_sec, final String[] audio_finish_flag) {
        Log.d("Audio_path",Uri.parse("file:///"+Environment.getExternalStorageDirectory()+"/story_assets/s"+book_id+"/"+fileName+".mp3")+"");
        MediaPlayer mp=null;
        if(!fileName.equals("")) {
            mp = MediaPlayer.create(context, Uri.parse("file:///" + Environment.getExternalStorageDirectory() + "/story_assets/s" + book_id + "/" + fileName + ".mp3"));
            final int audio_duration = mp.getDuration();
            final double[] volume = {1};

            Log.d("audio_duration", audio_duration + "");
//        fadeOut(mp,fadeOut_sec,audioManager);
            mp.setLooping(loop);
            mp.start();
            //20190731
            final Handler fadeIn = new Handler();
            final MediaPlayer finalMp2 = mp;
            fadeIn.postDelayed(new Runnable() {
                @Override
                public void run() {
                    float speed = 0.05f;
                    volume[0] = FadeIn(finalMp2, fadeIn_sec, (float) volume[0], speed);
                    Log.d("FadeInvolume", volume[0] + "");
                    Log.d("timer", PlayBookActivity.audio_timer + "");
                    if (PlayBookActivity.audio_timer < 5) {
                        fadeIn.postDelayed(this, 1000);
                        PlayBookActivity.audio_timer++;
                    } else {
                        fadeIn.removeCallbacksAndMessages(null);
                    }
                }
            }, 1000); // 1 second delay (takes millis)

            //20190731 fadeOut
            final Handler fadeOut = new Handler();
            final MediaPlayer finalMp1 = mp;
            fadeOut.postDelayed(new Runnable() {
                @Override
                public void run() {
                    float speed = 0.05f;
//                Log.d("CurrentPosition",mp.getCurrentPosition()+"");
                    if (finalMp1.getCurrentPosition() > (audio_duration - fadeOut_sec)) {
                        volume[0] = FadeOut(finalMp1, fadeIn_sec, (float) volume[0], speed);
                        PlayBookActivity.audio_timer++;
                    } else if (finalMp1.getCurrentPosition() < (audio_duration - (fadeOut_sec * 1000))) {
                        fadeOut.postDelayed(this, 1000);
                    } else if (finalMp1.getCurrentPosition() > (audio_duration - 500)) {
                        fadeOut.removeCallbacksAndMessages(null);
                        fadeOut.removeCallbacks(this);
                        finalMp1.stop();
                        finalMp1.release();
                    }
                }
            }, 1000); // 1 second delay (takes millis)

            //20190731 audio_finish
            if (audio_finish_flag[0].equals("1")) {
                Log.d("audio_finish_flag", "Yes");
                final Handler audio_finish = new Handler();
                final MediaPlayer finalMp = mp;
                audio_finish.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("CurrentPosition", finalMp.getCurrentPosition() + "s,Duration="+finalMp.getDuration());
                        if (finalMp.getCurrentPosition() < audio_duration - 1000) {
                            audio_finish.postDelayed(this, 1000);
                        } else {
                            Log.d("CurrentPosition", "超過!");
                            audio_finish.removeCallbacksAndMessages(null);
                            finalMp.stop();
                            PlayBookActivity.FakeCall(context, audio_finish_flag[1], audio_finish_flag[2], audio_finish_flag[3], audio_finish_flag[7], audio_finish_flag[5], audio_finish_flag[6]);
                        }
                    }
                }, 500); // 1 second delay (takes millis)
            } else if (audio_finish_flag[0].equals("2")) {
                final Handler audio_finish = new Handler();
                final MediaPlayer finalMp3 = mp;
                audio_finish.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("CurrentPosition", finalMp3.getCurrentPosition() + "s");
                        if (finalMp3.getCurrentPosition() < audio_duration - 1000) {
                            audio_finish.postDelayed(this, 1000);
                        } else {
                            Log.d("CurrentPosition", "超過!");
                            audio_finish.removeCallbacksAndMessages(null);
                            finalMp3.stop();
                            new WebInterface(context,webPresenter).loadHtmlUrl(book_id,audio_finish_flag[4]);

                        }
                    }
                }, 500); // 1 second delay (takes millis)
            }
        }
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
        if(story_map.containsKey(key)){
            tmp = story_map.get(key)+"";
        }
        else{
            tmp = "";
        }
        return tmp;
    }

    public static void wakeUpAndUnlock(Context context){
        //屏锁管理器
        KeyguardManager km= (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        //解锁
        kl.disableKeyguard();
        //获取电源管理器对象
        PowerManager pm=(PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK,"bright");
        //点亮屏幕
        wl.acquire();
        //释放
        wl.release();
    }

    //計算兩點GPS的距離
    public boolean IsGPSClosed(Location dis, Location cur,double distance){
        if(dis == null)
            Log.d("gps_dis","null");
        else
            Log.d("gps_dis","not null");
        double distanceInMeters = cur.distanceTo(dis);
        Log.d("gps_dis",String.valueOf(dis));
        Log.d("gps_cur",String.valueOf(cur));
        Log.d("gps_distanceInMeters","距離:"+distanceInMeters+"");
        if(distanceInMeters < distance){
            return true;
        }
        else{
            return false;//暫時 true
        }
    }

    public double calculateDistance(int txPower, double rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }
        double ratio = rssi*1.0/txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio,10);
        }
        else {
            double accuracy =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;
            return accuracy;
        }
    }

    /*
    * 以下為劇本JSON處理
    *
    * */
    public Map<String,String> JsonParser(JSONObject jsonObject) {
        Map<String, String> map = new HashMap<>();

        try {
//            Log.d("json_all", String.valueOf(jsonObject));
//            Log.d("json_initial", jsonObject.getString("initial"));
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
                if(new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("audio")).has("method")){
                    String method = new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("audio")).getString("method");
                    map.put("audio_method", method);
                }
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
                    map.put("trigger_match", new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("match"));
                }
                //20190730
                if (new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).has("actions")) {
                    if(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).has("qrScan")) {
                        map.put("trigger_action_name", "qrScan");
                    }
                    if(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).has("changePlaybookThumbnail")) {
                        map.put("trigger_action_changePlaybookThumbnail", new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("changePlaybookThumbnail")).getString("assetId"));
                    }
                    if(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).has("flag")) {
                        map.put("trigger_action_flag_names", new JSONArray(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("flag")).getString("names")).get(0).toString());

                    }
                    if(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).has("gotoScene")) {
                        map.put("trigger_action_sceneId" + i, new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("gotoScene")).getString("sceneId"));
                        map.put("trigger_action_reuse" + i, new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("gotoScene")).getString("reuse"));
                        map.put("trigger_action_target" + i, new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("gotoScene")).getString("target"));
                    }
                    if (new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).has("flag")) {
                        map.put("trigger_flag_names" + i, new JSONArray(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("flag")).getString("names")).get(0).toString());
                    }
                    if (new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).has("unflag")) {
                        map.put("trigger_unflag_names" + i, new JSONArray(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("unflag")).getString("names")).get(0).toString());
                    }
                    if(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).has("audio")) {
                        map.put("trigger_audio_method" + i, new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("audio")).getString("method"));
                        map.put("trigger_audio_assetsId" + i, new JSONArray(new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("audio")).getString("tracks")).getJSONObject(0).getString("assetId"));
//                        map.put("trigger_audio_assetsId" + i, new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("tracks")).getString("assetId"));
                    }
                    if(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).has("changePlaybookThumbnail")) {
                        map.put("trigger_audio_disablePlaybook_assetId" + i,new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("changePlaybookThumbnail")).getString("assetId"));
                    }
                    if(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).has("disablePlaybook")) {
                        map.put("trigger_audio_disablePlaybook_retryMessage",new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("disablePlaybook")).getString("retryMessage"));
                        map.put("trigger_audio_disablePlaybook_message",new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("disablePlaybook")).getString("message"));
                    }
                    if(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).has("finishPlaybook")) {
                        map.put("trigger_finishPlaybook" + i, new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("finishPlaybook"));
                    }
                    if (new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).has("fakeCall")) {
                        map.put("trigger_action_callerName", new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("callerName"));
                        map.put("trigger_action_callerNumber", new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("callerNumber"));
                        map.put("trigger_action_instanceId" + i, new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(i).getString("actions")).getString("fakeCall")).getString("instanceId"));
                        map.put("trigger_action_fakecallFinish", new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(1).getString("actions")).getString("gotoScene")).getString("sceneId"));
                        map.put("trigger_action_fakecallDeclined", new JSONObject(new JSONObject(new JSONArray(jsonObject.getString("triggers")).getJSONObject(1).getString("actions")).getString("gotoScene")).getString("sceneId"));
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

    public void startGPS(Context context , final float distance , final String book_id , final int next_sceneId, Map<String,String> gps_map) {
        double gps_latitude = 0;
        double gps_longitude = 0;
        final Location dis = new Location("dis");
        if(!IsMapNull(gps_map,"gps_latitude").equals("")){
            Log.d("gps_latitude","gps_latitude!"+IsMapNull(gps_map,"gps_latitude"));
            gps_latitude = Double.parseDouble(IsMapNull(gps_map,"gps_latitude"));
            dis.setLatitude(gps_latitude);
        }
        if(!IsMapNull(gps_map,"gps_longitude").equals("")){
            Log.d("gps_longitude","gps_longitude!"+IsMapNull(gps_map,"gps_longitude"));
            gps_longitude = Double.parseDouble(IsMapNull(gps_map,"gps_longitude"));
            dis.setLongitude(gps_longitude);
        }
        Log.d("gps_IsGpsStart",IsGpsStart+"");
        final Handler gps_sesor = new Handler();
        gps_sesor.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(IsGpsStart){
                        if(GlobalValue.Latitude != 0 && GlobalValue.Longtitude !=0){
                            Location cur = new Location("cur");
                            cur.setLatitude(GlobalValue.Latitude);
                            cur.setLongitude(GlobalValue.Longtitude);
                            if(IsGPSClosed(dis,cur,distance)){
                                if(PlayBookActivity.mp != null)
                                    PlayBookActivity.mp.stop();
                                IsGpsStart = false;
                                WebInterface.loadHtmlUrl(book_id,next_sceneId+"");
                            }
                            else{
                                gps_sesor.postDelayed(this,1000);
                            }
                        }
                    }
                    else{

                    }
                }
            }, 2000); // 1 second delay (takes millis)
    }
    public void startBLE(final Context context , final String book_id, final String[] ble_data) {
        Intent stopIntent = new Intent(context, BeaconScannerService.class);
        context.startService(stopIntent);

        final Handler ble_sensor = new Handler();
        ble_sensor.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(GlobalValue.BLE_UUID.equals(ble_data[0]) && GlobalValue.BLE_distance > Double.parseDouble(ble_data[3])){
                    Log.d("BLE","BLE not run");
                    IsBleStart = false;
                    GlobalValue.IsBleClosed = true;
                    WebInterface.loadHtmlUrl(book_id,ble_data[4]+"");
                }
                else{
                    Log.d("BLE","BLE run,GlobalValue.BLE_distance="+GlobalValue.BLE_distance+",uuid="+GlobalValue.BLE_UUID);
                    GlobalValue.IsBleClosed = false;
                    ble_sensor.postDelayed(this, 1000);
                }
            }
        }, 1000); // 1 second delay (takes millis)
    }
}

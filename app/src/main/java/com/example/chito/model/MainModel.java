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

    /*
    * 以下為劇本處理邏輯
    * */
    public Map<String,String> InitialParser(JSONObject jsonObject) {
        Map<String, String> map = new HashMap<>();
        try {
            // Display
//            Log.d("jsonObject",jsonObject.toString());
            if(Isempty(jsonObject,"display")) {
                String type = new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("display")).getString("type");
                Log.d("type",type);
                switch (type) {
                    case "webview":
                        String assetId = new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("display")).getString("assetId");
                        map.put("webview","1");
                        map.put("type",type);
                        map.put("assetsId",assetId);
                        break;
                    case "ar":

                        break;
                }
            }

            // Audio
            if(Isempty(jsonObject,"audio")) {
//                Log.d("audio_jsonObject", new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("audio")).getJSONArray("tracks").length()+"");
                JSONArray jsonArray_audio = new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("audio")).getJSONArray("tracks");
                for(int i=0;i<jsonArray_audio.length();i++) {
                    String assetId = new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("audio")).getJSONArray("tracks").getJSONObject(i).getString("assetId");
                    Log.d("assetId",assetId);
                    map.put("audio_assetId"+i, assetId);
                    if (new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("audio")).getJSONArray("tracks").getJSONObject(i).has("fadeOutSeconds")) {
                        String fadeOutSeconds = new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("audio")).getJSONArray("tracks").getJSONObject(i).getString("fadeOutSeconds");
                        map.put("audio_fadeOutSeconds"+i, fadeOutSeconds);
                        Log.d("audio_fadeOutSeconds"+i,"audio_fadeOutSeconds"+i);
                    }
                    if (new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("audio")).getJSONArray("tracks").getJSONObject(i).has("volume")) {
                        String volume_type = new JSONObject(new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("audio")).getJSONArray("tracks").getJSONObject(i).getString("volume")).getString("type");
                        String latitude =  new JSONObject(new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("audio")).getJSONArray("tracks").getJSONObject(i).getString("volume")).getString("latitude");
                        String longtitude =  new JSONObject(new JSONObject(new JSONObject(jsonObject.getString("initial")).getString("audio")).getJSONArray("tracks").getJSONObject(i).getString("volume")).getString("longtitude");

                        map.put("audio_volume_type"+i, volume_type);
                        map.put("latitude"+i, latitude);
                        map.put("longtitude"+i, longtitude);

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

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
}

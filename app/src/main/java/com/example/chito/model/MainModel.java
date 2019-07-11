package com.example.chito.model;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.File;

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
        Log.d("isFileExists",Environment.getExternalStorageDirectory().getPath()+dirname+"/"+filename);
        return folder1.exists();
    }

    public boolean deleteFile(String dirname,String filename){
        File folder1 = new File(Environment.getExternalStorageDirectory().getPath()+dirname+"/"+filename);
        Log.d("deleteFile",Environment.getExternalStorageDirectory().getPath()+dirname+"/"+filename);
        return folder1.delete();
    }
}

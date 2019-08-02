package com.example.chito.presenter;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.chito.Util.Beacon;
import com.example.chito.Util.BeaconFormateNotFoundException;
import com.example.chito.activities.MainActivity;
import com.example.chito.model.MainModel;
import com.example.chito.view.MainView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;


public class MainPresenter {
    private MainView mainView;
    private MainModel mainModel;

    //權限
    public static final int  ACCESS_FINE_LOCATION_CODE = 1;
    public static final int  REQUEST_ENABLE_BT_CODE = 2;
    public static final int  OVERLAY_PERMISSION_REQ_CODE = 3;
    public static final int  ACCESS_COARSE_LOCATION = 4;


    public MainPresenter(MainView mainView, MainModel mainModel){
        this.mainView = mainView;
        this.mainModel = mainModel;
    }
    public void onCreate(){
        mainView.setContentView();
    }
    public int checkBlueTooth(BluetoothAdapter mBtAdapter){
        int bt_flag = mainModel.IsBlueToothOpen(mBtAdapter);
//        switch (bt_flag){
//            case 0:
//                mainView.showToast("裝置為支援藍芽裝置！");
//                break;
//            case 1:
//                mainView.showToast("裝置支援藍芽裝置！但是未開啟！");
//                break;
//            case 2:
//                mainView.showToast("裝置支援藍芽裝置！已開啟！");
//                break;
//        }
        return bt_flag;
    }
    public void start_beaconScan(BluetoothAdapter mBtAdapter,BluetoothAdapter.LeScanCallback mLeScanCallback){
        mBtAdapter.startLeScan(mLeScanCallback);
    }
    public void showToast(String text){
        mainView.showToast(text);
    }
    public void reques_permission(){
        mainView.reques_permission();
    }


    public BluetoothAdapter.LeScanCallback setLeScanCallback(BluetoothAdapter.LeScanCallback mLeScanCallback){
        mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
                    @Override
                    public void onLeScan(final BluetoothDevice device, final int rssi,
                                         final byte[] scanRecord) {
                        Log.d("TAG", "BLE device : " + device.getName());

                        try {
                            Beacon beacon = new Beacon(scanRecord, device, rssi);

                            String message = "ibeaconName" +
                                    "\nMac：" + beacon.getMacAddress()
                                    + " \nUUID：" + beacon.getUuid()
                                    + "\nMajor：" + beacon.getMajor()
                                    + "\nMinor：" + beacon.getMinor()
                                    + "\nTxPower：" + beacon.getTxPower()
                                    + "\nrssi：" + rssi;

                            Log.d("Beacon", message);

                            Log.d("distance", "distance：" + beacon.distance());

//                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        } catch (BeaconFormateNotFoundException e) {
//                            Toast.makeText(context, "Beacon Formate Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                };
        return mLeScanCallback;
    }

    public void check_Permission(){
        mainView.reques_permission();
    }

    public void checkPermission(){

    }

    public boolean checkStoredPermission(Activity activity){
        return mainModel.haveStoragePermission(activity);
    }

    public boolean checkGpsStatus(Activity activity) {
        return mainModel.checkGpsStatus(activity);
    }
}

package com.example.chito.view;


import android.bluetooth.BluetoothAdapter;


public interface MainView {

    void setContentView();
    void showToast(String text);
    void LeScanCallback(BluetoothAdapter.LeScanCallback mLeScanCallback);
    void reques_permission();
}

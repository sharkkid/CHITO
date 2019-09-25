package com.example.chito.Util;

import android.util.ArrayMap;

import java.util.Map;

public class GlobalValue {
    public static String url = "chito-test.nya.tw:3000";
    public static String book_id = "";
    public static String BLE_UUID = "";
    public static String flag_sceneId = "";

    public static double Latitude = 0;
    public static double Longtitude = 0;
    public static double BLE_distance = 0;

    public static boolean IsGpsStart = false;
    public static boolean IsBleStart = false;
    public static boolean IsBleClosed = false;


    public static Map<String,String> flag_map = new ArrayMap<>();
    public static Map<String,String> flag_status = new ArrayMap<>();

}

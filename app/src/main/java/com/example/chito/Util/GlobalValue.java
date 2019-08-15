package com.example.chito.Util;

import android.util.ArrayMap;

import java.util.Map;

public class GlobalValue {
    public static String book_id = "";
    public static double Latitude = 0;
    public static double Longtitude = 0;
    public static boolean IsGpsStart = false;
    public static boolean IsBleStart = false;
    public static Map<String,String> flag_map = new ArrayMap<>();
    public static Map<String,String> flag_status = new ArrayMap<>();
    public static String flag_sceneId = "";
}

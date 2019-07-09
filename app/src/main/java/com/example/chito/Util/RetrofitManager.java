package com.example.chito.Util;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class RetrofitManager {

    // 以Singleton模式建立
    private static RetrofitManager mInstance = new RetrofitManager();

    private MyAPIService myAPIService;

    private RetrofitManager() {

        // 設置baseUrl即要連的網站，addConverterFactory用Gson作為資料處理Converter
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://chito-test.nya.tw:3000/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        myAPIService = retrofit.create(MyAPIService.class);
    }

    public static RetrofitManager getInstance() {
        return mInstance;
    }

    public MyAPIService getAPI() {
        return myAPIService;
    }

    // 注意是interface而不是class哦
    public interface MyAPIService {

        // 測試網站      https://jsonplaceholder.typicode.com/
        // GET網址      https://jsonplaceholder.typicode.com/albums/1
        // POST網址     https://jsonplaceholder.typicode.com/albums
        // ...typicode.com/[這裡就是API的路徑]

        @GET("v1/playbooks/1.json")    // 設置一個GET連線，路徑為albums/1
        Call<playbooks_pojo> getPlaybook();   // 取得的回傳資料用Albums物件接收，連線名稱取為getAlbums

    }
}


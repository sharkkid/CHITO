package com.example.chito.presenter;

import android.app.Activity;
import android.view.Window;

import com.example.chito.model.MainModel;
import com.example.chito.view.HtmlView;


public class WebPresenter {
    private HtmlView htmlView;
    private MainModel mainModel;

    //權限
    public static final int ACCESS_FINE_LOCATION_CODE = 1;
    public static final int REQUEST_ENABLE_BT_CODE = 2;
    public static final int  OVERLAY_PERMISSION_REQ_CODE = 3;

    public WebPresenter(HtmlView htmlView, MainModel mainModel){
        this.htmlView = htmlView;
        this.mainModel = mainModel;
    }
    public void onCreate(){
        htmlView.setContentView();
    }
    public void reques_permission(){
        htmlView.reques_permission();
    }

    public boolean checkStoredPermission(Activity activity){
        return mainModel.haveStoragePermission(activity);
    }

    public boolean isFileExists(String dirname,String filename){
        return mainModel.isFileExists(dirname,filename);
    }

    public boolean deleteFile(String dirname,String filename){
        return mainModel.deleteFile(dirname,filename);
    }
}

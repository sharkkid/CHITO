package com.example.chito.Util;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class WebInterface extends Object{
    private Context context;
    public WebInterface(Context context){
        this.context = context;
    }
    @JavascriptInterface
    public void ShowToast(String str){
        Toast.makeText(context,str,Toast.LENGTH_SHORT).show();
    }
}


package com.example.chito.presenter;

import com.example.chito.model.MainModel;
import com.example.chito.view.HtmlView;


public class WebPresenter {
    private HtmlView htmlView;
    private MainModel mainModel;

    public WebPresenter(HtmlView htmlView, MainModel mainModel){
        this.htmlView = htmlView;
        this.mainModel = mainModel;
    }
    public void onCreate(){
        htmlView.setContentView();
    }
}

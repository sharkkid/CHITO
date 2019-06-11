package com.example.chito.presenter;

import com.example.chito.model.MainModel;
import com.example.chito.view.MainView;

public class MainPresenter {
    private MainView mainView;
    private MainModel mainModel;
    public MainPresenter(MainView mainView, MainModel mainModel){
        this.mainView = mainView;
        this.mainModel = mainModel;
    }
    public void onCreate(){
        mainView.setContentView();
    }
}

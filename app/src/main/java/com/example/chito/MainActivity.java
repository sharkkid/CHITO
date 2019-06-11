package com.example.chito;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.chito.model.MainModel;
import com.example.chito.presenter.MainPresenter;
import com.example.chito.view.MainView;

public class MainActivity extends AppCompatActivity implements MainView {
    MainPresenter mainPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainPresenter = new MainPresenter(this,new MainModel());
        mainPresenter.onCreate();

    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_main);
    }
}

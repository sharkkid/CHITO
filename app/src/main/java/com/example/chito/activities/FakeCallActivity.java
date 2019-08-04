package com.example.chito.activities;


import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.chito.R;
import com.example.chito.model.MainModel;
import com.example.chito.presenter.WebPresenter;
import com.example.chito.view.HtmlView;

import java.io.File;
import java.io.IOException;

import static android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION;


public class FakeCallActivity extends AppCompatActivity{


    private static ProgressDialog progressDialog;
    public static boolean booklist_isDonwloaded = false;
    public ImageButton btn_receive,btn_reject;
    public MediaPlayer mediaPlayer;
    public int MAX_Playable = 15;
    public int current_play_n = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        Intent intent = this.getIntent();
        //取得傳遞過來的資料
        String book_id = intent.getStringExtra("book_id");
        int ring_id = intent.getIntExtra("ring_id",0);
        int call_id = intent.getIntExtra("call_id",0);
        playSound(this,book_id,ring_id+"");
        Log.d("ring_id",ring_id+"");
    }

    public void init() {
        //主要調配器宣告
        //20190804
        setContentView(R.layout.fakecall);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        btn_receive = findViewById(R.id.fakecall_receive);
        btn_reject = findViewById(R.id.fakecall_reject);

        btn_receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mediaPlayer.stop();
                finish();
            }
        });

        btn_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mediaPlayer.stop();
                finish();
            }
        });
    }
    public void playSound(Context context,String book_id,String fileName){
        MediaPlayer mp = MediaPlayer.create(context, Uri.parse("file:///"+Environment.getExternalStorageDirectory()+"/story_assets/s"+book_id+"/"+fileName+".mp3"));
        Log.d("Audio_path",Uri.parse("file:///"+Environment.getExternalStorageDirectory()+"/story_assets/s"+book_id+"/"+fileName+".mp3")+"");
        if(current_play_n == MAX_Playable){
            mp.setLooping(true);
            mp.stop();
        }
        else{
            current_play_n++;
        }

        mp.start();
    }
}

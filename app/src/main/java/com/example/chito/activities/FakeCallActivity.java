package com.example.chito.activities;


import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.app.Service;
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
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chito.R;
import com.example.chito.Util.WebInterface;
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
    public MediaPlayer mp;
    public TextView ui_name,ui_call_time;
    public int MAX_Playable = 15;
    public int current_play_n = 1;
    public String ring_id,call_id,name,next_sceneId;
    public String book_id;
    public Vibrator myVibrator;
    public MainModel mainModel;
    int time = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();


        playSound(this,book_id,ring_id+"");
        vibration(this,mp.getDuration());
    }

    public void init() {
        mainModel = new MainModel();
        //主要調配器宣告
        //20190804
        setContentView(R.layout.fakecall);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        Intent intent = this.getIntent();
        //取得傳遞過來的資料
        book_id = intent.getStringExtra("book_id");
        ring_id = intent.getStringExtra("ring_id");
        call_id = intent.getStringExtra("call_id");
        name = intent.getStringExtra("name");
        next_sceneId = intent.getStringExtra("next_sceneId");
        Log.d("next_sceneId",next_sceneId);

        btn_receive = findViewById(R.id.fakecall_receive);
        btn_reject = findViewById(R.id.fakecall_reject);
        ui_name = findViewById(R.id.call_name);
        ui_name.setText(name);
        ui_call_time = findViewById(R.id.call_time);

        btn_receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound(FakeCallActivity.this,book_id,call_id+"");
                ui_call_time.setVisibility(View.VISIBLE);
                final Handler pickup = new Handler();
                pickup.post(new Runnable() {
                    @Override
                    public void run() {
                        ui_call_time.setText((mainModel.secToTime(time)));
                        time++;
//                        Log.d("播放時間","current="+mp.getCurrentPosition()+",whole="+mp.getDuration());
                        if(mp.getCurrentPosition() < (mp.getDuration()-50300)){
                            pickup.post(this);
                        }
                        else{
                            WebInterface.loadHtmlUrl(book_id,next_sceneId);
                            pickup.removeCallbacksAndMessages(null);
                            mp.stop();

                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });


                btn_receive.setVisibility(View.GONE);
                btn_reject.setVisibility(View.GONE);
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
        try {
            if (mp.isPlaying()) {
                mp.stop();
                myVibrator.cancel();
            }
        }
        catch(Exception e){}
        mp = MediaPlayer.create(context, Uri.parse("file:///"+Environment.getExternalStorageDirectory()+"/story_assets/s"+book_id+"/"+fileName+".mp3"));
        Log.d("Audio_path",Uri.parse("file:///"+Environment.getExternalStorageDirectory()+"/story_assets/s"+book_id+"/"+fileName+".mp3")+"");
        mp.setLooping(true);
        if(current_play_n == MAX_Playable){
            mp.setLooping(false);
            mp.stop();
        }
        else{
            current_play_n++;
        }

        Log.d("sec",mp.getDuration()+"");
        mp.start();
    }

    public void vibration(Context context,long sec){
        long[] vbr = new long[(int) (sec/1000)];
        for(int i=0;i<vbr.length;i++){
            vbr[i] = 1000;
        }
        myVibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        myVibrator.vibrate(vbr, -1);
    }
}

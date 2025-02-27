package com.example.chito.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.chito.R;
import com.example.chito.Util.BeaconScannerService;
import com.example.chito.model.BleManagement;
import com.example.chito.model.MainModel;
import com.example.chito.presenter.MainPresenter;
import com.example.chito.view.MainView;
import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION;
import static com.example.chito.Util.GlobalValue.book_id;

public class MainActivity extends AppCompatActivity implements MainView {
    private MainPresenter mainPresenter;

    private Button btn_checkBT;
    private Button btn_scanBeacon;
    private Button btn_startService;
    private Button btn_web;
    private Button btn_download;
    private Button btn_qr;

    private BluetoothAdapter mBtAdapter;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;
    private BleManagement bleManagement;
    private BeaconScannerService beaconScannerService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private ServiceConnection mServiceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder serviceBinder)
        {
            // TODO Auto-generated method stub
            beaconScannerService = ((BeaconScannerService.LocalBinder)serviceBinder).getService();
        }

        public void onServiceDisconnected(ComponentName name)
        {
            // TODO Auto-generated method stub
        }
    };

    public void init(){
        //主要調配器宣告
        mainPresenter = new MainPresenter(this,new MainModel());
        mainPresenter.onCreate();
        //藍芽裝置
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        //權限
        mainPresenter.reques_permission();
        mainPresenter.checkStoredPermission(MainActivity.this);
        //元件設定
        btn_scanBeacon = findViewById(R.id.btn_scanBeacon);
        btn_scanBeacon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mainPresenter.checkBlueTooth(mBtAdapter) == 2) {
                    mLeScanCallback = mainPresenter.setLeScanCallback(mLeScanCallback);
                    bleManagement = new BleManagement(MainActivity.this,mLeScanCallback);
                    bleManagement.scanLeDevice(true);
                }
            }
        });

        btn_startService = findViewById(R.id.button3);
        btn_startService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent start_service = new Intent(MainActivity.this, BeaconScannerService.class);
                startService(start_service);
            }
        });
        btn_startService = findViewById(R.id.button4);
        btn_startService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stopIntent = new Intent(MainActivity.this, BeaconScannerService.class);
                stopService(stopIntent);
            }
        });

        btn_web = findViewById(R.id.btn_web);
        btn_web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goweb = new Intent(MainActivity.this, WebActivity.class);
                startActivity(goweb);
            }
        });

        btn_download = findViewById(R.id.btn_download);
        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goweb = new Intent(MainActivity.this, PlayBookActivity.class);
                goweb .putExtra("book_id","1");//傳遞劇本編號
                startActivity(goweb);
//                Intent goweb = new Intent(MainActivity.this, FakeCallActivity.class);
//                startActivity(goweb);
            }
        });

        btn_qr = findViewById(R.id.btn_qr);
        btn_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goqr = new Intent(MainActivity.this, QrScanner.class);
                startActivityForResult(goqr, 1);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                showToast(data.getStringExtra("qr_result"));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("OnDestroy","執行銷毀");
        SharedPreferences spref = (SharedPreferences) getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = spref.edit();
        editor.clear();
        editor.putString(PlayBookActivity.get_book_id()+"_save",PlayBookActivity.get_current_id());
//        editor.putString(PlayBookActivity.get_book_id()+"_save","0");
        editor.commit();
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_main);
    }
    @Override
    public void showToast(String text){
        Toast.makeText(this,text,Toast.LENGTH_SHORT).show();
    }
    public void LeScanCallback(BluetoothAdapter.LeScanCallback mLeScanCallback){

    }

    @Override
    public void reques_permission(){
        if(mainPresenter.checkBlueTooth(mBtAdapter) != 2){
            //没有 BLUETOOTH 权限
            Intent enableIntent = new Intent( BluetoothAdapter.ACTION_REQUEST_ENABLE );
            startActivityForResult( enableIntent, mainPresenter.REQUEST_ENABLE_BT_CODE );
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            //没有 ACCESS_FINE_LOCATION 权限
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},mainPresenter.ACCESS_FINE_LOCATION_CODE);
        }
        if(!Settings.canDrawOverlays(MainActivity.this)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setTitle("權限請求!");
            dialog.setMessage("請允許本程式可懸浮權限!");
            dialog.setPositiveButton("前往",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // TODO Auto-generated method stub
                    Intent enableIntent = new Intent( ACTION_MANAGE_OVERLAY_PERMISSION );
                    startActivityForResult( enableIntent, mainPresenter.OVERLAY_PERMISSION_REQ_CODE );
                }
            });
            dialog.show();
        }
        if(!mainPresenter.checkGpsStatus(this)){
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("GPS定位權限請求!");
            dialog.setMessage("請允許本程式GPS定位權限!");
            dialog.setPositiveButton("前往",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // TODO Auto-generated method stub
                    Intent enableIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS );
                    startActivityForResult( enableIntent, MainPresenter.ACCESS_COARSE_LOCATION );
                }
            });
            dialog.setNegativeButton("拒絕",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // TODO Auto-generated method stub
                }
            });
            dialog.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MainPresenter.ACCESS_FINE_LOCATION_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 申请同意
                } else {
                    //申请拒绝
                    Toast.makeText(this, "您已拒絕位置權限，將無法搜尋Beacon!", Toast.LENGTH_SHORT).show();
                }
                break;
            case MainPresenter.REQUEST_ENABLE_BT_CODE:
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

                ) {
                    // 申请同意
                } else {
                    //申请拒绝
                    mainPresenter.showToast("位置權限已拒絕!");
                }
                break;
            case MainPresenter.ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 申请同意
                } else {
                    //申请拒绝
                    Toast.makeText(this, "您已拒絕GPS定位權限，將無法搜尋GPS!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}

package com.example.chito.Util;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.chito.activities.FakeCallActivity;
import com.example.chito.model.BleManagement;
import com.example.chito.model.MainModel;
import com.example.chito.presenter.MainPresenter;

import static com.example.chito.Util.GlobalValue.IsBleStart;
import static com.example.chito.Util.GlobalValue.IsGpsStart;

public class BeaconScannerService extends Service {
    private BluetoothAdapter mBtAdapter;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;
    private BleManagement bleManagement;
    private Beacon beacon;
    private boolean flag = false;

    public class LocalBinder extends Binder //宣告一個繼承 Binder 的類別 LocalBinder
    {
        public BeaconScannerService getService() {
            return BeaconScannerService.this;
        }
    }

    private LocalBinder mLocBin = new LocalBinder();

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return mLocBin;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onLeScan(final BluetoothDevice device , final int rssi ,
                                 final byte[] scanRecord) {
//                Log.d("TAG" , "BLE device : " + device.getName());

                try {
                    if (IsBleStart) {
                        beacon = new Beacon(scanRecord , device , rssi);
                        String message = "ibeaconName" +
                                "\nMac：" + beacon.getMacAddress()
                                + " \nUUID：" + beacon.getUuid()
                                + "\nMajor：" + beacon.getMajor()
                                + "\nMinor：" + beacon.getMinor()
                                + "\nTxPower：" + beacon.getTxPower()
                                + "\nrssi：" + rssi;
                        Log.d("beacon.getUuid()" , message);
                        Log.d("Beacon" , message);
                        GlobalValue.BLE_UUID = beacon.getUuid().toLowerCase();
                        GlobalValue.BLE_distance = beacon.distance();
                        Log.d("BLE UUID" , GlobalValue.BLE_UUID);
//                        GlobalValue.BLE_UUID = "b9d4fe7a-be80-46a0-9d76-5fe78f1b9405";
//                        GlobalValue.BLE_distance = 31;
                    }
                } catch (BeaconFormateNotFoundException e) {
                    e.printStackTrace();
                }
            }
        };
        bleManagement = new BleManagement(this , mLeScanCallback);
        bleManagement.scanLeDevice(true);
        final Handler toast = new Handler();
        toast.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (flag) {
//                    Toast.makeText(BeaconScannerService.this,"d="+beacon.distance(),Toast.LENGTH_SHORT).show();
                } else {

                }
            }
        }, 0); // 1 second delay (takes millis)

        
//        showDialog();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // TODO Auto-generated method stub
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // TODO Auto-generated method stub
        bleManagement.scanLeDevice(false);
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("test");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i("owen", "Yes is clicked");
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i("owen", "No is clicked");
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
    }
}
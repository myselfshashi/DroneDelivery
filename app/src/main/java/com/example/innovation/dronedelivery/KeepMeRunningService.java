package com.example.innovation.dronedelivery;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kontakt.sdk.android.ble.configuration.ActivityCheckConfiguration;
import com.kontakt.sdk.android.ble.configuration.ForceScanConfiguration;
import com.kontakt.sdk.android.ble.configuration.ScanPeriod;
import com.kontakt.sdk.android.ble.configuration.scan.IBeaconScanContext;
import com.kontakt.sdk.android.ble.configuration.scan.ScanContext;
import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.device.DeviceProfile;
import com.kontakt.sdk.android.ble.discovery.BluetoothDeviceEvent;
import com.kontakt.sdk.android.ble.discovery.EventType;
import com.kontakt.sdk.android.ble.discovery.ibeacon.IBeaconDeviceEvent;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.util.BluetoothUtils;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by 145747 on 9/9/2015.
 */
public class KeepMeRunningService extends Service implements ProximityManager.ProximityListener {

    private String TAG = Helper.TAG;
    //For BLE Start -->
    private BluetoothAdapter mBluetoothAdapter;
    private final static int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_CODE_ENABLE_BLUETOOTH = 1;
    private ProximityManager proximityManager;
    private ScanContext scanContext;
    BluetoothDeviceEvent event;
    public Intent intent;



    @Override
    public void onScanStart() {
        Log.i(TAG,"I am in onScanStart()");
        Helper.deliveryArrived = false;
    }
    @Override
    public void onScanStop() {
        proximityManager.disconnect();
        proximityManager = null;
    }

    @Override
    public void onEvent(BluetoothDeviceEvent event) {
        this.event = event;
        /*new Helper().setDeliveryArrived(false);*/
        Log.i(TAG, "I am inside onEvent");
        Helper.deliveryArrived = false;
        if (event.getDeviceProfile() == DeviceProfile.IBEACON ) {
            final IBeaconDeviceEvent iBeaconDeviceEvent = (IBeaconDeviceEvent) event;
            if (iBeaconDeviceEvent.getEventType() == EventType.DEVICE_DISCOVERED) {
                Log.i(TAG, "A device has been identified");
            }
                List<IBeaconDevice> deviceList = iBeaconDeviceEvent.getDeviceList();
                if (deviceList != null) {
                    for (int i = 0; i < deviceList.size(); i++) {

                        String deviceDetails = "Device Name:" + deviceList.get(i).getName() + "\n" +
                                "UUID:" + deviceList.get(i).getProximityUUID() + "\n" +
                                "Device Unique Id:" + deviceList.get(i).getUniqueId() + "\n" +
                                "Major:" + deviceList.get(i).getMajor() + "\n" +
                                "Minor:" + deviceList.get(i).getMinor() + "\n" +
                                "Distance:" + deviceList.get(i).getDistance() + "\n" +
                                "Firmware Version:" + deviceList.get(i).getFirmwareVersion() + "\n" +
                                "RSSI:" + deviceList.get(i).getRssi() + "\n" +
                                "Battery Power:" + deviceList.get(i).getBatteryPower()
                        ;
                        Log.i(TAG, "Device Details are: " + deviceDetails);
/*                        if (deviceList.get(i).getDistance()>3.5) {
                            Log.i(TAG, "I am going away will avoid you, my distance is: "+deviceList.get(i).getDistance());
                        }*/
                        if (deviceList.get(i).getUniqueId().equals("cTHq")) {
                            Log.i(TAG, "Beacon Device of Drone is identified and the size of all devices are " + deviceList.size());
                            Helper.deliveryArrived  = true;
                            Log.i(TAG, "DeliveryArrived is: " + Helper.deliveryArrived);
                            /*proximityManager.disconnect();*/
                            Helper.stopFindingDrone = true;
                            break;
                        }
                        else {
                            Log.i(TAG,"I didn't find the beacon in onEvent ");
                            Helper.deliveryArrived = false;
                        }
                    }
                }
        }

    }


    IBeaconScanContext iBeaconScanContext = new IBeaconScanContext.Builder().build();

    //For BLE End -->

    private final IBinder mBinder = new MyBinder();
    private boolean isRunning = false;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (Helper.stopFindingDrone == false)
            keepCheckingBeacon();
        }
    };

    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        proximityManager = new ProximityManager(this);


        /*recognizeBeacon();*/
        intent = new Intent(this,MainActivity.class);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(isRunning ) {
            Log.i(TAG, "Service is already running");
        }
        else {
            Helper.mainContext = getApplicationContext();
            new Thread(runnable).start();
            isRunning = true;
            Log.i(TAG, "Started a new service");
        }
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {

        Log.i(TAG, "I am in onBind");
        return mBinder;
    }

    public class MyBinder extends Binder {
        KeepMeRunningService getService() {
            return KeepMeRunningService.this;
        }
    }

    public void keepCheckingBeacon(){
        int i =0;
        Log.i(TAG,"Entering keepCheckingBeacon");
        scanContext = new ScanContext.Builder()
                .setScanMode(ProximityManager.SCAN_MODE_BALANCED)
                .setIBeaconScanContext(iBeaconScanContext)
                .setActivityCheckConfiguration(ActivityCheckConfiguration.DEFAULT)
                .setForceScanConfiguration(ForceScanConfiguration.DEFAULT)
                .setScanPeriod(new ScanPeriod(TimeUnit.SECONDS.toMillis(12000), 0))
                .build();
        while(true){
            Log.i(TAG, "I am in the thread - Shashi flag deliveryArrived: " + Helper.deliveryArrived +
                    "delivery accepted: " + Helper.deliveryAccepted);
//            i++;
            try {
                if (Helper.deliveryArrived == true && Helper.deliveryAccepted == false) {

                    new Helper().createNotification(getApplicationContext());
                    Helper.disableConfirmButton = false;
                    Helper.stopFindingDrone = false;
                    Log.i(TAG,"I am going to sleep for 120000 milliseconds");
                    Thread.sleep(60000);
                    Log.i(TAG, "I am waking up");
                }
                else if (Helper.deliveryArrived == false) {
                    recognizeBeacon();
                }
                else if (Helper.deliveryAccepted == true){
                    Log.i(TAG,"I am sleeping as delivery is accepted");
                    Thread.sleep(60000);
                    Helper.disableConfirmButton = true;
                    Helper.deliveryAccepted = false;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void recognizeBeacon(){
        if (!BluetoothUtils.isBluetoothEnabled()) {
            Log.i(TAG,"Bluetooth is not Enabled");
            checkBluetoothEnable();
            checkBLESupportability();
            } else {
            Log.i(TAG,"Going to recognize the Beacon");
            initializeScan();
        }

    }

    private void initializeScan() {
        Helper.deliveryArrived = false;
        Log.i(TAG, " I am in intializescan");
        proximityManager.initializeScan(scanContext, new OnServiceReadyListener() {
            @Override
            public void onServiceReady() {
                Log.i(TAG, "I am in OnserviceReady");
                proximityManager.attachListener(KeepMeRunningService.this);
            }

            @Override
            public void onConnectionFailure() {
                Log.i(TAG, "I didn't find the beacon and in Connection failure");
                Helper.deliveryArrived = false;
            }
        });
    }

    public void checkBluetoothEnable(){
        Log.i(TAG, "I am in checkBTEnable");
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Log.i(TAG,"Bluetooth is not enabled");
            Helper.deliveryArrived = false;
            /*Toast.makeText(this, "Bluetooth is not enabled", Toast.LENGTH_SHORT).show();*/
        } else {
            Log.i(TAG, "Bluetooth is  enabled");
        }
    }

    public void checkBLESupportability(){
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            /*Toast.makeText(this, "BLE is not supported", Toast.LENGTH_SHORT).show();*/
            Log.i(TAG,"BLE is not supported");
        }
        else {
            Log.i(TAG,"BLE is supported");
        }
    }

}

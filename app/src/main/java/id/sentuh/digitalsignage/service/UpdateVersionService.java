package id.sentuh.digitalsignage.service;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.format.Formatter;
import android.util.Log;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.text.DecimalFormat;
import java.util.List;

import id.sentuh.digitalsignage.MainActivity;
import id.sentuh.digitalsignage.R;
import id.sentuh.digitalsignage.app.AppUtils;
import id.sentuh.digitalsignage.app.Configurate;
import id.sentuh.digitalsignage.app.GPSTracker;
import id.sentuh.digitalsignage.models.WifiList;
import id.sentuh.digitalsignage.models.WifiList_Table;

/**
 * Created by sony on 2/19/2018.
 */

public class UpdateVersionService extends Service {
    private static String TAG = "Service Update";
//    Handler mHandler;
    GPSTracker tracker;
    private int NOTIFY_ID=99880088;
//    private int INTERVAL = 60000;
    private NotificationManager notificationMgr;
    List<ScanResult> wifiList;
    private WifiManager wifi;
    private WifiInfo wifiInfo;
    private String MAC_ADDRESS;
    private String IP_LOCAL;
    private String DEVICE_ID;
    Configurate config;
//    UsbManager mUsbManager;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onCreate() {
        super.onCreate();
        config = new Configurate(this);
//        layoutId = config.getPageId();
        notificationMgr =(NotificationManager)getSystemService(
                NOTIFICATION_SERVICE);
//        mHandler = new Handler();
//        startTime = System.currentTimeMillis();

        Log.d(TAG,"start service lookup version");
        wifi = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
        wifiInfo = wifi.getConnectionInfo();
        MAC_ADDRESS = wifiInfo.getMacAddress();
        IP_LOCAL = Formatter.formatIpAddress(wifiInfo.getIpAddress());
        registerReceiver(mWifiReceiver,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifi.startScan();
        DEVICE_ID = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
//        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
//        IntentFilter filter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED);
//        registerReceiver(mUsbAttachReceiver , filter);
//        filter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED);
//        registerReceiver(mUsbDetachReceiver , filter);
        tracker = new GPSTracker(this);
//        Location loc = tracker.getLocation();
        int template_id=config.getPageId();
        Log.d(TAG,"Mac address : "+MAC_ADDRESS+","+IP_LOCAL);
        if(tracker.canGetLocation()){
            float lat=(float)tracker.getLatitude();
            float lng = (float)tracker.getLongitude();
            DecimalFormat dc = new DecimalFormat("#,###.#####");
            Log.d(TAG,"device location : "+dc.format(lat)+", "+dc.format(lng));
            if(AppUtils.isInternetAvailable(this)){
                AppUtils.registerDevice(UpdateVersionService.this,DEVICE_ID,lat,lng,"",IP_LOCAL,MAC_ADDRESS,template_id);
            }

        } else {
            Log.d(TAG,"cannot get location");
            if(AppUtils.isInternetAvailable(this)){
                AppUtils.getIpAddrLatLon(UpdateVersionService.this,MAC_ADDRESS,IP_LOCAL);
            }
        }

    }
//    BroadcastReceiver mUsbDetachReceiver = new BroadcastReceiver() {
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//
//            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
//                UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
//                if (device != null) {
//                    // call your method that cleans up and closes communication with the device
//                    Log.d(TAG,"device name : "+device.getDeviceName());
//                }
//            }
//        }
//    };
//    BroadcastReceiver mUsbAttachReceiver = new BroadcastReceiver() {
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            Log.d(TAG,"Usb detected action : "+action);
//            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
//                showDevices();
//                Log.d(TAG,"Usb detected...");
//                //checkVersion();
//            }
//        }
//    };
//    private void showDevices(){
//        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
//        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
//        StringBuilder mInfo = new StringBuilder();
//        while(deviceIterator.hasNext()){
//            UsbDevice device = deviceIterator.next();
//            if(device.getDeviceName().contains("usb")){
//                checkVersion();
//
//            }
//            mInfo.append("Device Name:"+device.getDeviceName() + "\n");
//            mInfo.append("Device ID:"+device.getDeviceId() + "\n");
//            mInfo.append("Protocol:"+device.getDeviceProtocol() + "\n");
//            mInfo.append("Product ID:"+device.getProductId() + "\n");
//            mInfo.append("Vendor ID:"+device.getVendorId() + "\n");
//        }
//        Log.d("usb", "device name: " + mInfo.toString());
//    }
//    private void checkVersion(){
//
//        Intent intent = new Intent(this,UpdateUSBActivity.class);
////        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//
//    }
    @Override
    public void onDestroy() {
        super.onDestroy();

//        mHandler.removeCallbacks(mRunnableUpdateVersion);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    @SuppressLint("WrongConstant")
    private  void loadMain(Context context){

        Intent intent = new Intent(context,MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);//FLAG_ACTIVITY_CLEAR_TOP|FLAG_ACTIVITY_SINGLE_TOP
    }
    BroadcastReceiver mWifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            wifiList=wifi.getScanResults();
            for(ScanResult result:wifiList){
                Log.d(TAG,"ssid name :"+result.SSID);
                WifiList dbwifi=SQLite.select().from(WifiList.class).where(WifiList_Table.ssid_name.eq(result.SSID)).querySingle();
                if(dbwifi==null){
                    dbwifi = new WifiList();
                    dbwifi.ssid_name = result.SSID;
                    dbwifi.save();
//                    Log.d(TAG,"ssid name :"+result.SSID+" saved");
                }
            }
        }
    };
    private void displayNotificationMessage(String message)
    {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Sentuh Digitan Signage")
                        .setContentText(message);
        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        notificationMgr.notify(NOTIFY_ID, mBuilder.build());
    }
}

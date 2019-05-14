package id.sentuh.digitalsignage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONObject;

import java.io.File;

import id.sentuh.digitalsignage.app.AppUtils;
import id.sentuh.digitalsignage.app.Configurate;
import id.sentuh.digitalsignage.app.EndPoints;
import id.sentuh.digitalsignage.helper.CheckConfig;
import id.sentuh.digitalsignage.helper.DBHelper;
import id.sentuh.digitalsignage.helper.InsertFileToDB;
import id.sentuh.digitalsignage.helper.OnPageFinishListener;

public class MainActivity extends FragmentActivity implements OnPageFinishListener{
    private static String TAG = "Main";
    private ProgressBar progressBar;
    private TextView caption;
    //    private static final String ACTION_USB_PERMISSION =
//        "id.sentuh.digitalsignage.USB_PERMISSION";
    Handler mHandler;
    //    UsbManager mUsbManager;
    Configurate config;
    int app_version;
    //    String zipFile;
    String fileSthxPath;
    boolean configExist = false;
    ProgressDialog dialogProgress;

    PendingIntent permissionIntent;
    TextView text1;
    VideoView videoView;
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent().getExtras() != null
                && getIntent().getExtras().getBoolean("CloseApp", false)) {
            finish();
        } else {
//            View decorView = getWindow().getDecorView();
//            AppUtils.hideSystemUI(decorView);
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_main);


//            ImageView imageView = findViewById(R.id.imageView2);
//
//            Glide.with(this)
//                    .asGif()
//                    .load(R.raw.logoreveal)
//                    .into(imageView);
//            videoView = (VideoView) findViewById(R.id.video_screen);
//            Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video);
//
//            videoView.setVideoURI(video);
//            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mp) {
//                startActivity(new Intent(MainActivity.this, ScreenActivity.class));
//                finish();
//                }
//            });
//            videoView.setZOrderOnTop(true);
//            videoView.start();
//            text1 = findViewById(R.id.caption);
//            progressBar = findViewById(R.id.progressLoad);
//            caption = findViewById(R.id.caption);
//            caption.setSelected(true);
//        readConfig();
            mHandler=new Handler();
            config = new Configurate(this);
            File dir = new File(EndPoints.STORAGE_DATA_PATH);
            if(!dir.exists()){
                dir.mkdirs();
                Toast.makeText(this,"Create Data DIR",Toast.LENGTH_SHORT).show();
            }
            DBHelper.clearDB(this);
            try {
                Thread.sleep(2000);
            } catch (Exception ex){

            }
            int pid = android.os.Process.myPid();
            Log.d(TAG," ds process id : "+Integer.toString(pid));
//        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
//        IntentFilter filter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED);
//        registerReceiver(mUsbAttachReceiver , filter);
//        filter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED);
//        registerReceiver(mUsbDetachReceiver , filter);
//        permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);

            int MyVersion = Build.VERSION.SDK_INT;
            if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
                if (!checkIfAlreadyhavePermission()) {
                    requestForSpecificPermission();
                } else {
                    chekDB();
                }
            } else {
                if(AppUtils.checkConfig()){
                    Log.d(TAG,"data ada "+dir.getAbsolutePath());

                    chekDB();
                } else {
                    fileSthxPath = AppUtils.getLocalZipFile();
                    try {
                        Thread.sleep(3000);
                    } catch (Exception ex){

                    }
                    if(fileSthxPath!=null){
                        File zip = new File(fileSthxPath);
                        if(zip.exists()){

//                        new ExtractDataFile(this,fileSthxPath,EndPoints.STORAGE_DATA_PATH).execute();
                            new CheckConfig(MainActivity.this,EndPoints.ZIP_DEST_FILE).execute();
                        } else {
//                        showStorages();
                            checkUSBVersion();
                        }
                    } else {
//                    showStorages();
                        checkUSBVersion();
                    }

                }
            }
        }



    }



    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // ignore orientation/keyboard change
        super.onConfigurationChanged(newConfig);
    }
    BroadcastReceiver mUsbAttachReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {

                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device != null) {
//                    showStorages();
                }

            }
        }
    };
    BroadcastReceiver mUsbDetachReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device != null) {
                    // call your method that cleans up and closes communication with the device
//                    Log.d(TAG,"Detach usb : "+device.getDeviceName());
                }
            }
        }
    };
    private void loadScreen(){

        Intent intent = new Intent(this,ScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
    private void checkUSBVersion(){
        Toast.makeText(this,"Periksa data USB",
                Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,UpdateUSBActivity2.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void downloadFromInternet(){
        if(AppUtils.isInternetAvailable(this)){
            String urldl =  config.getDownloadUrl();
            if(urldl.contains("http")){
                Log.d(TAG,"data download from server "+urldl);
                AppUtils.downloadZip(urldl,this);
            } else {
                Log.e(TAG,"url not found");
            }
        } else {
            Toast.makeText(this,"Internet not available",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void readConfig(){
        File file = new File(EndPoints.CONFIG_FILE_PATH);
        if(file.exists()){

            String content = AppUtils.openTextFile(file);
            try {
                JSONObject json = new JSONObject(content);
                int id = json.getInt("id");
                int version = json.getInt("version");
                String password = json.getString("password");
                String server = json.getString("server");
                if(!json.isNull("serialno")){
                    String serialno = json.getString("serialno");
                    config.setSerialNo(serialno);
                }

                String url_download = EndPoints.DOWNLOAD_URL.replace("_ID_",Integer.toString(id));
                Configurate config = new Configurate(this);
                int oldPageId = config.getPageId();
                if(id!=oldPageId){
                    Log.d(TAG,"version difference");
                }
                config.setServerUrl(server);
                config.setPageId(id);
                config.setPagePassword(password);
                config.setVersion(version);
                config.setDownloadUrl(url_download);

            } catch (Exception ex){
                Log.e(TAG,"Error"+ex.getMessage());
            }
            configExist = true;
            new InsertFileToDB(this).execute();
        } else {

            Toast.makeText(this,"Data Not Found!",
                    Toast.LENGTH_SHORT).show();
            file = new File(EndPoints.STORAGE_DATA_PATH);
            if(!file.exists()){
                file.mkdirs();
            }
        }
    }
    //    Runnable mWaitingforUsbData = new Runnable() {
//        @Override
//        public void run() {
//                  File file = new File(EndPoints.ZIP_USB_FILE);
//                  if(file.exists()){
//                      new CopyAllData(MainActivity.this,
//                              file.getAbsolutePath()).execute();
//                      mHandler.removeCallbacks(mWaitingforUsbData);
//                  } else {
//                      mHandler.postDelayed(this,2000);
//                  }
//        }
//    };
    private void startService(){
//        Intent intent = new Intent(this,UpdateVersionService.class);
//        startService(intent);
//        AppUtils.getIpAddrLatLon(this);

    }
    public void chekDB(){

//        Log.d(TAG,"Load config...");
        readConfig();
//        Log.d(TAG,"Load service...");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new InsertFileToDB(MainActivity.this).execute();
            }
        },2000);
//        Log.d(TAG,"Load database...");
//        startService();

    }


    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
        }, 101);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                    chekDB();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    public void OnPageFinish(int pager, boolean PageChanged) {
        Log.d(TAG,"load view "+Integer.toString(pager));
//        Intent intent = new Intent(MainActivity.this,ScreenRelativeActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putInt(ScreenRelativeActivity.VIEW_ID,pager);
//        intent.putExtras(bundle);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }



    /**
     * Handler of incoming messages from service.
     */

    @Override
    protected void onDestroy() {

//        unregisterReceiver(mUsbDetachReceiver);
//        unregisterReceiver(mUsbAttachReceiver);

        super.onDestroy();
    }

}

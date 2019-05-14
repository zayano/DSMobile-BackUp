package id.sentuh.digitalsignage;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.mjdev.libaums.fs.UsbFile;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import id.sentuh.digitalsignage.app.AppUtils;
import id.sentuh.digitalsignage.app.Configurate;
import id.sentuh.digitalsignage.app.EndPoints;
import id.sentuh.digitalsignage.fragment.ScreenFragment;
import id.sentuh.digitalsignage.helper.DBHelper;
import id.sentuh.digitalsignage.helper.DeviceBox;
import id.sentuh.digitalsignage.helper.OnDialogListener;
import id.sentuh.digitalsignage.helper.OnPageFinishListener;
import id.sentuh.digitalsignage.helper.UsbDeviceFilter;
import id.sentuh.digitalsignage.helper.WifiSettingDialog;
import id.sentuh.digitalsignage.models.MIBtree;
import id.sentuh.digitalsignage.models.Popups;
import id.sentuh.digitalsignage.models.Popups_Table;
import id.sentuh.digitalsignage.models.Views;
import id.sentuh.digitalsignage.service.AgentService;

public class ScreenActivity extends FragmentActivity
        implements OnPageFinishListener,OnDialogListener{
    Dialog myDialog;
    private static String TAG="Screen";
    Messenger mService = null;
    boolean mIsBound;
    AudioManager audioMgr;
    Configurate config;
    boolean popupShow=false;
    UsbManager mUsbManager;
    public static FragmentTransaction fragmentTransaction;
    private static final String ACTION_USB_PERMISSION = "id.sentuh.digitalsignage.USB_PERMISSION";
    public static ScreenFragment screenFragment;
    public static FragmentManager fragmentManager;
    boolean showQrCode;
    boolean click = true;
    FrameLayout frameLayout;
    PendingIntent permissionIntent;
    String fileSthxPath;

    @SuppressLint("WrongConstant")
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent().getExtras() != null
                && getIntent().getExtras().getBoolean("CloseApp", false)) {
            finish();
        }
        View decorView = getWindow().getDecorView();
        AppUtils.hideSystemUI(decorView);
        showQrCode = false;
        config = new Configurate(this);
        audioMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        IntentFilter filter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        registerReceiver(mUsbAttachReceiver , filter);
        filter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbDetachReceiver , filter);
        setVolumeControlStream(AudioManager.STREAM_SYSTEM);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_screen);
        List<Views> viewsList= DBHelper.getViews();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
//        if(viewsList.size()>1){
//            int idx = viewsList.size()-1;
//            screenFragment = ScreenFragment.newInstance(idx);
//        } else {
//
//        }
        screenFragment = ScreenFragment.newInstance(0);
        fragmentTransaction.replace(R.id.screenLayout,screenFragment).commit();
        permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
//        Intent intent = new Intent(this, AgentService.class);
//        startService(intent);
//        doBindAgentService();
        readConfig();
        mHandler = new Handler();
        mHandler.postDelayed(mRunnablePopup,1000);
        myDialog = new Dialog(this);
    }

        private void SleepDisplay() {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
            params.screenBrightness = -1;
            getWindow().setAttributes(params);
        }

        private void WakeDisplay() {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
            params.screenBrightness = 1;
            getWindow().setAttributes(params);
        }

        void killApp(){
            Intent CloseInt = new Intent(this, MainActivity.class);
            CloseInt.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            CloseInt.putExtra("CloseApp", true);
            startActivity(CloseInt);
            finish();
        }

    private void CloseApp(){

    }
//    void shutDown(){
//        try {
//            String commands[] = {"-c","reboot -p"};
//            CommandResult result = Shell.SU.run(commands);
//            if(result.isSuccessful()){
//                Toast.makeText(ScreenActivity.this,result.getStdout(),Toast.LENGTH_SHORT).show();
//            }
//        } catch (Exception ex){
//            Log.e(TAG,"error killing : "+ex.getMessage());
//        }
//
//    }
    void reBoot(){
        try {
            String commands[] = {"-c","reboot"};
//            CommandResult result = Shell.SU.run(commands);
//            if(result.isSuccessful()){
//                Toast.makeText(ScreenActivity.this,result.getStdout(),Toast.LENGTH_SHORT).show();
//            }
        } catch (Exception ex){
            Log.e(TAG,"error killing : "+ex.getMessage());
        }

    }

//   public void ShowPopup(View view) {
//        TextView txtclose;
//        myDialog.setContentView(R.layout.popup);
//        txtclose = myDialog.findViewById(R.id.txtclose);
//        txtclose.setText("M");
//        txtclose.setOnClickListener(new View.OnClickListener() {
//           @Override
//           public void onClick(View v) {
//               myDialog.dismiss();
//           }
//      });
//        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        myDialog.show();
//    }

    @SuppressLint("HandlerLeak")
    class IncomingHandler extends Handler {
        @Override public void handleMessage(Message msg) {
            switch (msg.what) {
                case AgentService.MSG_SET_VALUE:
                    break;

                case AgentService.MSG_SNMP_REQUEST_RECEIVED:
                    Log.d(TAG,"Requesst SNMP : "+AgentService.lastRequestReceived);
//                    new MessageBox(ScreenActivity.this,
//                            "Request",AgentService.lastRequestReceived).show();

                    break;

                case AgentService.MSG_MANAGER_MESSAGE_RECEIVED:
                    MIBtree miBtree = MIBtree.getInstance();
                    String message = miBtree.getNext(MIBtree.MNG_MANAGER_MESSAGE_OID).getVariable().toString();
//                    new MessageBox(ScreenActivity.this,
//                            "Message",message).show();
                    Toast.makeText(ScreenActivity.this,message,Toast.LENGTH_SHORT).show();

                    if(message.equals("sleep")){
                        SleepDisplay();
                    }
                    if(message.equals("wake")){
                        WakeDisplay();
                    }
                    if(message.equals("volup")) {
                        increseaseVolume();
                    }
                    if(message.equals("voldown")) {
                        decreaseVolume();
                    }
                    if(message.equals("stop")) {
                        killApp();
                        finishAffinity();
                        System.exit(0);
                    }
                    if(message.equals("shutdown")){
                        shutDown();
                    }
                    if(message.equals("restart")){
                        reBoot();
                    }


                    if(message.equals("uninstall")){
                        killApp();

//                        AppUtils.silentUnInstall();
                    }
                    if(message.equals("install")){

//                        AppUtils.installAPK(MainActivity.this);
//                        AppUtils.silentUnInstall();
                    }
                    Toast.makeText(ScreenActivity.this,message.toUpperCase(),Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"Message receive : "+message);
                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    }

    private void shutDown(){
        Log.d("SHUT","shutdown");
        android.os.Process.killProcess( android.os.Process.myPid() );
        try {
            Intent i = new Intent(Intent.ACTION_SHUTDOWN);
            i.putExtra("nowait", 1);
            i.putExtra("interval", 1);
            i.putExtra("window", 0);
            sendBroadcast(i);
        } catch (Exception ex){
            Log.e(TAG,"shutdown error : "+ex.getMessage());
        }
    }

    private void sendMessageToAgentService(Message msg){
        try {
            mService.send(msg);
        } catch (RemoteException e) {

        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {

            mService = new Messenger(service);

            // We want to monitor the service for as long as we are
            // connected to it.
            try {
                Message msg = Message.obtain(null,
                        AgentService.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);

                // Give it some value as an example.
                msg = Message.obtain(null,
                        AgentService.MSG_SET_VALUE, this.hashCode(), 0);
                mService.send(msg);
            } catch (RemoteException e) {

            }

        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
        }
    };

    void doBindAgentService() {
        // Establish a connection with the service.  We use an explicit
        // class name because there is no reason to be able to let other
        // applications replace our component.
        bindService(new Intent(this, AgentService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindAgentService() {
        if (mIsBound) {
            // If we have received the service, and hence registered with
            // it, then now is the time to unregister.
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null,
                            AgentService.MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                } catch (RemoteException e) {

                }
            }

            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
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

//            new InsertFileToDB(this).execute();
        } else {
            AppUtils.loadMain(this);

        }
    }

    @Override public void onConfigurationChanged(Configuration newConfig) {
        // ignore orientation/keyboard change
        super.onConfigurationChanged(newConfig);
    }

    private void checkUSBVersion(){
//        if(AppUtils.checkConfig()){
//            new BackupOldData(this).execute();
//        }
        Intent intent = new Intent(this,UpdateUSBActivity2.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    BroadcastReceiver mUsbAttachReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device != null) {
                    Log.d(TAG," item detected :");
                    boolean matches;
                    XmlResourceParser vendors = context.getResources().getXml(R.xml.device_filter);
                    try {
                        UsbDeviceFilter filter = new UsbDeviceFilter(vendors);
                        matches=filter.matchesHostDevice(device);
                        if(matches){
                            checkUSBVersion();
                            Toast.makeText(getApplicationContext(),"MEREK USB DRIVE KOMPATIBEL",Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getApplicationContext(),"MEREK USB DRIVE TIDAK KOMPATIBEL",Toast.LENGTH_LONG).show();
                        }
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    @SuppressLint("WrongConstant")
//                    UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
//                    HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
//                    UsbDevice device1 = deviceList.get("deviceName");
//                    @SuppressLint("ResourceType")
//                    XmlResourceParser vendors = context.getResources().getXml(R.xml.device_filter);
//                    checkUSBVersion(vendors);
                }
            }
        }
    };

//    private void showStorages(){
//        Toast.makeText(ScreenActivity.this,"Checking USB Storage...",Toast.LENGTH_SHORT).show();
//        UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(getApplicationContext());
//        StringBuilder mInfo = new StringBuilder();
//        try {
//            if(devices.length>0) {
//                for(UsbMassStorageDevice device: devices) {
//                    // before interacting with a device you need to call init()!
//                    mUsbManager.requestPermission(device.getUsbDevice(), permissionIntent);
//                    // before interacting with a device you need to call init()!
//                    device.init();
//                    String name = device.getUsbDevice().getDeviceName();
//                    // Only uses the first partition on the device
//                    mInfo.append("Name : "+name+"\n");
//                    FileSystem currentFs = device.getPartitions().get(0).getFileSystem();
//                    UsbFile root = currentFs.getRootDirectory();
//                    UsbFile[] files = root.listFiles();
//                    for(UsbFile file: files) {
//                        if(file.isDirectory()) {
//                            mInfo.append("Dirname : "+file.getName()+"\n");
//                        } else {
//                            mInfo.append("Filename : "+file.getName()+"\n");
//                            if(file.getName().contains(".sthx")){
//                                new CopyUsbToLocal(ScreenActivity.this,file).execute();
//                                return;
//                            }
//                        }
//                    }
//                    device.close();
//                }
//            } else {
//                Toast.makeText(ScreenActivity.this,"Data File not found",Toast.LENGTH_SHORT).show();
//            }
//
//        } catch (Exception ex){
//            Log.e(TAG,"Error usb : "+ex.getMessage());
//        }
//    }
    private static UsbFile getSTHX(UsbFile dir) throws IOException {
        UsbFile[] files = dir.listFiles();
        for(UsbFile file: files) {
            Log.d(TAG,"File usb : "+file.getName());
            if(!file.isDirectory()){
                if(file.getName().contains(".sthx")) {
                    return file;
                }
            }
        }
        return null;
    }

    @Override protected void onResume() {
        super.onResume();
        if(getIntent().getExtras() != null
                && getIntent().getExtras().getBoolean("CloseApp", false)) {
            finish();
        }
    }

        //Volume Control
        private void showCurrentVolume(){
            float cv = audioMgr.getStreamVolume(AudioManager.STREAM_SYSTEM);
            float max = audioMgr.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
            float percent = cv /max;
            DecimalFormat df = new DecimalFormat("'Volume' #,###.###");
            String volume = df.format(percent);//"Volume : "+Float.toString(cv)+" from "+Float.toString(max);
            Toast.makeText(this,volume,Toast.LENGTH_SHORT).show();
        }

        private void decreaseVolume() {
            audioMgr.adjustStreamVolume(AudioManager.STREAM_SYSTEM,AudioManager.ADJUST_LOWER,AudioManager.FLAG_SHOW_UI);
            showCurrentVolume();
        }

        private void increseaseVolume() {
            audioMgr.adjustStreamVolume(AudioManager.STREAM_SYSTEM,AudioManager.ADJUST_RAISE,AudioManager.FLAG_SHOW_UI);
            showCurrentVolume();
        }

    @SuppressLint("RestrictedApi")
    @Override public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        if (event.getAction()!=KeyEvent.ACTION_DOWN)
            return true;
//        Log.d(TAG, "Key action " + action);
        Log.d(TAG, "Key up, code " + event.getKeyCode());
        switch (keyCode) {

            case KeyEvent.KEYCODE_DPAD_LEFT:
//                audioMgr.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);

                decreaseVolume();
                return true;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
//                audioMgr.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                increseaseVolume();
//
                return true;
            case KeyEvent.KEYCODE_F8:
                //Toast.makeText(this,"Sentuh Digital Signage",Toast.LENGTH_SHORT).show();
                if(screenFragment!=null){
                    if(!showQrCode){
                        screenFragment.setShowQrCode(true);
                        showQrCode = true;
                    } else {
                        screenFragment.setShowQrCode(false);
                        showQrCode = false;
                    }

                }
                return true;
            case KeyEvent.KEYCODE_MOVE_HOME:

//            case 122://HOME Key

                WifiSettingDialog dialog = new WifiSettingDialog(this);
                dialog.show();
                return true;
            case KeyEvent.KEYCODE_BACK:
                onBackPressed();
                return true;
            case KeyEvent.KEYCODE_POWER:
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }


    private Runnable mRunnableUpdateTime = new Runnable(){

        @Override public void run() {
            Date tanggal = new Date(System.currentTimeMillis());

            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss \ndd-MM-yyyy");

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

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

        int currentPosition = 0;

        @Override
        public void onPageSelected(int newPosition) {


            currentPosition = newPosition;
        }

        @Override public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        public void onPageScrollStateChanged(int arg0) {

        }
    };

    Runnable mRunnablePopup = new Runnable()
    {
        @Override public void run()
        {
            Date tanggal = new Date(System.currentTimeMillis());

            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
//            long now = System.currentTimeMillis();
            String jam_menit = dateFormat.format(tanggal);
            if(jam_menit.charAt(0)=='0'){
                jam_menit=jam_menit.substring(1,5);
            }
//            String[] jams = jam_menit.split(":");
//
//            Log.d(TAG,"jam Popup "+dateFormat.format(tanggal)+" waktu:"+jam_menit);

//            int[] popmints = new int[popups.size()];
//            for(int i=0;i<popups.size();i++){
                Popups currentPopup = SQLite.select()
                        .from(Popups.class)
                        .where(Popups_Table.time.eq(jam_menit)).querySingle();


//                popmints[i] = Integer.parseInt(times[0]) * 60 + Integer.parseInt(times[1]);
//


                if(currentPopup!=null){
                    Log.d(TAG,"Jam Popup Exist");

                    String waktu = currentPopup.getTime();
                    String[] times = waktu.split(":");
                    Log.d(TAG,"jam Popup "+dateFormat.format(tanggal)+" waktu:"+waktu);
                    String filePath = EndPoints.RESOURCE_PATH+File.separator+currentPopup.getFile_name();
                    if(!popupShow){
//
                        if(currentPopup.getFile_name().contains(".jpg")||
                                currentPopup.getFile_name().contains(".jpeg")||
                                currentPopup.getFile_name().contains(".png")){
                            if(popupDialog==null){
                                popupDialog = new Dialog(ScreenActivity.this, R.style.DialogTheme);
                                popupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                popupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                popupDialog.setContentView(R.layout.image_dialog);
                            }
                            ImageView imageView= popupDialog.findViewById(R.id.image_dialog1);
                            File file = new File(filePath);
                            Uri imageUri = Uri.fromFile(file);
                            if(file.exists()){

                                Glide.with(getApplicationContext())
                                        .load(imageUri)
                                        .apply(new RequestOptions()
                                                .override(1000,1000)
//                                                .override(1000,1000)
                                                .fitCenter()
                                                .error(R.drawable.pagenotfound))
                                        .into(imageView);

//                                Picasso.with(getApplicationContext())
//                                        .load(imageUri)
//                                        .resize(900,900)
//                                        .error(R.drawable.pagenotfound)
//                                        .into(imageView);
                            }
                                mHandler.postDelayed(mPopupHide,20000);
                                popupDialog.show();
                                popupShow = true;


                        }
//                        if(currentPopup.getFile_name().contains(".txt")){
//                            String content = storage.readTextFile(filePath);
//                            TextDialogFragment textDialogFragment = TextDialogFragment.newInstance(content);
//                            textDialogFragment.show(fragmentTransaction,"popup");
//                        }
//                        if(currentPopup.getFile_name().contains(".website")){
//                            String content = storage.readTextFile(filePath);
//                            HtmlDialogFragment htmlDialogFragment = HtmlDialogFragment.newInstance(content);
//                            htmlDialogFragment.show(fragmentTransaction,"popup");
//                        }
                        if(currentPopup.getFile_name().contains(".mp4")){
                            if(popupDialog==null) {
                                popupDialog = new Dialog(ScreenActivity.this, R.style.DialogTheme);
                                popupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                popupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                popupDialog.setContentView(R.layout.video_popup);
                            }
//                                popupDialog.setOnKeyListener((DialogInterface.OnKeyListener) getApplicationContext());
                                VideoView videoView = popupDialog.findViewById(R.id.video_dialog);
                                MediaController controller = new MediaController(getApplicationContext());
                                File file = new File(filePath);
                                Uri videoUri = Uri.fromFile(file);
                                if(file.exists()){
                                controller.setMediaPlayer(videoView);
                                videoView.setVideoURI(videoUri);
                                videoView.setMediaController(controller);
                                videoView.requestFocus();
                                videoView.start();
//                                }
                            }
                            mHandler.postDelayed(mPopupHide, 60000);
                            popupDialog.show();
                            popupShow = true;
//                            VideoDialog videoDialogFragment = VideoDialogFragment.newInstance(filePath);
//                            videoDialogFragment.show(fragmentTransaction,"popup");
                        }

                    }


                } else {
                   popupShow = false;
                }

//            }
            mHandler.postDelayed(mRunnablePopup,1000);

        }
    };
    private Dialog popupDialog;
    private Handler mHandler;

    Runnable mPopupHide = new Runnable() {
        @Override public void run() {
            //
            if(popupDialog!=null){
                popupDialog.dismiss();
            }
            //popupShow = false;
            mHandler.removeCallbacks(this);
        }
    };

    private void handleSendDangerAlert() {
        Message msg = Message.obtain(null,
                AgentService.MSN_SEND_DANGER_TRAP);
        msg.replyTo = mMessenger;
        sendMessageToAgentService(msg);
    }

    @Override protected void onStop() {
        super.onStop();

    }

    @Override protected void onDestroy() {
        unregisterReceiver(mUsbDetachReceiver);
        unregisterReceiver(mUsbAttachReceiver);
        doUnbindAgentService();
        super.onDestroy();
    }

    @Override public void onBackPressed() {
//        super.onBackPressed();
//        MessageBox alert = new MessageBox(this,"Warning","Sure To Exit Program?");
//        alert.showAndClose(ScreenActivity.this);
        DeviceBox dialog=new DeviceBox(this);
        dialog.show();
    }

        private void checkAPK(){
            PackageManager pm = getPackageManager();
            int app_version = AppUtils.getPackageVersion("id.sentuh.digitalsignage",pm);
            Log.d(TAG,"app version : "+Integer.toString(app_version));
        }

    @Override public void OnPageFinish(int pager, boolean PageChanged) {
        Log.d(TAG,"pager index : "+Integer.toString(pager));

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.detach(screenFragment);
        screenFragment = ScreenFragment.newInstance(pager);
        fragmentTransaction.replace(R.id.screenLayout,screenFragment,"Add");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override public void OnDialogClose(boolean started) {
        if(started){
            mHandler.postDelayed(mRunnablePopup,1000);
            popupShow = false;
        }
    }

}

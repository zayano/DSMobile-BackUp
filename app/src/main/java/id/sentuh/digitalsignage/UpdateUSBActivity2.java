package id.sentuh.digitalsignage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cunoraz.gifview.library.GifView;
import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.fs.UsbFileStreamFactory;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import id.sentuh.digitalsignage.app.AppUtils;
import id.sentuh.digitalsignage.app.Configurate;
import id.sentuh.digitalsignage.app.EndPoints;
import id.sentuh.digitalsignage.helper.CheckConfig;
import id.sentuh.digitalsignage.helper.CopyUsbToLocal;
import id.sentuh.digitalsignage.helper.ExtractDataFile;


public class UpdateUSBActivity2 extends Activity {
    private static String TAG="Update Version";
    private static int DELAY = 6000;
    private Handler mHandler;
    private GifView gifView1;
    private TextView note;
    private int counter=0;
    private int max_counter=3;
    Typeface fontDefault;
    Configurate config;
    UsbManager mUsbManager;
    PendingIntent permissionIntent;
    private static final String ACTION_USB_PERMISSION =
            "id.sentuh.digitalsignage.USB_PERMISSION";
   public static UsbFile usbFile;
   public static ProgressBar progressBar;

    @SuppressLint("WrongConstant")
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        AppUtils.hideSystemUI(decorView);
        setContentView(R.layout.activity_update);
        progressBar = findViewById(R.id.progressBar2);
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        IntentFilter filter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        registerReceiver(mUsbAttachReceiver , filter);
        filter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbDetachReceiver , filter);
        fontDefault = AppUtils.getFont(this,"OpenSans-Regular");
        mHandler = new Handler();
        gifView1 = findViewById(R.id.gif1);
        note = findViewById(R.id.note);
        note.setTypeface(fontDefault);
        gifView1.setVisibility(View.VISIBLE);
        gifView1.play();
//        disk = new Storage(this);

        config = new Configurate(this);
        permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);

        config.setKeyInt("reading",1);
//        stopService();
//        mHandler.postDelayed(mCheckingUsbStorage,1000);
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
        showStorages();
//            }
//        },1000);
    }

    BroadcastReceiver mUsbAttachReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device != null) {
                    showStorages();
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

    @Override protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mUsbDetachReceiver);
        unregisterReceiver(mUsbAttachReceiver);
    }

    private UsbFile showStorages(){
        UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(getApplicationContext());
        // StringBuilder mInfo = new StringBuilder();
        try {
            if(devices.length>0) {
//                UsbFile passwordFile=null;
//                UsbFile dataFile = null;
                for(UsbMassStorageDevice device: devices) {
                    mUsbManager.requestPermission(device.getUsbDevice(), permissionIntent);
                    // before interacting with a device you need to call init()!
                    device.init();

                    FileSystem currentFs = device.getPartitions().get(0).getFileSystem();
                    UsbFile root = currentFs.getRootDirectory();
                    UsbFile[] files = root.listFiles();

                    for(UsbFile file: files) {
                        if(!file.isDirectory()) {

//                            if(file.getName().contains("_password")){
//                                passwordFile = file;
//                                Log.d(TAG,"password file found");
//                            }
                            String ext=FilenameUtils.getExtension(file.getName());
                            if(ext.equals("sthx")){
//                                dataFile = file;
                                Log.d(TAG,"copy file "+file.getName());
//                                InputStream is = new UsbFileInputStream(file);
//                                File destFile = new File(EndPoints.ZIP_DEST_FILE);
//                                if(!destFile.exists()){
//                                    destFile.createNewFile();
//                                }
//
//                                FileUtils.copyInputStreamToFile(is,destFile);
//                                IOUtils.closeQuietly(is);

//
                                new CopyUsbToLocal(UpdateUSBActivity2.this,progressBar,file,
                                        EndPoints.ZIP_DEST_FILE,true).execute();
                                break;
                            }
                        }

//                        if((passwordFile!=null) && (dataFile!=null)) {
//                            break;
//                        }
                    }
//                    device.close();

                }
//                if(passwordFile!=null) {
//                    new CopyUsbToLocal(UpdateUSBActivity2.this,
//                            passwordFile,EndPoints.PASSWORD_FILE,false).execute();
//                    try {
//                        Thread.sleep(5000);
//                    } catch (Exception ex){
//
//                    }
//                    if(dataFile!=null){
//
//                    }
//                } else {
//                    Toast.makeText(UpdateUSBActivity2.this,"Passoword file not found",Toast.LENGTH_SHORT).show();
//                    finish();
//                }



            } else {
                Toast.makeText(UpdateUSBActivity2.this,"Data file not found",Toast.LENGTH_SHORT).show();
                finish();
            }

        } catch (Exception ex){
            Log.e(TAG,"Error : "+ex.getMessage());
        }
        return null;
    }

    private static UsbFile getSTHX(UsbFile dir) throws IOException{
        UsbFile[] files = dir.listFiles();
        for(UsbFile file: files) {
            Log.d(TAG,"File usb : "+file.getName());
            if(file.isDirectory()){
                return getSTHX(file);
            } else {
                if(file.getName().contains(".sthx")) {
                    return file;
                }
            }


        }
        return null;
    }

    private class ReadUsbFileContent extends AsyncTask<UsbFile,Void,String> {
        FileSystem currentFs;
        public ReadUsbFileContent(FileSystem fs){
            this.currentFs = fs;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override protected String doInBackground(UsbFile... usbFiles) {
            UsbFile file = usbFiles[0];
            try {
                InputStream is = UsbFileStreamFactory.createBufferedInputStream(file,currentFs);
                byte[] buffer=new byte[currentFs.getChunkSize()];
                is.read(buffer);
                String content = IOUtils.toString(is,StandardCharsets.UTF_8);
                return content;
            } catch (Exception ex){
                return  null;
            }

        }

        @Override protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG,"content usb:"+s);
        }
    }

    @SuppressLint("SetTextI18n")
    private void readConfigUsb(final String filePath){

        File file = new File(filePath);
        Log.d(TAG,"file config : "+file.getAbsolutePath());
        if(file.exists()){
            note.setText("Membaca Konfigurasi ...");
            //new CopyAllData(this,filePath,"/sdcard/tmp",false).execute();
            new CheckConfig(UpdateUSBActivity2.this,filePath).execute();
        } else {
            Toast.makeText(this,"Mencoba sinkronisasi!", Toast.LENGTH_SHORT).show();
            note.setText("Sinkronisasi Flash storage ...");
            mHandler.postDelayed(mCheckingUsbStorage,DELAY);
        }
    }

    private void backToScreen(){
        mHandler.postDelayed(new Runnable() {
            @Override public void run() {
                finish();
            }
        },2000);
    }

    private Runnable mCheckingUsbStorage = new Runnable() {
        @Override public void run() {
            usbFile=showStorages();
            if(usbFile!=null){
                Toast.makeText(UpdateUSBActivity2.this,"Clear Temporary ...", Toast.LENGTH_SHORT).show();
                mHandler.removeCallbacks(mCheckingUsbStorage);
//                    new ExtractDataFile(UpdateUSBActivity.this,file.getAbsolutePath()).execute();
                //new DestroyTempDir(UpdateUSBActivity2.this,fileSthxPath).execute();
                new CopyUsbToLocal(UpdateUSBActivity2.this,progressBar,usbFile,EndPoints.ZIP_DEST_FILE,true).execute();
            } else {
                if(counter<max_counter){
                    counter++;
//                    Toast.makeText(UpdateUSBActivity.this,"Percobaan ke "+ Integer.toString(counter), Toast.LENGTH_SHORT).show();
                    Toast.makeText(UpdateUSBActivity2.this,"Masih mencoba membaca usb ...", Toast.LENGTH_SHORT).show();
                    mHandler.postDelayed(mCheckingUsbStorage,DELAY);
                } else {
                    mHandler.removeCallbacks(mCheckingUsbStorage);
                    Toast.makeText(UpdateUSBActivity2.this,"Data USB Tidak teridentifikasi", Toast.LENGTH_SHORT).show();
                    mHandler.postDelayed(new Runnable() {
                        @Override public void run() {
                            loadMain();
                        }
                    },2000);

                }


            }

        }
    };

    @SuppressLint("WrongConstant")
    private void loadMain(){
        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
//        startBgService();
        finish();
    }

    private class CopyingNewData extends  AsyncTask<Void,Void,Void>{
        Activity mContext;
        String filePath;
        public CopyingNewData(Activity context, String file_path){
            this.mContext = context;
            this.filePath = file_path;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            gifView1.setVisibility(View.VISIBLE);
            gifView1.play();
        }

        @Override protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            gifView1.play();
            Toast.makeText(mContext,"Copying File to local!", Toast.LENGTH_SHORT).show();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    loadMain();
                    new ExtractDataFile(mContext,filePath,EndPoints.STORAGE_DATA_PATH).execute();
                }
            },2000);
        }
    }

    private class DestroyOldData extends AsyncTask<Void,Void,Void> {

        Activity mContext;
        String filePath;
        public DestroyOldData(Activity context, String file_path){
            this.mContext = context;
            this.filePath = file_path;
        }
        private void deleteRecursive(File fileOrDirectory) {
            if (fileOrDirectory.isDirectory())
                for (File child : fileOrDirectory.listFiles()) {

                    deleteRecursive(child);
                }
            Log.d(TAG,"delete file : "+fileOrDirectory.getName());
            fileOrDirectory.delete();
        }

        @Override protected Void doInBackground(Void... params) {
//            AppUtils.eraseAllLocalData(mContext);
//            gifView1.play();
//            Storage storage = new Storage(mContext);
            File dirs = new File(EndPoints.STORAGE_DATA_PATH);
            if(dirs.exists()){
                for(File dir:dirs.listFiles()) {
                    if (dir.getName().equals("Views") ||
                            dir.getName().equals("Resources") ||
                            dir.getName().equals("Events") ||
                            dir.getName().equals("Models")||
                            dir.getName().equals("Config")) {
                        Log.d(TAG, "delete dir : " + dir.getName());
//               List<File> files = storage.getFiles(dir.getAbsolutePath());
//               for(File file:files){
//                   storage.deleteFile(file.getAbsolutePath());
//               }
                        deleteRecursive(dir);
                    }
                }
            }else {
                new CopyUsbToLocal(UpdateUSBActivity2.this,progressBar,usbFile,
                        EndPoints.ZIP_DEST_FILE,true).execute();
            }

            return null;
        }

        @Override protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            gifView1.setVisibility(View.VISIBLE);
            gifView1.play();
        }

        @Override protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            gifView1.play();
            Toast.makeText(mContext,"Copying File to local!", Toast.LENGTH_SHORT).show();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    loadMain();
                    new ExtractDataFile(mContext,filePath,EndPoints.STORAGE_DATA_PATH).execute();
                }
            },2000);

        }
    }

//    public static byte[] generateKey(String password) throws Exception
//    {
//        byte[] keyStart =  password.getBytes("UTF-8");
//
//        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
//        @SuppressLint("DeletedProvider")
//        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG", "Crypto");
//        secureRandom.setSeed(keyStart);
//        keyGenerator.init(128,secureRandom);
//        SecretKey secretKey = keyGenerator.generateKey();
//
//        return secretKey.getEncoded();
//    }
//
//    public static byte[] encodeFile(byte[] key, byte[] fileData) throws Exception
//    {
//        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
//        Cipher cipher = Cipher.getInstance("AES");
//        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
//
//        byte[] encrypted = cipher.doFinal(fileData);
//
//        return encrypted;
//    }
//
//    public static byte[] decodeFile(byte[] key, byte[] fileData) throws Exception
//    {
//        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
//        Cipher cipher = Cipher.getInstance("AES");
//        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
//
//        byte[] decrypted = cipher.doFinal(fileData);
//
//        return decrypted;
//    }
//
//    File file = new File(Environment.getExternalStorageDirectory() + File.separator + EndPoints.STORAGE_BASE_PATH, "password.txt");
//    BufferedOutputStream bufferedOutputStream;
//
//    {
//        try {
//            bufferedOutputStream = new   BufferedOutputStream(new FileOutputStream(file));
//            byte[] yourKey = generateKey("password");
//            byte[] filesBytes = encodeFile(yourKey, yourByteArrayContainigDataToEncrypt);
//            byte[] fileBytes = decodeFile(yourKey, byteOfYourFile);
//            bufferedOutputStream.write(filesBytes);
//            bufferedOutputStream.write(fileBytes);
//            bufferedOutputStream.flush();
//            bufferedOutputStream.close();
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}

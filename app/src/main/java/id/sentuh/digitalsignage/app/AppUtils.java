package id.sentuh.digitalsignage.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.snatik.storage.Storage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import id.sentuh.digitalsignage.MainActivity;
import id.sentuh.digitalsignage.ScreenActivity;
import id.sentuh.digitalsignage.helper.DBHelper;
import id.sentuh.digitalsignage.models.FrameResources;
import id.sentuh.digitalsignage.models.Popups;
import ir.mahdi.mzip.zip.ZipArchive;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by user on 7/9/17.
 */

public abstract class AppUtils {
    private static String TAG = "App Utils";
    public static float convertPixelsToDp(Context context,float px){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }
    public static float convertDpToPixel(Context context,float dp){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }
    public static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
    public static Typeface getFont(final Context context,String font_name) {
        return Typeface.createFromAsset(context.getAssets(),"fonts/"+font_name+".ttf");
    }

    public static Date stringToDate(String date) throws ParseException {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sf.setLenient(true);
        Date hasil = sf.parse(date);

        return hasil;
    }
    public static JSONObject child;
    public static String dateToPrettyTime(String dt) throws ParseException {
        Date waktu = stringToDate(dt);
        PrettyTime pt = new PrettyTime();
        return pt.format(waktu);
    }
    public static void getIpAddrLatLon(final Context context, final String mac_address, final String ip_local){
        final Configurate config = new Configurate(context);

        @SuppressLint("HardwareIds")
        final String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        try {
            Log.d(TAG,"getting ip address and location...");
            AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
            client.get(EndPoints.GET_IPADRESS, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        String json_string = new String(responseBody);
                        Log.d(TAG,"json ip : "+json_string);
                        JSONObject json = new JSONObject(json_string);
                        String status = json.getString("status");
                        if(status.equals("success")){
                            final String public_ip = json.getString("query");
                            float latitude = (float)json.getDouble("lat");
                            float longitude = (float)json.getDouble("lon");
                            config.setKeyString("ip_address",public_ip);
                            config.setKeyFloat("latitude",latitude);
                            config.setKeyFloat("longitude",longitude);
                            int templateid = config.getPageId();
                            registerDevice(context,android_id,latitude,longitude,public_ip,ip_local,mac_address,templateid);
                        }
                    } catch (Exception ex){
                        Log.e(TAG,"error convert json : "+ex.getMessage());
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(context,"Kegagalan Mengambil Alamat IP!",
                            Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception ex){
            Log.e(TAG,"error get ip address : "+ex.getMessage());
        }
    }
    public static boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean NisConnected = activeNetwork != null && activeNetwork.isConnected();
        if (NisConnected) {
            //  if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE || activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            } else return activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }
    public static void registerDevice(final Context context,String deviceid,
                                      float lat,float lng,String ipaddr,
                                      String iplocal,String mac_address,
                                      int template_id){

        try {

            final Configurate config = new Configurate(context);
            AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
//            client.addHeader("Content-type","form-data");
            RequestParams params = new RequestParams();
            params.put("device_code",deviceid);
//            params.put("token",token);
            params.put("latitude",lat);
            params.put("longitude",lng);
            params.put("ip_address",ipaddr);
            params.put("serial_number",deviceid);
            params.put("ip_local",iplocal);
            params.put("mac_address",mac_address);
            params.put("template_id",template_id);
//            String BaseServer = config.getServerUrl();
            String UrlRegister = EndPoints.DEVICE_REGISTER;
            Log.d(TAG,"register url :"+UrlRegister+"\n"+params.toString());

            client.post(UrlRegister, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        String json_string = new String(responseBody);
                        Log.d(TAG,"response register device :"+json_string);
                        JSONObject json = new JSONObject(json_string);
                        boolean status = json.getBoolean("status");
                        String message = "";
                        if(!json.isNull("message")){
                            message = json.getString("message");
                        }
                        if(status){
                            JSONObject data=json.getJSONObject("data");
                            String token = data.getString("token_");
                            config.setToken(token);
                            Toast.makeText(context,"Registrasi perangkat sukses!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG,"register device error");
                            Toast.makeText(context,"Registrasi perangkat gagal!\n"+message,
                                    Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception ex){
                        Toast.makeText(context,"Kesalahan Register perangkat!",
                                Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "error converting json : "+ex.getMessage());
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    if(statusCode!=0){
                        String html = new String(responseBody);

                        Log.e(TAG, "error register failed : "+html);
                    }

                }
            });
        } catch (Exception ex){
            Log.e(TAG, "error register device : "+ex.getMessage());
        }
    }
    public static String getMimeType(File url)
    {
        Uri selectedUri = Uri.fromFile(url);
        String fileExtension
                = MimeTypeMap.getFileExtensionFromUrl(selectedUri.toString());
        String mimeType
                = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
        return mimeType;
    }
    public static void unzipUsbFile(){
        ZipArchive zipArchive = new ZipArchive();
        File zip = new File(EndPoints.ZIP_USB_FILE);
        if(zip.exists()){
            ZipArchive.unzip(EndPoints.ZIP_USB_FILE,EndPoints.STORAGE_DATA_PATH,"");
        }
        //zip.delete();
//        insertedFileData();
    }
    public static void unzipFile(String fileZip){
        ZipArchive zipArchive = new ZipArchive();
        File zip = new File(fileZip);
        if(zip.exists()){
            ZipArchive.unzip(fileZip,EndPoints.STORAGE_DATA_PATH,"");
        }
        //zip.delete();
//        insertedFileData();
    }
    public static void hideSystemUI(View decorView) {
        //View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }
    public static void showSystemUI(View decorView) {
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
    public static void insertPopup(Context context){
        // caption.setText("Inserting PopUP ...");
        // SQLite.delete().from(Popups.class);
        Storage storage = new Storage(context);
        File file = new File(EndPoints.POPUP_PATH);
        if(file.exists()){
            String content = storage.readTextFile(EndPoints.POPUP_PATH);
            String json_string = String.format("{\"result\": %s }",content);
            try {
                JSONObject json = new JSONObject(json_string);
                JSONArray result = json.getJSONArray("result");
//            progressBar.setMax(result.length());
                for(int i=0;i<result.length();i++){
                    JSONObject item = result.getJSONObject(i);
                    String filename=item.getString("resource");
                    String waktu = item.getString("time");
                    Popups popups = new Popups();
                    popups.setFile_name(filename);
                    popups.setTime(waktu);
                    popups.save();
//                progressBar.setProgress(i);
                }
//            progressBar.setProgress(result.length());
//            loadScreen();
                loadScreen(context);
            } catch (Exception ex){
                Toast.makeText(context,"Kesalahan Format Data Popup!",
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG,"error:"+ex.getMessage());
            }
        } else {
            Toast.makeText(context,"Popup Not Found! Load Screen Without PopUp",
                    Toast.LENGTH_SHORT).show();
            loadScreen(context);
        }
    }
    @SuppressLint("WrongConstant")
    private static void loadScreen(final Context context){

        Log.d(TAG,"Load screen from app util");
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context,ScreenActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
            }
        },1000);
        //overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);

    }
    public static void insertedFileData(Context context){
        Storage storage = new Storage(context);
        //caption.setText("Inserting File Data ...");
        Log.d(TAG,"page insert frame ");
        File dir = new File(EndPoints.VIEW_PATH);

        for(File file : dir.listFiles()){
            Log.d(TAG,"file : "+file.getName());
            String content=storage.readTextFile(file.getAbsolutePath());
            Log.d(TAG,"content:"+content);
            try {
                JSONObject json = new JSONObject(content);
                int view_id = Integer.parseInt(json.getString("id"));
                String name = json.getString("name");
                String description = json.getString("description");

                String bg_color = "";
                String bg_image = "";
                String showTime = "00:00";
                if(!json.isNull("background-image")){
                    bg_image = json.getString("background-image");
                }
                if(!json.isNull("background_color")){
                    bg_color = json.getString("background-color");
                }
                if(!json.isNull("time")){
                    showTime = json.getString("time");
                }
                int delayTime = 10000;
                if(!json.isNull("delay")){
                    delayTime = json.getInt("delay");
                }
                int order = 0;
                if(!json.isNull("order")){
                    delayTime = json.getInt("order");
                }
                DBHelper.insertViews(view_id,name,bg_image,bg_color,showTime,delayTime,0);
                Log.d(TAG,"insert view : "+Integer.toString(view_id));
//                int view_id = DBHelper.getViewRow(name).getId();
                JSONArray jarray = json.getJSONArray("data");
                int jlength = jarray.length();
                //int id_frame = 1;
                for(int i=0;i<jlength;i++){
                    child = jarray.getJSONObject(i);
                    String frame_name = child.getString("name");

                    String frame_desc = child.getString("description");
                    int x = (int)Math.round(child.getDouble("x"));
                    int y = (int)Math.round(child.getDouble("y"));
                    int width = (int)Math.round(child.getDouble("width"));
                    int height = (int)Math.round(child.getDouble("height"));
                    int id_frame=DBHelper.insertFrame(view_id,frame_name,frame_desc,x,y,width,height);
                    Log.d(TAG,"frame name:"+frame_name + "inserted...");
//                    Frames dframe = DBHelper.getFrameDetail(frame_name,view_id);
                    JSONArray resources = child.getJSONArray("resources");
                    int rlength = resources.length();
                    for(int j=0;j<rlength;j++){
                        JSONObject item_res = resources.getJSONObject(j);
                        String filename = item_res.getString("resource");
                        String waktu = item_res.getString("waktu");
                        FrameResources frameResources = new FrameResources();
                        frameResources.setFrame_id(id_frame);
                        frameResources.setResource_name(filename);
                        frameResources.setWaktu(Integer.parseInt(waktu));
                        frameResources.save();

                    }
                    id_frame++;
                }
            } catch (JSONException e) {
                Toast.makeText(context,"Kesalahan Format Data Konten!",
                        Toast.LENGTH_SHORT).show();
//                e.printStackTrace();
            }

//            progressBar.setProgress(ifile);

        }

        insertPopup(context);
    }
    public static void insertResources(Context context){
//        caption.setText("Inserting Resource ...");
        Storage storage = new Storage(context);
        List<File> files = storage.getFiles(EndPoints.RESOURCE_PATH, null);
//        progressBar.setMax(files.size());
        if(files.size()>0){
            Log.d(TAG,"page insert resources ");
            int i=0;
            for(File file:files){
                if(!file.isDirectory()){
                    String mime = AppUtils.getMimeType(file);
                    if(!DBHelper.isResourceExist(file.getName())){
                        id.sentuh.digitalsignage.models.Resources resources = new id.sentuh.digitalsignage.models.Resources();
                        resources.setFile_name(file.getName());
                        resources.setMime(mime);
                        if(file.getName().contains(".txt")){
                            String content = storage.readTextFile(file.getAbsolutePath());
                            resources.setContent(content);
                        }
                        resources.save();
                        Log.d(TAG,"file name:"+file.getName()+" mime : "+mime+" saved!");
                    } else {
                        Toast.makeText(context,"File Resource tidak Ada!",
                                Toast.LENGTH_SHORT).show();
                    }

                }
                i++;
//                progressBar.setProgress(i);
            }
//            insertPopup(context,progressBar);
            insertedFileData(context);
        }
    }

    public static void lookupServer(final Context context,int layout_id){
        final Configurate config = new Configurate(context);
        final int old_version = config.getVersion();
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        try {
            client.get(EndPoints.UPDATE_VERSION.replace("_ID_",Integer.toString(layout_id)), new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        String json_string = new String(responseBody);
                        Log.d(TAG,"json version : "+json_string);
                        JSONObject json = new JSONObject(json_string);
                        int version = json.getInt("version");
                        if(version>old_version){
                            config.setVersion(version);
                            String url_download = json.getString("url");
                            Log.d(TAG,"downloading new version");
                            Toast.makeText(context,"Mengunduh Konten Versi Baru!",
                                    Toast.LENGTH_SHORT).show();
                            downloadZip(url_download,context);

                        }


                    } catch (Exception ex){
                        Log.e(TAG,"error lookup server : "+ex.getMessage());
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                   // displayNotificationMessage("Error Checking Server!");
                }
            });
        } catch (Exception ex){
            Log.e(TAG,"error lookup server : "+ex.getMessage());
        }
    }
    public static boolean checkConfig(){
        File file = new File(EndPoints.CONFIG_FILE_PATH);
        return file.exists();
    }
    public static boolean checkFolder(){
        String[] folders = new String[]{"Config","Events","Models","Resources","Views"};
        File dirs = new File(EndPoints.VIEW_PATH);
        if(dirs.exists()){
            File[] files = dirs.listFiles();
            return files.length > 0;
        } else {
            return false;
        }
    }
    public static boolean checkSthxFile(){
        boolean found= false;
        File dirs = new File("/mnt/udisk");
        if(dirs.exists()){
            File[] files = dirs.listFiles();
            if(files.length>0){
                for(File file:dirs.listFiles()){
                    if(file.getName().contains(".sthx")){
                        found = true;
                    }
                }
                return found;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    public static String getSthxFile(){
        File dirs = new File("/mnt/udisk");
        if(dirs.exists()){
            File[] files = dirs.listFiles();
            if(files!=null){
                for(File file:dirs.listFiles()){
                    if(file.getName().contains(".sthx")){
                        return file.getAbsolutePath();
                    }
                }

            } else {
                return null;
            }
        } else {
            return null;
        }
        return null;
    }
    public static void eraseAllLocalData(Context context){
        Storage storage = new Storage(context);
        File dirs = new File(EndPoints.STORAGE_DATA_PATH);
        for(File dir:dirs.listFiles()){
           if(dir.getName().equals("Views")||
                   dir.getName().equals("Resources")||
                   dir.getName().equals("Events")||
                   dir.getName().equals("Models")) {
               Log.d(TAG,"delete dir : "+dir.getName());
//               List<File> files = storage.getFiles(dir.getAbsolutePath());
//               for(File file:files){
//                   storage.deleteFile(file.getAbsolutePath());
//               }
               deleteRecursive(dir);
           }
        }

    }
    static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles()) {

                deleteRecursive(child);
            }
        Log.d(TAG,"delete file : "+fileOrDirectory.getName());
        fileOrDirectory.delete();
    }
    public static String extractYoutubeId(String url)
            throws MalformedURLException {
        String query = new URL(url).getQuery();
        String[] param = query.split("&");
        String id = null;
        for (String row : param) {
            String[] param1 = row.split("=");
            if (param1[0].equals("v")) {
                id = param1[1];
            }
        }
        return id;
    }
    public static String getYoutubeThumbnail(String url){

        try {
            String image_url = "http://img.youtube.com/vi/"+extractYoutubeId(url)+"/0.jpg";
            return image_url;
        } catch (MalformedURLException e) {
            //e.printStackTrace();
            return null;
        }
    }
    public static void downloadZip(String downloadURL,final Context context){
        try {
            AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);

            client.get(downloadURL, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
//                        .String json_string = new String(responseBody);
                        //String filepath = ext_path+"publish.zip";
                        File fileDest = new File(EndPoints.ZIP_DEST_FILE);
                        FileOutputStream fos = new FileOutputStream(fileDest);
                        fos.write(responseBody);
                        fos.close();
                        Log.d(TAG,"unzip file:"+EndPoints.ZIP_DEST_FILE);
                        AppUtils.unzipFile(fileDest.getAbsolutePath());
                        //AppUtils.insertResources(context);
                        //Log.d(TAG,"response:"+json_string);
                        Toast.makeText(context,"Mengunduh Konten Sukses!",Toast.LENGTH_SHORT).show();
                        loadMain(context);
                    } catch (Exception ex){
                        Toast.makeText(context,"Gagal Mengunduh Konten!",Toast.LENGTH_SHORT).show();
                        Log.e(TAG, ex.getMessage());
                    }
                }

                @Override
                public void onProgress(long bytesWritten, long totalSize) {
                    super.onProgress(bytesWritten, totalSize);
                    int progress = (int)(bytesWritten * 100/totalSize);
                    Log.d(TAG,"download progress "+Integer.toString(progress));
                }

                @Override
                public void onStart() {
                    super.onStart();
                    Log.d(TAG,"start download file");
                    //Toast.makeText(context,"Start Download",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    Log.d(TAG,"finish download file");
                    //Toast.makeText(context,"Download Finish",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.e(TAG, error.getMessage());
                }
            });
        } catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }
    }
    public static void loadMain(Context context){
        Intent intent = new Intent(context,MainActivity.class);
        context.startActivity(intent);
    }
    public static int getPackageVersion(String packageName, PackageManager packageManager) {
        try {
//           ApplicationInfo info= packageManager.getApplicationInfo(packageName, 0);
            PackageInfo info = packageManager.getPackageInfo(packageName,0);

            return info.versionCode;
        }
        catch (Exception e) {
            return 0;
        }
    }
    public static int getApkVersion(String apk_path, PackageManager packageManager) {
        try {
//           ApplicationInfo info= packageManager.getApplicationInfo(packageName, 0);
            PackageInfo info = packageManager.getPackageArchiveInfo(apk_path,0);

            return info.versionCode;
        }
        catch (Exception e) {
            return 0;
        }
    }
    public static String getLocalZipFile(){
            File dirs = new File(EndPoints.STORAGE_BASE_PATH);
            if(dirs.exists()){
                File[] files = dirs.listFiles();
                if(files!=null){
                    for(File file:dirs.listFiles()){
                        if(file.getName().contains(".sthx")){
                            return file.getAbsolutePath();
                        }
                    }

                } else {
                    return null;
                }
            } else {
                return null;
            }
            return null;
    }
    public static String getUsbZipFile(){
        boolean found=false;
        String filePath="";
        File dir = new File("/mnt/udisk");
        if(dir.exists()) {
            for(File file:dir.listFiles()){
                if(file.getName().contains(".sthx")){
                    filePath = file.getAbsolutePath();
                    found = true;
                    return filePath;
                }
            }

        } else {
            return null;
        }
        Log.d("USB Path","usb path : "+filePath);
        return filePath;
    }
    public static String openTextFile(File file){
        String dummy = "";
        try {
            StringBuilder text = new StringBuilder();

            try {
                FileReader fread = new FileReader(file);
                BufferedReader br = new BufferedReader(fread);
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();
                fread.close();
                return text.toString();
            }
            catch (IOException e) {
                //You'll need to add proper error handling here
            }

        } catch (Exception ex){
            ex.printStackTrace();
        }
        return dummy;
    }
    public static void openAgent(Context context){
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("id.sentuh.digitalsignageagent");
        if (launchIntent != null) {
            context.startActivity(launchIntent);//null pointer check in case package name was not found
        }
    }
    public static void installAPK(Context context){
        File file = new File(EndPoints.APK_LOCAL_PATH);
        Intent promptInstall = new Intent(Intent.ACTION_VIEW)
                .setDataAndType(Uri.fromFile(file),
                        "application/vnd.android.package-archive");
        context.startActivity(promptInstall);
    }
    public static boolean isAppInstalled(Context context) {
        try {
            context.getPackageManager().getApplicationInfo(EndPoints.APP_PACKAGE_NAME, 0);
            return true;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    public static void startAppService(Context context){
        if(isAppInstalled(context)){
            Intent i = context.getPackageManager().
                    getLaunchIntentForPackage(EndPoints.APP_PACKAGE_NAME);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } else {
            Toast.makeText(context,"Application not Installed!",Toast.LENGTH_SHORT).show();
        }
    }
    public static void setWifiSetting(Context mContext,String ssid, String password){
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", ssid);
        wifiConfig.preSharedKey = String.format("\"%s\"", password);

        WifiManager wifiManager = (WifiManager)mContext.getSystemService(WIFI_SERVICE);

        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
    }
}

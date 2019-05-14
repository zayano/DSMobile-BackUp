package id.sentuh.digitalsignage.app;


import android.os.Environment;

import java.io.File;

/**
 * Created by user on 12/5/17.
 */

public abstract class EndPoints {
    public static String TAG="Digital Signage";
    private static File extdir = Environment.getExternalStorageDirectory();
    private static String BASE_SERVER = "https://cms.sentuh.id";
//    public static String API_SERVER =  "%s/api";
    public static String UPDATE_VERSION = "%s/api/version/template/%s";

    public static String DEVICE_REGISTER = BASE_SERVER + "/api/devices"; //POST
    public static String DEVICE_GET = BASE_SERVER + "/api/devices/_ID_"; //POST
    public static String GET_IP_PUBLIC = "https://api.ipify.org/?format=json";
    public static String PUBLISH_ZIP_FILE = "%s/storage/publish.sthx";
    public static String STORAGE_BASE_PATH = extdir.getAbsolutePath(); ///mnt/udisk on device signage
    public static String STORAGE_DATA_PATH = STORAGE_BASE_PATH + "/SentuhOS";
    public static String STORAGE_TEMP_PATH = STORAGE_BASE_PATH + "/tmp";
    public static String VIEW_PATH = STORAGE_DATA_PATH + "/Views";
    public static String SYSTEM_PATH = STORAGE_DATA_PATH + "/System";
    public static String RESOURCE_PATH = STORAGE_DATA_PATH + "/Resources";
    public static String POPUP_PATH = STORAGE_DATA_PATH + "/Models/popup.txt";
    private static String EVENT_PATH = STORAGE_DATA_PATH + "/Events";
    public static String EVENT_FILE_PATH = EVENT_PATH + "/events.txt";
    public static String ZIP_DEST_FILE = STORAGE_BASE_PATH +"/publish.sthx";
    public static String APK_FILE_URL = BASE_SERVER + "/apk/release/latest.apk";
    public static String CONFIG_FILE_PATH = STORAGE_DATA_PATH + "/Config/config.txt";
    private static String USB_STORAGE_PATH = "/storage/usbhost2/SentuhOS";
    public static String ZIP_USB_FILE = "/storage/usbhost2/publish.sthx";
    public static String CONFIG_USB_PATH = USB_STORAGE_PATH + "/Config/config.txt";
    public static String DEVELOPER_KEY = "AIzaSyAosdkVzcIvw7MFCCLn5cfwCCGyISClLtI";
    public static String APK_LOCAL_PATH = STORAGE_DATA_PATH + "/latest.apk" ;
    public static String GET_IPADRESS = "http://ip-api.com/json";
    public static String APP_PACKAGE_NAME = "id.sentuh.digitalsignageagent";
    public static String DOWNLOAD_URL = BASE_SERVER + "/api/savetheme/template/_ID_/download";
//    public static String ZIP_OLD_FILE = STORAGE_BASE_PATH +"/backup.sthx";
    public static String PASSWORD_FILE = STORAGE_BASE_PATH +"/password.txt";
}

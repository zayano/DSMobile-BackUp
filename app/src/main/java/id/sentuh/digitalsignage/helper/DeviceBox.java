package id.sentuh.digitalsignage.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import id.sentuh.digitalsignage.MainActivity;
import id.sentuh.digitalsignage.R;
import id.sentuh.digitalsignage.ScreenActivity;
import id.sentuh.digitalsignage.app.Configurate;
import id.sentuh.digitalsignage.app.EndPoints;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by sonywibisono on 5/21/16.
 */
public class DeviceBox extends Dialog implements DialogInterface.OnClickListener{
    Activity mC;
    TextView lbName;
    TextView lbDeviceId;
    TextView lbServerAddr;
    TextView lbLocalIp;
    TextView lbPublicIp;
    TextView lbTemplateName;
    TextView lbVersion;
    TextView lbPing;
    Configurate config;
    String DEVICE_ID;
    WifiManager wifi;
    WifiInfo wifiInfo;
    String MAC_ADDRESS;
    String IP_LOCAL;
    Button btnCancel;
    Button btnExit;
    DialogInterface dialogInterface;
    public DeviceBox(@NonNull Activity context) {
        super(context);
        this.mC = context;
        wifi = (WifiManager)mC.getApplicationContext().getSystemService(WIFI_SERVICE);
        wifiInfo = wifi.getConnectionInfo();
        MAC_ADDRESS = wifiInfo.getMacAddress();
        IP_LOCAL = Formatter.formatIpAddress(wifiInfo.getIpAddress());
        DEVICE_ID = Settings.Secure.getString(mC.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        this.setContentView(R.layout.device_info);
        this.setTitle("Device Info");
        lbDeviceId = this.findViewById(R.id.device_id);
        lbName = this.findViewById(R.id.device_name);
        lbServerAddr = this.findViewById(R.id.server_address);
        lbLocalIp = this.findViewById(R.id.local_ip);
        lbPublicIp = this.findViewById(R.id.public_ip);
        lbTemplateName = this.findViewById(R.id.template_name);
        lbVersion = this.findViewById(R.id.template_version);
        lbPing = this.findViewById(R.id.template_speed_link);
        config = new Configurate(mC);
        config.setIPLocal(IP_LOCAL);
        int templateId = config.getPageId();
        if (wifiInfo != null) {
            int speedMbps = wifiInfo.getLinkSpeed();
            lbPing.setText(Integer.toString(speedMbps));
        } else {
            lbPing.setText("Tidak terdeteksi");
        }
        lbServerAddr.setText(config.getServerUrl());
        lbLocalIp.setText(IP_LOCAL);
        lbName.setText(android.os.Build.MODEL);
        lbDeviceId.setText(DEVICE_ID);
        lbVersion.setText(Integer.toString(config.getVersion()));
//        getDeviceInfo(DEVICE_ID);
        lbTemplateName.setText(Integer.toString(templateId));
        dialogInterface = this;

        getPublicIP();
        lbServerAddr.setSelected(true);
        this.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dialogInterface.dismiss();
            }
        });
        btnExit = this.findViewById(R.id.btn_exit);
        btnCancel = this.findViewById(R.id.btn_cancel);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dialogInterface.dismiss();
                CloseApp();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogInterface.dismiss();
            }
        });
    }
    private void CloseApp(){
        dialogInterface.dismiss();

        Intent CloseInt = new Intent(mC, MainActivity.class);
        CloseInt.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        CloseInt.putExtra("CloseApp", true);
        mC.startActivity(CloseInt);
        mC.finish();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        CloseApp();

    }

    //    public void show(){
//        this.show();
//    }
    private void getDeviceInfo(String deviceid){
        try {
            AsyncHttpClient client = new AsyncHttpClient();
            String URL = EndPoints.DEVICE_GET.replace("_ID_",deviceid);
            client.get(URL, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        String data = new String(responseBody);
                        String json_string= String.format("{\"data\":%s",data);
                        JSONObject json = new JSONObject(json_string);
                        JSONArray jarr = json.getJSONArray("data");
                        JSONObject item = jarr.getJSONObject(0);
                        String public_ip = item.getString("ip_address");
                        lbPublicIp.setText(public_ip);
                    } catch (Exception ex){
                        Log.e("Dialog",ex.getMessage());
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                }
            });
        } catch (Exception ex){

        }
    }
    private void getPublicIP(){
        try {
            AsyncHttpClient client = new AsyncHttpClient();

            client.get(EndPoints.GET_IP_PUBLIC, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        String data = new String(responseBody);
                        if(data!=null){
                            JSONObject json = new JSONObject(data);
                            String IpPublic = json.getString("ip");
                            lbPublicIp.setText(IpPublic);
                        }

                    } catch (Exception ex){
                        Log.e("Dialog",ex.getMessage());
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                }
            });
        } catch (Exception ex){

        }
    }
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

    }
}


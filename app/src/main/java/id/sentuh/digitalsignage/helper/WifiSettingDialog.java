package id.sentuh.digitalsignage.helper;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.language.SQLite;


import java.util.List;

import id.sentuh.digitalsignage.R;
import id.sentuh.digitalsignage.adapter.WifiListAdapter;
import id.sentuh.digitalsignage.app.Configurate;
import id.sentuh.digitalsignage.models.WifiList;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by user on 6/27/17.
 */

public class WifiSettingDialog extends Dialog {
    private static String TAG = "Comment Dialog";
    Context mContext;
    List<WifiList> wifis;
    Configurate config;
    Button btnSend;
    EditText ed_passwd;
    Spinner spinner;
    InputMethodManager imm;
    String wifiName;
    String wifiKey;
    public WifiSettingDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
        config = new Configurate(mContext);
        setContentView(R.layout.wifi_setting);
        spinner = findViewById(R.id.sp_wifi);
        ed_passwd = findViewById(R.id.wifi_passwd);
        btnSend = findViewById(R.id.save_setting);
        imm= (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        this.setTitle("Setting Wifi");
        refreshAdapter();
        ed_passwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                } else {
                    imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                }
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext,"Save Setting",Toast.LENGTH_SHORT).show();
                wifiKey = ed_passwd.getText().toString();
                wifiSetting(wifiName,wifiKey);
                imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                dismiss();
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TextView name = view.findViewById(R.id.wifi_name);
                if(name!=null){
                    wifiName = name.getText().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner.requestFocus();
    }
    private void wifiSetting(final String ssid,final String key){
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", ssid);
        wifiConfig.preSharedKey = String.format("\"%s\"", key);

        WifiManager wifiManager = (WifiManager)mContext.getSystemService(WIFI_SERVICE);
//remember id
        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
        Toast.makeText(mContext,"Try to Reconnect",Toast.LENGTH_SHORT).show();
        Handler mHandler=new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isNetworkAvailable(mContext)){
                    Toast.makeText(mContext,"Reconnect Success!",Toast.LENGTH_SHORT).show();
                    config.setWifiName(ssid);
                    config.setWifiPassword(key);
                } else {
                    Toast.makeText(mContext,"Reconnect Failed!",Toast.LENGTH_SHORT).show();
                }
            }
        },5000);
    }
    private void refreshAdapter() {
        wifis = SQLite.select().from(WifiList.class).queryList();
        WifiListAdapter mAdapter = new WifiListAdapter(mContext, R.layout.wifi_item,wifis);
        spinner.setAdapter(mAdapter);

    }
    public boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}

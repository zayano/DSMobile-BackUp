package id.sentuh.digitalsignage.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.*;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.Log;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.snmp4j.*;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import id.sentuh.digitalsignage.app.AppUtils;
import id.sentuh.digitalsignage.app.Configurate;
import id.sentuh.digitalsignage.app.GPSTracker;
import id.sentuh.digitalsignage.models.MIBtree;
import id.sentuh.digitalsignage.models.WifiList;
import id.sentuh.digitalsignage.models.WifiList_Table;

public class AgentService extends Service implements CommandResponder {
    private static String TAG="Agent SNMP";
    /** Keeps track of all current registered clients. */
    ArrayList<Messenger> mClients = new ArrayList<Messenger>();
    /** Holds last value set by a client. */
    int mValue = 0;
    GPSTracker tracker;
    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int MSG_SET_VALUE = 3;
    public static final int MSG_SNMP_REQUEST_RECEIVED = 4;
    public static final int MSN_SEND_DANGER_TRAP = 5;
    public static final int MSG_MANAGER_MESSAGE_RECEIVED = 6;

    public static String lastRequestReceived = "";

    private Snmp snmp;
    private static final String SNMP_PORT = "32150";

    private static ArrayList<Address> registeredManagers = null;

    //private MIBtree MIB_MAP;
    private Timer timer;
    Configurate config;
    List<ScanResult> wifiList;
    private WifiManager wifi;
    private WifiInfo wifiInfo;
    private String MAC_ADDRESS;
    private String IP_LOCAL;
    private String DEVICE_ID;
    /**
     * Handler of incoming messages from clients.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    mClients.add(msg.replyTo);
                    break;
                case MSG_UNREGISTER_CLIENT:
                    mClients.remove(msg.replyTo);
                    break;
                case MSG_SET_VALUE:
                    mValue = msg.arg1;
                    sendMessageToClients(MSG_SET_VALUE);
                    break;
                case MSN_SEND_DANGER_TRAP:
                    new SendTrap().execute();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private void sendMessageToClients(int msgCode) {
        for (int i=mClients.size()-1; i>=0; i--) {
            try {
                mClients.get(i).send(Message.obtain(null,
                        msgCode, 0, 0));
            } catch (RemoteException e) {
                // The client is dead.  Remove it from the list;
                // we are going through the list from back to front
                // so this is safe to do inside the loop.
                mClients.remove(i);
            }
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    int template_id;
    @Override
    public void onCreate() {
        timer = new Timer();
        wifi = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
        wifiInfo = wifi.getConnectionInfo();
        MAC_ADDRESS = wifiInfo.getMacAddress();
        IP_LOCAL = Formatter.formatIpAddress(wifiInfo.getIpAddress());
        registerReceiver(mWifiReceiver,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifi.startScan();
        DEVICE_ID = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        config = new Configurate(this);
        tracker = new GPSTracker(this);
        config.setIPLocal(IP_LOCAL);
        template_id = config.getPageId();
//        Location loc = tracker.getLocation();
        Log.d(TAG,"Mac address : "+MAC_ADDRESS+","+IP_LOCAL);
        if(tracker.canGetLocation()){
            float lat=(float)tracker.getLatitude();
            float lng = (float)tracker.getLongitude();
            DecimalFormat dc = new DecimalFormat("#,###.#####");
            Log.d(TAG,"device location : "+dc.format(lat)+", "+dc.format(lng));
            if(AppUtils.isInternetAvailable(this)){
                AppUtils.registerDevice(AgentService.this,DEVICE_ID,lat,lng,"",IP_LOCAL,MAC_ADDRESS,template_id);
            }

        } else {
            Log.d(TAG,"cannot get location");
            if(AppUtils.isInternetAvailable(this)){
                AppUtils.getIpAddrLatLon(AgentService.this,MAC_ADDRESS,IP_LOCAL);
            }
        }
        timer.scheduleAtFixedRate(new RefreshMIBData(), 0, 50000);
        new AgentListener().start();
    }
    BroadcastReceiver mWifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            wifiList=wifi.getScanResults();
            for(ScanResult result:wifiList){
//                Log.d(TAG,"ssid name :"+result.SSID);
                WifiList dbwifi= SQLite.select().from(WifiList.class).where(WifiList_Table.ssid_name.eq(result.SSID)).querySingle();
                if(dbwifi==null){
                    dbwifi = new WifiList();
                    dbwifi.ssid_name = result.SSID;
                    dbwifi.save();
//                    Log.d(TAG,"ssid name :"+result.SSID+" saved");
                }
            }
        }
    };
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public void onDestroy() {

    }

    private class SendTrap extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            PDUv1 pdu = new PDUv1();
            pdu.setType(PDU.V1TRAP);
            pdu.setGenericTrap(PDUv1.COLDSTART);
            pdu.add(new VariableBinding(new OID(new int[]{1, 3, 6, 1, 2, 1, 1, 2})));

            // Specify receiver
            Address targetAddress = new UdpAddress("192.168.0.102/1610");
            CommunityTarget target = new CommunityTarget();
            target.setCommunity(new OctetString("public"));
            target.setVersion(SnmpConstants.version1);
            target.setAddress(targetAddress);
            target.setRetries(2);
            target.setTimeout(1500);


            try {
                snmp.trap(pdu, target);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class AgentListener extends Thread {
        public void run() {
            try {

                initSnmp();

                snmp.listen();

            }  catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void initSnmp(){
            try {
                TransportMapping transport;
                transport = new DefaultUdpTransportMapping(new UdpAddress("0.0.0.0/" + SNMP_PORT));

                snmp = new Snmp(transport);


                snmp.addCommandResponder(AgentService.this);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static ArrayList<Address> getRegisteredManagers(){
        if(registeredManagers == null) return new ArrayList<>();
        return registeredManagers;
    }

    private static void registerManager(Address address){
        if(registeredManagers == null){
            registeredManagers = new ArrayList<>();
            registeredManagers.add(address);
        } else {
            boolean exists = false;
            for(Address a : registeredManagers){
                if(a.toString().equals(address.toString())) exists=true;
            }
            if(!exists) registeredManagers.add(address);
        }
    }

    @Override
    public synchronized void processPdu(CommandResponderEvent commandResponderEvent) {
        PDU command = (PDU) commandResponderEvent.getPDU().clone();
        registerManager(commandResponderEvent.getPeerAddress());

        if (command != null) {
            lastRequestReceived = command.toString() + " " + commandResponderEvent.getPeerAddress();
            sendMessageToClients(MSG_SNMP_REQUEST_RECEIVED);
            if (command.getType() == PDU.GET){
                handleGetRequest(command);
            } else if(command.getType() == PDU.GETNEXT){
                handleGetNextRequest(command);
            } else if (command.getType() == PDU.SET) {
                handleSetRequest(command);
            }
            Address address = commandResponderEvent.getPeerAddress();
            sendResponse(address, command);
        }
    }

    private void handleSetRequest(PDU command) {
        VariableBinding varBind;
        OID oid;
        for(int i = 0; i < command.size(); i++){
            varBind = command.get(i);
            oid = (OID)MIBtree.MNG_MANAGER_MESSAGE_OID.clone();
            oid.append(0);
            if(varBind.getOid().equals(oid)) {

                MIBtree MIB_MAP = MIBtree.getInstance();
                MIB_MAP.set(varBind);
                sendMessageToClients(MSG_MANAGER_MESSAGE_RECEIVED);
            }
        }
    }

    private void sendResponse(Address address, PDU command) {

        command.setType(PDU.RESPONSE);
        System.out.println(command.toString());
        // Specify receiver
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString("public"));
        target.setVersion(SnmpConstants.version1);
        target.setAddress(address);
        target.setRetries(0);
        target.setTimeout(1500);

        try {
            snmp.send(command, target);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleGetNextRequest(PDU command) {
        VariableBinding varBind;
        for(int i = 0; i < command.size(); i++){
            varBind = command.get(i);
            command.set(i, answerForGetNext(varBind.getOid()));
        }
    }

    private VariableBinding answerForGetNext(OID oid) {
        MIBtree MIB_MAP = MIBtree.getInstance();
        return MIB_MAP.getNext(oid);
    }

    private void handleGetRequest(PDU command) {
        VariableBinding varBind;
        for(int i = 0; i < command.size(); i++){
            varBind = command.get(i);
            varBind.setVariable(answerForGet(varBind.getOid()));

        }
    }

    private Variable answerForGet(OID oid) {
        MIBtree MIB_MAP = MIBtree.getInstance();
        VariableBinding vb = MIB_MAP.get(oid);
        return vb.getVariable();
    }


    class RefreshMIBData extends TimerTask {
        SystemInformation systemInformation;

        public RefreshMIBData(){
            systemInformation = new SystemInformation(AgentService.this);
        }

        public void run() {
            systemInformation.updateSystemInformation();
        }
    }
}

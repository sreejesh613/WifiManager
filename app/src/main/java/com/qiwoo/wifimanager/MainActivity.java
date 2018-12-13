package com.qiwoo.wifimanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    public static final String TAG = MainActivity.class.getSimpleName();
    private WifiManager.LocalOnlyHotspotReservation mReservation;

    private WifiManager manager;
    private int custom_wifi_id =0;
    private BroadcastHelper bh = null;
    private String wifiSsid = "notApublicHotspot";
    private String wifiPassword = "agent@99";
    //private String wifiSsid = "HealthApp"; //"\"" + HealthApp + "\"";
    // private String wifiPassword = "Hmicro@1234"; //"\"" + Hmicro@1234 + "\"";

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
          //  Log.e(TAG,"Intent :"+action);

            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                boolean success = intent.getBooleanExtra("EXTRA_RESULTS_UPDATED",false);
                List<ScanResult> data = manager.getScanResults ();
             //   Log.e(TAG,"scan suucess on R"+success);
                Toast.makeText(context, "Scan Result : "+success, Toast.LENGTH_SHORT).show();

            }
        }
    };

    public String getWifiApIpAddress() {

            try {
                NetworkInterface kP =null;
                for (NetworkInterface intf : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                    Log.e(TAG,"Network Interface 2 : "+intf.getName());
                    for (InetAddress addr : Collections.list(intf.getInetAddresses())) {
                        if (!addr.isLoopbackAddress()){
                            kP=intf;
                            Log.e(TAG,"\n\n IP Address: " + addr.getHostAddress() );
                          //  Log.e(TAG,"\n" + addr.getHostName() );
                         //   Log.e(TAG,"\n" + addr.getCanonicalHostName() );
                            Log.e(TAG,"Inf : " + intf.toString() );
                            Log.e(TAG,"Inf Name" + intf.getName() );
                            Log.e(TAG,"Inf Is Up " + intf.isUp() );
                        }
                    }
                }
                if(kP!=null){
                    Log.e(TAG,"kP Is Up ");
                    if(bh!=null)
                        bh.interrupt();
                    bh = new BroadcastHelper();
                    bh.start();
//                    InetAddress group = InetAddress.getByName("192.168.43.1");
//                    MulticastSocket s = new MulticastSocket(6789);
//                  //  s.setNetworkInterface(kP);
//                    String msg = "Hello";
//                    DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(),
//                            group, 6789);
//                    s.send(hi);
//




                }
                //InetAddress group = InetAddress.getByName("192.168.43.1");

                //NetworkInterface k= s.getNetworkInterface();
                //   s.setNetworkInterface();
                //   s.joinGroup(group);



            } catch (Exception ex) {
             //   Log.e(TAG,"\n\n Error getting IP address: " + ex );
                ex.printStackTrace();
            }

        /*    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
                    .hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                if (intf.getName().contains("wlan")) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
                            .hasMoreElements();) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()
                                && (inetAddress.getAddress().length == 4)) {
                            Log.d(TAG, "Address"+inetAddress.getHostAddress());
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            } */


        return null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(mReceiver, filter);

        manager = (WifiManager) this.getApplicationContext().getSystemService(WIFI_SERVICE);

        int state = manager.getWifiState();

        Log.e(TAG, "state: " + state);

        findViewById(R.id.wifi_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<WifiConfiguration> list = manager.getConfiguredNetworks();

                for (WifiConfiguration configuration : list) {
                    Log.e(TAG, configuration.toString());
                }

            }
        });

        findViewById(R.id.wifi_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean succeeded = manager.startScan();
                Log.e(TAG,"##start scan"+succeeded);
            }
        });


        findViewById(R.id.scan_result).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开启定位权限
                //Turn on targeting permissions
                List<ScanResult> scanResults = manager.getScanResults();
                for (ScanResult scanResult : scanResults) {
                    Log.e(TAG, scanResult.SSID);
                }
            }
        });


        findViewById(R.id.wifi_enable).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.setWifiEnabled(true);
                Log.e(TAG, "WiFi enabled");
            }
        });


        findViewById(R.id.wifi_disable).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.setWifiEnabled(false);
                Log.e(TAG, "WiFi disabled");
            }
        });


        findViewById(R.id.wifi_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //密码错误也会添加成功，只不过连接不上
                //Password error will also be added successfully, but not connected
                WifiConfiguration configuration = new WifiConfiguration();
                configuration.SSID = "\"" + wifiSsid + "\"";
                configuration.preSharedKey = "\"" + wifiPassword + "\"";

//                //WPA-PSK
//                configuration.preSharedKey = "\"" + "agent#99" + "\"";
                //WEP
                //configuration.wepKeys[0] = "\"" + 123456 + "\"";

//                WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
//                wifiManager.addNetwork(conf);

                custom_wifi_id = manager.addNetwork(configuration);
                boolean succeeded = manager.enableNetwork(custom_wifi_id, true);

                Log.e(TAG, "succeeded: " + succeeded);
                Log.e(TAG, "Network added : "+custom_wifi_id);
            }
        });


        findViewById(R.id.wifi_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             //   WifiInfo info = manager.getConnectionInfo();
            //    manager.removeNetwork(info.getNetworkId());
                boolean reply = manager.removeNetwork(custom_wifi_id);

                Log.e(TAG," Network Removed : "+custom_wifi_id+reply);
            }
        });


        findViewById(R.id.wifi_enable_one).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<WifiConfiguration> list = manager.getConfiguredNetworks();
                for (WifiConfiguration i : list) {
                    if (i.SSID != null && i.SSID.equals("\"" + wifiSsid + "\"")) {
                        manager.disconnect();
                        boolean succeeded = manager.enableNetwork(i.networkId,true);
                        Log.e(TAG, "wifi_enable_one succeeded: " + custom_wifi_id+succeeded);
                        manager.reconnect();
                        break;
                    }
                }

//                boolean succeeded =   manager.enableNetwork(custom_wifi_id, true);
//                Log.e(TAG, "wifi_enable_one succeeded: " + custom_wifi_id+succeeded);
            }
        });

        findViewById(R.id.wifi_disable_one).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // boolean succeeded = manager.disableNetwork(manager.getConnectionInfo().getNetworkId());

                boolean succeeded = manager.disableNetwork(custom_wifi_id);
                Log.e(TAG, " wifi_disable_one succeeded: " +custom_wifi_id+ succeeded);

            }
        });


        findViewById(R.id.wifi_disconnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean succeeded = manager.disconnect();

                Log.e(TAG, " wifi_disconnect succeeded: " + succeeded);

            }
        });


        findViewById(R.id.wifi_reconnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean reply = manager.reassociate();
                Log.e(TAG, " wifi_reassociate succeeded: " + reply);
                boolean succeeded = manager.reconnect();

                Log.e(TAG, " wifi_reconnect succeeded: " + succeeded);

            }
        });

        findViewById(R.id.create_hotspot).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                Log.e(TAG,"Hotspot button clicked!");

                getWifiApIpAddress();

//                manager.startLocalOnlyHotspot(new WifiManager.LocalOnlyHotspotCallback() {
//                    @Override
//                    public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
//                        super.onStarted(reservation);
//                        Log.e(TAG, "#Wifi Hotspot is on now");
//                        Log.e(TAG, "#Wifi Hotspot SSID : "+reservation.getWifiConfiguration().SSID);
//                        Log.e(TAG, "#Wifi Hotspot Password : "+reservation.getWifiConfiguration().preSharedKey);
//                        WifiConfiguration config =reservation.getWifiConfiguration();
//                        mReservation = reservation;
//                        getWifiApIpAddress();
//                    }
//
//                    @Override
//                    public void onStopped() {
//                        super.onStopped();
//                        Log.d(TAG, "#onStopped: ");
//                    }
//
//                    @Override
//                    public void onFailed(int reason) {
//                        super.onFailed(reason);
//                        Log.d(TAG, "#onFailed: ");
//                    }
//
//                },new Handler());


            }
        });

        findViewById(R.id.stop_hotspot).setOnClickListener(new View.OnClickListener(){
           @RequiresApi(api = Build.VERSION_CODES.O)
           @Override
            public void onClick(View v){
               if (mReservation != null)
               mReservation.close();
           }
        });
    }



}

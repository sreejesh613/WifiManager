package com.qiwoo.wifimanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {


    public static final String TAG = MainActivity.class.getSimpleName();

    private WifiManager manager;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {

                Toast.makeText(context, "扫描成功", Toast.LENGTH_SHORT).show();

            }
        }
    };

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
            }
        });


        findViewById(R.id.scan_result).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开启定位权限
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
            }
        });


        findViewById(R.id.wifi_disable).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.setWifiEnabled(false);
            }
        });


        findViewById(R.id.wifi_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //密码错误也会添加成功，只不过连接不上
                WifiConfiguration configuration = new WifiConfiguration();
                configuration.SSID = "live_iot";

                //WPA-PSK
                configuration.preSharedKey = "\"" + 123456 + "\"";
                //WEP
                //configuration.wepKeys[0] = "\"" + 123456 + "\"";

                int id = manager.addNetwork(configuration);
                boolean succeeded = manager.enableNetwork(id, true);

                Log.e(TAG, "succeeded: " + succeeded);
            }
        });


        findViewById(R.id.wifi_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                WifiInfo info = manager.getConnectionInfo();
                manager.removeNetwork(info.getNetworkId());

            }
        });


        findViewById(R.id.wifi_enable_one).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean succeeded = manager.enableNetwork(manager.getConnectionInfo().getNetworkId(), true);

                Log.e(TAG, "succeeded: " + succeeded);
            }
        });

        findViewById(R.id.wifi_disable_one).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean succeeded = manager.disableNetwork(manager.getConnectionInfo().getNetworkId());

                Log.e(TAG, "succeeded: " + succeeded);

            }
        });


        findViewById(R.id.wifi_disconnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean succeeded = manager.disconnect();

                Log.e(TAG, "succeeded: " + succeeded);

            }
        });


        findViewById(R.id.wifi_reconnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean succeeded = manager.reconnect();

                Log.e(TAG, "succeeded: " + succeeded);

            }
        });
    }
}

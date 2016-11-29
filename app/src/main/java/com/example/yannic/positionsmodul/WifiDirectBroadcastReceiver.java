package com.example.yannic.positionsmodul;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

/**
 * Created by Yannic on 25.10.2016.
 */

public class WifiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;
    private MainActivity mainActivity;
    private final String LOG_TAG = String.valueOf(this.getClass());

    public WifiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, MainActivity mainActivity) {
        super();
        this.wifiP2pManager = manager;
        this.channel = channel;
        this.mainActivity = mainActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                mainActivity.setWifiEnabled(true);
            } else {
                mainActivity.setWifiEnabled(false);
                //@TODO reset date if needed?
            }
            Log.v(LOG_TAG, "P2P state changed - " + state);
        } else if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            if(wifiP2pManager != null) {
                wifiP2pManager.requestPeers(channel, mainActivity);
            }
            Log.v(LOG_TAG,"Peers List changed");
        } else if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if (wifiP2pManager != null) {
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                if (networkInfo.isConnected()) {
                    wifiP2pManager.requestConnectionInfo(channel, mainActivity);
                } else {
                    mainActivity.startSearchTask();
                    Log.v(LOG_TAG, "No Connection");
                    mainActivity.tfConStatus.setText("Not Connected");
                }
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            //@TODO
        }
    }
}

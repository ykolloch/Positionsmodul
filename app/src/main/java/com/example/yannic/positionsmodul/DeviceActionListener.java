package com.example.yannic.positionsmodul;

import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;

/**
 * Created by Yannic on 25.10.2016.
 */

public interface DeviceActionListener {

    void showDetails(WifiP2pDevice wifiP2pDevice);
    void connect(WifiP2pConfig wifiP2pConfig);
    void disconnect();
}

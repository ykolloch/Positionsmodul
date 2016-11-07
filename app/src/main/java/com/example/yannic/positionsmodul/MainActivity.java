package com.example.yannic.positionsmodul;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements WifiP2pManager.ChannelListener, DeviceActionListener, WifiP2pManager.PeerListListener, WifiP2pManager.ConnectionInfoListener {

    Button btnConnect, btnDisc;
    TextView tfConStatus;
    ListView listViewDevices;
    private boolean isWifiEnabled = false;
    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver = null;


    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    private ArrayAdapter<WifiP2pDevice> adapter;
    private WifiP2pDeviceList p2pDeviceList;
    private ScanTask scanTask;
    private List<WifiP2pDevice> flaggedDevices = new ArrayList<>();
    private WifiP2pInfo wifiP2pInfo;
    private WifiP2pConfig wifiP2pConfig;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.findAllbyID();

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        flaggedDevices.clear();                                                                         //unnecessary i guess.

        /**
         * Starts the async to Scan for devices.
         */
        scanTask = new ScanTask(manager, channel, this);
        scanTask.execute();                                                                             //maybe into constructor.

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wifiP2pConfig != null && wifiP2pInfo != null && wifiP2pInfo.groupFormed) {
                    for (int i = 0; i < peers.size(); i++) {
                        if (wifiP2pConfig.deviceAddress.equals(peers.get(i).deviceAddress)) {
                            flaggedDevices.add(peers.get(i));
                            Log.v("Flagged", "Added " + peers.get(i).deviceAddress + " to flagged devices");
                            disconnect();
                        }
                    }
                }
                connectToAll(p2pDeviceList);
            }
        });

        adapter = new ArrayAdapter<WifiP2pDevice>(this, android.R.layout.simple_list_item_1, android.R.id.text1, peers);
        adapter = new CustomAdapter(this, peers);
        listViewDevices.setAdapter(adapter);

        btnDisc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new WifiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    private void findAllbyID() {
        tfConStatus = (TextView) findViewById(R.id.tfConStatus);
        btnConnect = (Button) findViewById(R.id.btnConnect);
        btnDisc = (Button) findViewById(R.id.btnDisc);
        listViewDevices = (ListView) findViewById(R.id.listViewDevices);
    }

    public ListView getListView() {
        return listViewDevices;
    }

    public boolean isWifiEnabled() {
        return isWifiEnabled;
    }

    public void setWifiEnabled(boolean wifiEnabled) {
        isWifiEnabled = wifiEnabled;
    }

    @Override
    public void onChannelDisconnected() {
        //@TODO
        tfConStatus.setText("Not Connected on Channel");
        resetData();
    }

    @Override
    public void showDetails(WifiP2pDevice wifiP2pDevice) {
        //@TODO
    }

    @Override
    public void connect(final WifiP2pConfig wifiP2pConfig) {
        manager.connect(channel, wifiP2pConfig, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                tfConStatus.setText("Trying to connect to: " + wifiP2pConfig.deviceAddress);
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(MainActivity.this, "Failed to Connect", Toast.LENGTH_LONG).show();
                tfConStatus.setText("Failed to connect to: " + wifiP2pConfig.deviceAddress);
            }
        });
    }

    @Override
    public void disconnect() {
        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                scanTask = new ScanTask(manager, channel, MainActivity.this);
                scanTask.execute();
                resetData();
            }

            @Override
            public void onFailure(int reason) {

            }
        });
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        Log.v("peers", String.valueOf(peers.getDeviceList()));
        this.peers.clear();
        this.peers.addAll(peers.getDeviceList());
        this.p2pDeviceList = peers;
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        if (info.groupFormed && !info.isGroupOwner) {
            this.wifiP2pInfo = info;
            scanTask.cancel(true);
            tfConStatus.setText("Connected to: " + info.groupOwnerAddress);
            Log.v("info", "Connected");
            new TransferData(info.groupOwnerAddress.getHostAddress(), 8288).execute();
        }
    }

    /**
     * Automated connect to simulate Hardware pairing.
     *
     * @param wifiP2pDeviceList
     */
    private void connectToAll(WifiP2pDeviceList wifiP2pDeviceList) {
        if (p2pDeviceList == null) {
            Toast.makeText(this, "No Devices to pair", Toast.LENGTH_SHORT).show();
            return;
        }
        List<WifiP2pDevice> allPeers = new ArrayList<>();
        allPeers.addAll(wifiP2pDeviceList.getDeviceList());
        if (allPeers.size() > 0) {
            WifiP2pDevice device = allPeers.get(0);
            wifiP2pConfig = new WifiP2pConfig();
            wifiP2pConfig.groupOwnerIntent = 0;                     //so client is not group owner
            wifiP2pConfig.deviceAddress = device.deviceAddress;
            wifiP2pConfig.wps.setup = WpsInfo.PBC;
            connect(wifiP2pConfig);
        }
        while(allPeers.size() > 0) {

        }
    }

    /**
     * reset if disconnected.
     */
    private void resetData() {
        this.wifiP2pInfo = null;
        this.wifiP2pConfig = null;
    }
}

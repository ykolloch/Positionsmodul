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
import android.text.LoginFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements WifiP2pManager.ChannelListener, DeviceActionListener, WifiP2pManager.PeerListListener, WifiP2pManager.ConnectionInfoListener {

    Button btnScan, btnConnect, btnDisc;
    ListView listViewDevices;
    private boolean isWifiEnabled = false;
    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver = null;
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    private WifiP2pDevice device;
    private ArrayAdapter<WifiP2pDevice> adapter;


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

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isWifiEnabled()) {
                    //@TODO
                    return;
                }
                manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(MainActivity.this, "Searching", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int reason) {
                        Toast.makeText(MainActivity.this, "Failed Searching", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });



        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiP2pConfig wifiP2pConfig = new WifiP2pConfig();
                wifiP2pConfig.deviceAddress = device.deviceAddress;
                wifiP2pConfig.wps.setup = WpsInfo.PBC;
                connect(wifiP2pConfig);
            }
        });

        adapter = new ArrayAdapter<WifiP2pDevice>(this, android.R.layout.simple_list_item_1, android.R.id.text1, peers);
        listViewDevices.setAdapter(adapter);

        listViewDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                device = peers.get(position);
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
        btnConnect = (Button) findViewById(R.id.btnConnect);
        btnDisc = (Button) findViewById(R.id.btnDisc);
        btnScan = (Button) findViewById(R.id.btnScan);
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
    }

    @Override
    public void showDetails(WifiP2pDevice wifiP2pDevice) {
        //@TODO
    }

    @Override
    public void connect(WifiP2pConfig wifiP2pConfig) {
        if(device != null) {
            manager.connect(channel, wifiP2pConfig, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFailure(int reason) {
                    Toast.makeText(MainActivity.this, "Failed to Connect", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Log.v("SELECT", "Nothing Selected");
        }
    }

    @Override
    public void disconnect() {

    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        Log.v("peers", String.valueOf(peers.getDeviceList()));
        this.peers.clear();
        this.peers.addAll(peers.getDeviceList());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        Log.v("OWNER", String.valueOf(info.isGroupOwner));
        if(info.groupFormed) {
            Log.v("info", "Connected");
            Log.v("info", info.groupOwnerAddress.getHostAddress());
            new TransferData(info.groupOwnerAddress.getHostAddress(), 8388).execute();
        }
    }
}

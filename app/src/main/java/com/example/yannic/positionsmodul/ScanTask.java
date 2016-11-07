package com.example.yannic.positionsmodul;

import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Yannic on 07.11.2016.
 */

public class ScanTask extends AsyncTask<Void, Void, String> {

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private MainActivity activity;
    private Boolean b = true;

    public ScanTask(WifiP2pManager manager, WifiP2pManager.Channel channel, MainActivity activity) {
        Log.v("ScanTask", "Start ScanTask");
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    @Override
    protected String doInBackground(Void... params) {
        while(b) {
            manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(activity, "Searching", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(int reason) {
                    Toast.makeText(activity, "Failed Searching", Toast.LENGTH_LONG).show();
                }
            });
            try {
                Thread.sleep(30000);            //sleeps for 30sec.
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            b = !isCancelled();
        }
        return null;
    }

    @Override
    protected void onCancelled() {
        Log.v("ScanTask", "Canceled");
    }
}

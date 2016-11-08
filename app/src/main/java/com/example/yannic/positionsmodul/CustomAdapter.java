package com.example.yannic.positionsmodul;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Yannic on 07.11.2016.
 */

public class CustomAdapter extends ArrayAdapter<WifiP2pDevice> {

    public CustomAdapter(Context context, List<WifiP2pDevice> wifiP2pDevices) {
        super(context, R.layout.row_decvices, wifiP2pDevices);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.row_decvices, parent, false);

        String deviceName = getItem(position).deviceName;
        String s = getItem(position).deviceAddress;

        TextView t1 = (TextView) view.findViewById(R.id.firstLine);
        TextView t2 = (TextView) view.findViewById(R.id.secondLine);

        t1.setText(deviceName);
        t2.setText(s);
        return view;
    }
}

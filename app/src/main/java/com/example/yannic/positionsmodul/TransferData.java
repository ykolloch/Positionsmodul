package com.example.yannic.positionsmodul;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Yannic on 25.10.2016.
 */

public class TransferData extends AsyncTask<Void, Void, String> {

    private String ip;
    private int port;
    private GPS gps;
    private static Boolean done = false;
    private final String LOG_TAG = String.valueOf(this.getClass());

    public TransferData(String ip, int port) {
        this.ip = ip;
        this.port = port;
        gps = GPS.getReference();
    }

    @Override
    protected String doInBackground(Void... params) {
        Socket socket = null;
        try {
            Thread.sleep(5000);
            socket = new Socket();
            socket.bind(null);
            socket.connect(new InetSocketAddress(ip, port), 5000);

            DataOutputStream stream = new DataOutputStream(socket.getOutputStream());
            while (!done) {
                Log.v(LOG_TAG, "Send NMEA");
                stream.writeByte(1);
                stream.writeUTF(gps.getNMEA());
                stream.flush();
                Thread.sleep(1000);
            }
            stream.write(-1);
            stream.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                    Log.v(LOG_TAG, "Socket closed");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void setDone(Boolean bDone) {
        done = bDone;
    }
}

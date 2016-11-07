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

    public TransferData(String ip, int port) {
        this.ip = ip;
        this.port = port;
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
            for (int i = 0; i < 20; i++) {
                stream.writeByte(1);
                stream.writeUTF("foo bar");
                stream.flush();
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
                    Log.v("Client", "Socket closed");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}

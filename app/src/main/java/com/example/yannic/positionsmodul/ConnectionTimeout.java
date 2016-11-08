package com.example.yannic.positionsmodul;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by Yannic on 08.11.2016.
 */

public class ConnectionTimeout extends AsyncTask<Void, Void, String> {

    private Boolean connection = false;
    private MainActivity activity;
    private final String LOG_TAG = this.getClass().toString();
    private final static int TIMEOUT = 10000;           //@TODO change time!

    public ConnectionTimeout(final MainActivity mainActivity) {
        Log.v(LOG_TAG, "Start Timeout Task");
        this.activity = mainActivity;
    }

    /**
     * If connection is not set on true within __ seconds the thread will return false;
     * @param params
     * @return true or false;
     */
    @Override
    protected String doInBackground(Void... params) {
        try {
            Thread.sleep(TIMEOUT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String aBoolean) {
        if(!connection) {
            activity.cancelInvite();
        }
    }

    public void setConnection(Boolean connection) {
        this.connection = connection;
    }
}

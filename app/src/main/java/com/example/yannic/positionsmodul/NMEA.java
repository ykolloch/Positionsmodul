package com.example.yannic.positionsmodul;

import java.io.Serializable;

/**
 * Created by Yannic on 25.10.2016.
 */

public class NMEA implements Serializable {

    private String name;

    public NMEA(String s) {
        this.name = s;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

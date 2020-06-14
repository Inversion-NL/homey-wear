package com.xseth.homey.utils;

import android.content.Context;

import com.xseth.homey.homey.Device;

public class utils {

    public static final String[] demoDataset = {"homey", "lamp", "roomba", "alarm", "koffie"};

    public static Device[] generateDemoDevices(Context ctx){
        Device[] devices = new Device[demoDataset.length];

        for (int i = 0; i < demoDataset.length; i++) {
            devices[i] = new Device(demoDataset[i], ctx);
        }

        return devices;
    }
}

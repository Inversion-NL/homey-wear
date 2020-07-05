package com.xseth.homey.homey.models;

public class Homey {

    private String _id;
    private String name;
    private String localUrl;
    private String localUrlSecure;
    private String remoteUrl;

    public String getId() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public String getRemoteUrl() {
        return remoteUrl;
    }
}

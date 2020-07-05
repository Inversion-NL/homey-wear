package com.xseth.homey.homey.models;

public class Homey {

    // Homey id
    private String _id;
    // Homey name
    private String name;
    // local URL used for accessing Homey API locally unsecure (HTTP)
    private String localUrl;
    // URL used for accessing Homey API locally securely (HTTPS)
    private String localUrlSecure;
    // URL used for accessing Homey API globally securely (HTTPS)
    private String remoteUrl;

    /**
     * Get id of Homey
     * @return id of Homey
     */
    public String getId() {
        return _id;
    }

    /**
     * Get name of Homey
     * @return name of Homey
     */
    public String getName() {
        return name;
    }

    /**
     * Get remote URL to access Homey API
     * @return remote URL of Homey
     */
    public String getRemoteUrl() {
        return remoteUrl;
    }
}

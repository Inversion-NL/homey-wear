package com.xseth.homey.homey.models;

import com.google.gson.annotations.SerializedName;

public class Homey {

    // Homey id
    @SerializedName("_id")
    private String _id;

    // Homey name
    @SerializedName("name")
    private String name;

    // local URL used for accessing Homey API locally unsecure (HTTP)
    @SerializedName("localUrl")
    private String localUrl;

    // URL used for accessing Homey API locally securely (HTTPS)
    @SerializedName("localUrlSecure")
    private String localUrlSecure;

    // URL used for accessing Homey API globally securely (HTTPS)
    @SerializedName("remoteUrl")
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

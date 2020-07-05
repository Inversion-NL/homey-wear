package com.xseth.homey.homey.models;

import java.util.List;
import java.util.Map;

public class User {

    private String firstname;
    private String lastname;
    private List<Homey> homeys;
    private Map<String, List<String>> properties;

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public Homey getFirstHomey(){
        return homeys.get(0);
    }

    public List<String> getDeviceFavorites() { return this.properties.get("favoriteDevices"); }
}

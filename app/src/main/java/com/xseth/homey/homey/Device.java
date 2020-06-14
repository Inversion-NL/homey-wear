package com.xseth.homey.homey;

import android.content.Context;

public class Device {

    private String name;
    private Boolean on;
    private Context context;

    public Device(String name, Context ctx){
        this.name = name;
        this.context = ctx;
        this.on = true;
    }

    public String getTitle(){
        return this.name;
    }

    public Boolean isOn(){
        return this.on;
    }

    public void inverse(){
        this.on = !this.on;
    }

    public int getIcon(){
        String resource_name = "ic_"+this.name;
        String package_name = context.getPackageName();
        return this.context.getResources().getIdentifier(resource_name, "drawable" , package_name);
    }
}

package com.xseth.homey.homey;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "devices")
public class Device {

    @PrimaryKey
    @NonNull
    private String id;

    private String name;
    private Boolean on;

    public Device(String id, String name){
        this.id = id;
        this.name = name;
        this.on = true;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOn(Boolean on) {
        this.on = on;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
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
        //String package_name = context.getPackageName();
        //return this.context.getResources().getIdentifier(resource_name, "drawable" , package_name);
        return 0;
    }
}

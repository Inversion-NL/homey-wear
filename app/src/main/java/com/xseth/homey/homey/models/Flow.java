package com.xseth.homey.homey.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.xseth.homey.homey.HomeyAPI;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import timber.log.Timber;

@Entity(tableName = "flows")
public class Flow {

    // Flow ID
    @PrimaryKey
    @NonNull
    private String id;

    // Flow Name
    @NonNull
    private String name;

    // Flow enabled
    @NonNull
    private Boolean enabled;

    // Flow folder
    @Ignore
    private Object folder;

    @NonNull
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(@NonNull Boolean enabled) {
        this.enabled = enabled;
    }

    public Object getFolder() {
        return folder;
    }

    public void setFolder(Object folder) {
        this.folder = folder;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Object getTrigger() {
        return trigger;
    }

    public void setTrigger(Object trigger) {
        this.trigger = trigger;
    }

    public List<Object> getConditions() {
        return conditions;
    }

    public void setConditions(List<Object> conditions) {
        this.conditions = conditions;
    }

    public List<Object> getActions() {
        return actions;
    }

    public void setActions(List<Object> actions) {
        this.actions = actions;
    }

    // Flow order
    @Ignore
    private Integer order;

    // Flow trigger
    @Ignore
    private Object trigger;

    // Flow conditions
    @Ignore
    private List<Object> conditions;

    // Flow actions
    @Ignore
    private List<Object> actions;

    /**
     * Device constructor
     * @param id device ID
     * @param name device name
     */
    public Flow(String id, String name){
        this.id = id;
        this.name = name;
    }

    /**
     * Get device ID
     * @return device ID
     */
    @NonNull
    public String getId() {
        return id;
    }

    /**
     * Get the device name
     * @return device name
     */
    @NonNull
    public String getName() {
        return name;
    }

    /**
     * Set device ID
     * @param id id to set
     */
    public void setId(@NonNull String id) {
        this.id = id;
    }

    /**
     * Set name
     * @param name name to set
     */
    public void setName(@NonNull String name) {
        this.name = name;
    }

    public Call triggerFlow(){
        HomeyAPI api = HomeyAPI.getAPI();
        // Wait if HomeyAPI is not yet authenticated
        api.waitForHomeyAPI();

        return api.triggerFlow(this);
    }
}

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

@Entity(tableName = "devices")
public class Device {

    // Device ID
    @PrimaryKey
    @NonNull
    private String id;

    // Device Name
    @NonNull
    private String name;

    // Device on or off
    @NonNull
    private Boolean on;

    // Device icon
    public Bitmap iconImage;

    // Capability which is modified
    @NonNull
    public String capability;

    // Capabilities Object returned by API
    @Ignore
    private Map<String, Map<String, Object>> capabilitiesObj;

    // Icon Object returned by API, containing icon IDs
    @Ignore
    private Map<String, String> iconObj;

    /**
     * Device constructor
     * @param id device ID
     * @param name device name
     */
    public Device(String id, String name){
        this.id = id;
        this.name = name;
        this.on = true;
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
     * Get the Icon bitmap
     * @return icon of device
     */
    public Bitmap getIconImage(){
        return this.iconImage;
    }

    /**
     * Get capability which is used
     * @return capability which is used
     */
    @NonNull
    public String getCapability() { return this.capability; }

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

    /**
     * Determine which capability of devices is used by this app. Get latest status of capability
     *
     * Available options are listed in HomeyAPI. Default value is onoff
     */
    public void setCapability(){
        List<String> capabilities = Arrays.asList(HomeyAPI.CAPABILITIES);

        for(String capability : this.capabilitiesObj.keySet()){
            if(capabilities.contains(capability)) {
                this.capability = capability;

                if(!capability.equals("button"))
                    this.on = Boolean.getBoolean(
                            this.capabilitiesObj.get(capability).get("value").toString()
                    );
                else
                    // Button contains no value, so default to true
                    this.on = true;
            }
        }

        // onoff is fallback capability
        if(this.capability == null) {
            this.capability = "onoff";
            this.on = true;
        }
    }

    /**
     * download the icon in bitmap form
     */
    public void fetchIconImage() {
        String iconId = this.iconObj.get("id");
        final String strUrl = HomeyAPI.ICON_URL + iconId + "-128.png";

        try{
            URL url = new URL(strUrl);
            URLConnection conn = url.openConnection();

            this.iconImage = BitmapFactory.decodeStream(conn.getInputStream());
        } catch (MalformedURLException mue) {
            Timber.e(mue, "Error invalid iconUrl");
        } catch (IOException ioe) {
            Timber.e(ioe,"Error downloading icon from: %s", strUrl);
        }
    }

    /**
     * Set the device on or off status
     * @param on on value to set
     */
    public void setOn(Boolean on){ this.on = on; }

    /**
     * Verify whether this device is turned on or off
     * @return whether onoff is different than currently in device
     */
    public boolean verifyOnOff(boolean onoff){
        if (onoff != this.on){
            this.setOn(onoff);
            return true;
        }

        return false;
    }

    /**
     * Return whether this device is on or off
     * @return if device is on
     */
    public Boolean isOn(){
        return this.on;
    }

    /**
     * Return whether this device contains button capability
     * @return if device is a button
     */
    public boolean isButton(){
        return this.getCapability().equals("button");
    }

    /**
     * Turn device on or off based on on value
     */
    public Call turnOnOff() {
        HomeyAPI api = HomeyAPI.getAPI();
        // Wait if HomeyAPI is not yet authenticated
        api.waitForHomeyAPI();

        return api.turnOnOff(this);
    }

    /**
     * Get the value (on|off) of capability specified by ID
     * @param id capability ID to get value from
     * @return boolean value whether capability is on|off
     */
    public boolean getCapabilityValue(String id){
        Map<String, Object> capability = this.capabilitiesObj.get(id);

        // If capability is not found or if button, fallback is true
        if(capability == null || this.isButton())
            return true;

        return (Boolean) capability.get("value");
    }
}

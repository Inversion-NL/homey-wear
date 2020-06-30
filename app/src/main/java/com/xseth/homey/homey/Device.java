package com.xseth.homey.homey;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.chaquo.python.PyObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

@Entity(tableName = "devices")
public class Device {

    // Logging TAG
    @Ignore
    public static final String TAG = "Device";

    // Device ID
    @PrimaryKey
    @NonNull
    private String id;

    // Device Name
    @NonNull
    private String name;

    // Device capability that is controlled
    @NonNull
    private String capability;

    // Device on or off
    @NonNull
    private Boolean on;

    // Device icon
    private Bitmap icon;

    /**
     * Device constructor
     * @param id device ID
     * @param name device name
     */
    public Device(String id, String name){
        this.id = id;
        this.name = name;
        this.on = true;
        this.capability = "onoff"; // fallback capability
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
    public Bitmap getIcon(){
        return this.icon;
    }

    @NonNull
    public String getCapability() { return capability; }

    public void setCapability(@NonNull String capability ) { this.capability = capability; }

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
     * Set the icon Bitmap
     * @param bitmap icon to set
     */
    public void setIcon(Bitmap bitmap) { this.icon = bitmap; }

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
        if(this.on == null)
            return true;

        return this.on;
    }

    /**
     * Return whether this device contains button capability
     * @return if device is a button
     */
    public boolean isButton(){
        return this.capability.equals("button");
    }

    /**
     * Turn device on or off based on on value
     */
    public void turnOnOff(){
        HomeyAPI api = HomeyAPI.getAPI();
        // Wait if HomeyAPI is not yet authenticated
        api.waitForHomeyAPI();

        // Button remains true
        if(!isButton())
            this.on = !this.on;

        api.turnOnOff(this);
        DeviceRepository.getInstance().update(this);
    }

    /**
     * Parse Python Device object to Java Device object. This fetches device in background
     * @param pyDevice Python Device object
     * @return Java Device object
     */
    public static Device parsePyDevice(PyObject pyDevice){
        Timber.v("Start parsing pyObject: %s", pyDevice.toString());

        String id = pyDevice.get("id").toString();
        String name = pyDevice.get("name").toString();

        Device device = new Device(id, name);

        final String iconId = pyDevice.get("iconObj").asMap().get("id").toString();
        final String strUrl = HomeyAPI.ICON_URL + iconId + "-128.png"; // 128 icon size

        // Set capability that is turned on/off, priority capability is in order
        // of capabilities in HomeyAPI.CAPABILITIES
        List<String> homeyCapabilities = Arrays.asList(HomeyAPI.CAPABILITIES);
        Map<PyObject, PyObject> capabilities = pyDevice.get("capabilitiesObj").asMap();

        for(PyObject capabilityId : capabilities.keySet()){
            Timber.d("Parse PyDevice, verifying capability: %s", capabilityId.toString());

            // Capability is in capabilities whitelist
            if(homeyCapabilities.contains(capabilityId.toString())){
                Timber.d("Parse PyDevice, found capability match: %s", capabilityId.toString());

                boolean status = true;
                PyObject pyStatus = capabilities.get(capabilityId).asMap().get("value");

                // if capability is button then value is always null, so for button status is
                // always true
                if(pyStatus != null)
                    status = pyStatus.toBoolean();

                device.setCapability(capabilityId.toString());
                device.setOn(status);

                break;
            }
        }

        // Fetch icon
        try{
            URL url = new URL(strUrl);

            URLConnection conn = url.openConnection();
            device.setIcon(BitmapFactory.decodeStream(conn.getInputStream()));
            DeviceRepository.getInstance().update(device);
        } catch (MalformedURLException mue) {
            Timber.e(mue, "Error invalid iconUrl");
        } catch (IOException ioe) {
            Timber.e(ioe,"Error downloading icon from: %s", strUrl);
        }

        return device;
    }
}

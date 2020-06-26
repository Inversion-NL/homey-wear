package com.xseth.homey.homey;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.chaquo.python.PyObject;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@Entity(tableName = "devices")
public class Device {

    @Ignore
    public static final String TAG = "Device";

    @PrimaryKey
    @NonNull
    private String id;
    @NonNull
    private String name;
    @NonNull
    private Boolean on;

    @Ignore
    private Bitmap icon;

    public Device(String id, String name){
        this.id = id;
        this.name = name;
        this.on = true;

        // Check whether device is on or off
        new Thread(() -> {
            this.verifyOnOff();
        }).start();
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public Bitmap getIcon(){
        return this.icon;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }
    public void setIcon(Bitmap bitmap) { this.icon = bitmap; }

    public void setOn(Boolean on){ this.on = on; }

    /**
     * Verify whether this device is turned on or off
     * @return whether onoff is different than currently in device
     */
    public boolean verifyOnOff(){
        HomeyAPI api = HomeyAPI.getAPI();

        // Wait for HomeyAPI to be authenticated
        api.waitForHomeyAPI();

        boolean onoff = api.isOn(this);

        if (onoff != this.on){
            this.setOn(onoff);
            DeviceRepository.getInstance().update(this);
            return true;
        }

        return false;
    }

    public Boolean isOn(){
        if(this.on == null)
            return true;

        return this.on;
    }

    public void turnOnOff(){
        HomeyAPI api = HomeyAPI.getAPI();
        // Wait if HomeyAPI is not yet authenticated
        api.waitForHomeyAPI();

        boolean onoff = !this.on;
        api.turnOnOff(this, onoff);

        this.setOn(onoff);
        DeviceRepository.getInstance().update(this);
    }

    public static Device parsePyDevice(PyObject pyDevice){
        Log.v(TAG, "Start parsing pyObject: "+pyDevice.toString());

        String id = pyDevice.get("id").toString();
        String name = pyDevice.get("name").toString();

        Device device = new Device(id, name);

        final String strUrl = HomeyAPI.getAPI().getHomeyURL() + pyDevice.get("iconObj").asMap().get("url").toString();

        // Fetch SVG icon in the background
        new Thread(() -> {
            try{
                URL url = new URL(strUrl);

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                //xpp.setInput(urlConnection.getInputStream(), "utf-8");

                //Drawable drawable = VectorDrawable.createFromXml(MainActivity.context.getResources(), xpp);

            } catch (MalformedURLException mue) {
                Log.e(TAG, "Error invalid iconUrl: "+mue.getLocalizedMessage());
            } catch (IOException ioe) {
                Log.e(TAG, "Error downloading icon from: "+strUrl+"\n"+ioe.getLocalizedMessage());
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        }).start();

        return device;
    }
}

package com.xseth.homey.homey;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.graphics.drawable.VectorDrawable;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.chaquo.python.PyObject;
import com.xseth.homey.MainActivity;
import com.xseth.homey.storage.BitmapConverter;
import com.xseth.homey.utils.HomeyAPI;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@Entity(tableName = "devices")
public class Device extends AsyncTask<String, Void, Bitmap>  {

    public static final String TAG = "Device";

    @PrimaryKey
    @NonNull
    private String id;
    @NonNull
    private String name;

    private Boolean on;
    @Ignore
    private Bitmap icon;

    public Device(String id, String name){
        this.id = id;
        this.name = name;
        this.on = true;

        // Check whether device is on or off
        new Thread(() -> {
            HomeyAPI api = HomeyAPI.getAPI();

            // Wait for HomeyAPI to be authenticated
            api.waitForHomeyAPI();

            this.setOn(api.isOn(this));
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

    public Boolean isOn(){
        if(this.on == null)
            return true;

        return this.on;
    }

    public void turnOnOff(){
        // Check whether device is on or off
        new Thread(() -> {
            HomeyAPI api = HomeyAPI.getAPI();
            // Wait if HomeyAPI is not yet authenticated
            api.waitForHomeyAPI();

            api.turnOnOff(this);

            this.setOn(!this.isOn());
        }).start();
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        Bitmap result = null;
        Log.i(TAG, "Downloading icon from: "+urls[0]);

        try{
            URL url = new URL(urls[0]);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            //factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            xpp.setInput(urlConnection.getInputStream(), "utf-8");

            Drawable drawable = VectorDrawable.createFromXml(MainActivity.context.getResources(), xpp);

            Log.i(TAG, "PNG File: "+drawable.toString());
        } catch (MalformedURLException mue) {
            Log.e(TAG, "Error invalid iconUrl: "+mue.getLocalizedMessage());
        } catch (IOException ioe) {
            Log.e(TAG, "Error downloading icon from: "+urls[0]+"\n"+ioe.getLocalizedMessage());
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        //Log.i(TAG, bitmap.toString());
        //this.setIcon(bitmap);
    }

    public static Device parsePyDevice(PyObject pyDevice){
        Log.i(TAG, "Start parsing pyObject");

        String id = pyDevice.get("id").toString();
        String name = pyDevice.get("name").toString();

        Device device = new Device(id, name);

        String url = HomeyAPI.getAPI().getHomeyURL();
        url += pyDevice.get("iconObj").asMap().get("url").toString();

        // Retrieve Icon in background
        //device.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);

        Log.i(TAG, "Started task");

        return device;
    }
}

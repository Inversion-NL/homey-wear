package com.xseth.homey.homey;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.chaquo.python.PyObject;
import com.xseth.homey.storage.DeviceDAO;
import com.xseth.homey.storage.HomeyRoomDatabase;
import com.xseth.homey.utils.HomeyAPI;

import java.util.List;

public class DeviceRepository {
    private static final String TAG = "DeviceRepository";

    private static DeviceRepository instance;

    private DeviceDAO deviceDAO;
    private LiveData<List<Device>> devices;

    public static DeviceRepository getInstance(){
        return instance;
    }

    public static DeviceRepository buildInstance(Application application){
        instance = new DeviceRepository(application);
        return instance;
    }

    public DeviceRepository(Application application) {
        HomeyRoomDatabase db = HomeyRoomDatabase.getDatabase(application);
        deviceDAO = db.deviceDAO();
        devices = deviceDAO.getDevices();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public synchronized LiveData<List<Device>> getAllDevices(boolean force) {
        new Thread(() -> {
            if(!deviceDAO.hasDevices() || force) {
                Log.i(TAG, "No saved devices, gathering");
                HomeyAPI api = HomeyAPI.getAPI();

                // Wait for HomeyAPI to be ready
                api.waitForHomeyAPI();

                // Save the devices in DB
                for (Device dev : api.getDevices())
                    this.insert(dev);
            }
        }).start();

        return devices;
    }

    public synchronized LiveData<List<Device>> getAllDevices() {
        return this.getAllDevices(false);
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void insert(final Device device) {
        HomeyRoomDatabase.databaseWriteExecutor.execute(() -> {
            deviceDAO.insert(device);
        });
    }

    public void update(final Device device) {
        HomeyRoomDatabase.databaseWriteExecutor.execute(() -> {
            deviceDAO.updateDevices(device);
        });
    }

    public void deleteDevice(Device dev){
        HomeyRoomDatabase.databaseWriteExecutor.execute(() -> {
            deviceDAO.deleteDevice(dev);
        });
    }

    public void deleteDevices(){
        HomeyRoomDatabase.databaseWriteExecutor.execute(() -> {
            deviceDAO.deleteAll();
        });
    }

}

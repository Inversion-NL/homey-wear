package com.xseth.homey.homey;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.xseth.homey.storage.DeviceDAO;
import com.xseth.homey.storage.HomeyRoomDatabase;

import java.util.List;

public class DeviceRepository {
    // Logging tag
    private static final String TAG = "DeviceRepository";
    // DeviceRepository instance
    private static DeviceRepository instance;
    // Device DAO for room access
    private DeviceDAO deviceDAO;
    // LiveData of devices
    private LiveData<List<Device>> devices;

    /**
     * Get instance of DeviceRepository
     * @return instance of DeviceRepository
     */
    public static DeviceRepository getInstance(){
        return instance;
    }

    /**
     * Build an instance of deviceRepository
     * @param application application used to create room used by deviceRepository
     * @return instance of DeviceRepository
     */
    public static DeviceRepository buildInstance(Application application){
        instance = new DeviceRepository(application);
        return instance;
    }

    /**
     * Device repository constructor
     * @param application application used to create room used by deviceRepository
     */
    public DeviceRepository(Application application) {
        HomeyRoomDatabase db = HomeyRoomDatabase.getDatabase(application);
        deviceDAO = db.deviceDAO();
        devices = deviceDAO.getDevices();
    }

    /**
     * Get list of available devices
     * @param force if force to pull device list from API
     * @return livedata list of devices
     */
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

    /**
     * Get list of devices
     * @return livedata list of devices
     */
    public synchronized LiveData<List<Device>> getAllDevices() {
        return this.getAllDevices(false);
    }

    /**
     * Add device to DB
     * @param device device to add
     */
    public void insert(final Device device) {
        HomeyRoomDatabase.databaseWriteExecutor.execute(() -> {
            deviceDAO.insert(device);
        });
    }

    /**
     * Update a device in room
     * @param device device to update
     */
    public void update(final Device device) {
        HomeyRoomDatabase.databaseWriteExecutor.execute(() -> {
            deviceDAO.updateDevices(device);
        });
    }

    /**
     * Delete device in room
     * @param device device to delete
     */
    public void deleteDevice(Device device){
        HomeyRoomDatabase.databaseWriteExecutor.execute(() -> {
            deviceDAO.deleteDevice(device);
        });
    }

    /**
     * Delete all devices in room
     */
    public void deleteDevices(){
        HomeyRoomDatabase.databaseWriteExecutor.execute(() -> {
            deviceDAO.deleteAll();
        });
    }

}

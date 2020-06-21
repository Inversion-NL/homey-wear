package com.xseth.homey.homey;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.xseth.homey.storage.DeviceDAO;
import com.xseth.homey.storage.HomeyRoomDatabase;

import java.util.List;

public class DeviceRepository {
    private DeviceDAO deviceDAO;
    private LiveData<List<Device>> devices;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public DeviceRepository(Application application) {
        HomeyRoomDatabase db = HomeyRoomDatabase.getDatabase(application);
        deviceDAO = db.deviceDAO();
        devices = deviceDAO.getDevices();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<Device>> getAllDevices() {
        return devices;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void insert(final Device device) {
        HomeyRoomDatabase.databaseWriteExecutor.execute(() -> {
            deviceDAO.insert(device);
            Log.d("DeviceViewModel", "Inserted");
        });
    }
}
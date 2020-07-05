package com.xseth.homey.adapters;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.xseth.homey.homey.models.Device;
import com.xseth.homey.homey.DeviceRepository;

import java.util.List;

public class DeviceViewModel extends AndroidViewModel {

    // DeviceRepository instance
    private DeviceRepository mRepository;
    // Livedata list of devices
    private LiveData<List<Device>> devices;

    /**
     * Constructor DeviceViewModel
     * @param application application used to create room used by deviceRepository
     */
    public DeviceViewModel (Application application) {
        super(application);

        mRepository = DeviceRepository.getInstance();
        if (mRepository == null)
            mRepository = DeviceRepository.buildInstance(application);

        devices = mRepository.getAllDevices();
    }

    /**
     * Get livedata list of devices
     * @return livedata list of devices
     */
    public LiveData<List<Device>> getDevices() { return devices; }

    /**
     * Refresh list of devices.
     *
     * Remove all devices from DB and pull latest devices via API
     */
    public void refreshDevices(){
        // Remove all devices from viewModel & DB
        mRepository.deleteDevices();

        // Force reloading of all devices
        mRepository.getAllDevices(true);
    }
}
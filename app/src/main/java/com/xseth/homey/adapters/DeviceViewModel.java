package com.xseth.homey.adapters;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.xseth.homey.homey.Device;
import com.xseth.homey.homey.DeviceRepository;

import java.util.List;

public class DeviceViewModel extends AndroidViewModel {

    private DeviceRepository mRepository;

    private LiveData<List<Device>> devices;

    public DeviceViewModel (Application application) {
        super(application);

        mRepository = DeviceRepository.getInstance();
        if (mRepository == null)
            mRepository = DeviceRepository.buildInstance(application);

        devices = mRepository.getAllDevices();
    }

    public LiveData<List<Device>> getDevices() { return devices; }

    public void updateDevice(Device device) { mRepository.update(device); }

    public void refreshDevices(){
        // Remove all devices from viewModel & DB
        for(Device dev : this.devices.getValue())
            mRepository.deleteDevices();

        // Force reloading of all devices
        mRepository.getAllDevices(true);
    }
}
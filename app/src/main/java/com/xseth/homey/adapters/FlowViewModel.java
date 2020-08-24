package com.xseth.homey.adapters;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.xseth.homey.homey.DeviceRepository;
import com.xseth.homey.homey.models.Device;
import com.xseth.homey.homey.models.Flow;

import java.util.List;

public class FlowViewModel extends AndroidViewModel {

    // DeviceRepository instance
    private DeviceRepository mRepository;
    // Livedata list of devices
    private LiveData<List<Flow>> flows;

    /**
     * Constructor DeviceViewModel
     * @param application application used to create room used by deviceRepository
     */
    public FlowViewModel(Application application) {
        super(application);

        mRepository = DeviceRepository.getInstance();
        if (mRepository == null)
            mRepository = DeviceRepository.buildInstance(application);

        flows = mRepository.getAllFlows();
    }

    /**
     * Get livedata list of devices
     * @return livedata list of devices
     */
    public LiveData<List<Flow>> getFlows() { return flows; }

    /**
     * Refresh list of devices.
     *
     * Remove all devices from DB and pull latest devices via API
     */
    public void refreshFlows(){
        // Remove all devices from viewModel & DB
        mRepository.deleteFlows();

        // Force reloading of all devices
        mRepository.getAllFlows(true);
    }
}
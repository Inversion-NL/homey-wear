package com.xseth.homey.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.wear.widget.WearableRecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xseth.homey.R;
import com.xseth.homey.adapters.DeviceViewModel;
import com.xseth.homey.adapters.OnOffAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class DevicesFragment extends Fragment {

    // Recyclerview containing devices
    public WearableRecyclerView vOnOffList;
    // Adapter for showing onoff devices
    private OnOffAdapter onOffAdapter;
    // deviceViewModel for holding device data
    private DeviceViewModel deviceViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_devices, container, false);

        // Recycler view containing devices
        vOnOffList = view.findViewById(R.id.onoff_list);
        vOnOffList.requestFocus(); // Focus required for scrolling via hw-buttons

        // use a linear layout manager
        vOnOffList.setLayoutManager(new LinearLayoutManager(getActivity()));

        // specify an adapter (see also next example)
        onOffAdapter = new OnOffAdapter();
        vOnOffList.setAdapter(onOffAdapter);

        // Add PagerSnapHelper to vOnOffList
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(vOnOffList);

        // Get ViewModelProvider, and set LiveData devices list as input for adapter
        deviceViewModel = new ViewModelProvider(this).get(DeviceViewModel.class);
        deviceViewModel.getDevices().observe(getActivity(), onOffAdapter::setDevices);

        return view;
    }

    public void setLoading(boolean loading){
        //getActivity().runOnUiThread(() -> onOffAdapter.setLoading(false));
    }
}
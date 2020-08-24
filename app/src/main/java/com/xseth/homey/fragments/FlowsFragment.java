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
import com.xseth.homey.adapters.FlowAdapter;
import com.xseth.homey.adapters.FlowViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class FlowsFragment extends Fragment {

    // Recyclerview containing devices
    public WearableRecyclerView flowList;
    // Adapter for showing onoff devices
    private FlowAdapter flowAdapter;
    // deviceViewModel for holding device data
    private FlowViewModel flowViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_flows, container, false);

        // Recycler view containing devices
        flowList = view.findViewById(R.id.flow_list);
        flowList.requestFocus(); // Focus required for scrolling via hw-buttons

        // use a linear layout manager
        flowList.setLayoutManager(new LinearLayoutManager(getActivity()));

        // specify an adapter (see also next example)
        flowAdapter = new FlowAdapter();
        flowList.setAdapter(flowAdapter);

        // Add PagerSnapHelper to vOnOffList
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(flowList);

        // Get ViewModelProvider, and set LiveData devices list as input for adapter
        flowViewModel = new ViewModelProvider(this).get(FlowViewModel.class);
        flowViewModel.getFlows().observe(getActivity(), flowAdapter::setFlows);

        return view;
    }

    public void refreshFlows(){
        flowViewModel.refreshFlows();
    }
}
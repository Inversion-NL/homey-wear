package com.xseth.homey;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.authentication.OAuthClient;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.wear.ambient.AmbientModeSupport;
import androidx.wear.widget.WearableRecyclerView;
import androidx.wear.widget.drawer.WearableDrawerLayout;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.xseth.homey.adapters.DeviceViewModel;
import com.xseth.homey.adapters.OnOffAdapter;
import com.xseth.homey.homey.Device;
import com.xseth.homey.utils.ColorRunner;
import com.xseth.homey.utils.HomeyAPI;
import com.xseth.homey.utils.OAuth;

import java.util.List;

import static com.xseth.homey.utils.ColorRunner.startColorRunner;
import static com.xseth.homey.utils.utils.generateDemoDevices;

public class MainActivity extends FragmentActivity {

    public static final String TAG = "HomeyWear";

    private DeviceViewModel deviceViewModel;
    private OAuthClient oAuthClient;
    private HomeyAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Adapter used for holding device data
        OnOffAdapter onOffAdapter;

        // Create view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // View used for rainbow background
        WearableDrawerLayout vOnOffBack = findViewById(R.id.onoff_back);
        // Recycler view containing devices
        WearableRecyclerView vOnOffList = findViewById(R.id.onoff_list);

        // Start rainbow color thread
        startColorRunner(vOnOffBack);

        api = new HomeyAPI(this);

        // use a linear layout manager
        vOnOffList.setLayoutManager(new LinearLayoutManager(this));

        // specify an adapter (see also next example)
        onOffAdapter = new OnOffAdapter();
        vOnOffList.setAdapter(onOffAdapter);

        // Add PagerSnapHelper to vOnOffList
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(vOnOffList);

        // Get ViewModelProvider, and get LiveData devices list
        deviceViewModel = new ViewModelProvider(this).get(DeviceViewModel.class);
        deviceViewModel.getDevices().observe(this, new Observer<List<Device>>() {
            @Override
            public void onChanged(@Nullable final List<Device> devices) {
                // Update the cached copy of the words in the adapter.
                onOffAdapter.setDevices(devices);
            }
        });

        Device[] tmp = generateDemoDevices(this.getApplicationContext());
        for(int i=0; i < tmp.length; i++){
            deviceViewModel.insert(tmp[i]);
        }

        // Enables Always-on
        //setAmbientEnabled();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OAuth.stopOAuth();
    }

    private Python getPython() {
        Python.start(new AndroidPlatform(this));
        return Python.getInstance();
    }
}



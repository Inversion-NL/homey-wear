package com.xseth.homey;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.wear.widget.WearableRecyclerView;
import androidx.wear.widget.drawer.WearableDrawerLayout;

import com.xseth.homey.adapters.DeviceViewModel;
import com.xseth.homey.adapters.OnOffAdapter;
import com.xseth.homey.utils.HomeyAPI;
import com.xseth.homey.utils.OAuth;

import static com.xseth.homey.utils.ColorRunner.startColorRunner;

public class MainActivity extends FragmentActivity {

    // Logging tag
    public static final String TAG = "HomeyWear";
    // General android context
    public static Context context;
    // deviceViewModel for holding device data
    private DeviceViewModel deviceViewModel;
    // Adapter used for holding device data
    private OnOffAdapter onOffAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this.getApplicationContext();

        // Create view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // View used for rainbow background
        WearableDrawerLayout vOnOffBack = findViewById(R.id.onoff_back);
        // Recycler view containing devices
        WearableRecyclerView vOnOffList = findViewById(R.id.onoff_list);

        // Start rainbow color thread
        startColorRunner(vOnOffBack);

        // Start the HomeyAPI
        HomeyAPI api = HomeyAPI.buildHomeyAPI(this);

        // use a linear layout manager
        vOnOffList.setLayoutManager(new LinearLayoutManager(this));

        // specify an adapter (see also next example)
        onOffAdapter = new OnOffAdapter();
        vOnOffList.setAdapter(onOffAdapter);

        // Add PagerSnapHelper to vOnOffList
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(vOnOffList);

        if(!api.isLoggedIn()){
            /**
             Intent intent = new Intent(this, ConfirmationActivity.class);
             intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
             ConfirmationActivity.OPEN_ON_PHONE_ANIMATION);
             intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE,
             "Authenticate via phone");
             startActivity(intent);
             **/
            OAuth.startOAuth(this);
        }else {
            //OAuth.startOAuth(this);
            api.authenticateHomey();
        }

        // Get ViewModelProvider, and get LiveData devices list
        deviceViewModel = new ViewModelProvider(this).get(DeviceViewModel.class);
        deviceViewModel.getDevices().observe(this, devices -> {
            onOffAdapter.setDevices(devices);
        });

        // Enables Always-on
        //setAmbientEnabled();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OAuth.stopOAuth();
    }
}



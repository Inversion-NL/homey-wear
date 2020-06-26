package com.xseth.homey;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.wear.widget.WearableRecyclerView;
import androidx.wear.widget.drawer.WearableActionDrawerView;
import androidx.wear.widget.drawer.WearableDrawerLayout;

import com.xseth.homey.adapters.DeviceViewModel;
import com.xseth.homey.adapters.OnOffAdapter;
import com.xseth.homey.utils.HomeyAPI;
import com.xseth.homey.utils.OAuth;
import com.xseth.homey.utils.utils;

import static com.xseth.homey.utils.ColorRunner.startColorRunner;

public class MainActivity extends FragmentActivity implements MenuItem.OnMenuItemClickListener {

    // Logging tag
    public static final String TAG = "HomeyWear";

    public static final String NOTIF_MESSAGE = "com.xseth.message.NOTIF_MESSAGE";
    public static final String NOTIF_ICON = "com.xseth.message.NOTIF_ICON";

    // General android context
    public static Context context;
    // deviceViewModel for holding device data
    public static DeviceViewModel deviceViewModel;
    // Adapter used for holding device data
    private OnOffAdapter onOffAdapter;
    // Top drawer object
    private WearableActionDrawerView drawer;

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
        vOnOffList.requestFocus(); // Focus required for scrolling via hw-buttons

        // Start rainbow color thread
        startColorRunner(vOnOffBack);

        // Verify if there is authentication/internet in background
        new Thread(() -> {

            try {

                // Start the HomeyAPI
                HomeyAPI api = HomeyAPI.buildHomeyAPI(this);

                if (api.isLoggedIn())
                    api.authenticateHomey();

                else {
                    Log.w(TAG, "No session, authenticating!");
                    utils.showConfirmationPhone(this.getApplicationContext(), R.string.authenticate);
                    OAuth.startOAuth(this);
                }
            }
            catch(Exception e) {
                Log.e(TAG, e.getLocalizedMessage());

                // No internet connection, show notification
                if (e.getLocalizedMessage().contains("AthomAPIConnectionError")){
                    Intent intent = new Intent(this, NotificationActivity.class);
                    intent.putExtra(NOTIF_MESSAGE, R.string.no_internet);
                    intent.putExtra(NOTIF_ICON, R.drawable.ic_cloud_off);
                    startActivity(intent);
                }
            }
        }).start();

        // use a linear layout manager
        vOnOffList.setLayoutManager(new LinearLayoutManager(this));

        // specify an adapter (see also next example)
        onOffAdapter = new OnOffAdapter();
        vOnOffList.setAdapter(onOffAdapter);

        // Add PagerSnapHelper to vOnOffList
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(vOnOffList);

        // Get ViewModelProvider, and set LiveData devices list as input for adapter
        deviceViewModel = new ViewModelProvider(this).get(DeviceViewModel.class);
        deviceViewModel.getDevices().observe(this, devices -> { onOffAdapter.setDevices(devices); });

        // Top Navigation Drawer
        drawer = findViewById(R.id.action_drawer);
        drawer.setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        final int itemId = menuItem.getItemId();

        switch (itemId) {
            case R.id.device_refresh:
                this.deviceViewModel.refreshDevices();
                break;
        }

        drawer.getController().closeDrawer();

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OAuth.stopOAuth();
    }
}



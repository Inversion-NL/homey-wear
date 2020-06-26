package com.xseth.homey;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.wear.widget.WearableRecyclerView;
import androidx.wear.widget.drawer.WearableActionDrawerView;
import androidx.wear.widget.drawer.WearableDrawerLayout;

import com.xseth.homey.adapters.DeviceViewModel;
import com.xseth.homey.adapters.OnOffAdapter;
import com.xseth.homey.utils.ColorRunner;
import com.xseth.homey.homey.HomeyAPI;
import com.xseth.homey.utils.OAuth;
import com.xseth.homey.utils.utils;

public class MainActivity extends FragmentActivity implements MenuItem.OnMenuItemClickListener,
        View.OnClickListener{

    // Logging tag
    public static final String TAG = "HomeyWear";
    // General android context
    public static Context context;
    // deviceViewModel for holding device data
    private DeviceViewModel deviceViewModel;
    // Bottom drawer object
    private WearableActionDrawerView drawer;
    // Layout used in notifications
    private FrameLayout notifications;
    // Recyclerview containing devices
    private WearableRecyclerView vOnOffList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this.getApplicationContext();

        // Create view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // View used for notifications
        notifications = findViewById(R.id.notification);

        // View used for rainbow background
        WearableDrawerLayout vOnOffBack = findViewById(R.id.onoff_back);
        ColorRunner.startColorRunner(vOnOffBack);

        // Verify if there is authentication/internet in background
        new Thread(() -> {

            try {

                // Start the HomeyAPI
                HomeyAPI api = HomeyAPI.buildHomeyAPI(this);

                if (api.isLoggedIn())
                    api.authenticateHomey();

                else {
                    Log.w(TAG, "No session, authenticating!");
                    OAuth.startOAuth(this);
                    setNotification(R.string.login, R.drawable.ic_login);
                }
            }catch(Exception e) {
                Log.e(TAG, e.getLocalizedMessage());

                // No internet connection, show notification
                if (e.getLocalizedMessage().contains("AthomAPIConnectionError"))
                    setNotification(R.string.no_internet, R.drawable.ic_cloud_off);

                // Contains invalid session, reauthorizing
                else if (e.getLocalizedMessage().contains("AthomCloudAuthenticationError")){
                    Log.w(TAG, "Invalid session, reauthorizing!");
                    OAuth.startOAuth(this);
                    setNotification(R.string.login, R.drawable.ic_login);
                }
                // Unknown error
                else
                    setNotification(R.string.error, R.drawable.ic_error);
            }
        }).start();

        // Recycler view containing devices
        vOnOffList = findViewById(R.id.onoff_list);
        vOnOffList.requestFocus(); // Focus required for scrolling via hw-buttons

        // use a linear layout manager
        vOnOffList.setLayoutManager(new LinearLayoutManager(this));

        // specify an adapter (see also next example)
        OnOffAdapter onOffAdapter = new OnOffAdapter();
        vOnOffList.setAdapter(onOffAdapter);

        // Add PagerSnapHelper to vOnOffList
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(vOnOffList);

        // Get ViewModelProvider, and set LiveData devices list as input for adapter
        deviceViewModel = new ViewModelProvider(this).get(DeviceViewModel.class);
        deviceViewModel.getDevices().observe(this, devices -> {
            onOffAdapter.setDevices(devices);
        });

        // Top Navigation Drawer
        drawer = findViewById(R.id.action_drawer);
        drawer.setOnMenuItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        TextView message = findViewById(R.id.notification_message);

        // Check if login notification is shown
        if(!message.getText().toString().equals(getResources().getString(R.string.login)))
            return;

        utils.showConfirmationPhone(this.getApplicationContext(), R.string.authenticate);
        OAuth.sendAuthoriziation();
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

    /**
     * Show the notification fragment with selected text & icon
     * @param message_id text to show in fragment
     * @param icon_id icon to show in fragment
     */
    public void setNotification(int message_id, int icon_id){
        TextView message = findViewById(R.id.notification_message);
        ImageView icon = findViewById(R.id.notification_icon);

        message.setText(message_id);
        icon.setImageResource(icon_id);

        runOnUiThread(() -> {
            vOnOffList.setVisibility(View.GONE);
            notifications.setVisibility(View.VISIBLE);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OAuth.stopOAuth();
    }
}



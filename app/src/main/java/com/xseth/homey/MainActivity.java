package com.xseth.homey;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.viewpager.widget.ViewPager;
import androidx.wear.widget.WearableRecyclerView;
import androidx.wear.widget.drawer.WearableActionDrawerView;
import androidx.wear.widget.drawer.WearableDrawerLayout;

import com.xseth.homey.adapters.DeviceViewModel;
import com.xseth.homey.adapters.OnOffAdapter;
import com.xseth.homey.adapters.PagerAdapter;
import com.xseth.homey.fragments.DevicesFragment;
import com.xseth.homey.fragments.FlowsFragment;
import com.xseth.homey.homey.DeviceRepository;
import com.xseth.homey.utils.ColorRunner;
import com.xseth.homey.homey.HomeyAPI;
import com.xseth.homey.utils.OAuth;
import com.xseth.homey.utils.utils;

import java.net.UnknownHostException;

import timber.log.Timber;

public class MainActivity extends FragmentActivity implements MenuItem.OnMenuItemClickListener,
        View.OnClickListener{

    // Bottom drawer object
    private WearableActionDrawerView drawer;
    // Layout used in notifications
    public FrameLayout notifications;
    // ProgressBar in notifications view
    public ProgressBar notificationsProgress;
    // Path to app directory on system
    public static String appPath;
    // ApplicationContext
    public static Context context;

    public ViewPager mPager;
    private DevicesFragment devicesFragment;
    private FlowsFragment flowsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Configure Timber logging
        if (BuildConfig.DEBUG)
            Timber.plant(new Timber.DebugTree());

        // Create view
        super.onCreate(savedInstanceState);

        context = this.getApplicationContext();
        appPath = context.getFilesDir().getAbsolutePath();

        // Create all views
        setupViews();

        // Verify if there is authentication/internet in background
        new Thread(this::authenticateHomey).start();
        Timber.d("Finish onCreate");
    }

    /**
     * Setup all view related items in MainActivity
     */
    private void setupViews(){
        setContentView(R.layout.activity_main);
        ColorRunner.startColorRunner(this, findViewById(R.id.onoff_back));

        // View used for notifications
        notifications = findViewById(R.id.notification);
        notifications.setOnClickListener(this);

        notificationsProgress = notifications.findViewById(R.id.progressBar);
        utils.randomiseProgressBar(notificationsProgress);

        // Top Navigation Drawer
        drawer = findViewById(R.id.action_drawer);
        drawer.setOnMenuItemClickListener(this);

        // Create pagerAdapters
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());

        devicesFragment = new DevicesFragment();
        flowsFragment = new FlowsFragment();

        adapter.addFragment(devicesFragment);
        adapter.addFragment(flowsFragment);

        mPager = findViewById(R.id.pager);
        mPager.setAdapter(adapter);
    }

    /**
     * Get active session from Athom Homey, if fails show login sequence.
     */
    private void authenticateHomey(){
        try {
            // Start the HomeyAPI
            HomeyAPI api = HomeyAPI.getAPI();

            if (api.isLoggedIn()) {
                Timber.i("Got session");
                api.authenticateHomey();

            } else {
                Timber.w("No session, authenticating!");
                OAuth.startOAuth(this);
                setNotification(R.string.login, R.drawable.ic_login);
            }

            // No internet connection, show notification
        } catch(UnknownHostException uhe){
            setNotification(R.string.no_internet, R.drawable.ic_cloud_off);

        }catch(Exception e) {
            Timber.e(e);
            setNotification(R.string.error, R.drawable.ic_error);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Restart background thread
        ColorRunner.resumeColorRunner();
        devicesFragment.setLoading(true);

        // Sync statuses of devices.
        new Thread(() -> {
            DeviceRepository.buildInstance(this.getApplication());
            DeviceRepository.getInstance().refreshDeviceStatuses();

            // Device statusses updated, remove loading
            devicesFragment.setLoading(false);
        }).start();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Pause background thread
        ColorRunner.pauseColorRunner();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OAuth.stopOAuth();
        ColorRunner.stopColorRunner();
    }

    @Override
    public void onClick(View v) {
        TextView message = notifications.findViewById(R.id.message);

        // Check if login notification is shown
        if(!message.getText().toString().equals(getResources().getString(R.string.login)))
            return;

        utils.showConfirmationPhone(this.getApplicationContext(), R.string.authenticate);
        notificationsProgress.setVisibility(View.VISIBLE);

        OAuth.sendAuthorization(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        final int itemId = menuItem.getItemId();

        switch (itemId) {
            case R.id.device_refresh:
                devicesFragment.setLoading(true);

                flowsFragment.refreshFlows();
                devicesFragment.refreshDevices();

                // Device refreshed, remove loading
                devicesFragment.setLoading(false);
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
        TextView message = notifications.findViewById(R.id.message);
        ImageView icon = findViewById(R.id.icon);

        message.setText(message_id);
        icon.setImageResource(icon_id);

        runOnUiThread(() -> {
            mPager.setVisibility(View.GONE);
            notifications.setVisibility(View.VISIBLE);
        });
    }
}



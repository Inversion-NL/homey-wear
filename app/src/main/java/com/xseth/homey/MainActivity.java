package com.xseth.homey;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.authentication.OAuthClient;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.wear.widget.WearableRecyclerView;
import androidx.wear.widget.drawer.WearableDrawerLayout;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.xseth.homey.adapters.OnOffAdapter;
import com.xseth.homey.utils.ColorRunner;
import com.xseth.homey.utils.HomeyAPI;

import static com.xseth.homey.utils.ColorRunner.startColorRunner;
import static com.xseth.homey.utils.utils.generateDemoDevices;

public class MainActivity extends WearableActivity {

    public static final String TAG = "HomeyWear";

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
        onOffAdapter = new OnOffAdapter(generateDemoDevices(this));
        vOnOffList.setAdapter(onOffAdapter);

        // Add PagerSnapHelper to vOnOffList
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(vOnOffList);

        // Notify data has changed
        onOffAdapter.notifyDataSetChanged();

        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        oAuthClient.destroy();
    }

    private Python getPython() {
        Python.start(new AndroidPlatform(this));
        return Python.getInstance();
    }
}



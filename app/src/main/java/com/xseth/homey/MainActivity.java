package com.xseth.homey;

import android.net.Uri;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.authentication.OAuthClient;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.BoxInsetLayout;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;
import androidx.wear.widget.drawer.WearableDrawerLayout;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.xseth.homey.utils.ColorRunner;

public class MainActivity extends WearableActivity {

    public static final String TAG = "HomeyWear";
    public static final String CLIENT_ID = "TBD";
    public static final String CLIENT_SECRET = "TBD";

    public static final String RETURN_URL = "https://wear.googleapis.com/3p_auth/com.xseth.homey";

    private WearableRecyclerView vOnOffList;
    private WearableDrawerLayout vOnOffBack;
    private OAuthClient oAuthClient;
    private PyObject athomCloudAPI;

    private class MyOAuthCallback extends OAuthClient.Callback {
        @Override
        public void onAuthorizationResponse(Uri requestUrl, Uri responseUrl) {
            Log.d(TAG, "onResult(). requestUrl:" + requestUrl + " responseUrl: " + responseUrl);
        }

        @Override
        public void onAuthorizationError(int errorCode) {
            Log.d(TAG, ""+errorCode);

        }
    }
    public void onClickStartGoogleOAuth2Flow(View view) {
        String url = athomCloudAPI.callAttr("getLoginUrl").toString();
        oAuthClient.sendAuthorizationRequest(Uri.parse(url), new MyOAuthCallback());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LinearLayoutManager layoutManager;
        MyAdapter mAdapter;

        String[] dataset = {"homey", "lamp", "roomba", "alarm", "koffie"};

        for (int i = 0; i < dataset.length; i++) {
            int f = getResources().getIdentifier("ic_"+dataset[i] , "drawable" , getPackageName());
            dataset[i] = String.valueOf(f);
        }

        Python py = getPython();
        oAuthClient = OAuthClient.create(this);

        PyObject athomCloud = py.getModule("athom.cloud");
        athomCloudAPI = athomCloud.callAttr("AthomCloudAPI", CLIENT_ID, CLIENT_SECRET, RETURN_URL);
        Boolean loggedIn = athomCloudAPI.callAttr("isLoggedIn").toBoolean();

        if(!loggedIn)
            Log.d(TAG, "No active AthomSession");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vOnOffBack = findViewById(R.id.onoff_back);
        vOnOffList = findViewById(R.id.onoff_list);
        new ColorRunner(vOnOffBack).execute(vOnOffBack);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        vOnOffList.setLayoutManager(layoutManager);


        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(dataset);
        vOnOffList.setAdapter(mAdapter);
        //vOnOffList.setLayoutManager(new WearableLinearLayoutManager(this));


        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(vOnOffList);

        mAdapter.notifyDataSetChanged();

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

class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private String[] mDataset;
    private static final String TAG = "Adapter";

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout textView;
        public MyViewHolder(LinearLayout v) {
            super(v);
            textView = v;
            Log.d(TAG, "myViewHolder");
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(String[] myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.onoff_fragment, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        Log.d(TAG, "onCreateViewHolder");
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        String a = mDataset[position];

        ImageView tmp2 = holder.textView.findViewById(R.id.imageView3);
        tmp2.setImageResource(Integer.parseInt(a));
        Log.d(TAG, "onBindVIewHolder");
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}

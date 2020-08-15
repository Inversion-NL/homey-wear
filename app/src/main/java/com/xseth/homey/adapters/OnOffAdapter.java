package com.xseth.homey.adapters;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.xseth.homey.R;
import com.xseth.homey.homey.models.Device;
import com.xseth.homey.homey.DeviceRepository;
import com.xseth.homey.utils.utils;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

interface RecyclerViewClickListener {
    void onClick(View view, int position);
}

public class OnOffAdapter extends RecyclerView.Adapter<OnOffAdapter.viewHolder>
        implements RecyclerViewClickListener{

    // Array of data objects related to adapter
    private List<Device> devices;
    // Boolean indicating if items in list are loading
    private boolean loading = false;
    // Boolean indicating if specific item should show progressBar
    private int loadingIndex = -1;

    /**
     * Class used to contain view for displaying devices
     */
    public static class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Device fragment
        public FrameLayout onOffFragment;
        // View for showing device name
        public TextView onOffTitle;
        // View used for showing device icon
        public ImageView onOffIcon;
        // View progressbar
        public ProgressBar progressBar;
        // Click listener for recycle view
        private RecyclerViewClickListener mListener;

        /**
         * ViewHolder constructor
         * @param view View layout
         * @param listener instance of listener
         */
        public viewHolder(LinearLayout view, RecyclerViewClickListener listener) {
            super(view);
            onOffFragment = view.findViewById(R.id.onoff_fragment);
            onOffTitle = view.findViewById(R.id.message);
            onOffIcon = view.findViewById(R.id.icon);
            progressBar = view.findViewById(R.id.progressBar);
            mListener = listener;

            utils.randomiseProgressBar(progressBar);
            onOffFragment.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }
    }

    /**
     * Indicate whether a specific item, indicated by index, should show progressbar loading.
     * @param loading if items in list should show progressBar loading
     * @param index index of item in list for which to show/not show progressbar
     */
    public void setLoading(boolean loading, int index) {
        this.loading = loading;
        this.loadingIndex = index;
        this.notifyDataSetChanged();
    }

    /**
     * Indicate whether all items in the Recyclerview should show progressbar loading.
     * @param loading if all items in list should show progressBar loading
     */
    public void setLoading(boolean loading) {
        this.setLoading(loading, -1);
    }

    @Override
    public OnOffAdapter.viewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view based on onoff_fragment
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.onoff_fragment, parent, false);

        return new viewHolder(v, this);
    }

    @Override
    // Replace the contents of a view (invoked by the layout manager)
    public void onBindViewHolder(viewHolder holder, int position) {
        Device device = devices.get(position);

        holder.onOffIcon.setImageBitmap(device.getIconImage());
        holder.onOffTitle.setText(device.getName());

        // Indicate whether progressBar should be shown
        if(loading && (loadingIndex == position || loadingIndex == -1))
            holder.progressBar.setVisibility(View.VISIBLE);
        else
            holder.progressBar.setVisibility(View.INVISIBLE);

        // define background color
        int color_id = device.isOn() ? R.color.device_on : R.color.device_off;

        GradientDrawable bgShape = (GradientDrawable)holder.onOffFragment.getBackground();
        String color = holder.onOffFragment.getContext().getResources().getString(0+color_id);
        bgShape.setColor(Color.parseColor(color));
    }

    @Override
    public void onClick(View view, int position) {
        Device device = this.devices.get(position);
        setLoading(true, position);

        device.turnOnOff().enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                boolean status = true;

                // get status from call and update device, if button no value is returned
                if(response.body().containsKey("value"))
                    status = (boolean) response.body().get("value");

                device.setOn(status);
                DeviceRepository.getInstance().update(device);

                int color_id = device.isOn() ? R.color.device_on : R.color.device_off;
                GradientDrawable bgShape = (GradientDrawable)view.getBackground();
                String color = view.getContext().getResources().getString(0+color_id);
                bgShape.setColor(Color.parseColor(color));

                // If device is button, background never changes so notify via Toast message
                if(device.isButton()) {
                    String text = view.getResources().getString(R.string.button_press, device.getName());
                    Toast.makeText(view.getContext(), text, Toast.LENGTH_LONG).show();
                }

                setLoading(false, position);
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                setLoading(false, position);
                Timber.e(t, "Failed to turn onoff");
                // Show popup if fail to turn on or off
                Toast.makeText(view.getContext(), R.string.fail_turnonoff, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Set new list of devices then update view
     * @param devices devices list to set
     */
    public void setDevices(List<Device> devices){
        Timber.d("on setDevices");

        if(devices.size() > 0) {
            this.devices = devices;
            this.notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        if(devices == null)
            return 0;

        return devices.size();
    }
}
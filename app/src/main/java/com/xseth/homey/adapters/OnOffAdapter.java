package com.xseth.homey.adapters;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.xseth.homey.R;
import com.xseth.homey.homey.models.Device;
import com.xseth.homey.homey.DeviceRepository;

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

    /**
     * Class used to contain view for displaying devices
     */
    public static class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Device fragment
        public LinearLayout onOffFragment;
        // View for showing device name
        public TextView onOffTitle;
        // View used for showing device icon
        public ImageView onOffIcon;
        // Click listener for recycle view
        private RecyclerViewClickListener mListener;

        /**
         * ViewHolder constructor
         * @param view View layout
         * @param listener instance of listener
         */
        public viewHolder(LinearLayout view, RecyclerViewClickListener listener) {
            super(view);
            onOffFragment = view;
            onOffTitle = view.findViewById(R.id.onOffTitle);
            onOffIcon = view.findViewById(R.id.onOffIcon);
            mListener = listener;

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }
    }

    @Override
    public OnOffAdapter.viewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view based on onoff_fragment
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.onoff_fragment, parent, false);

        return new viewHolder(v, this);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(viewHolder holder, int position) {
        Device device = devices.get(position);

        holder.onOffIcon.setImageBitmap(device.getIconImage());
        holder.onOffTitle.setText(device.getName());

        // define background color
        int color_id = device.isOn() ? R.color.device_on : R.color.device_off;
        int color = holder.onOffFragment.getContext().getResources().getColor(color_id);
        holder.onOffFragment.getBackground().setColorFilter(new PorterDuffColorFilter(color,
                PorterDuff.Mode.MULTIPLY));
    }

    @Override
    public void onClick(View view, int position) {
        Device device = this.devices.get(position);

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
                int color = view.getContext().getResources().getColor(color_id);

                view.getBackground().setColorFilter(new PorterDuffColorFilter(
                        color, PorterDuff.Mode.MULTIPLY
                ));

                // If device is button, background never changes so notify via Toast message
                if(device.isButton()) {
                    String text = view.getResources().getString(R.string.button_press, device.getName());
                    Toast.makeText(view.getContext(), text, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
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
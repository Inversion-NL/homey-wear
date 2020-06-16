package com.xseth.homey.adapters;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.xseth.homey.R;
import com.xseth.homey.homey.Device;

import java.util.LinkedList;
import java.util.List;

interface RecyclerViewClickListener {
    void onClick(View view, int position);
}

public class OnOffAdapter extends RecyclerView.Adapter<OnOffAdapter.viewHolder> implements RecyclerViewClickListener{
    // Array of data objects related to adapter
    private List<Device> devices;
    // Logging Tag
    private static final String TAG = "OnOffAdapter";

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public LinearLayout onOffFragment;
        public TextView onOffTitle;
        public ImageView onOffIcon;
        private RecyclerViewClickListener mListener;

        public viewHolder(LinearLayout v, RecyclerViewClickListener listener) {
            super(v);
            onOffFragment = v;
            onOffTitle = v.findViewById(R.id.onOffTitle);
            onOffIcon = v.findViewById(R.id.onOffIcon);
            mListener = listener;

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public OnOffAdapter() { this.devices = new LinkedList<Device>(); }

    // Create new views (invoked by the layout manager)
    @Override
    public OnOffAdapter.viewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view based on onoff_fragment
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.onoff_fragment, parent, false);

        Log.d(TAG, "onCreateViewHolder");
        return new viewHolder(v, this);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(viewHolder holder, int position) {
        Device device = devices.get(position);

        holder.onOffIcon.setImageResource(device.getIcon());
        holder.onOffTitle.setText(device.getTitle());

        // define background color
        int color_id = device.isOn() ? R.color.device_on : R.color.device_off;
        int color = holder.onOffFragment.getContext().getResources().getColor(color_id);
        holder.onOffFragment.getBackground().setColorFilter(new PorterDuffColorFilter(color,
                PorterDuff.Mode.MULTIPLY));

        Log.d(TAG, "onBindVIewHolder");
    }


    @Override
    public void onClick(View view, int position) {
        Device device = this.devices.get(position);
        device.inverse();

        String text = "Device: "+device.getTitle()+" set to: "+device.isOn();
        Toast.makeText(view.getContext(), text, Toast.LENGTH_SHORT).show();

        int color_id = device.isOn() ? R.color.device_on : R.color.device_off;
        int color = view.getContext().getResources().getColor(color_id);
        view.getBackground().setColorFilter(new PorterDuffColorFilter(color,
                PorterDuff.Mode.MULTIPLY));
    }

    public void setDevices(List<Device> devices){
        if(devices.size() > 0) {
            this.devices = devices;
            this.notifyDataSetChanged();
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return devices.size();
    }
}
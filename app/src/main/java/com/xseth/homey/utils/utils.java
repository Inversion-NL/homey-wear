package com.xseth.homey.utils;

import android.content.Context;
import android.content.Intent;
import android.support.wearable.activity.ConfirmationActivity;

import com.xseth.homey.R;
import com.xseth.homey.homey.Device;

public class utils {

    public static final String[] demoDataset = {"homey", "lamp", "roomba", "alarm", "koffie"};

    public static Device[] generateDemoDevices(Context ctx){
        Device[] devices = new Device[demoDataset.length];

        for (int i = 0; i < demoDataset.length; i++) {
            devices[i] = new Device(Integer.toString(i), demoDataset[i]);
        }

        return devices;
    }

    public static void showConfirmationSuccess(Context ctx, int strId){
        Intent intent = new Intent(ctx, ConfirmationActivity.class);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.SUCCESS_ANIMATION);
        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, ctx.getString(strId));
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }

    public static void showConfirmationFailure(Context ctx, int strId){
        Intent intent = new Intent(ctx, ConfirmationActivity.class);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.FAILURE_ANIMATION);
        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, ctx.getString(strId));
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }

    public static void showConfirmationPhone(Context ctx, int strId){
        Intent intent = new Intent(ctx, ConfirmationActivity.class);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.OPEN_ON_PHONE_ANIMATION);
        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, ctx.getString(strId));
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }
}

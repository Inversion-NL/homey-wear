package com.xseth.homey.utils;

import android.content.Context;
import android.content.Intent;
import android.support.wearable.activity.ConfirmationActivity;

public class utils {

    /**
     * Show ConfirmationActivity with success animation
     * @param ctx context to show in
     * @param strId text message to show
     */
    public static void showConfirmationSuccess(Context ctx, int strId){
        Intent intent = new Intent(ctx, ConfirmationActivity.class);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                ConfirmationActivity.SUCCESS_ANIMATION);
        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, ctx.getString(strId));
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }

    /**
     * Show ConfirmationActivity with failure animation
     * @param ctx context to show in
     * @param strId text message to show
     */
    public static void showConfirmationFailure(Context ctx, int strId){
        Intent intent = new Intent(ctx, ConfirmationActivity.class);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                ConfirmationActivity.FAILURE_ANIMATION);
        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, ctx.getString(strId));
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }

    /**
     * Show ConfirmationActivity with open on phone animation
     * @param ctx context to show in
     * @param strId text message to show
     */
    public static void showConfirmationPhone(Context ctx, int strId){
        Intent intent = new Intent(ctx, ConfirmationActivity.class);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                ConfirmationActivity.OPEN_ON_PHONE_ANIMATION);
        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, ctx.getString(strId));
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }
}

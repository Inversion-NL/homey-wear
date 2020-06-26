package com.xseth.homey.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.authentication.OAuthClient;
import android.util.Log;

import com.xseth.homey.MainActivity;
import com.xseth.homey.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OAuth {

    public static final String TAG = "OAuth";
    private static OAuthClient mOAuthClient;

    private static class MyOAuthCallback extends OAuthClient.Callback {
        @Override
        public void onAuthorizationResponse(Uri requestUrl, Uri responseUrl) {
            Log.i(TAG, "Received onAuth response");
            String token = responseUrl.getQueryParameter("code");

            utils.showConfirmationSuccess(MainActivity.context, R.string.success_authenticate);

            // Set APItoken in seperate thread
            HomeyAPI.getAPI().setToken(token);
            Runnable runnable = () -> HomeyAPI.getAPI().setToken(token);
            ExecutorService executor = Executors.newCachedThreadPool();
            executor.submit(runnable);
            OAuth.stopOAuth();
        }

        @Override
        public void onAuthorizationError(int errorCode) {
            Log.e(TAG, "OAuth error: "+errorCode);

            // Start thread to show login failed. Wait for some time to fix notification overlap
            new Thread(() -> {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignored) {}

                utils.showConfirmationFailure(MainActivity.context, R.string.failure_authenticate);
            }).start();
        }
    }

    public static void startOAuth(Context context) {
        Log.d(TAG, "Start OAuth2 context");
        mOAuthClient = OAuthClient.create(context);
    }

    public static void sendAuthoriziation(){
        HomeyAPI api = HomeyAPI.getAPI();
        String url = api.getLoginURL();

        Log.i(TAG, "Send authentication via url: "+url);
        mOAuthClient.sendAuthorizationRequest(Uri.parse(url), new MyOAuthCallback());
    }

    public static void stopOAuth(){ if (mOAuthClient != null) mOAuthClient.destroy();}
}

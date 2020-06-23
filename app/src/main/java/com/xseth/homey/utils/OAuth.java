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
            utils.showConfirmationFailure(MainActivity.context, R.string.failure_authenticate);
            Log.e(TAG, "OAuth error: "+errorCode);
        }
    }

    public static void startOAuth(Context context) {
        HomeyAPI api = HomeyAPI.getAPI();
        String url = api.getLoginURL();

        Log.d(TAG, "Start OAuth2 authentication via url: "+url);

        mOAuthClient = OAuthClient.create(context);
        mOAuthClient.sendAuthorizationRequest(Uri.parse(url), new MyOAuthCallback());
    }

    public static void stopOAuth(){ if (mOAuthClient != null) mOAuthClient.destroy();}
}

package com.xseth.homey.utils;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.wearable.authentication.OAuthClient;
import android.view.View;

import com.xseth.homey.MainActivity;
import com.xseth.homey.R;
import com.xseth.homey.homey.HomeyAPI;

import timber.log.Timber;

public class OAuth {

    // OAuthClient instance
    private static OAuthClient mOAuthClient;

    /**
     * Class for handling OAUTH2 callbacks
     */
    private static class MyOAuthCallback extends OAuthClient.Callback {

        // Instance of activity starting OAuth
        private MainActivity activity;

        private MyOAuthCallback(MainActivity activity){
            this.activity = activity;
        }

        @Override
        public void onAuthorizationResponse(Uri requestUrl, Uri responseUrl) {
            Timber.i("Received onAuth response");
            String token = responseUrl.getQueryParameter("code");

            utils.showConfirmationSuccess(activity.getApplicationContext(), R.string.success_authenticate);

            // Set APItoken in separate thread
            new Thread(() -> {
                try {
                    HomeyAPI.getAPI().setToken(token);

                    // 'Remove' notifications and show default layout
                    this.activity.runOnUiThread(() -> {
                        this.activity.vOnOffList.setVisibility(View.VISIBLE);
                        this.activity.notifications.setVisibility(View.GONE);
                        this.activity.notificationsProgress.setVisibility(View.INVISIBLE);
                    });
                } catch (Exception e){
                    Timber.e(e, "Failed to parse Oauth code");
                    utils.showConfirmationFailure(activity.getApplicationContext(), R.string.failure_authenticate);
                }
            }).start();

            OAuth.stopOAuth();
        }

        @Override
        public void onAuthorizationError(int errorCode) {
            Timber.e("OAuth error: %d", errorCode);

            // Start thread to show login failed. Wait for some time to fix notification overlap
            new Thread(() -> {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignored) {}

                utils.showConfirmationFailure(activity.getApplicationContext(), R.string.failure_authenticate);
                activity.runOnUiThread(() -> this.activity.notificationsProgress.setVisibility(View.INVISIBLE));
            }).start();
        }
    }

    /**
     * Start an OAUTH2 context
     * @param context context to create OAUTH2 client in
     */
    public static void startOAuth(Context context) {
        Timber.d("Start OAuth2 context");
        mOAuthClient = OAuthClient.create(context);
    }

    /**
     * Start OAUTH2 authorization procedure
     */
    public static void sendAuthorization(MainActivity a){
        HomeyAPI api = HomeyAPI.getAPI();
        String url = api.getLoginURL();

        Timber.i("Send authentication via url: %s", url);
        mOAuthClient.sendAuthorizationRequest(Uri.parse(url), new MyOAuthCallback(a));
    }

    /**
     * Destroy OAUTH instance
     */
    public static void stopOAuth(){
        if (mOAuthClient != null) mOAuthClient.destroy();
    }
}

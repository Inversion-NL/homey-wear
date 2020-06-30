package com.xseth.homey.utils;

import android.content.Context;
import android.net.Uri;
import android.support.wearable.authentication.OAuthClient;

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

        @Override
        public void onAuthorizationResponse(Uri requestUrl, Uri responseUrl) {
            Timber.i("Received onAuth response");
            String token = responseUrl.getQueryParameter("code");

            utils.showConfirmationSuccess(MainActivity.context, R.string.success_authenticate);

            // Set APItoken in separate thread
            HomeyAPI.getAPI().setToken(token);
            new Thread(() -> {
                HomeyAPI.getAPI().setToken(token);
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

                utils.showConfirmationFailure(MainActivity.context, R.string.failure_authenticate);
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
    public static void sendAuthoriziation(){
        HomeyAPI api = HomeyAPI.getAPI();
        String url = api.getLoginURL();

        Timber.i("Send authentication via url: %s", url);
        mOAuthClient.sendAuthorizationRequest(Uri.parse(url), new MyOAuthCallback());
    }

    /**
     * Destroy OAUTH instance
     */
    public static void stopOAuth(){
        if (mOAuthClient != null) mOAuthClient.destroy();
    }
}

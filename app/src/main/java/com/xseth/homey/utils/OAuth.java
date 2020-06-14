package com.xseth.homey.utils;

import android.net.Uri;
import android.support.wearable.authentication.OAuthClient;
import android.util.Log;
import android.view.View;

public class OAuth {

    public static final String TAG = "OAuth";


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
        // String url = athomCloudAPI.callAttr("getLoginUrl").toString();
        // oAuthClient.sendAuthorizationRequest(Uri.parse(url), new MyOAuthCallback());
    }
}

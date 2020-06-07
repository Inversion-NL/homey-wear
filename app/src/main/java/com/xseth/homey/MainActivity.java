package com.xseth.homey;

import android.net.Uri;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.authentication.OAuthClient;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

public class MainActivity extends WearableActivity {

    public static final String TAG = "HomeyWear";
    public static final String CLIENT_ID = "TBD";
    public static final String CLIENT_SECRET = "TBD";

    public static final String RETURN_URL = "https://wear.googleapis.com/3p_auth/com.xseth.homey";

    private TextView mTextView;
    private OAuthClient oAuthClient;
    private PyObject athomCloudAPI;

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
        String url = athomCloudAPI.callAttr("getLoginUrl").toString();
        oAuthClient.sendAuthorizationRequest(Uri.parse(url), new MyOAuthCallback());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Python py = getPython();
        oAuthClient = OAuthClient.create(this);

        PyObject athomCloud = py.getModule("athom.cloud");
        athomCloudAPI = athomCloud.callAttr("AthomCloudAPI", CLIENT_ID, CLIENT_SECRET, RETURN_URL);
        Boolean loggedIn = athomCloudAPI.callAttr("isLoggedIn").toBoolean();

        if(!loggedIn)
            Log.v(TAG, "No active AthomSession");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text);

        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        oAuthClient.destroy();
    }

    private Python getPython() {
        Python.start(new AndroidPlatform(this));
        return Python.getInstance();
    }
}

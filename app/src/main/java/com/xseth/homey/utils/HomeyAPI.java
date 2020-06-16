package com.xseth.homey.utils;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;

import com.chaquo.python.Kwarg;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.xseth.homey.BuildConfig;
import com.xseth.homey.storage.HomeyRoomDatabase;

public class HomeyAPI {

    public static final String TAG = "HomeyAPI";
    private static volatile HomeyAPI INSTANCE;
    public static final String CLIENT_ID = BuildConfig.ATHOM_CLIENT_ID;
    public static final String CLIENT_SECRET = BuildConfig.ATHOM_CLIENT_SECRET;
    public static final String RETURN_URL = "https://wear.googleapis.com/3p_auth/com.xseth.homey";
    public static final String[] SCOPES = {
            "homey.user.self",
            "homey.device.readonly",
            "homey.device.control",
            "homey.flow.start",
            "homey.flow.readonly"
    };

    private PyObject athomCloudAPI;

    public static HomeyAPI getAPI() { return INSTANCE; }

    public static HomeyAPI buildHomeyAPI(final Context context){
        HomeyAPI.INSTANCE = new HomeyAPI(context);
        return HomeyAPI.INSTANCE;
    }

    public HomeyAPI(Context ctx){
        Python.start(new AndroidPlatform(ctx));
        Python py = Python.getInstance();
        PyObject athomCloud = py.getModule("athom.cloud");

        // Get path to local storage
        String storagePath = ctx.getFilesDir().getAbsolutePath() + "/homeyLocalStorage.db";

        // Create LocalStorage obj with path in local storage
        PyObject storageObj = py.getModule("athom.storage.localstorage").callAttr(
                "LocalStorage",
                new Kwarg("path", storagePath)
                );

        athomCloudAPI = athomCloud.callAttr("AthomCloudAPI",
                CLIENT_ID,
                CLIENT_SECRET,
                RETURN_URL,
                new Kwarg("storage", storageObj)
        );
    }

    public void setToken(String token){
        athomCloudAPI.callAttr("authenticateWithAuthorizationCode", token);
        Log.i(TAG, "Stored OAuth token");
    }

    public Boolean isLoggedIn(){
        return athomCloudAPI.callAttr("isLoggedIn").toBoolean();
    }

    public String getLoginURL() {
        return athomCloudAPI.callAttr(
                "getLoginUrl",
                new Kwarg("scopes", SCOPES)
        ).toString();
    }
}

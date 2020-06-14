package com.xseth.homey.utils;

import android.content.Context;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

public class HomeyAPI {

    public static final String TAG = "HomeyAPI";

    public static final String CLIENT_ID = "TBD";
    public static final String CLIENT_SECRET = "TBD";
    public static final String RETURN_URL = "https://wear.googleapis.com/3p_auth/com.xseth.homey";

    private PyObject athomCloudAPI;

    public HomeyAPI(Context ctx){
        Python.start(new AndroidPlatform(ctx));
        Python py = Python.getInstance();
        PyObject athomCloud = py.getModule("athom.cloud");
        athomCloudAPI = athomCloud.callAttr("AthomCloudAPI", CLIENT_ID, CLIENT_SECRET, RETURN_URL);
    }

    public Boolean isLoggedIn(){
        return athomCloudAPI.callAttr("isLoggedIn").toBoolean();
    }
}

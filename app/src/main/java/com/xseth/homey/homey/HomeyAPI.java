package com.xseth.homey.homey;

import android.content.Context;
import android.util.Log;

import com.chaquo.python.Kwarg;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.xseth.homey.BuildConfig;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HomeyAPI {

    // Debug log
    public static final String TAG = "HomeyAPI";
    // Athom Homey client ID for accessing API
    public static final String CLIENT_ID = BuildConfig.ATHOM_CLIENT_ID;
    // Athom Homey client secret for accessing API
    public static final String CLIENT_SECRET = BuildConfig.ATHOM_CLIENT_SECRET;
    // ReturnURL for OAuth2
    public static final String RETURN_URL = "https://wear.googleapis.com/3p_auth/com.xseth.homey";
    // URL for Icon CDN
    public static final String ICON_URL = "https://icons-cdn.athom.com/";
    // List of OAuth2 scopes used in Athom Homey API
    public static final String[] SCOPES = {
            "account.homeys.readonly",
            "homey.user.self",
            "homey.device.readonly",
            "homey.device.control",
            "homey.flow.start",
            "homey.flow.readonly"
    };
    // List of supported capabilities
    public static final String[] CAPABILITIES = {
            "onoff",
            "speaker_playing"
    };

    // Instance HomeyAPI for singleton
    private static volatile HomeyAPI INSTANCE;
    // AthomCloudAPI instance
    private PyObject athomCloudAPI;
    // HomeyAPI instance
    private PyObject homeyAPI;
    // HomeyAPI UsersManager instance
    private PyObject usersManager;
    // HomeyAPI DevicesManager instance
    private PyObject devicesManager;
    // List of favorite devices IDs
    private List<PyObject> deviceFavorites;

    /**
     * Get HomeyAPI instance
     * @return instance of HomeyAPI
     */
    public synchronized static HomeyAPI getAPI() {
        // Wait for Thread to build HomeyAPI
        while(INSTANCE == null) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {}
        }

        return INSTANCE;
    }

    /**
     * Build HomeyAPI
     * @param context ApplicationContext
     * @return HomeyAPI instance
     */
    public static HomeyAPI buildHomeyAPI(final Context context){
        HomeyAPI.INSTANCE = new HomeyAPI(context);
        return HomeyAPI.INSTANCE;
    }

    /**
     * HomeyAPI constructor
     * @param ctx context for Python environment
     */
    public HomeyAPI(Context ctx){
        // Retrieve the python environment
        Python py = getPython(ctx);

        // Get path to local storage
        String storagePath = ctx.getFilesDir().getAbsolutePath() + "/homeyLocalStorage.db";

        // Create LocalStorage obj with path in local storage
        PyObject storageObj = py.getModule("athom.storage.localstorage").callAttr(
                "LocalStorage",
                new Kwarg("path", storagePath)
                );

        // Get AthomCloudAPI instance
        athomCloudAPI = py.getModule("athom.cloud").callAttr("AthomCloudAPI",
                CLIENT_ID,
                CLIENT_SECRET,
                RETURN_URL,
                new Kwarg("storage", storageObj)
        );
    }

    /**
     * Verify whether there is an authorized session
     * @return if there is an authorized session
     */
    public synchronized Boolean isLoggedIn(){
        return athomCloudAPI.callAttr("isLoggedIn").toBoolean();
    }

    /**
     * Verify whether there is authorization for the HomeyAPI
     * @return if there is an authorized HomeyAPI
     */
    public synchronized Boolean isHomeyAuthenticated(){
        return this.homeyAPI != null;
    }

    /**
     * Wait in thread for authorized HomeyAPI
     */
    public synchronized void waitForHomeyAPI(){
        if (!this.isHomeyAuthenticated()) {
            synchronized (this) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Set an OAUTH2 session token
     * @param token OAUTH2 session token
     */
    public void setToken(String token){
        Log.i(TAG, "Stored OAuth token");
        athomCloudAPI.callAttr("authenticateWithAuthorizationCode", token);
        authenticateHomey();
    }

    /**
     * Get LoginURL used by OAUTH2
     * @return login url used by OAUTH2
     */
    public String getLoginURL() {
        return athomCloudAPI.callAttr(
                "getLoginUrl",
                new Kwarg("scopes", SCOPES)
        ).toString();
    }

    /**
     * Get the URL pointing to the HomeyAPI
     * @return URL of HomeyAPI
     */
    public String getHomeyURL(){
        return homeyAPI.get("url").toString();
    }

    /**
     * Authenticate against the Homey
     */
    public synchronized void authenticateHomey() {
        Log.i(TAG, "Start authenticating API");
        if (this.homeyAPI == null) {
            Log.d(TAG, "Start authenticating API (Thread)");
            PyObject user = athomCloudAPI.callAttr("getUser");
            PyObject homey = user.callAttr("getFirstHomey");

            homeyAPI = homey.callAttr("authenticate", new Kwarg("strategy", "cloud"));
            devicesManager = homeyAPI.get("devices");
            usersManager = homeyAPI.get("users");

            Log.i(TAG, "Authenticated against HomeyAPI: " + homey.toString());

            // Notify all threads that the homeyAPI is authenticated
            synchronized (this){
                this.notifyAll();
            }
        }
    }

    /**
     * Get a list of favorite devices
     * @return list of favorite devices
     */
    public List<Device> getDevices(){
        List<Device> newList = new LinkedList<>();
        Map<String, PyObject> deviceMap = new HashMap<>();

        // retrieve favorites list from user
        Map<PyObject, PyObject> favorites = usersManager.callAttr("getUserMe").get("properties").asMap();
        deviceFavorites = favorites.get("favoriteDevices").asList();

        // Change device list to Map to quickly filter required favorites
        for (PyObject dev : devicesManager.callAttr("getDevices").asList())
            deviceMap.put(dev.get("id").toString(), dev);

        // Only parse favorite devices
        for (PyObject favoriteId : deviceFavorites){
            PyObject dev = deviceMap.get(favoriteId.toString());
            newList.add(Device.parsePyDevice(dev));
        }

        return newList;
    }

    /**
     * Get a list of favorite devices in PyObject form
     * @return map of favorite Pyobject devices with id as key
     */
    public Map<String, PyObject> getDevicesPyObject(){
        List<Device> newList = new LinkedList<>();
        Map<String, PyObject> deviceMap = new HashMap<>();

        // retrieve favorites list from user if undefined
        if(deviceFavorites == null) {
            Map<PyObject, PyObject> favorites = usersManager.callAttr("getUserMe").get("properties").asMap();
            deviceFavorites = favorites.get("favoriteDevices").asList();
        }

        // Change device list to Map to quickly filter required favorites
        for (PyObject dev : devicesManager.callAttr("getDevices").asList())
            if(deviceFavorites.contains(dev.get("id")))
                deviceMap.put(dev.get("id").toString(), dev);

        return deviceMap;
    }

    /**
     * Turn device on or off
     * @param device device to turn on or off
     */
    public void turnOnOff(Device device, boolean value){
        PyObject ret = devicesManager.callAttr(
                "setCapabilityValue",
                new Kwarg("deviceId", device.getId()),
                new Kwarg("capabilityId", device.getCapability()),
                new Kwarg("value", value)
        );
    }

    /**
     * Check for device if its on or off
     * @param device device to check
     * @return device is on?
     */
    public boolean isOn(Device device) {
        try {
            String capability = device.getCapability();
            PyObject pyDevice = devicesManager.callAttrThrows(
                    "getDevice",
                    new Kwarg("id", device.getId())
            );

            Map<PyObject, PyObject> capabilities = pyDevice.get("capabilitiesObj").asMap();
            if (capabilities.containsKey(capability)) {
                return capabilities.get(capability).asMap().get("value").toBoolean();
            } else {
                return true;
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "Device<"+device+">.isOn error: "+throwable.getLocalizedMessage());
        }

        return true;
    }

    /**
     * Retrieve Python instance, start environment if necessary
     * @param ctx ApplicationContext
     * @return Python instance
     */
    private Python getPython(Context ctx){
        if(!Python.isStarted())
            Python.start(new AndroidPlatform(ctx));

        return Python.getInstance();
    }
}

package com.xseth.homey.homey.models;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.xseth.homey.BuildConfig;
import com.xseth.homey.MainActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.GeneralSecurityException;

import timber.log.Timber;

import static com.xseth.homey.utils.utils.getEncryptedInputStream;
import static com.xseth.homey.utils.utils.getEncryptedOutputStream;

public class Token implements Serializable {

    // String holding OAuth bearer token
    @SerializedName("access_token")
    private String accessToken;

    // Time after which accessToken expires
    @SerializedName("expires_in")
    private int expiresIn;

    // States type of accessToken
    @SerializedName("token_type")
    private String tokenType;
    // Token used to get new accessToken

    @SerializedName("refresh_token")
    private String refreshToken;
    // File name used for storing Token value

    @Expose(serialize = false)
    private static String file_name = "token.json";

    /**
     * Get accessToken in Bearer token format used by OAuth2
     * @return accessToken in Bearer token format
     */
    public String getAuthorizationHeader(){ return "Bearer " + this.accessToken; }

    /**
     * Get refreshToken
     * @return refreshToken
     */
    public String getRefreshToken() { return this.refreshToken; }

    /**
     * Set new accessToken
     * @param token new accessToken
     */
    public void setAccessToken(String token) { this.accessToken = token; }

    /**
     * Save current token object to file
     */
    public void save(){
        String path = MainActivity.appPath + "/" + file_name;
        Gson gson = new Gson();

        File f = new File(MainActivity.appPath + "/" + "token.debug.json");

        // In DebugMode also write token in cleartext, required for access in emulator
        if(f.exists() && BuildConfig.DEBUG) {
            try (FileWriter file = new FileWriter(f)) {
                file.write(gson.toJson(this));
                file.flush();

                Timber.i("Load token from file: %s", path);
            } catch (Exception e) {
                Timber.e(e, "Cannot load token to file");
            }
        }

        try (OutputStream output = getEncryptedOutputStream(MainActivity.context,
                "athomToken", path)) {

            output.write(gson.toJson(this).getBytes());
            output.flush();

        } catch (IOException e) {
            Timber.e(e, "Cannot save token to file");
        } catch(GeneralSecurityException gse){
            Timber.e(gse, "Error getting key material to save token to file");
        }
    }

    /**
     * Load a token object from json file
     * @return Token object
     */
    public static Token load(){
        Token token = null;
        Gson gson = new Gson();
        String path = MainActivity.appPath + "/" + file_name;

        File f = new File(MainActivity.appPath + "/" + "token.debug.json");

        // In DebugMode read access Token in cleartext, required for access in emulator
        if(f.exists() && BuildConfig.DEBUG) {
            try (FileReader file = new FileReader(path)) {
                token = gson.fromJson(file, Token.class);
                Timber.i("Load token cleartext from file: %s", path);
                return token;
            } catch (Exception e) {
                Timber.e(e, "Cannot load token to file");
            }
        }

        try (InputStream input = getEncryptedInputStream(MainActivity.context,
                "athomToken", path)) {

            BufferedReader r = new BufferedReader(new InputStreamReader(input));
            StringBuilder total = new StringBuilder();

            // Read all input
            for (String line; (line = r.readLine()) != null; ) {
                total.append(line).append('\n');
            }

            token = gson.fromJson(total.toString(), Token.class);

        } catch (IOException e) {
            Timber.e(e, "Cannot save token to file");
        } catch(GeneralSecurityException gse){
            Timber.e(gse, "Error getting key material to save token to file");
        }

        return token;
    }
}

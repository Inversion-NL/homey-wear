package com.xseth.homey.homey.models;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.xseth.homey.MainActivity;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;

import timber.log.Timber;

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
        try (FileWriter file = new FileWriter(path)) {

            file.write(gson.toJson(this));
            file.flush();

        } catch (IOException e) {
            Timber.e(e, "Cannot save token to file");
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

        try (FileReader file = new FileReader(path)) {
            token = gson.fromJson(file, Token.class);
            Timber.i("Load token from file: %s", path);
        } catch (Exception e) {
            Timber.e(e, "Cannot load token to file");
        }

        return token;
    }
}

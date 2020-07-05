package com.xseth.homey.homey.models;

import com.google.gson.Gson;
import com.xseth.homey.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import timber.log.Timber;

public class Token implements Serializable {

    private String access_token;
    private int expires_in;
    private String token_type;
    private String refresh_token;

    private static String file_name = "token.json";

    public String getAuthorizationHeader(){ return "Bearer " + this.access_token; }

    public String getRefreshToken() { return this.refresh_token; }

    public void setAccessToken(String token) { this.access_token = token; }

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

    @Override
    public String toString(){
        return "<Token> " + this.access_token + "("+this.expires_in+")";
    }

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

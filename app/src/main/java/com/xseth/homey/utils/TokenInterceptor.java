package com.xseth.homey.utils;

import com.xseth.homey.homey.HomeyAPI;
import com.xseth.homey.homey.models.Token;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TokenInterceptor implements Interceptor {

    private Token token;

    public void setSessionToken(Token token) {
        this.token = token;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        Request.Builder requestBuilder = request.newBuilder();

        if (token != null)
            requestBuilder.addHeader("Authorization", token.getAuthorizationHeader());

        Response response = chain.proceed(requestBuilder.build());

        // Token expired, refresh token!
        if((response.code() == 400 || response.code() == 401) && token != null) {
            HomeyAPI.getAPI().refreshToken(token.getRefreshToken());
            return chain.proceed(requestBuilder.build());
        } else{
            return response;
        }
    }
}

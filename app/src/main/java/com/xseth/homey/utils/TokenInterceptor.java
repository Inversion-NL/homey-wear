package com.xseth.homey.utils;

import com.xseth.homey.homey.HomeyAPI;
import com.xseth.homey.homey.models.Token;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

public class TokenInterceptor implements Interceptor {

    // Token object holding session tokens
    private Token token;

    /**
     * Set Token object
     * @param token token object to set
     */
    public void setSessionToken(Token token) {
        this.token = token;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        Request.Builder requestBuilder = request.newBuilder();

        // If token present set Authorization header
        if (token != null)
            requestBuilder.addHeader("Authorization", token.getAuthorizationHeader());

        Response response = chain.proceed(requestBuilder.build());

        // Token expired, refresh token if available
        if((response.code() == 400 || response.code() == 401) && token != null &&
                token.getRefreshToken() != null) {

            Timber.w("401 denied, refreshing token!");
            response.close();

            HomeyAPI.getAPI().refreshToken(token.getRefreshToken());
            requestBuilder.header("Authorization", token.getAuthorizationHeader());

            return chain.proceed(requestBuilder.build());

        } else
            return response;
    }


}

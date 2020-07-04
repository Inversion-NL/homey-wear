package com.xseth.homey.homey.services;

import com.xseth.homey.homey.models.Token;
import com.xseth.homey.homey.models.User;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface CloudService {

    @GET("user/me")
    Call<User> getUser();

    @POST("delegation/token?audience=homey")
    Call<String> authenticateHomey(@Body RequestBody body);

    @GET("oauth2/authorise?response_type=code")
    Call<String> getLoginURL(@Query("client_id") String client_id, @Query("redirect_uri") String
            redirect_uri, @Query("scopes") String scopes);

    @FormUrlEncoded
    @POST("oauth2/token")
    Call<Token> authenticate(@Field("client_id") String client_id, @Field("client_secret") String
            client_secret, @Field("grant_type") String grant_type, @Field("code") String code);

    @FormUrlEncoded
    @POST("oauth2/token")
    Call<Token> refreshToken(@Field("client_id") String client_id, @Field("client_secret") String
            client_secret, @Field("grant_type") String grant_type, @Field("refresh_token") String code);
}

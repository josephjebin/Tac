package com.example.tac.data

import com.google.android.gms.common.internal.safeparcel.SafeParcelable

import com.renaultnissan.acms.platform.oauth.githubsample.transverse.model.OAuthToken;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Mathias Seguy - Android2EE on 04/01/2017.
 */
public interface OAuthServerInterface {
    /**
     * The call to request a token
     */
    @FormUrlEncoded
    @POST("oauth2/v4/token")
    Call<OAuthToken> requestTokenForm(
    @SafeParcelable.Field("code") String code,
    @SafeParcelable.Field("client_id") String client_id,
//            @Field("client_secret")String client_secret, //Is not relevant for Android application
    @SafeParcelable.Field("redirect_uri") String redirect_uri,
    @SafeParcelable.Field("grant_type") String grant_type);

    /**
     * The call to refresh a token
     */
    @FormUrlEncoded
    @POST("oauth2/v4/token")
    Call<OAuthToken> refreshTokenForm(
    @SafeParcelable.Field("refresh_token") String refresh_token,
    @SafeParcelable.Field("client_id") String client_id,
//            @Field("client_secret")String client_secret, //Is not relevant for Android application
    @SafeParcelable.Field("grant_type") String grant_type);
}
package org.musicbrainz.android.api.oauth;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;

/**
 * Created by Alex on 07.12.2017.
 */

public interface OAuthServiceInterface {

    Flowable<Result<OAuthCredential>> authorize(String username, String password);
    Flowable<Result<OAuthCredential>> refreshToken(String refreshToken);
    Flowable<Result<OAuthCredential.UserInfo>> getUserInfo(String accessToken);
    Flowable<Result<OAuthCredential.TokenInfo>> getTokenInfo(String accessToken);

}

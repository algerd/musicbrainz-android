package org.musicbrainz.android.api;

import java.io.IOException;

import retrofit2.Response;

/**
 * Created by Alex on 07.12.2017.
 */

public class HttpException extends Exception {

    private Response response;

    public HttpException(Response response) {
        super("HTTP Error. Status Code: " + response.code() + " " + response.message());
        this.response = response;
    }

    public Response getResponse() {
        return response;
    }

    public String getContent() {
        try {
            return response.errorBody().string();
        } catch (IOException e) {
            return "";
        }
    }
}

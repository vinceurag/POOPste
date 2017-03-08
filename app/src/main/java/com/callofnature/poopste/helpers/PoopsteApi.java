package com.callofnature.poopste.helpers;

/**
 * Created by vinceurag on 06/03/2017.
 */

import com.callofnature.poopste.model.Model;
import com.loopj.android.http.*;

import cz.msebera.android.httpclient.entity.StringEntity;

public class PoopsteApi {
    private static final String BASE_URL = "http://yellowbird.cafe/poopste_api/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), responseHandler);
    }

    public static void post(String url, StringEntity params, AsyncHttpResponseHandler responseHandler) {
        client.post(null, getAbsoluteUrl(url), params, "application/json", responseHandler);
    }

    public static void postWithHeader(String url, StringEntity params, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("Authorization", "Bearer " + Model.getToken());
        client.post(null, getAbsoluteUrl(url), params, "application/json", responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}

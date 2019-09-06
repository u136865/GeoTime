package com.locactio.geotime.WS;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class RestClient {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final static String BASE_URL = "http://newapi.intratime.es/api/user/";

    private static final String USER_HEADER = "user";
    private static final String PIN_HEADER = "pin";
    private static final String TOKEN_HEADER = "token";


    private static final String LOGIN_METHOD = "login";
    private static final String CLOCKING_METHOD = "clockings";

    public static void LOGIN(String user, String password, Callback callback) {
        Log.e("POST", BASE_URL + LOGIN_METHOD);
        JSONObject json = new JSONObject();
        try {
            json.put(USER_HEADER, user);
            json.put(PIN_HEADER, password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(JSON, json.toString());
        OkHttpClient client = buildClient();
        Request request = new Request.Builder()
                .url(BASE_URL + LOGIN_METHOD)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void USER_CLOCKINGS(String token, String from, String to, Callback callback)
    {
        Log.e("GET", BASE_URL + CLOCKING_METHOD);

        String params = "?from=" + from + "&to=" + to;
        OkHttpClient client = buildClient();
        Request request = new Request.Builder()
                .url(BASE_URL + CLOCKING_METHOD + params)
                .addHeader("Accept", "application/json")
                .addHeader("token", token)
                .build();

        client.newCall(request).enqueue(callback);
    }

    private static OkHttpClient buildClient() {
        return buildClient(45);
    }

    private static OkHttpClient buildClient(int timeout) {
        return new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .build();
    }


}

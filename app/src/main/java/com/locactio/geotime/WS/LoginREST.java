package com.locactio.geotime.WS;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginREST {


    private static final String token = "USER_TOKEN";
    private static final String working_hours = "USER_WORKING_TIME";
    private static final String name = "USER_NAME";

    public static void execute(String user, String pass, final LoginResponseHandler handler)
    {
        RestClient.LOGIN(user, pass, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("On failure", call.toString());
                handler.onError();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                Log.d("On response", string);
                if (response.code() == 401) {
                    handler.onLoginFailure();
                    return;
                }else if (response.code() == 201) {
                    try {
                        JSONObject jsonObject = new JSONObject(string);
                        String tokenString = jsonObject.getString(token);
                        String nombre = jsonObject.getString(name);
                        int hours = jsonObject.getInt(working_hours);
                        handler.onResponse(tokenString, hours, nombre);
                        return;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                handler.onError();

            }
        });
    }
}

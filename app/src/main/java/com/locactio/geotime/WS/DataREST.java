package com.locactio.geotime.WS;

import android.util.Log;

import com.locactio.geotime.entities.Clocking;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.Format;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DataREST {

    private static final String tipo = "INOUT_TYPE";
    private static final String momento = "INOUT_DATE";

    public static synchronized void execute(String token, Date from, Date to, final ClockingResponseHandler handler)
    {

        Calendar start = Calendar.getInstance();
        start.setTime(from);
        Calendar end = Calendar.getInstance();
        end.setTime(to);

        String st = start.get(Calendar.YEAR) + "-" + String.format("%02d",start.get(Calendar.MONTH)+1) + "-" + String.format("%02d",start.get(Calendar.DAY_OF_MONTH)) +
                " " + String.format("%02d",start.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d",start.get(Calendar.MINUTE)) + ":" + String.format("%02d",start.get(Calendar.SECOND));
        String nd = end.get(Calendar.YEAR) + "-" + String.format("%02d",end.get(Calendar.MONTH)+1) + "-" + String.format("%02d",end.get(Calendar.DAY_OF_MONTH)) +
                " " + String.format("%02d",end.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d",end.get(Calendar.MINUTE)) + ":" + String.format("%02d",end.get(Calendar.SECOND));

        RestClient.USER_CLOCKINGS(token, st, nd, new Callback() {
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
                    handler.onRequestFailure();
                    return;
                }else if (response.code() == 200) {
                    try {
                        JSONArray jsonArray = new JSONArray(string);
                        ArrayList<Clocking> clockings = new ArrayList<>();
                        for (int i= 0; i < jsonArray.length(); i++)
                        {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String momentoString = jsonObject.getString(momento);
                            int type = jsonObject.getInt(tipo);
                            clockings.add(new Clocking(momentoString, type));
                        }

                        handler.onResponse(clockings);
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

package com.locactio.geotime.WS;

import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.locactio.geotime.PrincipalActivity;
import com.locactio.geotime.entities.Clocking;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ClockingRest {

    private static final String tipo = "INOUT_TYPE";
    private static final String momento = "INOUT_DATE";

    public static final String ENTRAR = "0";
    public static final String SALIR = "1";
    public static final String PAUSA = "2";
    public static final String VOLVER = "3";

    public static void execute(String token, String accion, PrincipalActivity activity, final FichajeResponseHandler handler)
    {
        //2019-10-01 21:34:00
        Calendar c = Calendar.getInstance();
        String timestamp = String.format("%04d-%02d-%02d %02d:%02d:%02d", c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1, c.get(Calendar.DAY_OF_MONTH),
                c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
        Location l = activity.getLocalizacion();
        String location = String.format("%f*%f", l.getLatitude(), l.getLongitude());
        location = location.replace(",",".").replace("*", ",");


        RestClient.CLOCK(token, accion, timestamp, location, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.onError();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                Log.d("On response", string);
                if (response.code() == 401) {
                    handler.onRequestFailure();
                    return;
                }else if (response.code() == 200 || response.code() == 201) {
                    handler.onResponse();
                    return;
                }
                handler.onError();
            }
        });


        /*Calendar start = Calendar.getInstance();
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
        });*/
    }
}

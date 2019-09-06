package com.locactio.geotime.WS;

public interface LoginResponseHandler {
    void onError();
    void onLoginFailure();
    void onResponse(String token, int hours, String nombre_completo);
}

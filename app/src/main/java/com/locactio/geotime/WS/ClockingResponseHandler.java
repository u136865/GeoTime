package com.locactio.geotime.WS;

import com.locactio.geotime.entities.Clocking;

import java.util.List;

public interface ClockingResponseHandler {
    void onError();
    void onRequestFailure();
    void onResponse(List<Clocking> clockingList);
}

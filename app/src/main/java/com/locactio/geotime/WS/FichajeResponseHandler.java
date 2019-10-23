package com.locactio.geotime.WS;

import com.locactio.geotime.entities.Clocking;

import java.util.List;

public interface FichajeResponseHandler {
        void onError();
        void onRequestFailure();
        void onResponse();
}

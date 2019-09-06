package com.locactio.geotime;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class GeoTime extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        /*CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Typo.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );*/
    }
}

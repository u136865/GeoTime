package com.locactio.geotime;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.lemonade.widgets.slidesidemenu.SlideSideMenuTransitionLayout;
import com.locactio.geotime.fragments.ClockingFragment;
import com.locactio.geotime.fragments.RangeFragment;


public class PrincipalActivity extends Template {

    String token = "";
    int hours = 0;
    ClockingFragment cf;
    RangeFragment rf;
    SlideSideMenuTransitionLayout slide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Intent i = getIntent();
        token = i.getStringExtra("token");
        hours = i.getIntExtra("hours", 41);

        slide = findViewById(R.id.slide_side_menu);

        cf = ClockingFragment.newInstance(token, hours);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_content, cf);
        ft.commit();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void horario(View view) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_content, cf);
        ft.commit();
        slide.closeSideMenu();
    }


    public void rango(View view) {
        if (rf == null) {
            rf = RangeFragment.newInstance();
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_content, rf);
        ft.commit();
        slide.closeSideMenu();

    }

    public void informes(View view) {

    }

    private static final String pinTkn = "9V1%YaPO&dX&";

    public void cerrarSesion(View view) {
        SharedPreferences.Editor pref = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE).edit();
        pref.remove(pinTkn).apply();
        Intent intent = new Intent(PrincipalActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

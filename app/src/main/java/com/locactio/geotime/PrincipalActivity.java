package com.locactio.geotime;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.lemonade.widgets.slidesidemenu.SlideSideMenuTransitionLayout;
import com.locactio.geotime.fragments.ClockingFragment;
import com.locactio.geotime.fragments.MainFragment;
import com.locactio.geotime.fragments.RangeFragment;
import com.locactio.geotime.utils.Utils;


public class PrincipalActivity extends Template {

    String token = "";
    int hours = 0;
    MainFragment mf;
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

        Utils.setToken(token,this);

        slide = findViewById(R.id.slide_side_menu);

        //cf = ClockingFragment.newInstance(hours);
        mf = MainFragment.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_content, mf);
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
        if (cf == null)
            cf = ClockingFragment.newInstance(hours);
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

    public void principal(View view) {
        if (mf == null)
        {
            mf = MainFragment.newInstance();
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_content, mf);
        ft.commit();
        slide.closeSideMenu();
    }
}

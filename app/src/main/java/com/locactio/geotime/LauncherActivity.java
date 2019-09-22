package com.locactio.geotime;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class LauncherActivity extends Activity {

    private static final String userTkn = "x2Lhp3Iun5Nc";
    private static final String pinTkn = "9V1%YaPO&dX&";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_launcher);
        SharedPreferences pref = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        String correoPref = pref.getString(userTkn, "");
        String passPref = pref.getString(pinTkn,"");

        if (!correoPref.equalsIgnoreCase("") && !passPref.equalsIgnoreCase(""))
        {
            Intent i = new Intent(LauncherActivity.this, PrincipalActivity.class);
            i.putExtra("token", "");
            i.putExtra("hours", 0);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }else{
            Intent intent = new Intent(LauncherActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

}

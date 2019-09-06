package com.locactio.geotime;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.locactio.geotime.WS.LoginREST;
import com.locactio.geotime.WS.LoginResponseHandler;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.support.design.widget.Snackbar.LENGTH_LONG;

public class LoginActivity extends Template {

    EditText correo;
    EditText pass;
    CoordinatorLayout coordinatorLayout;

    private static final String userTkn = "x2Lhp3Iun5Nc";
    private static final String pinTkn = "9V1%YaPO&dX&";
    private static SharedPreferences pref;
    private static boolean pulsado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pref = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        correo = findViewById(R.id.email);
        pass = findViewById(R.id.password);
        correo.setText(pref.getString(userTkn,""));
        pass.setText(pref.getString(pinTkn,""));
        coordinatorLayout = findViewById(R.id.coordinator);

    }



    public void conectar(View view) {

        if(pulsado)
            return;
            pulsado = true;

        LoginREST.execute(correo.getText().toString(), pass.getText().toString(), new LoginResponseHandler() {
            @Override
            public void onError() {
                Snackbar.make(coordinatorLayout, getString(R.string.error_ocurred), LENGTH_LONG).show();
                pulsado = false;
            }

            @Override
            public void onLoginFailure() {
                Snackbar.make(coordinatorLayout, getString(R.string.error_login), LENGTH_LONG).show();
                SharedPreferences.Editor editor = pref.edit();
                editor.remove(userTkn);
                editor.remove(pinTkn);
                editor.commit();
                pulsado = false;
            }

            @Override
            public void onResponse(String token, int horas, String nombre) {
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(userTkn,correo.getText().toString());
                editor.putString(pinTkn,pass.getText().toString());
                editor.commit();

                pulsado = false;
                Intent i = new Intent(LoginActivity.this, PrincipalActivity.class);
                i.putExtra("token", token);
                i.putExtra("name", nombre);
                i.putExtra("hours", horas);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);

            }
        });
    }

}
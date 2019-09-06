package com.locactio.geotime;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.locactio.geotime.WS.ClockingResponseHandler;
import com.locactio.geotime.WS.DataREST;
import com.locactio.geotime.WS.LoginREST;
import com.locactio.geotime.WS.LoginResponseHandler;
import com.locactio.geotime.entities.Clocking;
import com.locactio.geotime.entities.Day;
import com.locactio.geotime.entities.Week;
import com.locactio.geotime.utils.TableViewAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;
import segmented_control.widget.custom.android.com.segmentedcontrol.item_row_column.SegmentViewHolder;
import segmented_control.widget.custom.android.com.segmentedcontrol.listeners.OnSegmentSelectRequestListener;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PrincipalActivity extends Template {

    String token;
    String nombreCompleto;
    int hours;
    Date today, monday, sunday, yearAgo;
    ArrayList<Clocking> horasSemana;
    ArrayList<Clocking> horas;
    ArrayList<Clocking> horasTotales;

    ArrayList<Day> dias = new ArrayList<>();
    ArrayList<Week> semanas = new ArrayList<>();

    CoordinatorLayout coordinatorLayout;
    ListView table;
    TableViewAdapter adapter;
    SegmentedControl segmentedControl;
    TextView t1,t2,t3,d1,d2,d3,ft;
    Day diaSeleccionado;
    Week semanaSeleccionada;
    private static final String userTkn = "x2Lhp3Iun5Nc";
    private static final String pinTkn = "9V1%YaPO&dX&";
    private static SharedPreferences pref;
    KProgressHUD hud;
    Date startOfSummer, endOfSummer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);


        coordinatorLayout = findViewById(R.id.coordinator);
        segmentedControl = findViewById(R.id.segmented);
        table = findViewById(R.id.table);
        t1 = findViewById(R.id.title1);
        t2 = findViewById(R.id.title2);
        t3 = findViewById(R.id.title3);
        d1 = findViewById(R.id.detail1);
        d2 = findViewById(R.id.detail2);
        d3 = findViewById(R.id.detail3);
        ft = findViewById(R.id.fechaTitulo);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.MONTH, 6);
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND,0);
        c.add(Calendar.SECOND, -1);
        startOfSummer = c.getTime();

        c.set(Calendar.DAY_OF_MONTH, 31);
        c.set(Calendar.MONTH, 7);
        c.set(Calendar.HOUR_OF_DAY,23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND,59);
        c.add(Calendar.SECOND,1);
        endOfSummer = c.getTime();


        t1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                hud.dismiss();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        horasSemana = new ArrayList<>();
        horasTotales = new ArrayList<>();
        Intent i = getIntent();
        token = i.getStringExtra("token");
        nombreCompleto = i.getStringExtra("name");
        hours = i.getIntExtra("hours",41);

        c = Calendar.getInstance();
        today = c.getTime();
        c.add(Calendar.DAY_OF_MONTH, -c.get(Calendar.DAY_OF_WEEK) + 2 );
        monday = c.getTime();
        c.add(Calendar.DAY_OF_MONTH, 6);
        sunday = c.getTime();
        c = Calendar.getInstance();
        c.add(Calendar.YEAR, -1);
        yearAgo = c.getTime();

        segmentedControl.setSelectedSegment(0);

        Log.d("Fechas", "Hoy " + today.toString() + "\nLunes: " + monday.toString() + "\nDomingo: " + sunday.toString());

        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Espere")
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .setBackgroundColor(getResources().getColor(R.color.naranjaFondo));
        hud.show();

        request_info(yearAgo, sunday);

        adapter = new TableViewAdapter(PrincipalActivity.this, R.layout.hour_row, horasSemana);
        table.setAdapter(adapter);

        segmentedControl.setOnSegmentSelectRequestListener(new OnSegmentSelectRequestListener() {
            @Override
            public boolean onSegmentSelectRequest(SegmentViewHolder segmentViewHolder) {
                refreshScreenData(false);
                return true;
            }
        });
    }


    private void request_info(Date d1, Date d2)
    {
        DataREST.execute(token, d1, d2, new ClockingResponseHandler() {
            @Override
            public void onError() {
                hud.dismiss();
                Snackbar.make(coordinatorLayout, getResources().getString(R.string.error_ocurred),Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onRequestFailure() {
                pref = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
                String user = pref.getString(userTkn,"");
                String pin = pref.getString(pinTkn, "");
                LoginREST.execute(user, pin, new LoginResponseHandler() {
                    @Override
                    public void onError() {
                        hud.dismiss();
                        Snackbar.make(coordinatorLayout, getResources().getString(R.string.error_ocurred),Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onLoginFailure() {
                        hud.dismiss();
                        Snackbar.make(coordinatorLayout, getResources().getString(R.string.error_ocurred),Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String tkn, int hours, String nombre_completo) {
                        token = tkn;
                        request_info(yearAgo, today);
                    }
                });
            }

            @Override
            public void onResponse(List<Clocking> clockingList) {
                horasTotales.addAll(clockingList);
                if (horas != null)
                    horas.clear();
                else
                    horas = new ArrayList<>();

                Date valorando = new Date(0);
                Date valorandoW = new Date(0);
                Day inProgress = null;
                Week weekInProgress = null;
                for (Clocking c : horasTotales)
                {
                    if (!sameDay(valorando, c.getMomento()))
                    {
                        valorando = c.getMomento();

                        if(inProgress != null)
                        {
                            inProgress.calculateTimes();
                            dias.add(inProgress);


                            if (!sameWeek(valorandoW, inProgress.getFecha()))
                            {
                                valorandoW = inProgress.getFecha();

                                if(weekInProgress != null)
                                {
                                    weekInProgress.calculateTimes();
                                    semanas.add(weekInProgress);
                                }
                                weekInProgress = new Week(hours * 1.0f, startOfSummer, endOfSummer);
                            }
                            weekInProgress.addDay(inProgress);

                        }
                        inProgress = new Day(hours * 1.0f, startOfSummer, endOfSummer);
                    }
                    inProgress.addClocking(c);

                }

/*               if (segmentedControl.getLastSelectedAbsolutePosition() == 0)
                {
                    for (Clocking c: dias.get(0).getFichajes())
                    {
                        if (sameDay(new Date(),c.getMomento()))
                            horas.add(c);
                    }
                }else{
                    horas.addAll(horasSemana);
                }
*/

                diaSeleccionado = dias.get(0);
                Calendar c = Calendar.getInstance();
                c.setTime(diaSeleccionado.getFecha());
                semanaSeleccionada = semanas.get(0);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshScreenData(true);
                    }
                });
            }
        });
    }


    private void refreshScreenData(boolean init)
    {
        Calendar c = Calendar.getInstance();
        int selected = segmentedControl.getLastSelectedAbsolutePosition();
        if ( (selected == 0 && init) || (selected == 1 && !init))
        {
            t1.setText(R.string.trabajado);
            if (diaSeleccionado.isEnDescanso())
            {
                t2.setText(R.string.tiempo_restante);
            }else {
                t2.setText(R.string.hora_salida);
            }
            t3.setText(R.string.descansado);
            Date trabajado = new Date(diaSeleccionado.getSegundosTrabajados() * 1000);
            c.setTime(trabajado);
            d1.setText(String.format("%02d",c.get(Calendar.HOUR_OF_DAY)-1) + ":" + String.format("%02d",c.get(Calendar.MINUTE)) + ":" + String.format("%02d",c.get(Calendar.SECOND)));
            c.setTime(diaSeleccionado.getHoraSalida());
            d2.setText(String.format("%02d",c.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d",c.get(Calendar.MINUTE)) + ":" + String.format("%02d",c.get(Calendar.SECOND)));
            String recalculado = diaSeleccionado.friday(semanas);
            d2.setText(recalculado.equalsIgnoreCase("")?d2.getText():recalculado);

            Date descansando = new Date(diaSeleccionado.getDescanso() * 1000);
            c.setTime(descansando);
            d3.setText(String.format("%02d",c.get(Calendar.HOUR_OF_DAY)-1) + ":" + String.format("%02d",c.get(Calendar.MINUTE)) + ":" + String.format("%02d",c.get(Calendar.SECOND)));
            c.setTime(diaSeleccionado.getFecha());
            ft.setText(diaDeLaSemana() + ", " + String.format("%02d",c.get(Calendar.DAY_OF_MONTH)) + "-" + String.format("%02d",c.get(Calendar.MONTH)+1) + "-" + String.format("%04d",c.get(Calendar.YEAR)));
            adapter.renewData(diaSeleccionado.getFichajes());
        }else{
            t1.setText(R.string.total_semana);
            t3.setText(R.string.horas_trabajadas);
            t2.setText(R.string.horas_restantes);

            long horas = semanaSeleccionada.getSegundosObjetivo() / 3600;
            long minutos = (semanaSeleccionada.getSegundosObjetivo() - (horas*3600)) / 60;
            long segundos = (semanaSeleccionada.getSegundosObjetivo() - (horas*3600) - (minutos * 60));
            d1.setText(String.format("%02d",horas) + ":" + String.format("%02d",minutos) + ":" + String.format("%02d",segundos));

            horas = semanaSeleccionada.getSegundosTrabajados() / 3600;
            minutos = (semanaSeleccionada.getSegundosTrabajados() - (horas*3600)) / 60;
            segundos = (semanaSeleccionada.getSegundosTrabajados() - (horas*3600) - (minutos * 60));
            d3.setText(String.format("%02d",horas) + ":" + String.format("%02d",minutos) + ":" + String.format("%02d",segundos));

            long segundosRestantes = semanaSeleccionada.getSegundosObjetivo() - semanaSeleccionada.getSegundosTrabajados();
            if (segundosRestantes < 0)
                segundosRestantes = 0;

            horas = segundosRestantes / 3600;
            minutos = (segundosRestantes - (horas*3600)) / 60;
            segundos = (segundosRestantes - (horas*3600) - (minutos * 60));
            d2.setText(String.format("%02d",horas) + ":" + String.format("%02d",minutos) + ":" + String.format("%02d",segundos));

            c.setTime(semanaSeleccionada.getHours().get(0).getMomento());
            c.add(Calendar.DAY_OF_MONTH, -c.get(Calendar.DAY_OF_WEEK) + 2 );
            ft.setText(String.format("%02d",c.get(Calendar.DAY_OF_MONTH)) + "/" + String.format("%02d",c.get(Calendar.MONTH)+1) + "/" + String.format("%04d",c.get(Calendar.YEAR)) + " - ");
            c.add(Calendar.DAY_OF_MONTH, 6);
            ft.setText(ft.getText() + String.format("%02d",c.get(Calendar.DAY_OF_MONTH)) + "/" + String.format("%02d",c.get(Calendar.MONTH)+1) + "/" + String.format("%04d",c.get(Calendar.YEAR)));

            adapter.renewData(semanaSeleccionada.getHours());
        }
    }


    private boolean sameDay(Date date1, Date date2)
    {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        boolean sameDay = cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
        return sameDay;
    }

    public static boolean sameWeek(Date date1, Date date2)
    {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        boolean sameDay = cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
        return sameDay;
    }

    private String diaDeLaSemana()
    {
        Calendar c = Calendar.getInstance();
        c.setTime(diaSeleccionado.getFecha());

        switch (c.get(Calendar.DAY_OF_WEEK))
        {
            case Calendar.MONDAY:
                return getString(R.string.lunes);
            case Calendar.TUESDAY:
                return getString(R.string.martes);
            case Calendar.WEDNESDAY:
                return getString(R.string.miercoles);
            case Calendar.THURSDAY:
                return getString(R.string.jueves);
            case Calendar.FRIDAY:
                return getString(R.string.viernes);
            case Calendar.SATURDAY:
                return getString(R.string.sabado);
            case Calendar.SUNDAY:
                return getString(R.string.domingo);
        }
        return "";
    }

    public void anterior(View view) {
        int selected = segmentedControl.getLastSelectedAbsolutePosition();

        if (selected == 0)
        {

            if (dias.indexOf(diaSeleccionado) < dias.size() - 1) {
                diaSeleccionado = dias.get(dias.indexOf(diaSeleccionado) + 1);
                refreshScreenData(true);
            }
        }else{
            if (semanas.indexOf(semanaSeleccionada) < semanas.size() - 1){
                semanaSeleccionada = semanas.get(semanas.indexOf(semanaSeleccionada) + 1);
                refreshScreenData(true);
            }
        }
    }

    public void posterior(View view) {
        int selected = segmentedControl.getLastSelectedAbsolutePosition();
        if (selected == 0)
        {
            if (dias.indexOf(diaSeleccionado) > 0) {
                diaSeleccionado = dias.get(dias.indexOf(diaSeleccionado) - 1);
                refreshScreenData(true);
            }
        }else{
            if (semanas.indexOf(semanaSeleccionada) > 0){
                semanaSeleccionada = semanas.get(semanas.indexOf(semanaSeleccionada) - 1);
                refreshScreenData( true);
            }
        }
    }
}
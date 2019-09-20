package com.locactio.geotime;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.lemonade.widgets.slidesidemenu.SlideSideMenuTransitionLayout;
import com.locactio.geotime.WS.ClockingResponseHandler;
import com.locactio.geotime.WS.DataREST;
import com.locactio.geotime.WS.LoginREST;
import com.locactio.geotime.WS.LoginResponseHandler;
import com.locactio.geotime.bbdd.facades.ClockingLab;
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

public class PrincipalActivity extends Template {

    String token;
    String nombreCompleto;
    int hours;
    Date today, monday, sunday, yearAgo;
    ArrayList<Clocking> horasSemana;
    ArrayList<Clocking> horasTotales;

    ArrayList<Day> dias = new ArrayList<>();
    ArrayList<Week> semanas = new ArrayList<>();

    private SlideSideMenuTransitionLayout mSlideSideMenu;

    CoordinatorLayout coordinatorLayout;
    ListView table;
    TableViewAdapter adapter;
    SegmentedControl segmentedControl;
    SegmentedControl segmentedControl2;
    SegmentViewHolder sC1, sC2;
    TextView t1, t2, t3, d1, d2, d3, ft;
    Day diaSeleccionado;
    Week semanaSeleccionada;
    private static final String userTkn = "x2Lhp3Iun5Nc";
    private static final String pinTkn = "9V1%YaPO&dX&";
    private static SharedPreferences pref;
    private boolean seleccionManual = false;
    KProgressHUD hud;
    Date startOfSummer, endOfSummer;
    Boolean onCreateRequest = true;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);


        coordinatorLayout = findViewById(R.id.coordinator);
        segmentedControl = findViewById(R.id.segmented);
        segmentedControl2 = findViewById(R.id.segmented0);
        swipeRefreshLayout = findViewById(R.id.refreshLayout);
        mSlideSideMenu = (SlideSideMenuTransitionLayout)findViewById(R.id.slide_side_menu);

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
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.add(Calendar.SECOND, -1);
        startOfSummer = c.getTime();

        c.set(Calendar.DAY_OF_MONTH, 31);
        c.set(Calendar.MONTH, 7);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.add(Calendar.SECOND, 1);
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
        hours = i.getIntExtra("hours", 41);

        c = Calendar.getInstance();
        today = c.getTime();
        c.add(Calendar.DAY_OF_MONTH, -c.get(Calendar.DAY_OF_WEEK) + 2);
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

        Date prev = null;
        horasTotales = ClockingLab.get(this).getClockings();

        if (horasTotales.size() == 0)
            prev = yearAgo;
        else
            prev = horasTotales.get(0).getMomento();

        Calendar cal = Calendar.getInstance();
        cal.setTime(prev);
        cal.add(Calendar.SECOND, 1);
        prev = cal.getTime();

        request_info(prev, sunday);

        adapter = new TableViewAdapter(PrincipalActivity.this, R.layout.hour_row, horasSemana);
        table.setAdapter(adapter);

        segmentedControl.setOnSegmentSelectRequestListener(new OnSegmentSelectRequestListener() {
                                                               @Override
                                                               public boolean onSegmentSelectRequest(SegmentViewHolder segmentViewHolder) {

                                                                   refreshScreenData(false);

                                                                   Log.d("SEGMENTED", "" + segmentViewHolder.getColumn() + " " + segmentViewHolder.getAbsolutePosition() + " " + segmentViewHolder);
                                                                   sC1 = segmentViewHolder;
                                                                   if (segmentViewHolder.getColumn() == 0) {
                                                                       if (sameDay(diaSeleccionado.getFecha(), dias.get(0).getFecha())) {
                                                                           seleccionarHoy(true);
                                                                       } else {
                                                                           seleccionarHoy(false);
                                                                       }

                                                                   } else if (segmentViewHolder.getColumn() == 1) {
                                                                       if (sameWeek(semanaSeleccionada.getDays().get(0).getFecha(), dias.get(0).getFecha())) {
                                                                           seleccionarHoy(true);
                                                                       } else {
                                                                           seleccionarHoy(false);
                                                                       }
                                                                   }

                                                                   return true;
                                                               }
                                                           }
        );

        segmentedControl2.setSelectedSegment(0);

        segmentedControl2.setOnSegmentSelectRequestListener(new OnSegmentSelectRequestListener() {
            @Override
            public boolean onSegmentSelectRequest(SegmentViewHolder segmentViewHolder) {

                if (seleccionManual) {
                    seleccionManual = false;
                    return true;
                }

                if (sC1 != null) {
                    if (sC1.getColumn() == 0) {
                        diaSeleccionado = dias.get(0);
                        refreshScreenData(true);
                    } else {
                        semanaSeleccionada = semanas.get(0);
                        //segmentedControl.setSelectedSegment(0);
                        refreshScreenData(true);
                    }
                } else {
                    if (segmentedControl.getLastSelectedAbsolutePosition() == 0) {
                        diaSeleccionado = dias.get(0);
                        refreshScreenData(true);
                    } else if (segmentedControl.getLastSelectedAbsolutePosition() == 1) {
                        semanaSeleccionada = semanas.get(0);
                        //segmentedControl.setSelectedSegment(0);
                        refreshScreenData(true);
                    }
                }

                sC1 = null;

                return true;
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Calendar c = Calendar.getInstance();
                c.setTime(dias.get(0).getFecha());
                c.add(Calendar.SECOND, 1);
                request_info(c.getTime(), new Date());
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (mSlideSideMenu != null && mSlideSideMenu.closeSideMenu()) {
            // Closed the side menu, override the default back pressed behavior
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        refreshScreenData(true);

        if (!onCreateRequest) {
            Calendar c = Calendar.getInstance();
            c.setTime(dias.get(0).getFecha());
            c.add(Calendar.SECOND, 1);
            request_info(c.getTime(), new Date());
        }
    }

    Chronometer chronometer;

    private void chronometer(boolean activar) {
        if (chronometer == null) {
            chronometer = new Chronometer(this);
            chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    refreshScreenData(true);
                }
            });
            chronometer.setBase(SystemClock.elapsedRealtime());
        }

        if (activar)
            chronometer.start();
        else
            chronometer.stop();

    }

    private void request_info(Date d1, Date d2) {
        hud.show();
        DataREST.execute(token, d1, d2, new ClockingResponseHandler() {
            @Override
            public void onError() {
                hud.dismiss();
                swipeRefreshLayout.setRefreshing(false);
                Snackbar.make(coordinatorLayout, getResources().getString(R.string.error_ocurred), Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onRequestFailure() {
                pref = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
                String user = pref.getString(userTkn, "");
                String pin = pref.getString(pinTkn, "");
                LoginREST.execute(user, pin, new LoginResponseHandler() {
                    @Override
                    public void onError() {
                        hud.dismiss();
                        swipeRefreshLayout.setRefreshing(false);
                        Snackbar.make(coordinatorLayout, getResources().getString(R.string.error_ocurred), Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onLoginFailure() {
                        hud.dismiss();
                        swipeRefreshLayout.setRefreshing(false);
                        Snackbar.make(coordinatorLayout, getResources().getString(R.string.error_ocurred), Snackbar.LENGTH_LONG).show();
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
                onCreateRequest = false;
                horasTotales.addAll(0, clockingList);
                dias.clear();
                semanas.clear();

                ClockingLab.get(PrincipalActivity.this).addAllClockings(clockingList);
                Date valorando = new Date(0);
                Date valorandoW = new Date(0);
                Day inProgress = null;
                Week weekInProgress = null;
                for (Clocking c : horasTotales) {
                    if (!sameDay(valorando, c.getMomento())) {
                        valorando = c.getMomento();

                        if (inProgress != null) {
                            inProgress.calculateTimes();
                            dias.add(inProgress);


                            if (!sameWeek(valorandoW, inProgress.getFecha())) {
                                valorandoW = inProgress.getFecha();

                                if (weekInProgress != null) {
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

                if (dias.size() == 0) {
                    inProgress.calculateTimes();
                    dias.add(inProgress);
                }

                if (semanas.size() == 0) {
                    weekInProgress = new Week(hours * 1.0f, startOfSummer, endOfSummer);
                    weekInProgress.addDay(inProgress);
                    weekInProgress.calculateTimes();
                    semanas.add(weekInProgress);
                }

                diaSeleccionado = dias.get(0);
                Calendar c = Calendar.getInstance();
                c.setTime(diaSeleccionado.getFecha());
                semanaSeleccionada = semanas.get(0);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshScreenData(true);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }


    private void refreshScreenData(boolean init) {
        diaSeleccionado.calculateTimes();
        semanaSeleccionada.calculateTimes();

        Calendar c = Calendar.getInstance();
        int selected = segmentedControl.getLastSelectedAbsolutePosition();
        if ((selected == 0 && init) || (selected == 1 && !init)) {
            t1.setText(R.string.trabajado);
            if (diaSeleccionado.isEnDescanso()) {
                t2.setText(R.string.tiempo_restante);
            } else {
                t2.setText(R.string.hora_salida);
            }
            t3.setText(R.string.descansado);
            Date trabajado = new Date(diaSeleccionado.getSegundosTrabajados() * 1000);
            c.setTime(trabajado);
            d1.setText(String.format("%02d", c.get(Calendar.HOUR_OF_DAY) - 1) + ":" + String.format("%02d", c.get(Calendar.MINUTE)) + ":" + String.format("%02d", c.get(Calendar.SECOND)));
            c.setTime(diaSeleccionado.getHoraSalida());
            d2.setText(String.format("%02d", c.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", c.get(Calendar.MINUTE)) + ":" + String.format("%02d", c.get(Calendar.SECOND)));
            String recalculado = diaSeleccionado.friday(semanas);
            d2.setText(recalculado.equalsIgnoreCase("") ? d2.getText() : recalculado);

            Date descansando = new Date(diaSeleccionado.getDescanso() * 1000);
            c.setTime(descansando);
            d3.setText(String.format("%02d", c.get(Calendar.HOUR_OF_DAY) - 1) + ":" + String.format("%02d", c.get(Calendar.MINUTE)) + ":" + String.format("%02d", c.get(Calendar.SECOND)));
            c.setTime(diaSeleccionado.getFecha());
            ft.setText(diaDeLaSemana() + ", " + String.format("%02d", c.get(Calendar.DAY_OF_MONTH)) + "-" + String.format("%02d", c.get(Calendar.MONTH) + 1) + "-" + String.format("%04d", c.get(Calendar.YEAR)));
            adapter.renewData(diaSeleccionado.getFichajes());
        } else {
            t1.setText(R.string.total_semana);
            t3.setText(R.string.horas_trabajadas);
            t2.setText(R.string.horas_restantes);

            long horas = semanaSeleccionada.getSegundosObjetivo() / 3600;
            long minutos = (semanaSeleccionada.getSegundosObjetivo() - (horas * 3600)) / 60;
            long segundos = (semanaSeleccionada.getSegundosObjetivo() - (horas * 3600) - (minutos * 60));
            d1.setText(String.format("%02d", horas) + ":" + String.format("%02d", minutos) + ":" + String.format("%02d", segundos));

            horas = semanaSeleccionada.getSegundosTrabajados() / 3600;
            minutos = (semanaSeleccionada.getSegundosTrabajados() - (horas * 3600)) / 60;
            segundos = (semanaSeleccionada.getSegundosTrabajados() - (horas * 3600) - (minutos * 60));
            d3.setText(String.format("%02d", horas) + ":" + String.format("%02d", minutos) + ":" + String.format("%02d", segundos));

            long segundosRestantes = semanaSeleccionada.getSegundosObjetivo() - semanaSeleccionada.getSegundosTrabajados();
            if (segundosRestantes < 0)
                segundosRestantes = 0;

            horas = segundosRestantes / 3600;
            minutos = (segundosRestantes - (horas * 3600)) / 60;
            segundos = (segundosRestantes - (horas * 3600) - (minutos * 60));
            d2.setText(String.format("%02d", horas) + ":" + String.format("%02d", minutos) + ":" + String.format("%02d", segundos));

            c.setTime(semanaSeleccionada.getHours().get(0).getMomento());
            c.add(Calendar.DAY_OF_MONTH, -c.get(Calendar.DAY_OF_WEEK) + 2);
            ft.setText(String.format("%02d", c.get(Calendar.DAY_OF_MONTH)) + "/" + String.format("%02d", c.get(Calendar.MONTH) + 1) + "/" + String.format("%04d", c.get(Calendar.YEAR)) + " - ");
            c.add(Calendar.DAY_OF_MONTH, 6);
            ft.setText(ft.getText() + String.format("%02d", c.get(Calendar.DAY_OF_MONTH)) + "/" + String.format("%02d", c.get(Calendar.MONTH) + 1) + "/" + String.format("%04d", c.get(Calendar.YEAR)));

            adapter.renewData(semanaSeleccionada.getHours());
        }
        if (selected == 0 && sameDay(new Date(), diaSeleccionado.getFecha())) {
            chronometer(true);
        } else if (selected == 1 && sameWeek(new Date(), semanaSeleccionada.getDays().get(0).getFecha())) {
            chronometer(true);
        } else {
            chronometer(false);
        }
    }


    private boolean sameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null)
            return false;
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        boolean sameDay = cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
        return sameDay;
    }

    public static boolean sameWeek(Date date1, Date date2) {
        if (date1 == null || date2 == null)
            return false;
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        boolean sameDay = cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
        return sameDay;
    }

    private String diaDeLaSemana() {
        Calendar c = Calendar.getInstance();
        c.setTime(diaSeleccionado.getFecha());

        switch (c.get(Calendar.DAY_OF_WEEK)) {
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

        if (selected == 0) {

            if (dias.indexOf(diaSeleccionado) < dias.size() - 1) {
                diaSeleccionado = dias.get(dias.indexOf(diaSeleccionado) + 1);
                refreshScreenData(true);
            }

            segmentedControl2.clearSelection();

        } else {
            if (semanas.indexOf(semanaSeleccionada) < semanas.size() - 1) {
                semanaSeleccionada = semanas.get(semanas.indexOf(semanaSeleccionada) + 1);
                refreshScreenData(true);
            }

            segmentedControl2.clearSelection();


        }


    }

    public void posterior(View view) {
        int selected = segmentedControl.getLastSelectedAbsolutePosition();
        if (selected == 0) {
            if (dias.indexOf(diaSeleccionado) > 0) {
                diaSeleccionado = dias.get(dias.indexOf(diaSeleccionado) - 1);
                refreshScreenData(true);
            }

            if (sameDay(diaSeleccionado.getFecha(), dias.get(0).getFecha())) {
                seleccionarHoy(true);
            } else {
                seleccionarHoy(false);
            }

        } else {
            if (semanas.indexOf(semanaSeleccionada) > 0) {
                semanaSeleccionada = semanas.get(semanas.indexOf(semanaSeleccionada) - 1);
                refreshScreenData(true);
            }

            if (sameWeek(semanaSeleccionada.getDays().get(0).getFecha(), semanas.get(0).getDays().get(0).getFecha())) {
                seleccionarHoy(true);
            } else {
                seleccionarHoy(false);
            }
        }
    }

    void seleccionarHoy(boolean seleccionar) {
        Boolean seleccionado = false;
        if (segmentedControl2.getLastSelectedAbsolutePosition() == 0)
            seleccionado = true;
        seleccionManual = !seleccionado && seleccionar;
        if (seleccionar) {
            segmentedControl2.setSelectedSegment(0);
        } else {
            segmentedControl2.clearSelection();
        }
    }

    Calendar myCalendar = Calendar.getInstance();

    public void seleccionFecha(View view) {


        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                if (segmentedControl.getLastSelectedAbsolutePosition() == 0) {
                    for (Day d : dias) {
                        if (sameDay(d.getFecha(), myCalendar.getTime())) {
                            diaSeleccionado = d;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    refreshScreenData(true);
                                }
                            });
                            break;
                        }
                    }
                } else if (segmentedControl.getLastSelectedAbsolutePosition() == 1) {
                    for (Week w : semanas) {
                        if (sameWeek(myCalendar.getTime(), w.getDays().get(0).getFecha())) {
                            semanaSeleccionada = w;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    refreshScreenData(true);
                                }
                            });
                            break;
                        }
                    }
                }

            }

        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setFirstDayOfWeek(Calendar.MONDAY);
        datePickerDialog.show();

    }
}

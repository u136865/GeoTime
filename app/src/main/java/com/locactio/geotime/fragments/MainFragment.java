package com.locactio.geotime.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.print.PrinterId;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.common.util.Clock;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.locactio.geotime.PrincipalActivity;
import com.locactio.geotime.R;
import com.locactio.geotime.WS.ClockingResponseHandler;
import com.locactio.geotime.WS.ClockingRest;
import com.locactio.geotime.WS.DataREST;
import com.locactio.geotime.WS.FichajeResponseHandler;
import com.locactio.geotime.WS.LoginREST;
import com.locactio.geotime.WS.LoginResponseHandler;
import com.locactio.geotime.bbdd.facades.ClockingLab;
import com.locactio.geotime.entities.Clocking;
import com.locactio.geotime.entities.Day;
import com.locactio.geotime.entities.Week;
import com.locactio.geotime.utils.Utils;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.locactio.geotime.utils.Utils.sameDay;
import static com.locactio.geotime.utils.Utils.sameWeek;

public class MainFragment extends Fragment{

    KProgressHUD hud;
    ArrayList<Day> dias = new ArrayList<>();
    String token = null;
    PieChart grafico;
    ArrayList<Clocking> horasTotales;
    CoordinatorLayout coordinatorLayout;
    SharedPreferences pref;
    private static final String userTkn = "x2Lhp3Iun5Nc";
    private static final String pinTkn = "9V1%YaPO&dX&";
    Date today, yearAgo;
    private Date startOfSummer, endOfSummer;
    private Day diaSeleccionado;
    ArrayList<PieEntry> entries;
    PieDataSet pieDataSet;
    PieData pieData;
    private TextView clock;
    private Chronometer chronometer;
    private Boolean chronometerStarted = false;
    private ConstraintLayout btn1, btn2;
    private ImageView img1, img2;
    private TextView text1, text2;
    private int estadoAnterior = -1;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        grafico = v.findViewById(R.id.grafico);
        coordinatorLayout = v.findViewById(R.id.coordinator);
        clock = v.findViewById(R.id.clockTime);
        chronometer = v.findViewById(R.id.chronometer);
        btn1 = v.findViewById(R.id.btn1);
        btn2 = v.findViewById(R.id.btn2);
        img1 = v.findViewById(R.id.img1);
        img2 = v.findViewById(R.id.img2);
        text1 = v.findViewById(R.id.text1);
        text2 = v.findViewById(R.id.text2);

        Calendar c = Calendar.getInstance();
        today = c.getTime();
        c.add(Calendar.YEAR, -1);
        yearAgo = c.getTime();

        c = Calendar.getInstance();
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

        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshScreenData(false);
                    }
                });
            }
        });

        btn1.setOnClickListener(clickListener1);
        btn2.setOnClickListener(clickListener2);
        return v;
    }

    View.OnClickListener clickListener1 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            TextView txt1, txt2;
            txt1 = view.findViewById(R.id.text1);

            if (txt1.getText().toString().equalsIgnoreCase(getResources().getString(R.string.comenzar)))
            {
                callClock(ClockingRest.ENTRAR);
            }else if(txt1.getText().toString().equalsIgnoreCase(getResources().getString(R.string.regreso)))
            {
                callClock(ClockingRest.VOLVER);
            }else if(txt1.getText().toString().equalsIgnoreCase(getResources().getString(R.string.descanso)))
            {
                callClock(ClockingRest.PAUSA);
            }
        }
    };

    View.OnClickListener clickListener2 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            callClock(ClockingRest.SALIR);
        }
    };

    private void callClock(String action)
    {
        ClockingRest.execute(token, action, (PrincipalActivity)getActivity(), new FichajeResponseHandler() {
            @Override
            public void onError() {

            }

            @Override
            public void onRequestFailure() {

            }

            @Override
            public void onResponse() {
                final Calendar cA = Calendar.getInstance();
                cA.setTime(diaSeleccionado.getFichajes().get(0).getMomento());
                cA.add(Calendar.SECOND, 1);

                final Calendar cN = Calendar.getInstance();
                cN.add(Calendar.SECOND, 1);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        request_info(cA.getTime(), cN.getTime());
                    }
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        horasTotales = ClockingLab.get(getActivity()).getClockings();
        Calendar c = Calendar.getInstance();
        if (horasTotales.size() > 0)
            c.setTime(horasTotales.get(0).getMomento());
        else
            c.add(Calendar.YEAR, -1);
        c.add(Calendar.SECOND, 1);
        hud = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Espere")
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .setBackgroundColor(getResources().getColor(R.color.naranjaFondo));
        request_info(c.getTime(), new Date());
        estadoAnterior = -1;
        pieDataSet = null;
    }
    private void request_info(Date d1, Date d2) {
        if (token == null)
            token = Utils.getToken(getActivity());
        hud.show();
        DataREST.execute(token, d1, d2, new ClockingResponseHandler() {
            @Override
            public void onError() {
                hud.dismiss();
                Snackbar.make(coordinatorLayout, getResources().getString(R.string.error_ocurred), Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onRequestFailure() {
                pref = getActivity().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
                String user = pref.getString(userTkn, "");
                String pin = pref.getString(pinTkn, "");
                LoginREST.execute(user, pin, new LoginResponseHandler() {
                    @Override
                    public void onError() {
                        hud.dismiss();
                        Snackbar.make(coordinatorLayout, getResources().getString(R.string.error_ocurred), Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onLoginFailure() {
                        hud.dismiss();
                        Snackbar.make(coordinatorLayout, getResources().getString(R.string.error_ocurred), Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String tkn, int hours, String nombre_completo) {
                        token = tkn;
                        if (dias.size() == 0)
                            request_info(yearAgo, today);
                        else
                            request_info(dias.get(0).getFecha(),today);
                    }
                });
            }

            @Override
            public void onResponse(List<Clocking> clockingList) {
                horasTotales.addAll(0, clockingList);
                dias.clear();

                ClockingLab.get(getActivity()).addAllClockings(clockingList);
                Date valorandoW = new Date(0);
                Date valorando = new Date(0);
                Day inProgress = null;
                Week weekInProgress = null;

                for (Clocking c : horasTotales) {

                    if (!sameDay(valorando, c.getMomento())) {
                        valorando = c.getMomento();

                        if (inProgress != null) {
                            inProgress.calculateTimes();
                            dias.add(inProgress);
                        }
                        inProgress = new Day(41.0f, startOfSummer, endOfSummer);
                    }
                    inProgress.addClocking(c);

                }

                if (dias.size() == 0) {
                    inProgress.calculateTimes();
                    dias.add(inProgress);
                }

                diaSeleccionado = dias.get(0);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshScreenData(true);
                        hud.dismiss();
                    }
                });
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        chronometer.stop();
        chronometerStarted = false;
    }

    private void refreshScreenData(Boolean animated)
    {
        Boolean permitStart = diaSeleccionado.calculateTimes();
        entries = new ArrayList<>();
        double acumulados = 0;
        double segundosDiarios = diaSeleccionado.getHorasDiarias() * 3600.0f;
        int cantidadValores = diaSeleccionado.getPeriodos().length + 1;
        int[] colores = new int[cantidadValores];

        if (!sameDay(new Date(), diaSeleccionado.getFecha()) && diaSeleccionado.isTerminado())
        {
            Log.d("DATE", "EL momento es: " + new Date());
            entries.add(new PieEntry(100,0));
            pieDataSet = new PieDataSet(entries,"");
            pieDataSet.setColors(new int[]{getResources().getColor(R.color.naranjaFondo)});
            pieData = new PieData(pieDataSet);
            pieData.setDrawValues(false);
            grafico.setData(pieData);
            grafico.animateY(1);
            grafico.setTransparentCircleRadius(90);
            grafico.setHoleRadius(90);
            grafico.setHoleColor(getActivity().getResources().getColor(R.color.clearColor));
            grafico.getDescription().setEnabled(false);
            grafico.getLegend().setEnabled(false);
            Calendar c = Calendar.getInstance();
            clock.setText(String.format("%02d:%02d:%02d",c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),c.get(Calendar.SECOND)));
            if (!chronometerStarted) {
                chronometerStarted = true;
                chronometer.start();
            }
            if (estadoAnterior != diaSeleccionado.getFichajes().get(0).getTipo())
            {
                btn1.setVisibility(View.VISIBLE);
                btn2.setVisibility(View.INVISIBLE);
                img1.setImageDrawable(getActivity().getDrawable(R.drawable.ic_start));
                text1.setText(getResources().getString(R.string.comenzar));
                estadoAnterior = diaSeleccionado.getFichajes().get(0).getTipo();
            }
            return;
        }

        for (int i = 0; i < diaSeleccionado.getPeriodos().length ; i++) {
            double entryValue = (diaSeleccionado.getPeriodos()[i] / segundosDiarios) * 100.0f;
            Log.d("PERIODO", "El periodo es de: " + (diaSeleccionado.getPeriodos()[i] / segundosDiarios) * 100 + "%");
            PieEntry pE = new PieEntry((float) (entryValue),i);
            entries.add(pE);
            acumulados += entryValue;
            colores[i] = getResources().getColor(R.color.blanco);
        }
        if (acumulados <= 100) {
            entries.add(new PieEntry((float) (100 - acumulados), entries.size() - 1));
            colores[colores.length - 1] = getResources().getColor(R.color.naranjaFondo);
        }else{
            colores = new int[] {getResources().getColor(R.color.rojo), getResources().getColor(R.color.blanco)};
            entries.clear();
            double diferencia = acumulados-100;
            PieEntry pE = new PieEntry((float)(diferencia),0);
            entries.add(pE);
            pE = new PieEntry((float)(100-diferencia),1);
            entries.add(pE);
        }
        if (pieDataSet == null) {
            pieDataSet = new PieDataSet(entries, "");

            pieData = new PieData(pieDataSet);
            pieDataSet.setColors(colores);

            pieData.setDrawValues(false);
            grafico.setData(pieData);
            if (animated)
                grafico.animateY(500);
            grafico.setTransparentCircleRadius(90);
            grafico.setHoleRadius(90);
            grafico.setHoleColor(getActivity().getResources().getColor(R.color.clearColor));
            grafico.getDescription().setEnabled(false);
            grafico.getLegend().setEnabled(false);
        }
        grafico.notifyDataSetChanged();

        int horas = (int) (diaSeleccionado.getSegundosTrabajados() / 3600);
        long restante = diaSeleccionado.getSegundosTrabajados() - horas * 3600;
        int minutos = (int)(restante / 60);
        int segundos = (int)(restante - minutos * 60);

        clock.setText(String.format("%02d:%02d:%02d",horas, minutos,segundos));

        if (diaSeleccionado.isTrabajando())
        {
            if (permitStart && !chronometerStarted) {
                chronometerStarted = true;
                chronometer.start();
            }

            if (estadoAnterior != diaSeleccionado.getFichajes().get(0).getTipo())
            {
                btn1.setVisibility(View.VISIBLE);
                btn2.setVisibility(View.VISIBLE);
                img1.setImageDrawable(getActivity().getDrawable(R.drawable.ic_pause));
                img2.setImageDrawable(getActivity().getDrawable(R.drawable.ic_stop));
                text1.setText(getResources().getString(R.string.descanso));
                text2.setText(getResources().getString(R.string.salir));
            }

        }else if (diaSeleccionado.isEnDescanso())
        {

            if (estadoAnterior != diaSeleccionado.getFichajes().get(0).getTipo())
            {
                btn1.setVisibility(View.VISIBLE);
                btn2.setVisibility(View.INVISIBLE);
                img1.setImageDrawable(getActivity().getDrawable(R.drawable.ic_return));
                text1.setText(getResources().getString(R.string.regreso));

            }
        }else if (diaSeleccionado.isTerminado())
        {

            if (estadoAnterior != diaSeleccionado.getFichajes().get(0).getTipo())
            {
                btn1.setVisibility(View.VISIBLE);
                btn2.setVisibility(View.INVISIBLE);
                img1.setImageDrawable(getActivity().getDrawable(R.drawable.ic_start));
                text1.setText(getResources().getString(R.string.comenzar));
            }
        }
        estadoAnterior = diaSeleccionado.getFichajes().get(0).getTipo();
    }


}

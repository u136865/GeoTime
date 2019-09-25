package com.locactio.geotime.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.locactio.geotime.R;
import com.locactio.geotime.bbdd.facades.ClockingLab;
import com.locactio.geotime.entities.Clocking;
import com.locactio.geotime.entities.Day;
import com.locactio.geotime.entities.Week;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.locactio.geotime.utils.Utils.sameDay;
import static com.locactio.geotime.utils.Utils.sameWeek;

public class RangeFragment extends Fragment{

    EditText desde, hasta;
    ArrayList<Day> dias = new ArrayList<>();
    ArrayList<Clocking> clockings = new ArrayList<>();
    Date startOfSummer, endOfSummer;
    Date startRange, endRange;
    TextView txt_dias, txt_objetivo, txt_trabajadas, txt_diferencia;
    TextView txt_rango;

    public static RangeFragment newInstance() {
        return new RangeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_range, container, false);
        desde = v.findViewById(R.id.desde_label);
        hasta = v.findViewById(R.id.hasta_label);
        txt_dias = v.findViewById(R.id.dias_trabajados_value);
        txt_objetivo = v.findViewById(R.id.horas_objetivo_text);
        txt_trabajadas = v.findViewById(R.id.horas_trabajadas_text);
        txt_diferencia = v.findViewById(R.id.diferencia_horas_text);
        txt_rango = v.findViewById(R.id.rango);
        desde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                desde_click(view);
            }
        });
        hasta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hasta_click(view);
            }
        });
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
        initValues();

        return v;
    }

    public void desde_click(View view) {
        final Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                desde.setText(String.format("%02d/%02d/%04d", myCalendar.get(Calendar.DAY_OF_MONTH),myCalendar.get(Calendar.MONTH) + 1, myCalendar.get(Calendar.YEAR)));
                Calendar c = Calendar.getInstance();
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                c.set(Calendar.MONTH, monthOfYear);
                c.set(Calendar.YEAR, year);
                c.set(Calendar.HOUR_OF_DAY, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                startRange = c.getTime();
                refreshScreen();
            }

        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.TimePickerTheme, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setFirstDayOfWeek(Calendar.MONDAY);

        datePickerDialog.show();
    }

    public void hasta_click(View view){
        final Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                hasta.setText(String.format("%02d/%02d/%04d", myCalendar.get(Calendar.DAY_OF_MONTH),myCalendar.get(Calendar.MONTH) + 1,myCalendar.get(Calendar.YEAR)));

                Calendar c = Calendar.getInstance();
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                c.set(Calendar.MONTH, monthOfYear);
                c.set(Calendar.YEAR, year);
                c.set(Calendar.HOUR_OF_DAY, 23);
                c.set(Calendar.MINUTE, 59);
                c.set(Calendar.SECOND, 59);
                endRange = c.getTime();
                refreshScreen();
            }

        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.TimePickerTheme, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setFirstDayOfWeek(Calendar.MONDAY);

        datePickerDialog.show();
    }

    private void initValues(){
        clockings = ClockingLab.get(getActivity()).getClockings();
        dias = new ArrayList<>();
        Date valorando = new Date(0);
        Day inProgress = null;
        for (Clocking c : clockings) {
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

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        startRange = c.getTime();
        c.set(Calendar.HOUR_OF_DAY,23);
        c.set(Calendar.MINUTE,59);
        c.set(Calendar.SECOND,59);
        endRange = c.getTime();
        refreshScreen();
        desde.setText(String.format("%02d/%02d/%04d", c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH) + 1,c.get(Calendar.YEAR)));
        hasta.setText(String.format("%02d/%02d/%04d", c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH) + 1,c.get(Calendar.YEAR)));
    }


    private void refreshScreen()
    {
        if (startRange == null || endRange == null)
            return;
        if (startRange.after(endRange))
            return;




        final ArrayList<Day> rango = new ArrayList<>();
        double segundosTeoricos = 0;
        double segundosTrabajados = 0;
        for (Day d : dias)
        {
            if(d.getFecha().after(startRange) && d.getFecha().before(endRange)) {
                rango.add(0, d);
                segundosTeoricos += d.getHorasDiarias() * 3600;
                segundosTrabajados += d.getSegundosTrabajados();
            }
        }

        final double finalSegundosTeoricos = segundosTeoricos;
        final double finalSegundosTrabajados = segundosTrabajados;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Calendar cInit = Calendar.getInstance();
                cInit.setTime(startRange);
                Calendar cEnd = Calendar.getInstance();
                cEnd.setTime(endRange);
                txt_rango.setText(String.format("%02d/%02d/%04d - %02d/%02d/%04d",
                        cInit.get(Calendar.DAY_OF_MONTH),cInit.get(Calendar.MONTH) + 1,cInit.get(Calendar.YEAR),
                        cEnd.get(Calendar.DAY_OF_MONTH),cEnd.get(Calendar.MONTH) + 1,cEnd.get(Calendar.YEAR)));

                Log.d("DIAS TRABAJADOS", String.valueOf(rango.size()));
                txt_dias.setText(String.valueOf(rango.size()));

                Log.d("HORAS TRABAJO", String.format("%g", finalSegundosTeoricos));
                txt_objetivo.setText(calculateHours(finalSegundosTeoricos));

                Log.d("HORAS TRABAJADO", String.format("%g", finalSegundosTrabajados));
                txt_trabajadas.setText(calculateHours(finalSegundosTrabajados));

                txt_diferencia.setText(calculateHours(finalSegundosTrabajados-finalSegundosTeoricos));
            }
        });
    }

    private String calculateHours(double segundos)
    {
        String inicio = "";
        if (segundos < 0) {
            inicio = "-";
            segundos *= -1.0f;
        }
        String horas;
        long hours = (long)(segundos / 3600);
        if (hours < 90)
            horas = String.format("%02d",hours);
        else
            horas = String.format("%d",hours);
        double resto = segundos - hours*3600;
        long minutes = (long)(resto / 60);

        return String.format("%s%s : %02d",inicio,horas, minutes);
    }
}

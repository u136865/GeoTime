package com.locactio.geotime.entities;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

public class Week {

    ArrayList<Day> days = new ArrayList<>();

    public ArrayList<Clocking> getHours() {
        return hours;
    }

    public void setHours(ArrayList<Clocking> hours) {
        this.hours = hours;
    }

    ArrayList<Clocking> hours = new ArrayList<>();
    long segundosTrabajados = 0;
    long segundosObjetivo;

    boolean trabajando = false;

    Date initOfSummer, endOfSummer;

    public Week( Date initOfSummer, Date endOfSummer) {
        this.initOfSummer = initOfSummer;
        this.endOfSummer = endOfSummer;

    }

    public void addDay(Day day)
    {
        days.add(day);
    }

    public void calculateTimes(){
        //Log.d("Calculate Times", "Calc");
        segundosTrabajados = 0;
        if (days.get(0).getFichajes().get(0).getTipo() == 0 || days.get(0).getFichajes().get(0).getTipo() == 2)
            trabajando = true;
        hours.clear();
        for (Day d : days)
        {
            segundosTrabajados += d.segundosTrabajados;
            hours.addAll(d.fichajes);
        }

        Date hora = hours.get(0).getMomento();
        if (hora.after(initOfSummer) && hora.before(endOfSummer))
        {
            segundosObjetivo = 35 * 3600;
        }else{
            float objetivoHoras = 0;
            for (Day d : days)
            {
                objetivoHoras += d.horasDiarias;
            }

            segundosObjetivo = Math.round(objetivoHoras * 3600);
        }

    }

    public ArrayList<Day> getDays() {
        return days;
    }

    public void setDays(ArrayList<Day> days) {
        this.days = days;
    }

    public long getSegundosTrabajados() {
        return segundosTrabajados;
    }

    public void setSegundosTrabajados(long segundosTrabajados) {
        this.segundosTrabajados = segundosTrabajados;
    }

    public long getSegundosObjetivo() {
        return segundosObjetivo;
    }

    public void setSegundosObjetivo(long segundosObjetivo) {
        this.segundosObjetivo = segundosObjetivo;
    }

    public boolean isTrabajando() {
        return trabajando;
    }

    public void setTrabajando(boolean trabajando) {
        this.trabajando = trabajando;
    }
}

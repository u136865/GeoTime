package com.locactio.geotime.entities;

import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Clocking implements Comparable {

    private int tipo;
    private Date momento;

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public Date getMomento() {
        return momento;
    }

    public void setMomento(Date momento) {
        this.momento = momento;
    }

    public Clocking(String fecha, int tipo)
    {
        this.tipo = tipo;

        String[] split0 = fecha.split(" ");
        String[] fechaSplit = split0[0].split("-");
        String[] horaSplit = split0[1].split(":");

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, Integer.valueOf(fechaSplit[0]));
        c.set(Calendar.MONTH, Integer.valueOf(fechaSplit[1])-1);
        c.set(Calendar.DAY_OF_MONTH, Integer.valueOf(fechaSplit[2]));
        c.set(Calendar.HOUR_OF_DAY, Integer.valueOf(horaSplit[0]));
        c.set(Calendar.MINUTE, Integer.valueOf(horaSplit[1]));
        c.set(Calendar.SECOND, Integer.valueOf(horaSplit[2]));
        momento = c.getTime();

    }

    @Override
    public int compareTo(@NonNull Object o) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(getMomento());
        Calendar c2 = Calendar.getInstance();
        c2.setTime(((Clocking)o).getMomento());

        return c1.before(c2) ? 0 : 1;
    }
}

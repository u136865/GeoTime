package com.locactio.geotime.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Entity (tableName = "clocking")
public class Clocking{

    public Clocking()
    {

    }

    @ColumnInfo(name = "tipo")
    private int tipo;

    @PrimaryKey
    @NonNull
    private String strMomento;

    @Ignore
    private Date momento;

    public Date getMomento() {
        if (momento == null)
        {
            String[] split0 = strMomento.split(" ");
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

        return momento;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public void setMomento(Date momento) {
        this.momento = momento;
    }

    public String getStrMomento() {
        return strMomento;
    }

    public void setStrMomento(String sMomento) {
        this.strMomento = sMomento;
    }

    public Clocking(String fecha, int tipo)
    {
        this.tipo = tipo;
        this.strMomento = fecha;
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

}

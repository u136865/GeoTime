package com.locactio.geotime.utils;

import android.content.Context;

import com.locactio.geotime.R;
import com.locactio.geotime.entities.Day;

import java.util.Calendar;
import java.util.Date;

public class Utils {

    public static boolean sameDay(Date date1, Date date2) {
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

    public static String diaDeLaSemana(Context context, Day diaSeleccionado)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(diaSeleccionado.getFecha());

        switch (c.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                return context.getString(R.string.lunes);
            case Calendar.TUESDAY:
                return context.getString(R.string.martes);
            case Calendar.WEDNESDAY:
                return context.getString(R.string.miercoles);
            case Calendar.THURSDAY:
                return context.getString(R.string.jueves);
            case Calendar.FRIDAY:
                return context.getString(R.string.viernes);
            case Calendar.SATURDAY:
                return context.getString(R.string.sabado);
            case Calendar.SUNDAY:
                return context.getString(R.string.domingo);
        }
        return "";
    }
}

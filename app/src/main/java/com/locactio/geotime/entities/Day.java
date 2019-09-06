package com.locactio.geotime.entities;

import android.util.Log;
import android.widget.TextView;

import com.locactio.geotime.PrincipalActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import static java.util.Calendar.FRIDAY;
import static java.util.Calendar.SECOND;

public class Day {
    Date fecha;
    ArrayList<Clocking> fichajes = new ArrayList<>();
    long segundosTrabajados = 0;
    Date horaSalida = new Date(0);
    long descanso = 0;
    boolean terminado = false;
    boolean trabajando = false;
    boolean enDescanso = false;
    long segundosRestantes = 0;
    float horasSemanales = 0;
    float horasDiarias = 0;

    Date initOfSummer, endOfSummer;

    boolean isSummer = false;

    Date horaSalidaReducido=null;

    public Day(float horasSemanales, Date initOfSummer, Date endOfSummer)
    {

        this.initOfSummer = initOfSummer;
        this.endOfSummer = endOfSummer;

        this.horasSemanales = horasSemanales;
    }

    public void addClocking(Clocking clocking)
    {
        fichajes.add(clocking);
    }

    public void calculateTimes() {


        fecha = fichajes.get(0).getMomento();

        if (fichajes.get(0).getTipo() == 6)
        {
            terminado = true;
            return;
        }

        if (fecha.before(initOfSummer) || fecha.after(endOfSummer)) {
            if (horasSemanales == 41) {
                Calendar c1 = Calendar.getInstance();
                c1.setTime(fichajes.get(0).getMomento());
                horasDiarias = c1.get(Calendar.DAY_OF_WEEK) == FRIDAY ? 7 : 8.5f;
            } else {
                horasDiarias = horasSemanales / 5;
            }
        }else{
            isSummer = true;
            horasSemanales = 35;
            horasDiarias = 7;
        }


        Date termina = null, comienza = null;
        switch (fichajes.get(0).getTipo())
        {
            case 0:
            case 3:
                trabajando = true;
                break;
            case 1:
                terminado = true;
                break;
            case 2:
                enDescanso = true;
                break;
        }

        Boolean activo = false;

        for (int i = 0; i < fichajes.size(); i++)
        {
            if (fichajes.get(i).getTipo() == 1 || fichajes.get(i).getTipo() == 2)
            {
                activo = true;
                termina = fichajes.get(i).getMomento();
            }else if (fichajes.get(i).getTipo() == 0 || fichajes.get(i).getTipo() == 3)
            {
                activo = false;
                comienza = fichajes.get(i).getMomento();
            }

            if (termina != null && comienza != null) {
                if (!activo) {
                    segundosTrabajados += (termina.getTime() - comienza.getTime()) / 1000;
                } else {
                    descanso += (comienza.getTime() - termina.getTime()) / 1000;
                }
            }
        }

        if (enDescanso)
        {
            int sR = Math.round(horasDiarias * 3600);
            segundosRestantes = sR - segundosTrabajados;
            descanso += (new Date().getTime() * 1000) - (fichajes.get(0).getMomento().getTime() * 1000);
            horaSalida = new Date((segundosRestantes * 1000) - 3600000);


        }

        if (trabajando)
        {
            Date ahora = new Date();
            Date hF = fichajes.get(0).getMomento();
            long difDate = (ahora.getTime() - hF.getTime())/1000;
            segundosTrabajados += difDate;
            segundosRestantes = Math.round(horasDiarias * 3600) - segundosTrabajados;
            Calendar chs = Calendar.getInstance();
            chs.add(SECOND, (int) segundosRestantes);
            horaSalida = chs.getTime();
        }

        if (terminado)
        {
            long segundosDif = (long)(horasDiarias * 3600) - segundosTrabajados;
            horaSalida = new Date(fichajes.get(0).getMomento().getTime() + (segundosDif * 1000));
        }

    }

    public ArrayList<Clocking> getFichajes() {
        return fichajes;
    }

    public Date getFecha() {
        return fecha;
    }

    public long getSegundosTrabajados() {
        return segundosTrabajados;
    }

    public Date getHoraSalida() {
        return horaSalida;
    }

    public long getDescanso() {
        return descanso;
    }

    public boolean isTerminado() {
        return terminado;
    }

    public boolean isTrabajando() {
        return trabajando;
    }

    public boolean isEnDescanso() {
        return enDescanso;
    }

    public long getSegundosRestantes() {
        return segundosRestantes;
    }

    public float getHorasSemanales() {
        return horasSemanales;
    }

    public float getHorasDiarias() {
        return horasDiarias;
    }

    public String friday(ArrayList<Week> weeks) {
        Calendar c = Calendar.getInstance();
        c.setTime(fecha);
        Week semana = null;

        if (c.get(Calendar.DAY_OF_WEEK) != FRIDAY)
        {
            return "";
        }

         for (Week w : weeks)
         {
             if (PrincipalActivity.sameWeek(w.days.get(0).getFecha(), fecha))
             {
                 semana = w;
                 break;
             }
         }

         if (semana == null)
             return "";

        if (trabajando)
        {
            long tiempoRestante = semana.getSegundosObjetivo() - semana.getSegundosTrabajados();
            long salidaTeorica = horaSalida.getTime() - tiempoRestante;
            Date sT = new Date(salidaTeorica);
            c = Calendar.getInstance();
            c.add(SECOND, (int) tiempoRestante);
            return String.format("%02d", c.get(Calendar.HOUR_OF_DAY)) + ":"+String.format("%02d", c.get(Calendar.MINUTE)) + ":"+String.format("%02d", c.get(SECOND)) + "*";
        }
        return "";
    }
}

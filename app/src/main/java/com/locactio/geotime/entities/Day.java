package com.locactio.geotime.entities;

import com.locactio.geotime.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import static java.util.Calendar.FRIDAY;
import static java.util.Calendar.SECOND;

public class Day {
    private final int INICIO = 0;
    private final int SALIR = 1;
    private final int PAUSA = 2;
    private final int REANUDA = 3;
    Date fecha;
    ArrayList<Clocking> fichajes = new ArrayList<>();

    double[] periodos;
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

    Date horaSalidaReducido = null;

    ArrayList<Period> periodosArray = new ArrayList<>();

    public Day(float horasSemanales, Date initOfSummer, Date endOfSummer) {

        this.initOfSummer = initOfSummer;
        this.endOfSummer = endOfSummer;

        this.horasSemanales = horasSemanales;
    }

    public void addClocking(Clocking clocking) {
        fichajes.add(clocking);
    }

    public Boolean calculateTimes() {
        horaSalida = new Date(0);
        segundosTrabajados = 0;
        segundosRestantes = 0;
        descanso = 0;
        periodosArray.clear();
        fecha = fichajes.get(0).getMomento();

        if (fichajes.get(0).getTipo() == 6) {
            terminado = true;
            return true;
        }

        if (fecha.before(initOfSummer) || fecha.after(endOfSummer)) {
            if (horasSemanales == 41) {
                Calendar c1 = Calendar.getInstance();
                c1.setTime(fichajes.get(0).getMomento());
                horasDiarias = c1.get(Calendar.DAY_OF_WEEK) == FRIDAY ? 7 : 8.5f;
            } else {
                horasDiarias = horasSemanales / 5;
            }
        } else {
            isSummer = true;
            horasSemanales = 35;
            horasDiarias = 7;
        }

        Date termina = null, comienza = null;
        switch (fichajes.get(0).getTipo()) {
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

        for (int i = 0; i < fichajes.size(); i++) {
            if (fichajes.get(i).getTipo() == 1 || fichajes.get(i).getTipo() == 2) {
                activo = true;
                termina = fichajes.get(i).getMomento();
            } else if (fichajes.get(i).getTipo() == 0 || fichajes.get(i).getTipo() == 3) {
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

            try {
                if (i != 0) {
                    periodosArray.add(new Period((fichajes.get(i-1).getMomento().getTime() - fichajes.get(i).getMomento().getTime()) / 1000, activo ? Period.DESCANSO : Period.TRABAJANDO));
                } else {
                    periodosArray.add(new Period((new Date().getTime() - fichajes.get(i).getMomento().getTime()) / 1000, activo ? Period.DESCANSO : Period.TRABAJANDO));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        Collections.reverse(periodosArray);
        if (enDescanso) {
            int sR = Math.round(horasDiarias * 3600);
            segundosRestantes = sR - segundosTrabajados;
            //Calendar cD = Calendar.getInstance();
            Date ahora = new Date();
            Date hF = fichajes.get(0).getMomento();
            long difDate = (ahora.getTime() - hF.getTime()) / 1000;
            //cD.add(SECOND,1);
            descanso += difDate;
            horaSalida = new Date((segundosRestantes * 1000) - 3600000);
        }

        if (trabajando) {
            Calendar c = Calendar.getInstance();
            //Log.d("Calendar antes: ", c.getTime().toString());
            //Log.d("Resto: ", fichajes.get(0).getMomento().toString());
            c.add(SECOND, -((int) (fichajes.get(0).getMomento().getTime() / 1000)));

            //Log.d("Calendar despues:", c.getTime().toString());
            Date ahora = c.getTime();
            Date hF = fichajes.get(0).getMomento();
            Date nuevo = new Date(ahora.getTime() - hF.getTime());
            long difDate = c.getTime().getTime() / 1000;
//            long difDate = (ahora.getTime() - hF.getTime())/1000;
            segundosTrabajados += difDate;
            segundosRestantes = Math.round(horasDiarias * 3600) - segundosTrabajados;


            Calendar chs = Calendar.getInstance();
            chs.add(SECOND, (int) segundosRestantes);
            horaSalida = chs.getTime();
        }

        if (terminado) {
            long segundosDif = (long) (horasDiarias * 3600) - segundosTrabajados;
            horaSalida = new Date(fichajes.get(0).getMomento().getTime() + (segundosDif * 1000));
        }
        if (fichajes.size() == 1)
            return true;

        try {
            int cuantos = 0;
            for (int i = 0; i < fichajes.size(); i++)
                if (fichajes.get(i).getTipo() == SALIR || fichajes.get(i).getTipo() == PAUSA)
                    cuantos++;

            periodos = new double[cuantos];
            cuantos = 0;
            for (int i = 1; i < fichajes.size(); i++) {
                if (fichajes.get(i).getTipo() == REANUDA || fichajes.get(i).getTipo() == INICIO) {
                    Date inicioPeriodo = fichajes.get(i).getMomento();
                    Date finPeriodo = fichajes.get(i - 1).getMomento();
                    if (cuantos < periodos.length) {
                        periodos[cuantos++] = (finPeriodo.getTime() / 1000) - (inicioPeriodo.getTime() / 1000);
                    } else {
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
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

    public double[] getPeriodos() {
        if (periodos == null)
            periodos = new double[0];
        return periodos;
    }

    public void setPeriodos(double[] periodos) {
        this.periodos = periodos;
    }

    public ArrayList<Period> getPeriodosArray()
    {
        return periodosArray;
    }

    public String friday(ArrayList<Week> weeks) {
        Calendar c = Calendar.getInstance();
        c.setTime(fecha);
        Week semana = null;

        if (c.get(Calendar.DAY_OF_WEEK) != FRIDAY) {
            return "";
        }

        for (Week w : weeks) {
            if (Utils.sameWeek(w.days.get(0).getFecha(), fecha)) {
                semana = w;
                break;
            }
        }

        if (semana == null)
            return "";

        if (trabajando) {
            calculateTimes();
            semana.calculateTimes();
            long tiempoRestante = semana.getSegundosObjetivo() - semana.getSegundosTrabajados();
            c = Calendar.getInstance();
            c.add(SECOND, (int) tiempoRestante);

            return String.format("%02d", c.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", c.get(Calendar.MINUTE)) + ":" + String.format("%02d", c.get(SECOND)) + "*";
        }
        return "";
    }
}

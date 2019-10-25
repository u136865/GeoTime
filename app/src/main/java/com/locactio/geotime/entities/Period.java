package com.locactio.geotime.entities;

public class Period {
    public static final int TRABAJANDO = 0;
    public static final int DESCANSO = 1;
    private long segundos;
    private int tipo;

    public Period(long segundos, int tipo)
    {
        this.segundos = segundos;
        this.tipo = tipo;
    }

    public long getSegundos() {
        return segundos;
    }

    public int getTipo() {
        return tipo;
    }

    public void setSegundos(long segundos) {
        this.segundos = segundos;
    }
}

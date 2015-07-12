package com.ex.saulantonio.enruta3;

/**
 * Created by SaulAntonio on 5/1/2015.
 */
public class Lugares {
    String nombre, placeid;

    public Lugares(String nombre, String placeid) {
        this.nombre = nombre;
        this.placeid = placeid;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPlaceid() {
        return placeid;
    }

    public void setPlaceid(String placeid) {
        this.placeid = placeid;
    }
}
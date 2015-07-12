package com.ex.saulantonio.enruta3;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Created by User on 27/04/2015.
 */
public class Ruta {

    private PolylineOptions puntosRuta = new PolylineOptions();
    private GoogleMap mapa;
    private String nombre;
    private int color;

    public Ruta(GoogleMap mapa,String nombre,int color){
        this.mapa=mapa;
        this.nombre=nombre;
        this.color=color;
    }

    public void dibujarRuta(){
        //mapa.clear();
        Polyline poligono = mapa.addPolyline(puntosRuta);
        poligono.setColor(color);
    }

    public PolylineOptions getPuntosRuta() {
        return puntosRuta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPuntosRuta(PolylineOptions puntosRuta) {
        this.puntosRuta = puntosRuta;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}

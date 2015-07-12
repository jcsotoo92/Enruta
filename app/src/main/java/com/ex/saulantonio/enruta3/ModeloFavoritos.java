package com.ex.saulantonio.enruta3;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by SaulAntonio on 5/3/2015.
 */
public class ModeloFavoritos {

    private String nombre;
    private int foto,id;
    private  String Descripcion;
    private LatLng latLng;


    public ModeloFavoritos(int id,String nombre, int foto, String descripcion,LatLng latLng) {
        this.latLng = latLng;
        this.nombre = nombre;
        this.foto = foto;
        this.Descripcion = descripcion;
        this.id = id;
    }

    public LatLng getLatLng() { return latLng;}

    public void setLatLng(LatLng latLng) {this.latLng = latLng;}

    public int getId() { return id;}

    public void setId(int id) {this.id = id;}

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getFoto() {
        return foto;
    }

    public void setFoto(int foto) {
        this.foto = foto;
    }
}

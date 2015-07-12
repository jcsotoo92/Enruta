package com.ex.saulantonio.enruta3;

/**
 * Created by SaulAntonio on 4/26/2015.
 */
public class ModeloRutas {

    private String nombre;
    private int foto;
    private  String Descripcion;

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

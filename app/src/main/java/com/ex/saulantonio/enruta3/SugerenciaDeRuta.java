package com.ex.saulantonio.enruta3;

/**
 * Created by User on 30/04/2015.
 */
public class SugerenciaDeRuta {
    private Ruta ruta;
    private double distancia;
    private SugerenciaDeRuta transbordo;
    private boolean tieneTransbordo=false;

    public SugerenciaDeRuta(Ruta ruta,double distancia) {
        this.ruta = ruta;
        this.distancia = distancia;
    }
    public SugerenciaDeRuta(Ruta ruta,double distancia,SugerenciaDeRuta transbordo){
        this.ruta=ruta;
        this.distancia=distancia;
        this.transbordo=transbordo;
    }

    public String toString(){
        if(tieneTransbordo) return ruta.getNombre()+" Distancia: "+(int)distancia+"m Transbordo: "+transbordo.getRuta().getNombre()+" "+(int)transbordo.getDistancia()+"m";
        else return ruta.getNombre()+" Distancia: "+(int)distancia+"m";
    }

    public double getDistancia() {
        return distancia;
    }

    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }

    public Ruta getRuta() {
        return ruta;
    }

    public void setRuta(Ruta ruta) {
        this.ruta = ruta;
    }
    public SugerenciaDeRuta getTransbordo() {
        return transbordo;
    }

    public void setTransbordo(SugerenciaDeRuta transbordo) {
        this.transbordo = transbordo;
    }

    public boolean isTieneTransbordo() {
        return tieneTransbordo;
    }

    public void setTieneTransbordo(boolean tieneTransbordo) {
        this.tieneTransbordo = tieneTransbordo;
    }
}

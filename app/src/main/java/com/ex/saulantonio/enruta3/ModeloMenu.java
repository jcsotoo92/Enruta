package com.ex.saulantonio.enruta3;

/**
 * Created by SaulAntonio on 4/20/2015.
 */
public class ModeloMenu {

    int iconId;
    String tittle;

    public ModeloMenu(String tittle, int iconId){
        this.iconId = iconId;
        this.tittle =tittle;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }
}
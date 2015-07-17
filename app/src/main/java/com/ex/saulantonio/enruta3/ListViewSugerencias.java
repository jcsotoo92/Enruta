package com.ex.saulantonio.enruta3;

/**
 * Created by User on 07/05/2015.
 */
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;

public class ListViewSugerencias extends BaseAdapter {
    // Declare Variables
    Context context;
    LinkedList<SugerenciaDeRuta>  titulos;
    int[] imagenes;
    LayoutInflater inflater;

    public ListViewSugerencias(Context context,LinkedList<SugerenciaDeRuta> titulos, int[] imagenes) {
        this.context = context;
        this.titulos = titulos;
        this.imagenes = imagenes;
    }

    @Override
    public int getCount() {
        return titulos.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        // Declare Variables
        TextView ruta,distanciaRuta,transbordo,distanciaTransbordo;
        ImageView imgImg;
        //http://developer.android.com/intl/es/reference/android/view/LayoutInflater.html
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView;
        if(titulos.get(position).isTieneTransbordo()){
            itemView = inflater.inflate(R.layout.list_row_sugerencia_transbordo, parent, false);
            // Locate the TextViews in listview_item.xml
            imgImg = (ImageView) itemView.findViewById(R.id.list_row_image);
            ruta = (TextView) itemView.findViewById(R.id.tituloRutaT);
            distanciaRuta= (TextView) itemView.findViewById(R.id.distanciaRutaT);
            transbordo = (TextView) itemView.findViewById(R.id.tituloTransbordo);
            distanciaTransbordo = (TextView) itemView.findViewById(R.id.distanciaTranbsbordo);

            // Capture position and set to the TextViews
            imgImg.setImageResource(imagenes[0]);
            ruta.setText(titulos.get(position).getRuta().getNombre());
            distanciaRuta.setText((int)titulos.get(position).getDistancia()+"m");
            transbordo.setText(titulos.get(position).getTransbordo().getRuta().getNombre());
            distanciaTransbordo.setText((int)titulos.get(position).getTransbordo().getDistancia()+"m");
        }else{
            itemView = inflater.inflate(R.layout.list_row_sugerencia, parent, false);

            // Locate the TextViews in listview_item.xml
            imgImg = (ImageView) itemView.findViewById(R.id.list_row_image);
            ruta= (TextView) itemView.findViewById(R.id.tituloRuta);
            distanciaRuta=(TextView) itemView.findViewById(R.id.distanciaRuta);

            imgImg.setImageResource(imagenes[0]);
            ruta.setText(titulos.get(position).getRuta().getNombre());
            distanciaRuta.setText((int)titulos.get(position).getDistancia()+"m");
        }


        return itemView;
    }
}
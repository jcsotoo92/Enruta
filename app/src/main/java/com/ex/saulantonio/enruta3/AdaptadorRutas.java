package com.ex.saulantonio.enruta3;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SaulAntonio on 4/26/2015.
 */
public class AdaptadorRutas extends RecyclerView.Adapter <AdaptadorRutas.ViewHolder> implements View.OnClickListener{
    List<ModeloRutas> Items;
    private View.OnClickListener clickListener;



    public AdaptadorRutas(){
        super();
        Items = new ArrayList<ModeloRutas>();
        for (int i=1; i<=13;i++){
            ModeloRutas camion = new ModeloRutas();
            camion.setNombre("Ruta "+ i);
            camion.setFoto(R.drawable.ic_rutas);
            camion.setDescripcion("Descripcion de la ruta " + i);
            Items.add(camion);
        }

    }






    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_card, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        v.setOnClickListener(clickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        ModeloRutas camion = Items.get(i);
        viewHolder.txtTitulo.setText(camion.getNombre());
        viewHolder.imgThumbnail.setImageResource(camion.getFoto());
    }

    @Override
    public int getItemCount() {
        return Items.size();
    }

    public void setOnClickListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void onClick(View v) {
        if(clickListener != null)
            clickListener.onClick(v);
    }



    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgThumbnail;
        public TextView txtTitulo;
        public TextView txtDesc;

        public ViewHolder(View itemView) {
            super(itemView);
            imgThumbnail = (ImageView) itemView.findViewById(R.id.image);
            txtTitulo = (TextView) itemView.findViewById(R.id.name);
        }
    }


}

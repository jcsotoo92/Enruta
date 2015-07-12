package com.ex.saulantonio.enruta3;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SaulAntonio on 5/3/2015.
 */

public class AdaptadorFavoritos extends RecyclerView.Adapter <AdaptadorFavoritos.ViewHolder> implements View.OnClickListener,View.OnCreateContextMenuListener{
    ArrayList<ModeloFavoritos> Items;
    private View.OnClickListener clickListener;
    private int position;


    public AdaptadorFavoritos(ArrayList<ModeloFavoritos> Items){
        super();
       this.Items = Items;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_row_favoritos, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        v.setOnClickListener(clickListener);
        v.setOnCreateContextMenuListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder,final int i) {
        ModeloFavoritos camion = Items.get(i);
        viewHolder.txtTitulo.setText(camion.getNombre());
        viewHolder.imgThumbnail.setImageResource(camion.getFoto());
        viewHolder.txtDesc.setText(camion.getDescripcion());
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v){
                setPosition(i);
                return false;
            }
        });
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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }



    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, v.getId(), 0, "Eliminar");
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgThumbnail;
        public TextView txtTitulo;
        public TextView txtDesc;

        public ViewHolder(View itemView) {
            super(itemView);
            imgThumbnail = (ImageView) itemView.findViewById(R.id.image);
            txtTitulo = (TextView) itemView.findViewById(R.id.name);
            txtDesc = (TextView) itemView.findViewById(R.id.description);
        }
    }


}
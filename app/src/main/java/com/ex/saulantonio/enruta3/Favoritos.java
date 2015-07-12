package com.ex.saulantonio.enruta3;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


public class Favoritos extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public ArrayList<ModeloFavoritos> datos;
    private OnFragmentFavInteractionListener mListener;
    AddFloatingActionButton btnAgregar;
    private  AdaptadorFavoritos card =null;
    private ControladorSQL sqlControl;
    RecyclerView recycler;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Favoritos.
     */
    // TODO: Rename and change types and number of parameters
    public static Favoritos newInstance(String param1, String param2) {
        Favoritos fragment = new Favoritos();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public Favoritos() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
            Log.d("PERRA", card.getPosition() + "");
            ControladorSQL controladorSQL = new ControladorSQL(getActivity());
            controladorSQL.eliminarValor(controladorSQL.obtenerDatos().get(card.getPosition()).getId());
            datos= controladorSQL.obtenerDatos();
            card = new AdaptadorFavoritos(datos);
            recycler.setAdapter(card);

        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_favoritos, container, false);
        recycler = (RecyclerView) v.findViewById(R.id.RecViewFavoritos);
        datos = new ArrayList<>();
        sqlControl = new ControladorSQL(getActivity());
        datos= sqlControl.obtenerDatos();
        card = new AdaptadorFavoritos(datos);
        recycler.setAdapter(card);
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed(false, datos.get(recycler.getChildAdapterPosition(v)).getLatLng());
            }
        });
        registerForContextMenu(recycler);
        btnAgregar =(AddFloatingActionButton)  v.findViewById(R.id.agregarLugarbtn);
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed(true,null);
            }
        });
        recycler.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(boolean flag,LatLng latLng) {
        if (mListener != null) {
            mListener.onFragmentFavInteraction(flag,latLng);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentFavInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**{}
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentFavInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentFavInteraction(boolean flag,LatLng latLng);
    }


}

package com.ex.saulantonio.enruta3;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.ui.IconGenerator;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,Rutas.OnFragmentInteractionListener, Favoritos.OnFragmentFavInteractionListener,SlidingListFragment.OnFragmentInteractionListener {
    LinkedList<SugerenciaDeRuta> sugerenciasRutas;
    Marker origen, destino;
    GoogleMap mMap;
    FloatingActionButton imageButton, runButton;
    ImageButton refresh;
    boolean favoritos = false;
    boolean sugerencia = false;
    AutoCompleteTextView autoCompleteTextView;
    Toolbar toolbar;
    int[] imagenes = {
            R.drawable.ic_drawer,
            R.drawable.ic_drawer,
            R.drawable.ic_drawer,
            R.drawable.ic_drawer
    };


    int idRutas,idMapa;//EMPIEZA SOTO
    LinkedList<Ruta> listaRutas=new LinkedList<Ruta>();
    private final int MAX_INTERPOLACIONES=2;
    private final double REFERENCIA_MAX_CAMINAR=500;
    private final double REFERENCIA_LINEA_LARGA=120;
    ListaRutas lr;
    int position;
    private static final String LIST_FRAGMENT_TAG = "list_fragment";//TERMINA SOTO
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;


    private CharSequence mTitle;
    public Fragment rutas, mapa;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        refresh = (ImageButton) findViewById(R.id.Refresh);
        imageButton = (FloatingActionButton) findViewById(R.id.myLocationButton);
        runButton = (FloatingActionButton) findViewById(R.id.mainRun);
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autocompletado);
        final PlacesAutoCompleteAdapter adapter = new PlacesAutoCompleteAdapter(this, R.layout.list_item);
        autoCompleteTextView.setAdapter(adapter);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("Enruta");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMap!=null)
                 mMap.clear();
                origen= null;
                destino= null;
            }
        });
        runButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (origen != null && destino != null) {
                    sugerenciasRutas = enrutamiento(origen.getPosition(), destino.getPosition());
                    sugerencia = true;
                    toggleList(sugerenciasRutas);
                } else {
                    Toast.makeText(getBaseContext(), "Seleccionar origen y destino", Toast.LENGTH_LONG);
                }
            }
        });
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Double[] latlng = null;
                getLatLng task = new getLatLng();
                try {
                    latlng = task.execute(adapter.getItem(position).getPlaceid()).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                String[] split = null;
                String descripcion = "";
                hideSoftKeyboard();
                if (favoritos) {
                    split = adapter.getItem(position).getNombre().split(",");
                    for (int i = 1; i < split.length; i++) {
                        descripcion += split[i];
                    }
                    ponerMarkersOrigenDestino(new LatLng(latlng[0], latlng[1]), split[0], descripcion);
                }
                LatLng latLng=new LatLng(latlng[0], latlng[1]);
                ponerMarkersOrigenDestino(latLng, "", "");
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
                autoCompleteTextView.setText("");

            }
        });


        // Inicializado componentes
        mNavigationDrawerFragment.setUp(toolbar,
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                .getMap();
        getSupportFragmentManager().findFragmentById(R.id.map).setRetainInstance(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LatLng latLng = new LatLng(27.485685, -109.938312);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(origen!=null) {
                    origen.remove();
                    origen=null;
                }
                else
                    origen=null;*/
                getMyLocation();
            }
        });
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.equals(origen)) {
                    marker.remove();
                    origen = null;
                } else if (marker.equals(destino)) {
                    marker.remove();
                    destino = null;
                }

                return false;
            }
        });
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                ponerMarkersOrigenDestino(latLng, adressPicker(latLng.latitude, latLng.longitude), "");
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (sugerencia) {
                    toggleList(new LinkedList<SugerenciaDeRuta>());
                    sugerencia = false;
                }
            }
        });


        //EMPIEZA SOTO
        lr=new ListaRutas(listaRutas,mMap);
        lr.llenarRutas();
        lr.llenarRutas2();/*
        interpolarRuta();
        enrutamiento();*///TERMINA SOTO
    }
    public void hideSoftKeyboard() {


        InputMethodManager inputManager = (InputMethodManager) MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), 0);
    }
    public String adressPicker(double lat, double lng) {
        List<Address> addresses = null;
        String result = null;
        Geocoder g = new Geocoder(this, Locale.getDefault());
        Log.d("Coordenadas", lat + "," + lng);
        try {
            addresses = g.getFromLocation(lat, lng, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.get(0).getMaxAddressLineIndex() < 0) {
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            result = address + " " + city;
        }
        return result+"";
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        //   Log.d("Position","position" + position);

        Fragment f = null;
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position) {
            case 0:
                toolbar.setTitle("Enruta");
                if (getSupportFragmentManager().findFragmentById(idRutas) != null) {

                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.remove(fragmentManager.findFragmentById(idRutas));
                    transaction.commit();

                }mostrarElementos();
                origen=null;
                destino=null;
                break;
            case 1:
                toolbar.setTitle("Rutas");
                ocultarElementos();
                f = new Rutas();
                fragmentManager.beginTransaction()
                        .replace(R.id.map, f)
                        .commit();
                idRutas = f.getId();
                break;
            case 2:
                toolbar.setTitle("Favoritos");
                ocultarElementos();
                f = new Favoritos();
                fragmentManager.beginTransaction().replace(R.id.map, f).commit();
                idRutas = f.getId();
                break;
            default:
                ;
        }
    }


    private void ponerMarkersOrigenDestino(LatLng latLng, String nombre, String descripcion) {
        MarkerOptions markerOption = new MarkerOptions();
        if (favoritos) {
            markerOption.draggable(true);
            markerOption.position(latLng);
            Marker favorito = mMap.addMarker(markerOption);
            ocultarElementos();
            Fragment f = new Favoritos();
            getSupportFragmentManager().beginTransaction().replace(R.id.map, f).commit();
            ControladorSQL sqlControl = new ControladorSQL(this);
            String latlong = latLng.latitude + "," + latLng.longitude;
            sqlControl.insertarValores(latlong, nombre, descripcion);
            idRutas = f.getId();
            favoritos = false;
        } else {
            if (origen == null) {
                markerOption.draggable(true);
                markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.origen));
                markerOption.position(latLng);
                origen = mMap.addMarker(markerOption);
            } else {
                if (destino == null) {
                    markerOption.draggable(true);
                    markerOption.position(latLng);
                    markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.destino));
                    destino = mMap.addMarker(markerOption);
                } else {
                    destino.remove();
                    markerOption.draggable(true);
                    markerOption.position(latLng);
                    markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.destino));
                    destino = mMap.addMarker(markerOption);
                }
            }

        }
    }

    private void ocultarElementos() {
        LinearLayout ocultar = (LinearLayout) findViewById(R.id.autocompletadoLayout);
        ocultar.setVisibility(LinearLayout.GONE);
        imageButton.setVisibility(View.GONE);
        runButton.setVisibility(View.GONE);
        refresh.setVisibility(View.GONE);
    }

    private void mostrarElementos() {
        mMap.clear();
        LinearLayout ocultar = (LinearLayout) findViewById(R.id.autocompletadoLayout);
        ocultar.setVisibility(LinearLayout.VISIBLE);
        autoCompleteTextView.setText("");
        imageButton.setVisibility(View.VISIBLE);
        runButton.setVisibility(View.VISIBLE);
        refresh.setVisibility(View.VISIBLE);
    }


    private void getMyLocation() {
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        Location location = mMap.getMyLocation();
        if (location != null) {
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);
            mMap.animateCamera(cameraUpdate);
            ponerMarkersOrigenDestino(new LatLng(latitude,longitude),"","");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            System.err.println("ENTRAAAAAAAAAAAAAAAAAAAA");
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRutasInteraction(int position) {//METODO QUE PINTA LA RUTA SELECCIONADA EN EL MAPA
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.remove(fragmentManager.findFragmentById(idRutas));
        transaction.commit();
        idRutas=0;
        mostrarElementos();
        listaRutas.get(position).dibujarRuta();
    }

    @Override
    public void onFragmentFavInteraction(boolean flag, LatLng lat) {
        if (flag) {
            favoritos = true;
            FragmentManager fm = getSupportFragmentManager();
            if (fm.findFragmentById(idRutas) != null) {
                mostrarElementos();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.remove(fm.findFragmentById(idRutas));
                transaction.commit();

            }
        } else {
            FragmentManager fm = getSupportFragmentManager();
            if (fm.findFragmentById(idRutas) != null) {
                mostrarElementos();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.remove(fm.findFragmentById(idRutas));
                transaction.commit();
                ponerMarkersOrigenDestino(lat,"","");
            }

        }
    }

    private void toggleList(LinkedList<SugerenciaDeRuta> datos) {//METODO VOLADOR QUE DESPLIEGA LA LISTA DE RUTAS SUGERIDAS
        android.app.Fragment f = getFragmentManager().findFragmentByTag(LIST_FRAGMENT_TAG);
        origen=null;
        destino=null;
        if (f != null) {
                    getFragmentManager().popBackStack();
                } else {
           f         =  SlidingListFragment
                    .instantiate(this, SlidingListFragment.class.getName());
            ((SlidingListFragment)f).setListAdapter(new ListViewSugerencias(this,datos,imagenes));
                    getFragmentManager().beginTransaction()
                            .setCustomAnimations(R.animator.slide_up,
                                    R.animator.slide_down,
                                    R.animator.slide_up,
                                    R.animator.slide_down)
                            .add(R.id.container,f,
                                    LIST_FRAGMENT_TAG
                            ).addToBackStack(null).commit();
        }
    }

    public void interpolarRuta(){
        for (Ruta listaRuta : listaRutas) {
            List<LatLng> lista=listaRuta.getPuntosRuta().getPoints();
            LinkedList<LatLng> aux=new LinkedList<>();
            for (int i = 0; i < lista.size(); i++) {
                aux.addLast(lista.get(i));
                if(i+1!=lista.size()) {//Si el punto que sige no es el final de la lista
                    if (esLineaRectaLarga(lista.get(i), lista.get(i + 1))) {
                        double incr=0;
                        for (int j = 0; j < MAX_INTERPOLACIONES; j++, incr += (1/MAX_INTERPOLACIONES)) {
                            aux.addLast(SphericalUtil.interpolate(lista.get(i), lista.get(i + 1), incr));
                        }
                    }
                }
                PolylineOptions po=new PolylineOptions();
                for (LatLng latLng : aux) {
                    po.add(latLng);
                }
                listaRuta.setPuntosRuta(po);
            }
        }
    }
    private boolean esLineaRectaLarga(LatLng origen, LatLng destino) {
        double distanciaCalleRectaLongitud=SphericalUtil.computeDistanceBetween(new LatLng(27.480262, -109.932705),new LatLng(27.480262, -109.932662));
        double distanciaCalleRectaLatitud=SphericalUtil.computeDistanceBetween(new LatLng(27.501333, -109.953748),new LatLng(27.501295, -109.953748));
        if(SphericalUtil.computeDistanceBetween(origen, destino)>=REFERENCIA_LINEA_LARGA){//La linea es suficientemente larga para interpolarla
            if(SphericalUtil.computeDistanceBetween(new LatLng(origen.latitude,origen.longitude),new LatLng(origen.latitude,destino.longitude))<=distanciaCalleRectaLongitud
                    ||SphericalUtil.computeDistanceBetween(new LatLng(origen.latitude,origen.longitude),new LatLng(destino.latitude,origen.longitude))<=distanciaCalleRectaLatitud){
                return true;
            }
        }
        return false;
    }

    public LinkedList<SugerenciaDeRuta> enrutamiento(LatLng origen,LatLng destino){

        Location locOrigen = new Location("Origen");
        locOrigen.setLatitude(origen.latitude);
        locOrigen.setLongitude(origen.longitude);

        Location locDestino = new Location("Destino");
        locDestino.setLatitude(destino.latitude);
        locDestino.setLongitude(destino.longitude);

        Location locComparacion = new Location("Comparacion");

        double distanciaOrigen;//distancia inicial del origen
        double distanciaDestino;//distancia inicial del destino
        double distanciaInterseccion;
        LinkedList<SugerenciaDeRuta> sugerenciasRutas=new LinkedList<SugerenciaDeRuta>();//Lista de rutas sugeridas
        LinkedList<SugerenciaDeRuta> rutasCercanasAlOrigen=new LinkedList<SugerenciaDeRuta>();//Lista de rutas cercanas al origen
        LinkedList<SugerenciaDeRuta> rutasCercanasAlDestino=new LinkedList<SugerenciaDeRuta>();//Lista de rutas cercanas al origen

        for(Ruta ruta:listaRutas){//Recorre todas las rutas de obregon
            distanciaOrigen=0;
            distanciaDestino=0;
            boolean flagOrigen=false,flagDestino=false;
            for(LatLng punto:ruta.getPuntosRuta().getPoints()){//Recorre cada punto de la ruta actual
                locComparacion.setLatitude(punto.latitude);
                locComparacion.setLongitude(punto.longitude);

                double comparacionOrigen=locOrigen.distanceTo(locComparacion);//Distancia del origen al punto actual de la ruta
                double comparacionDestino=locDestino.distanceTo(locComparacion);//Distancia del destino al punto actual de la ruta

                if(comparacionOrigen<REFERENCIA_MAX_CAMINAR) {//Si la distancia es menor a lo maximo permitido de caminata
                    if(distanciaOrigen==0||comparacionOrigen<distanciaOrigen) {//Asigna una menor distancia si es que encuentra el punto de menor distancia al origen
                        distanciaOrigen = comparacionOrigen;
                        flagOrigen = true;
                    }
                }
                if(comparacionDestino<REFERENCIA_MAX_CAMINAR) {//Si la distancia es menor a lo maximo permitido de caminata
                    if(distanciaDestino==0||comparacionDestino<distanciaDestino) {//Asigna una menor distancia si es que encuentra el punto de menor distancia al origen
                        distanciaDestino = comparacionDestino;
                        flagDestino=true;
                    }
                }
            }
            if(flagOrigen){//Si hubo una ruta cercana a mi origen
                rutasCercanasAlOrigen.addLast(new SugerenciaDeRuta(ruta, distanciaOrigen));
                if(meLlevaADestino(ruta,destino)) {//Si esa ruta me lleva a mi destino
                    sugerenciasRutas.addLast(new SugerenciaDeRuta(ruta, distanciaOrigen));
                }
            }
            if(flagDestino){//Si hubo una ruta cercana a mi destino
                rutasCercanasAlDestino.addLast(new SugerenciaDeRuta(ruta, distanciaDestino));
            }
        }
        double comparacionInterseccion;
        int indiceOrigen,indiceDestino;
        if(sugerenciasRutas.size()==0){//Si no hay ninguna ruta sugerida en la lista es porque se tiene que transbordar
            for (SugerenciaDeRuta rutaCercDestino : rutasCercanasAlDestino) {
                for (SugerenciaDeRuta rutaCercOrigen : rutasCercanasAlOrigen) {
                    boolean flagInterseccion=false;
                    distanciaInterseccion=0;

                    indiceDestino=0;
                    for (LatLng latLng : rutaCercDestino.getRuta().getPuntosRuta().getPoints()) {
                        indiceDestino++;
                        indiceOrigen=0;
                        for (LatLng lng : rutaCercOrigen.getRuta().getPuntosRuta().getPoints()) {
                            indiceOrigen++;
                            comparacionInterseccion=SphericalUtil.computeDistanceBetween(latLng,lng);
                            if(comparacionInterseccion<REFERENCIA_MAX_CAMINAR) {//Si la distancia es menor a lo maximo permitido de caminata
                                if(distanciaInterseccion==0||comparacionInterseccion<distanciaInterseccion) {//Asigna una menor distancia si es que encuentra el punto de menor distancia entre las dos rutas
                                    distanciaInterseccion=comparacionInterseccion;
                                    flagInterseccion = true;
                                }
                            }
                        }
                    }
                    if(flagInterseccion){
                        sugerenciasRutas.addLast(new SugerenciaDeRuta(rutaCercOrigen.getRuta(), rutaCercOrigen.getDistancia(), new SugerenciaDeRuta(rutaCercDestino.getRuta(), distanciaInterseccion)));
                        sugerenciasRutas.getLast().setTieneTransbordo(true);
                        Log.d("","ResultadoooooooooOOOO :" + sugerenciasRutas.getLast().toString());
                    }
                }
            }
        }

        for (SugerenciaDeRuta sugerenciasRuta : sugerenciasRutas) {
            Log.d("", "SUGERENCIA: " + sugerenciasRuta.toString());
        }
        return  sugerenciasRutas;
    }

    private int distanciaMasCorta(Ruta ruta,int indiceOrigen,int indiceDestino){
        int distanciaForward=0,distanciaBack=0;
        for(int i=indiceOrigen;true;i++){//recorre lista de puntos de la ruta hacia adelante
            if(i==ruta.getPuntosRuta().getPoints().size()-1)
                i=0;
            LatLng punto1=ruta.getPuntosRuta().getPoints().get(i);
            LatLng punto2=ruta.getPuntosRuta().getPoints().get(i+1);
            distanciaForward+=(int)SphericalUtil.computeDistanceBetween(punto1,punto2);
            if(i+1==indiceDestino)
                break;
        }

        for(int i=indiceOrigen;true;i--){//recorre puntos de la lista hacie atras
            if(i==0)
                i=ruta.getPuntosRuta().getPoints().size()-1;
            LatLng punto1=ruta.getPuntosRuta().getPoints().get(i);
            LatLng punto2=ruta.getPuntosRuta().getPoints().get(i-1);
            distanciaBack+=(int)SphericalUtil.computeDistanceBetween(punto1,punto2);
            if(i-1==indiceDestino)
                break;
        }
        if(distanciaBack>=distanciaForward)
            return distanciaBack;
        else
            return distanciaForward;
    }
    private SugerenciaDeRuta rutaOptima(LinkedList<SugerenciaDeRuta> sugerencias, int indiceOrigen,int indiceDestino){
        SugerenciaDeRuta optima=null;
        int distancia=0;
        for (SugerenciaDeRuta sugerencia : sugerencias) {
            int aux=0;
            if(sugerencia.isTieneTransbordo()){
                aux=(int)distanciaMasCorta(sugerencia.getRuta(),indiceOrigen
                        ,indiceDestino)+(int)distanciaMasCorta(sugerencia.getTransbordo().getRuta(),indiceOrigen,indiceDestino);

            }else{
                aux=(int)distanciaMasCorta(sugerencia.getRuta(),indiceOrigen
                        ,indiceDestino)+(int)distanciaMasCorta(sugerencia.getTransbordo().getRuta(),indiceOrigen,indiceDestino);
            }
            if(aux<distancia||distancia==0) {
                distancia = aux;
                optima=sugerencia;
            }
        }
        return optima;
    }
    private boolean meLlevaADestino(Ruta ruta, LatLng destino) {
        Location locComparacion = new Location("Comparacion");//punto de la ruta con el cual se comparara el destino
        Location locDestino = new Location("Destino");//punto del destino recibido
        locDestino.setLatitude( destino.latitude);
        locDestino.setLongitude(destino.longitude);
        for(LatLng punto:ruta.getPuntosRuta().getPoints()){
            locComparacion.setLatitude(punto.latitude);
            locComparacion.setLongitude(punto.longitude);
            double comparacion=locDestino.distanceTo(locComparacion);
            if(comparacion<REFERENCIA_MAX_CAMINAR) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onSugerenciasInteraction(int position) {
        SugerenciaDeRuta sug=sugerenciasRutas.get(position);
        sug.getRuta().dibujarRuta();
        if(sug.isTieneTransbordo())
            sug.getTransbordo().getRuta().dibujarRuta();
        toggleList(sugerenciasRutas);
    }
}


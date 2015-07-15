package com.ex.saulantonio.enruta3;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class PlacesAutoCompleteAdapter extends ArrayAdapter<Lugares> implements Filterable {
    private ArrayList  <Lugares> resultList = new ArrayList<>();
    private Context mContext;

    private static final String LOG_TAG = "ExampleApp";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String TYPE_DETAILS = "/details";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyBNiZ1zulMPbB-B1aJkvCgRQECrCiNCMLE";
    private getLatLng task;
    public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        mContext = context;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public Lugares getItem(int index) {
        return resultList.get(index);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item,null);
           /* LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, parent, false);
       */
        }
        Lugares lugar = getItem(position);
        Log.d("DEBUG", "" + lugar.getNombre());
        TextView text = (TextView) convertView.findViewById(R.id.text);
        text.setText(getItem(position).getNombre());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            public String convertResultToString(Object resultValue) {
                String str = ((Lugares)(resultValue)).getNombre();
                return str;
            }


            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Recibe los valores del autocompletado
                    try {
                        resultList = autocomplete(constraint.toString());
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }};
        return filter;
    }

    public ArrayList<Lugares> autocomplete(String input) throws ExecutionException, InterruptedException {
        ArrayList<Lugares> resultList = null;
        HttpURLConnection connec = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?sensor=true&key=" + API_KEY);
            sb.append("&location=27.483897,-109.932402");
            sb.append("&radius=1000");
            sb.append("&components=country:mx");
            sb.append("&input="+URLEncoder.encode(input, "utf8"));
            Log.d("","Url: " + sb.toString());
            URL url = new URL(sb.toString());
            connec = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(connec.getInputStream());// Carga los resultados en json
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (connec != null) {
                connec.disconnect();
            }
        }




        try {
            Log.d("Rsult", jsonResults.toString());
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
            // Extract the Place descriptions from the results
            ArrayList result = new ArrayList<String>(predsJsonArray.length());
            resultList  = new ArrayList<Lugares>();

            for (int i = 0; i < predsJsonArray.length(); i++) {
                task = new getLatLng();
                Double[] latlng = task.execute(predsJsonArray.getJSONObject(i).getString("place_id")).get();
                if(SphericalUtil.computeDistanceBetween(new LatLng(27.48389, -109.932402),new LatLng(latlng[0],latlng[1]))<19000){
                    resultList.add(new Lugares(predsJsonArray.getJSONObject(i).getString("description"),predsJsonArray.getJSONObject(i).getString("place_id")));
                }
            }
            //Obtiene el nombre del lugar del json
            //  resultList = new ArrayList<String>(predsJsonArray.length());

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }
        return resultList;

    }





}

class getLatLng extends AsyncTask<String,Void,Double[]>{
    private static final String LOG_TAG = "ExampleApp";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_DETAILS = "/details";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyBNiZ1zulMPbB-B1aJkvCgRQECrCiNCMLE";

    @Override
    protected Double[] doInBackground(String... params) {
        String placeid = params[0];
        String result = null;
        HttpURLConnection connec = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_DETAILS + OUT_JSON);
            sb.append("?sensor=true&key=" + API_KEY);
            sb.append("&placeid="+placeid);
            Log.d("","Url: " + sb.toString());
            URL url = new URL(sb.toString());
            connec = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(connec.getInputStream());// Carga los resultados en json
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return null;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return null;
        } finally {
            if (connec != null) {
                connec.disconnect();
            }
        }

        try {
            //Crea un objeto json con los resultados
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            // Log.d("Prueba",jsonResults.toString()+"");
            JSONObject predsJsonArray = jsonObj.getJSONObject("result").getJSONObject("geometry").getJSONObject("location");
            Double lat = predsJsonArray.getDouble("lat");
            Double lng = predsJsonArray.getDouble("lng");
            Double[] resultado =new Double[2];
            resultado[0] = lat;
            resultado[1] = lng;
            return resultado;
            //Obtiene el nombre del lugar del json
            //  resultList = new ArrayList<String>(predsJsonArray.length());

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }
        return null;

    }



}


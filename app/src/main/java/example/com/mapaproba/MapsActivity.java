package example.com.mapaproba;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public List<String> lista_registracija;
    private List<String> lista_koordinata = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void getVozila() {
        class GetVozila extends AsyncTask<Void,Void,String> {
            String id = getIntent().getStringExtra("KORISNIK_ID");
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try
                {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray result = jsonObject.getJSONArray("result");
                    for(int i=0; i<result.length();i++) {
                        JSONObject c = result.getJSONObject(i);
                        String registracija = c.getString("registracija");
                        lista_registracija.add(registracija);
                    }
                    getKoordinateVozila();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam("http://192.168.0.101:80/getVozila.php?id=", id,"");
                return s;
            }
        }
        GetVozila gv = new GetVozila();
        gv.execute();
    }

    public void getKoordinateVozila() {
        class GetKoordinateVozila extends AsyncTask<Void,Void,List<String> > {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(List<String> s) {
                super.onPostExecute(s);
                try
                {
                    for (int i=0; i<s.size();i++) {
                        JSONObject jsonObject = new JSONObject(s.get(i));
                        JSONArray result = jsonObject.getJSONArray("result");
                        for(int j=0; j<result.length();j++) {
                            JSONObject c = result.getJSONObject(j);
                            String duzina = c.getString("duzina");
                            String sirina = c.getString("sirina");
                            lista_koordinata.add(duzina);
                            lista_koordinata.add(sirina);
                        }
                    }
                    for (int i=0;i<lista_koordinata.size();i++) {
                        String duzina = lista_koordinata.get(i);
                        String sirina = lista_koordinata.get(i+1);
                        System.out.println(duzina);

                        LatLng poz = new LatLng(Double.parseDouble(duzina),Double.parseDouble(sirina));
                        mMap.addMarker(new MarkerOptions().position(poz).title(lista_registracija.get(i/2)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(poz));
                        i++;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected List<String> doInBackground(Void... params) {
                List<String> str_list = new ArrayList<String>();
                RequestHandler rh = new RequestHandler();
                for (int i=0; i<lista_registracija.size();i++) {
                    String s = rh.sendGetRequestParam("http://192.168.0.101:80/getGpsKoordinate.php?vozilo_id=", lista_registracija.get(i),"");
                    str_list.add(s);
                }
                return str_list;
            }
        }
        GetKoordinateVozila gkv = new GetKoordinateVozila();
        gkv.execute();
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        lista_registracija = new ArrayList<String>();
        getVozila();
        System.out.println("Duzina: " + Integer.toString(lista_registracija.size()));
        //getKoordinateVozila();
        // Add a marker in Sydney and move the camera
        for (int i=0;i<lista_koordinata.size();i++) {
            String duzina = lista_koordinata.get(i);
            String sirina = lista_koordinata.get(i+1);
            System.out.println(duzina);

            LatLng poz = new LatLng(Double.parseDouble(duzina),Double.parseDouble(sirina));
            mMap.addMarker(new MarkerOptions().position(poz).title(lista_registracija.get(i)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(poz));
            i++;
        }
        /*
        LatLng sydney = new LatLng(-34, 151);
        String s = getIntent().getStringExtra("KORISNIK_ID");
        mMap.addMarker(new MarkerOptions().position(sydney).title(s));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }
}

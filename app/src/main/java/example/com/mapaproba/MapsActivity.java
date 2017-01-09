package example.com.mapaproba;

import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    Boolean flag = false;
    int i=0,j=0;
    private GoogleMap mMap;
    public List<String> lista_registracija;
    private List<String> lista_koordinata = new ArrayList<String>();
    private List<Marker> markeri = new ArrayList<Marker>();
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
                String s = rh.sendGetRequestParam("http://10.0.2.2:80/getVozila.php?id=", id,"");
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
                        markeri.add(mMap.addMarker(new MarkerOptions().position(poz).title(lista_registracija.get(i/2))));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(poz));
                        i++;
                    }
                    ArrayList<LatLng> krajnjePozicije = new ArrayList<LatLng>();
                    for(int i = 0; i < markeri.size(); i++)
                    {
                        double latPomak = 3.0;
                        double lngPomak = 2.5;
                        if(i % 2 == 0)
                        {
                            latPomak = 2.0;
                            lngPomak = -1.5;
                        }
                        LatLng pozFin = new LatLng(markeri.get(i).getPosition().latitude + latPomak, markeri.get(i).getPosition().longitude + lngPomak);
                        krajnjePozicije.add(pozFin);
                    }
                    for(int i = 0; i < markeri.size(); i++)
                    {
                        animateMarker(markeri.get(i), krajnjePozicije.get(i), false);

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
                    String s = rh.sendGetRequestParam("http://10.0.2.2:80/getGpsKoordinate.php?vozilo_id=", lista_registracija.get(i),"");
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
    }

    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 60000;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 1000);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }
}

package wada1028.info3.oepnv_navigator.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import wada1028.info3.oepnv_navigator.R;

public class Routing_Activity extends AppCompatActivity {
    private MapView mapview;
    ListView listView;
    RequestQueue queue_Routing;
    String startHalte = getIntent().getExtras().getString(HomeFragment.KEY_Start);
    String zielHalte = getIntent().getExtras().getString(HomeFragment.KEY_Ziel);
    List<HashMap> journeyList = new ArrayList<HashMap>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapboxAccountManager.start(this, getString(R.string.access_token));
        setContentView(R.layout.activity_routing_);
        mapview = (MapView) findViewById(R.id.mapView);
        listView = (ListView) findViewById(R.id.listView_route);
        queue_Routing = Volley.newRequestQueue(this);
        jsonParse();


        mapview.getMapAsync(new

                                    OnMapReadyCallback() {
                                        @Override
                                        public void onMapReady(MapboxMap mapboxMap) {

                                        }
                                    });
        mapview.onCreate(savedInstanceState);
    }

    private void jsonParse() {
        //Link bauen für Abfrage
        //http://smartmmi.demo.mentz.net/smartmmi/XML_TRIP_REQUEST2?outputFormat=rapidJson&type_sf=any&type_origin=stop&coordOutputFormat=WGS84[DD.DDDDD]&name_origin=Synagoge,Karlsruhe&type_destination=stop&name_destination=Schlossplatz, Durlach

        String link_teil1 = "http://smartmmi.demo.mentz.net/smartmmi/XML_TRIP_REQUEST2?outputFormat=rapidJson&type_sf=any&type_origin=";
        String link_teil2 = "&type_destination=stop&name_destination=";
        String fertigerLink = link_teil1 + startHalte + link_teil2 + zielHalte;

        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, fertigerLink, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                //Log.i("METHODE", "in OnResponse");
                try {
                    JSONArray jsonJourneyArray = response.getJSONArray("journeys");
                    for (int i = 0; i < jsonJourneyArray.length(); i++) {
                        HashMap<String, HashMap> legHashMap = new HashMap<String, HashMap>();
                        journeyList.add(legHashMap);
                        JSONObject actJourney = (JSONObject) jsonJourneyArray.get(i);
                        JSONArray jsonLegArray = actJourney.getJSONArray("legs");
                        for (int j = 0; j < jsonLegArray.length(); j++) {
                            HashMap<String, Float> legCoordMap = new HashMap<>();
                            HashMap<String, String> legTimeMap = new HashMap<>();
                            HashMap<String, String> legMeanOfTransMap = new HashMap<>();
                            legHashMap.put("legTime", legTimeMap);
                            legHashMap.put("coords", legCoordMap);
                            legHashMap.put("transportation",legMeanOfTransMap);
                            JSONObject actLeg = (JSONObject) jsonLegArray.get(i);

                            //Origin
                            JSONObject actOrigin = (JSONObject) actLeg.getJSONObject("origin");
                            String depTimeString = actOrigin.getString("departureTimePlanned");
                            legTimeMap.put("departureTimePlanned", depTimeString);

                            JSONArray depCoordArray = actOrigin.getJSONArray("coord");
                            float depCoordX = (float) (depCoordArray.get(0));
                            float depCoordY = (float) (depCoordArray.get(1));
                            legTimeMap.put("depCoordX", String.valueOf(depCoordX));
                            legTimeMap.put("depCoordY", String.valueOf(depCoordY));

                            //Transportation (means of transportation)
                            JSONObject transpArray = actLeg.getJSONObject("transportation");
                            String meanOfTransString = transpArray.getString("name");
                            legMeanOfTransMap.put("name", meanOfTransString);


                            //Destination
                            JSONObject actDestination = (JSONObject) actLeg.getJSONObject("destination");
                            String desTimeString = actDestination.getString("arrivalTimePlanned");
                            legTimeMap.put("arrivalTimePlanned", desTimeString);

                            JSONArray desCoordArray = actDestination.getJSONArray("coord");
                            float desCoordX = (float) (desCoordArray.get(0));
                            float desCoordY = (float) (desCoordArray.get(1));
                            legTimeMap.put("desCoordX", String.valueOf(desCoordX));
                            legTimeMap.put("desCoordY", String.valueOf(desCoordY));

                            String resTimeString = actDestination.getString("departureTimePlanned");
                            legTimeMap.put("arrivalTimePlanned", desTimeString);

                            //Coordinates for Map; Keys: X1,X2...;Y1,Y2...
                            JSONArray coordArray = actLeg.getJSONArray("coords");
                            for (int k = 0; k < coordArray.length(); k++) {
                                JSONArray coord2Array = coordArray.getJSONArray(k);
                                float coordLX = (float) (coord2Array.get(0));
                                float coordLY = (float) (coord2Array.get(1));
                                legCoordMap.put("X" + k, coordLX);
                                legCoordMap.put("Y" + k, coordLY);
                            }

                        }


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("METHODE", "in catch");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.i("METHODE", "Error: no connection");
            }
        });
        queue_Routing.add(objectRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapview.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapview.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapview.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapview.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapview.onLowMemory();
    }
}
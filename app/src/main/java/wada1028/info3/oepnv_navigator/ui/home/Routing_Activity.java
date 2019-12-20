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
    HashMap<Date,String> dateHashMap= new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapboxAccountManager.start(this, getString(R.string.access_token));
        setContentView(R.layout.activity_routing_);
        mapview = (MapView) findViewById(R.id.mapView);
        listView = (ListView) findViewById(R.id.listView_route);
        queue_Routing = Volley.newRequestQueue(this);

        //Link bauen für Abfrage
        //http://smartmmi.demo.mentz.net/smartmmi/XML_TRIP_REQUEST2?outputFormat=rapidJson&type_sf=any&type_origin=stop&coordOutputFormat=WGS84[DD.DDDDD]&name_origin=Synagoge,Karlsruhe&type_destination=stop&name_destination=Schlossplatz, Durlach




        // Jason Parsing


        mapview.getMapAsync(new

        OnMapReadyCallback() {
                @Override
                public void onMapReady(MapboxMap mapboxMap) {

                }
            });
        mapview.onCreate(savedInstanceState);
    }

    private void jsonParse(AutoCompleteTextView aCTextView) {
        String link_teil1 = "http://smartmmi.demo.mentz.net/smartmmi/XML_TRIP_REQUEST2?outputFormat=rapidJson&type_sf=any&type_origin=";
        String link_teil2 = "&type_destination=stop&name_destination=";
        String fertigerLink = link_teil1 + startHalte + link_teil2 + zielHalte;
        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, fertigerLink, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Log.i("METHODE", "in OnResponse");
                try {
                    JSONArray jsonArray = response.getJSONArray("journeys");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject parent = jsonArray.getJSONObject(i);
                        String departureTimePlannedString= parent.getString("departureTimePlanned");
                        String arrivalTimePlannedString = parent.getString("arrivalTimePlanned");
                        JSONObject transportation = jsonArray.getJSONObject(i);
                        String name = transportation.getString("name");

                        dateHashMap.put(departureTimePlannedString,name);
                        //Log.i("METHODE", "" + name);
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
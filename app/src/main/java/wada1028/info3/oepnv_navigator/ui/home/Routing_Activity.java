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
import com.android.volley.RetryPolicy;
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

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import wada1028.info3.oepnv_navigator.CustomListAdapter;
import wada1028.info3.oepnv_navigator.R;

import static wada1028.info3.oepnv_navigator.ui.home.HomeFragment.*;
import static wada1028.info3.oepnv_navigator.ui.home.HomeFragment.KEY_Ziel;

public class Routing_Activity extends AppCompatActivity {
    public static String startHalteString;
    public static String zielHalteString;
    RequestQueue queue_Routing;
    String startHalte;
    String zielHalte;
    MapView mapview;
    List<HashMap> journeyList = new ArrayList<>();
    CustomListAdapter customListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapboxAccountManager.start(this, getString(R.string.access_token));
        setContentView(R.layout.activity_routing_);
        ListView listView = (ListView)findViewById(R.id.listView_route);
        mapview = (MapView)findViewById(R.id.mapView);



        queue_Routing = Volley.newRequestQueue(this);
        startHalte= getIntent().getStringExtra(KEY_Start);
        zielHalte = getIntent().getStringExtra(KEY_Ziel);

        customListAdapter = new CustomListAdapter(this, journeyList);
        listView.setAdapter(customListAdapter);

        //Für Ausgabe:
        startHalteString = startHalte;
        zielHalteString = zielHalte;

        jsonParse();



        //Date:
        //TestDate: "2019-12-24T10:39:00Z"

        /*String testDateString = "2019-12-24T10:39:07Z";
        String testStringDate = dateParse(testDateString);
        Log.i("DANI",testStringDate);*/



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

        String link_teil1 = "http://smartmmi.demo.mentz.net/smartmmi/XML_TRIP_REQUEST2?outputFormat=rapidJson&type_sf=any&type_origin=stop&coordOutputFormat=WGS84%5bDD.DDDDD%5d&name_origin=";
        String link_teil2 = "&type_destination=stop&name_destination=";
        try {
            startHalte = URLEncoder.encode(startHalte,"UTF-8");
            startHalte = startHalte.replace("+","%20");
            startHalte = startHalte.replace("%2C",",");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            zielHalte = URLEncoder.encode(zielHalte,"UTF-8");
            zielHalte = zielHalte.replace("+","%20");
            zielHalte = zielHalte.replace("%2C",",");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String fertigerLink = link_teil1 + startHalte + link_teil2 + zielHalte;
        Log.i("DANI",""+fertigerLink);

        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, fertigerLink, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                //Log.i("METHODE", "in OnResponse");
                try {
                    JSONArray jsonJourneyArray = response.getJSONArray("journeys");
                    for (int i = 0; i < jsonJourneyArray.length(); i++) {
                        HashMap<String, HashMap> journeyHashMap = new HashMap<>();
                        journeyList.add(journeyHashMap);
                        JSONObject actJourney = (JSONObject) jsonJourneyArray.get(i);
                        JSONArray jsonLegArray = actJourney.getJSONArray("legs");
                        HashMap<String, Double> legCoordMap = new HashMap<>();
                        HashMap<String, String> legTimeMap = new HashMap<>();
                        HashMap<String, String> legMeanOfTransMap = new HashMap<>();
                        journeyHashMap.put("legTime", legTimeMap);
                        journeyHashMap.put("coords", legCoordMap);
                        journeyHashMap.put("transportation",legMeanOfTransMap);
                        for (int j = 0; j < jsonLegArray.length(); j++) {
                            JSONObject actLeg = (JSONObject) jsonLegArray.get(j);
                            //Origin
                            JSONObject actOrigin = actLeg.getJSONObject("origin");
                            String depTimeString = actOrigin.getString("departureTimePlanned");
                            legTimeMap.put("departureTimePlanned"+j, depTimeString);

                            JSONArray depCoordArray = actOrigin.getJSONArray("coord");
                            double depCoordX = (double)(depCoordArray.get(0));
                            double depCoordY = (double) depCoordArray.get(1);
                            legCoordMap.put("depCoordX"+j, depCoordX);
                            legCoordMap.put("depCoordY"+j, depCoordY);

                            //Transportation (means of transportation)
                            JSONObject transpArray = actLeg.getJSONObject("transportation");
                            String meanOfTransString = transpArray.getString("name");
                            legMeanOfTransMap.put("name"+j, meanOfTransString);


                            //Destination
                            JSONObject actDestination = actLeg.getJSONObject("destination");
                            String desTimeString = actDestination.getString("arrivalTimePlanned");
                            legTimeMap.put("arrivalTimePlanned"+j, desTimeString);

                            JSONArray desCoordArray = actDestination.getJSONArray("coord");
                            double desCoordX = (double) (desCoordArray.get(0));
                            double desCoordY = (double) (desCoordArray.get(1));
                            legCoordMap.put("desCoordX"+j, desCoordX);
                            legCoordMap.put("desCoordY"+j, desCoordY);


                            //Coordinates for Map; Keys: X1,X2...;Y1,Y2...
                            JSONArray coordArray = actLeg.getJSONArray("coords");
                            for (int k = 0; k < coordArray.length(); k++) {
                                JSONArray coord2Array = coordArray.getJSONArray(k);
                                double coordLX = (double) (coord2Array.get(0));
                                double coordLY = (double) (coord2Array.get(1));
                                legCoordMap.put("X" + k, coordLX);
                                legCoordMap.put("Y" + k, coordLY);
                            }

                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("METHODE", "in catch");
                }
                customListAdapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.e("METHODE", "Error: no connection");
            }

        });
        objectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 30000;
            }
            @Override
            public int getCurrentRetryCount() {
                return 0;
            }
            @Override
            public void retry(VolleyError error) throws VolleyError {
            }
        });
        queue_Routing.add(objectRequest);
    }

    public static String dateParse (String dateString){
        Date dateDate = null;
        String resultString = "";
        try {
            SimpleDateFormat dateSDF = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss'Z'");
            dateSDF.setTimeZone(TimeZone.getTimeZone("UTC"));
            dateDate = dateSDF.parse(dateString);
            SimpleDateFormat stringSDF = new SimpleDateFormat("kk:mm");
            resultString = stringSDF.format(dateDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return resultString;
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
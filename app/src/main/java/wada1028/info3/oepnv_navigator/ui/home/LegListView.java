package wada1028.info3.oepnv_navigator.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import wada1028.info3.oepnv_navigator.CustomListAdapter;
import wada1028.info3.oepnv_navigator.R;

public class LegListView extends AppCompatActivity {

    RequestQueue queue_Routing;
    public static String startHalte;
    public static String zielHalte;
    public static String startHalteID;
    public static String zielHalteID;
    public static String time;
    public static String date;
    public static final String KEY_Start = "StartName";
    public static final String KEY_Ziel  = "ZielName";
    public static final String KEY_Start_ID = "StartId";
    public static final String KEY_Ziel_ID = "ZielId";
    public static final String KEY_Date = "Date";
    public static final String KEY_Time = "Time";


    List<HashMap> journeyList = new ArrayList<>();
    CustomListAdapter customListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Calling Application class (see application tag in AndroidManifest.xml)
        final GlobalApplication globalApplication = (GlobalApplication) getApplicationContext();
        // set empty journey list
        globalApplication.setJourneyList(journeyList);
        setContentView(R.layout.activity_list_view);
        final android.widget.ListView listView = findViewById(R.id.listView_route);

        queue_Routing = Volley.newRequestQueue(this);
        startHalte = getIntent().getStringExtra(KEY_Start);
        zielHalte = getIntent().getStringExtra(KEY_Ziel);
        startHalteID = getIntent().getStringExtra(KEY_Start_ID);
        zielHalteID = getIntent().getStringExtra(KEY_Ziel_ID);
        time = getIntent().getStringExtra(KEY_Time);
        date = getIntent().getStringExtra(KEY_Date);

        customListAdapter = new CustomListAdapter(this, journeyList);
        listView.setAdapter(customListAdapter);
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {


                Intent mapIntent = new Intent(LegListView.this,Routing_Activity.class);
                mapIntent.putExtra(Routing_Activity.KEY_JourneyPosition,position);
                startActivity(mapIntent);

            }
        });

        jsonParse();

    }

    private void jsonParse() {
        //Link bauen für Abfrage
        //http://smartmmi.demo.mentz.net/smartmmi/XML_TRIP_REQUEST2?outputFormat=rapidJson&type_sf=any&type_origin=stop&coordOutputFormat=WGS84[DD.DDDDD]&name_origin=Synagoge,Karlsruhe&type_destination=stop&name_destination=Schlossplatz, Durlach

        String link_teil1 = "http://smartmmi.demo.mentz.net/smartmmi/XML_TRIP_REQUEST2?outputFormat=rapidJson&type_sf=any&type_origin=stop&coordOutputFormat=WGS84%5bDD.DDDDD%5d&name_origin=";
        String link_teil2 = "&type_destination=stop&name_destination=";
        String link_teil3 = "&itdTime="+time;
        String link_teil4 = "&itdDate="+date;
        String startHalteParam = "Error";
        String zielHalteParam = "Error";
        try{
            startHalteParam = URLEncoder.encode(startHalteID,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e("DANI","Can not encode startHalteID");
        }
        try{
            zielHalteParam = URLEncoder.encode(zielHalteID,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e("DANI","Can not encode ZielHalteID");
        }

        String fertigerLink = link_teil1 + startHalteParam + link_teil2 + zielHalteParam+ link_teil3 + link_teil4;
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
                        HashMap<String,String> legNames = new HashMap<>();
                        journeyHashMap.put("legTime", legTimeMap);
                        journeyHashMap.put("coords", legCoordMap);
                        journeyHashMap.put("transportation",legMeanOfTransMap);
                        journeyHashMap.put("stopNames",legNames);
                        for (int legNumber = 0; legNumber < jsonLegArray.length(); legNumber++) {
                            JSONObject actLeg = (JSONObject) jsonLegArray.get(legNumber);
                            //Origin
                            JSONObject actOrigin = actLeg.getJSONObject("origin");
                            String depTimeString = actOrigin.getString("departureTimePlanned");
                            String depNameString = actOrigin.getString("name");
                            legTimeMap.put("departureTimePlanned"+legNumber, depTimeString);
                            legNames.put("departureName"+legNumber,depNameString);


                            JSONArray depCoordArray = actOrigin.getJSONArray("coord");
                            double depCoordX = (double)(depCoordArray.get(0));
                            double depCoordY = (double) depCoordArray.get(1);
                            legCoordMap.put("depCoordX"+legNumber, depCoordX);
                            legCoordMap.put("depCoordY"+legNumber, depCoordY);

                            //Transportation (means of transportation)
                            JSONObject transpArray = actLeg.getJSONObject("transportation");
                            String meanOfTransString="error";
                            if (transpArray.has("name")) {
                                meanOfTransString = transpArray.getString("name");
                            } else {
                                JSONObject product = transpArray.getJSONObject("product");
                                meanOfTransString = product.getString("name");
                            }
                            legMeanOfTransMap.put("name"+legNumber, meanOfTransString);


                            //Destination
                            JSONObject actDestination = actLeg.getJSONObject("destination");
                            String desTimeString = actDestination.getString("arrivalTimePlanned");
                            String desNameString = actDestination.getString("name");
                            legTimeMap.put("arrivalTimePlanned"+legNumber, desTimeString);
                            legNames.put("arrivalName"+legNumber,desNameString);

                            JSONArray desCoordArray = actDestination.getJSONArray("coord");
                            double desCoordX = (double) (desCoordArray.get(0));
                            double desCoordY = (double) (desCoordArray.get(1));
                            legCoordMap.put("desCoordX"+legNumber, desCoordX);
                            legCoordMap.put("desCoordY"+legNumber, desCoordY);


                            //Coordinates for Map; Keys: X1,X2...;Y1,Y2...
                            JSONArray coordArray = actLeg.getJSONArray("coords");
                            for (int k = 0; k < coordArray.length(); k++) {
                                JSONArray coord2Array = coordArray.getJSONArray(k);
                                double coordLX = (double) (coord2Array.get(0));
                                double coordLY = (double) (coord2Array.get(1));
                                legCoordMap.put("X" + legNumber + "." + k, coordLX);
                                legCoordMap.put("Y" + legNumber + "." + k, coordLY);
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
}

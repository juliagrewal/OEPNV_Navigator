package wada1028.info3.oepnv_navigator.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;

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

import wada1028.info3.oepnv_navigator.CustomLegListAdapter;
import wada1028.info3.oepnv_navigator.CustomListAdapter;
import wada1028.info3.oepnv_navigator.Leg;
import wada1028.info3.oepnv_navigator.R;

public class Routing_Activity extends AppCompatActivity {
    public static String KEY_JourneyPosition = "JourneyPosition";
    public static String startHalte;
    public static String zielHalte;
    public static String startHalteID;


    MapView mapview;
    CustomLegListAdapter customLegListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i("Julia", "in Routing Activity ");

        super.onCreate(savedInstanceState);
        MapboxAccountManager.start(this, getString(R.string.access_token));
        setContentView(R.layout.activity_routing_);
        ListView listView = (ListView)findViewById(R.id.leg_listView_route);
        mapview = (MapView)findViewById(R.id.mapView);

        List<Leg> legList = new ArrayList<Leg>();
        int position = getIntent().getIntExtra(KEY_JourneyPosition,0);
        // Calling Application class (see application tag in AndroidManifest.xml)
        final GlobalApplication globalApplication = (GlobalApplication) getApplicationContext();
        List<HashMap> journeyList = globalApplication.getJourneyList();
        HashMap actJourney = journeyList.get(position);

        HashMap actLegTimeMap =(HashMap) actJourney.get("legTime");
        HashMap actTransMode = (HashMap) actJourney.get("transportation");
        HashMap actNames = (HashMap) actJourney.get("stopNames");

        int numberOfLegs = (actLegTimeMap.size()/2)-1;
        for(int legIndex =0;legIndex<numberOfLegs;legIndex++){
            Leg actLeg = new Leg();
            actLeg.depTime = (String) actLegTimeMap.get("departureTimePlanned"+legIndex);
            actLeg.depName = (String) actNames.get("departureName"+legIndex);
            actLeg.transMode = (String) actTransMode.get("name"+legIndex);
            actLeg.desTime = (String) actLegTimeMap.get("arrivalTimePlanned"+legIndex);
            actLeg.desName = (String) actNames.get("arrivalName"+legIndex);
            legList.add(actLeg);
        }





        customLegListAdapter = new CustomLegListAdapter(this, legList);
        listView.setAdapter(customLegListAdapter);



        mapview.getMapAsync(new
                                    OnMapReadyCallback() {
                                        @Override
                                        public void onMapReady(MapboxMap mapboxMap) {

                                        }
                                    });
        mapview.onCreate(savedInstanceState);

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
package wada1028.info3.oepnv_navigator.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import wada1028.info3.oepnv_navigator.CustomLegListAdapter;
import wada1028.info3.oepnv_navigator.Leg;
import wada1028.info3.oepnv_navigator.R;
public class Routing_Activity extends AppCompatActivity {
    public static String KEY_JourneyPosition = "JourneyPosition";
    List<Leg> legList;
    List<List<LatLng>> trailList;
    MapView mapview;
    CustomLegListAdapter customLegListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("Julia", "in Routing Activity ");
        super.onCreate(savedInstanceState);
        MapboxAccountManager.start(this, getString(R.string.access_token));
        setContentView(R.layout.activity_routing_);
        ListView listView = findViewById(R.id.leg_listView_route);
        mapview = findViewById(R.id.mapView);
        legList = new ArrayList<>();
        trailList = new ArrayList<>();
        int position = getIntent().getIntExtra(KEY_JourneyPosition,0);
        // Calling Application class (see application tag in AndroidManifest.xml)
        final GlobalApplication globalApplication = (GlobalApplication) getApplicationContext();
        List<HashMap> journeyList = globalApplication.getJourneyList();
        HashMap actJourney = journeyList.get(position);
        HashMap actLegTimeMap =(HashMap) actJourney.get("legTime");
        HashMap actTransMode = (HashMap) actJourney.get("transportation");
        HashMap actNames = (HashMap) actJourney.get("stopNames");
        HashMap actCoords = (HashMap) actJourney.get("coords");
        int numberOfLegs = (actLegTimeMap.size()/2);
        for(int legIndex =0;legIndex<numberOfLegs;legIndex++){
            Leg actLeg = new Leg();
            actLeg.depTime = (String) actLegTimeMap.get("departureTimePlanned"+legIndex);
            actLeg.depName = (String) actNames.get("departureName"+legIndex);
            actLeg.transMode = (String) actTransMode.get("name"+legIndex);
            actLeg.desTime = (String) actLegTimeMap.get("arrivalTimePlanned"+legIndex);
            actLeg.desName = (String) actNames.get("arrivalName"+legIndex);
            legList.add(actLeg);
            // collect trail points for one leg
            List<LatLng> trail = new ArrayList<LatLng>();
            for (int k=0; actCoords.containsKey("X"+legIndex+"."+k);k++){
                double lat = (double) actCoords.get("X"+legIndex+"."+k);
                double lng = (double) actCoords.get("Y"+legIndex+"."+k);
                LatLng trailPoint = new LatLng(lat,lng);
                trail.add(trailPoint);
            }
            trailList.add(trail);
        }
        customLegListAdapter = new CustomLegListAdapter(this, legList);
        listView.setAdapter(customLegListAdapter);
        customLegListAdapter.notifyDataSetChanged();
        // collect input for Map (Marker, route)
        double xCoordinate, yCoordinate;
        //departure marker
        xCoordinate = (double) actCoords.get("depCoordX0");
        yCoordinate = (double) actCoords.get("depCoordY0");
        final LatLng depCoord = new LatLng(xCoordinate, yCoordinate);
        final MarkerOptions departureMarkerOptions = new MarkerOptions()
                .setTitle("Start")
                .setPosition(depCoord);
        //destination marker
        for (int i=0; actCoords.containsKey("desCoordX" + i); i++) {
            xCoordinate = (double) actCoords.get("desCoordX" + i);
            yCoordinate = (double) actCoords.get("desCoordY" + i);
        }
        final LatLng desCoord = new LatLng(xCoordinate, yCoordinate);
        final MarkerOptions destinationMarkerOptions = new MarkerOptions()
                .setTitle("Ziel")
                .setPosition(desCoord);
        final LatLng center = average(depCoord, desCoord);
        final List<Integer> colorList = new ArrayList<>();
        colorList.add(Color.RED);
        colorList.add(Color.GREEN);
        colorList.add(Color.BLUE);
        colorList.add(Color.BLACK);
        colorList.add(Color.YELLOW);
        // create Poly Line options for all trails
        final List<PolylineOptions> polylineOptionsList = new ArrayList<>();
        for (int trailIndex=0; trailIndex < trailList.size(); trailIndex++){
            final PolylineOptions polylineOptions = new PolylineOptions()
                    .addAll(trailList.get(trailIndex))
                    .color(colorList.get(trailIndex))
                    .width(3f);
            polylineOptionsList.add(polylineOptions);
        }

        IconFactory iconFactory = IconFactory.getInstance(Routing_Activity.this);
        final Icon icon = iconFactory.fromResource(R.drawable.flag);


        mapview.getMapAsync(new
                                    OnMapReadyCallback() {
                                        @Override
                                        public void onMapReady(MapboxMap mapboxMap) {
                                            mapboxMap.getUiSettings().setZoomControlsEnabled(true);
                                            mapboxMap.getUiSettings().setCompassEnabled(true);
                                            mapboxMap.getUiSettings().setAllGesturesEnabled(true);
                                            mapboxMap.setCameraPosition( new CameraPosition.Builder()
                                                    .zoom(11)
                                                    .target(center)
                                                    .tilt(10)
                                                    .build());
                                            mapboxMap.removeAnnotations();
                                            mapboxMap.addMarker(departureMarkerOptions);
                                            for (int i=0; i<polylineOptionsList.size(); i++){
                                                mapboxMap.addPolyline(polylineOptionsList.get(i));
                                            }
                                            mapboxMap.addMarker(destinationMarkerOptions.icon(icon));
                                            LatLngBounds latLngBounds = new LatLngBounds.Builder()
                                                    .include(depCoord)
                                                    .include(desCoord)
                                                    .build();
                                            mapboxMap.easeCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100), 1000);
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
    // helper function to calculate average of two coords
    LatLng average(LatLng c1, LatLng c2){
        LatLng result = new LatLng();
        result.setLatitude((c1.getLatitude()+c2.getLatitude())/2);
        result.setLongitude((c1.getLongitude()+c2.getLongitude())/2);
        return result;
    }
}
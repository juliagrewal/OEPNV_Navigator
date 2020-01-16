package wada1028.info3.oepnv_navigator.ui.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import wada1028.info3.oepnv_navigator.Leg;
import wada1028.info3.oepnv_navigator.R;
import wada1028.info3.oepnv_navigator.ui.CustomRouteListAdapter;
import wada1028.info3.oepnv_navigator.ui.db.Route;
import wada1028.info3.oepnv_navigator.ui.db.RouteDB;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private List<String> halteList = new ArrayList<>();
    private AutoCompleteTextView autoCompleteTextViewStart;
    private AutoCompleteTextView autoCompleteTextViewZiel;
    private String startHalt;
    private String zielHalt;
    private HashMap<String,String> stopIDList = new HashMap<>();

    private ListView listView;
    private List<Route> routeList;
    private CustomRouteListAdapter customRouteListAdapter;
    private RouteDB routeDB = null;

    final private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    final private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    private String startDate;
    private String startTime;
    private TextView dateTimeTextView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        Button suchenButton = (Button) root.findViewById(R.id.button_suche);
        suchenButton.setOnClickListener(this);
        autoCompleteTextViewStart = root.findViewById(R.id.autoCompleteTextView_Starthaltestelle);
        autoCompleteTextViewZiel = root.findViewById(R.id.autoCompleteTextView_zielhaltestelle);


        autoCompleteTextViewStart.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (autoCompleteTextViewStart.getText().length() >= 3) {
                    jsonParse(autoCompleteTextViewStart);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        autoCompleteTextViewZiel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (autoCompleteTextViewZiel.getText().length() >= 3) {
                    jsonParse(autoCompleteTextViewZiel);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        listView = (ListView)(root.findViewById(R.id.route_listView));

        // get database
        routeDB = new RouteDB(getContext());
        routeList = new ArrayList<Route>();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                Intent intentHalte = new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(), LegListView.class);

                Route actRoute = routeList.get(position);

                intentHalte.putExtra(LegListView.KEY_Start,actRoute.depName);
                intentHalte.putExtra(LegListView.KEY_Ziel,actRoute.destName);
                intentHalte.putExtra(LegListView.KEY_Start_ID,actRoute.depID);
                intentHalte.putExtra(LegListView.KEY_Ziel_ID,actRoute.destID);
                intentHalte.putExtra(LegListView.KEY_Time,startTime);
                intentHalte.putExtra(LegListView.KEY_Date,startDate);
                startActivity(intentHalte);
            }


        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {

                final int actPosition = position;

                // Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Löschen Bestätigen");
                builder.setMessage("Wollen Sie die gespeicherte Anfrage wirklich löschen ?");
                builder.setCancelable(false);
                builder.setPositiveButton("Löschen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // delete route from route DB
                        String depName = ((Route)(routeList.get(actPosition))).depName;
                        String destName = ((Route)(routeList.get(actPosition))).destName;
                        routeDB.deleteRoute(depName, destName);
                        routeList = routeDB.getAllRoutes();
                        customRouteListAdapter = new CustomRouteListAdapter(getContext(), routeList);
                        listView.setAdapter(customRouteListAdapter);
                    }
                });

                builder.setNegativeButton("Abbruch", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });

                builder.show();


                return true;
            }

        });

        FavOnClickListener.homeFragment = this;

        //Initialize Date and Time
        startDate = getCurrentHumanReadableDate();
        startTime = getCurrentHumanReadableTime();

        //Date Time Dialog
        dateTimeTextView = root.findViewById(R.id.textViewDateTime);

        dateTimeTextView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    DateTimeDialog dateTimeDialog = new DateTimeDialog();
                                                    dateTimeDialog.showDialog(getActivity());
                                                }
                                            }
        );

        return root;
    }

    private void jsonParse(AutoCompleteTextView aCTextView) {
        Log.i("METHODE", "in jsonParse");
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "http://smartmmi.demo.mentz.net/smartmmi/XML_STOPFINDER_REQUEST?outputFormat=rapidJson&type_sf=any&name_sf=" + aCTextView.getText().toString();
        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("METHODE", "in OnResponse");
                try {
                    JSONArray jsonArray = response.getJSONArray("locations");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject location = jsonArray.getJSONObject(i);
                        String name = location.getString("name");
                        String id = location.getString("id");
                        String type = location.getString("type");
                        if (type.equals("stop")) {
                            if (!halteList.contains(name)) {
                                halteList.add(name);
                                stopIDList.put(name, id);
                                Log.i("METHODE", "" + name + " " + id);
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
        queue.add(objectRequest);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, halteList);
        aCTextView.setAdapter(arrayAdapter);


    }

    @Override
    // on click of search button
    public void onClick(View view) {

        startHalt = autoCompleteTextViewStart.getText().toString();
        zielHalt = autoCompleteTextViewZiel.getText().toString();
        // set test values if user does not specify stops
        if ((startHalt==null) || (startHalt.length()==0)) {
            startHalt = "Hauptbahnhof, Karlsruhe";
            stopIDList.put(startHalt, "de:08212:90");
        }
        if ((zielHalt==null) || (zielHalt.length()==0)){
            zielHalt = "Marktplatz, Karlsruhe";
            stopIDList.put(zielHalt,"de:08212:1");
        }

        // store route in route DB
        routeDB.insertRoute(startHalt, stopIDList.get(startHalt), zielHalt, stopIDList.get(zielHalt));
        customRouteListAdapter.notifyDataSetChanged();

        Intent intentHalte = new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(), LegListView.class);

        intentHalte.putExtra(LegListView.KEY_Start,startHalt);
        intentHalte.putExtra(LegListView.KEY_Ziel,zielHalt);
        if(stopIDList.containsKey(startHalt)&&stopIDList.containsKey(zielHalt)){
            intentHalte.putExtra(LegListView.KEY_Start_ID,stopIDList.get(startHalt));
            intentHalte.putExtra(LegListView.KEY_Ziel_ID,stopIDList.get(zielHalt));
            intentHalte.putExtra(LegListView.KEY_Time,startTime);
            intentHalte.putExtra(LegListView.KEY_Date,startDate);
            startActivity(intentHalte);
        }

    }

    @Override
    public void onDestroy() {
        routeDB.disconnect();
        super.onDestroy();
    }

    public void onResume(){
        super.onResume();

        routeDB.connect();
        routeList = routeDB.getAllRoutes();

        customRouteListAdapter = new CustomRouteListAdapter(getContext(), routeList);
        listView.setAdapter(customRouteListAdapter);

        updateDateTimeTextView();
    }

    public static class FavOnClickListener implements View.OnClickListener {

        final int position;
        final View view;
        static HomeFragment homeFragment;

        public FavOnClickListener(int position, View view){
            this.position = position;
            this.view = view;
        }

        @Override
        public void onClick(View view){
            Log.d("onclick","FAV-button on click. Position:"+position);
            Route actRoute = homeFragment.routeList.get(position);
            String depName = actRoute.depName;
            String destName = actRoute.destName;

            String isFav = actRoute.isFav;
            if (isFav.equals("N")){
                homeFragment.routeDB.setRouteFav(depName, destName, true);
            } else {
                homeFragment.routeDB.setRouteFav(depName, destName, false);
            }
            homeFragment.routeList = homeFragment.routeDB.getAllRoutes();

            homeFragment.customRouteListAdapter = new CustomRouteListAdapter(view.getContext(), homeFragment.routeList);
            homeFragment.listView.setAdapter(homeFragment.customRouteListAdapter);

        }
    }

    public class DateTimeDialog{
        public void showDialog(Activity activity) {
            final Dialog dialog = new Dialog(activity);
            dialog.setTitle("Abfahrtszeit");
            // dialog.setCancelable(true);
            dialog.setContentView(R.layout.date_time_dialog);
            // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            final TimePicker timePicker = (TimePicker) dialog.findViewById(R.id.timePicker1);
            final DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.datePicker1);
            timePicker.setIs24HourView(true);

            Button buttonCancel = (Button) dialog.findViewById(R.id.dateTimeButtonCancel);
            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            Button buttonOk = (Button)dialog.findViewById(R.id.dateTimeButtonOK);
            buttonOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar newDate = new GregorianCalendar(
                            datePicker.getYear(),
                            datePicker.getMonth(),
                            datePicker.getDayOfMonth(),
                            timePicker.getHour(),
                            timePicker.getMinute());
                    startTime = timeFormat.format(newDate);
                    startDate = dateFormat.format(newDate);
                    updateDateTimeTextView();

                    dialog.cancel();
                }
            });

            dialog.show();
        }
    }

    private void updateDateTimeTextView() {
        dateTimeTextView.setText( startTime + " Uhr " + startDate);
    }

    private static String getCurrentHumanReadableDate() {
        Date today = Calendar.getInstance().getTime();
        return dateFormat.format(today);
    }

    private static String getCurrentHumanReadableTime() {
        Date today = Calendar.getInstance().getTime();
        return timeFormat.format(today);
    }

}
package wada1028.info3.oepnv_navigator.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import wada1028.info3.oepnv_navigator.R;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private List<String> halteList = new ArrayList<>();
    private AutoCompleteTextView autoCompleteTextViewStart;
    private AutoCompleteTextView autoCompleteTextViewZiel;
    private String startHalt;
    private String zielHalt;
    private HashMap<String,String> stopIDList = new HashMap<>();


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

        Intent intentHalte = new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(),ListView.class);
        intentHalte.putExtra(ListView.KEY_Start,startHalt);
        intentHalte.putExtra(ListView.KEY_Ziel,zielHalt);
        if(stopIDList.containsKey(startHalt)&&stopIDList.containsKey(zielHalt)){
            intentHalte.putExtra(ListView.KEY_Start_ID,stopIDList.get(startHalt));
            intentHalte.putExtra(ListView.KEY_Ziel_ID,stopIDList.get(zielHalt));
            startActivity(intentHalte);
        }

    }

}
package wada1028.info3.oepnv_navigator.ui.home;

import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import wada1028.info3.oepnv_navigator.R;
import wada1028.info3.oepnv_navigator.ui.Dialog;

import static java.lang.String.valueOf;

public class HomeFragment extends Fragment {

    private List<String> halteList = new ArrayList<>();
    private AutoCompleteTextView autoCompleteTextViewStart;
    private AutoCompleteTextView autoCompleteTextViewZiel;
    private Integer hour;
    private Integer minute;
    private Integer day;
    private Integer month;
    private Integer year;
    private String startHalt;
    private String zielHalt;
    private HashMap<String, String> stopIDList = new HashMap<>();
    private Button suchenButton;
    private TextView textViewTime;
    private TextView textViewDate;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        suchenButton = root.findViewById(R.id.button_suche);


        TimePickerDialog timeDialog;
        timeDialog = new

                TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                textViewTime.setText("" + selectedHour + ":" + selectedMinute);
            }
        }, hour, minute, true);//Yes 24 hour time
        timeDialog.setTitle("Select Time");
        timeDialog.show();

        textViewTime = root.findViewById(R.id.textViewTime);
        textViewTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewIn) {

            }
        });

        textViewDate = root.findViewById(R.id.textViewDate);


        autoCompleteTextViewStart = root.findViewById(R.id.autoCompleteTextView_Starthaltestelle);
        autoCompleteTextViewZiel = root.findViewById(R.id.autoCompleteTextView_zielhaltestelle);
        //timePicker = (TimePicker) root.findViewById(R.id.timePicker);
        //timePicker.setIs24HourView(true);
        //datePicker = (DatePicker) root.findViewById(R.id.datePicker);
        //datePicker.setCalendarViewShown(false);

        //Datum in TextView schreiben
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        textViewTime.setText(timeFormat.format(calendar.getTime()));

        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        int year = calendar.get(yearFormat.getCalendar().YEAR);
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        int monthInt = calendar.get(monthFormat.getCalendar().MONTH);
        monthInt++;
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
        int dayInt = calendar.get(dayFormat.getCalendar().DAY_OF_MONTH);

        String monthString = valueOf(monthInt);
        String dayString = valueOf(dayInt);

        if (monthInt < 10) {
            monthString = "0" + monthInt;
            Log.i("LUISA", "Month: " + monthString);
        }

        if (dayInt < 10) {
            dayString = "0" + dayInt;
            Log.i("LUISA", "Date: " + dayString);
        }

        String dateString = "" + dayString + ". " + monthString + ". " + year;

        textViewDate.setText(dateString);


        autoCompleteTextViewStart.addTextChangedListener(new

                                                                 TextWatcher() {
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

        autoCompleteTextViewZiel.addTextChangedListener(new

                                                                TextWatcher() {
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


    public String dateLinkParse(int date, int month, int year) {
        String monthString = valueOf(month);
        String dateString = valueOf(date);

        if (month < 10) {
            monthString = "0" + month;
            //Log.i("LUISA", "Month: " + monthString);
        }

        if (date < 10) {
            dateString = "" + 0 + date;
            //Log.i("LUISA", "Date: " + dateString);
        }

        String dateMonthYear = "" + year + monthString + dateString;
        //Log.i("LUISA", "methodeDatum: " +dateMonthYear);
        return dateMonthYear;

    }

    public String timeLinkParse(int minute, int hour) {
        String minuteString = valueOf(minute);
        String hourString = valueOf(hour);

        if (hour < 10) {
            hourString = "" + 0 + hour;
            //Log.i("LUISA", "Hour: " + hourString);
        }

        if (minute < 10) {
            minuteString = "" + 0 + minute;
            //Log.i("LUISA", "minute: " + minuteString);
        }


        String minuteHour = hourString + minuteString;
        //Log.i("LUISA", "Minute'Hour: " + minuteHour);
        return minuteHour;
    }



}
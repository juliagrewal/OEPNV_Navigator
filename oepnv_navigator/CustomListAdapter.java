package wada1028.info3.oepnv_navigator;

import android.app.Activity;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import wada1028.info3.oepnv_navigator.ui.home.Routing_Activity;


public class CustomListAdapter extends ArrayAdapter <HashMap> {

        private List<HashMap> journeyList;


    public CustomListAdapter(Activity context, List<HashMap> journeyList){
        super(context, R.layout.listview_one_row,journeyList);
        this.journeyList = journeyList;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //super.getView(position, convertView, parent);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_one_row, parent, false);
        }
        TextView textViewDepLoc = (TextView) convertView.findViewById(R.id.textViewDepLoc);
        TextView textViewDepTim = (TextView) convertView.findViewById(R.id.textViewDepTim);
        TextView textViewArrLoc = (TextView) convertView.findViewById(R.id.textViewArrLoc);
        TextView textViewArrTim = (TextView) convertView.findViewById(R.id.textViewArrTim);
        TextView textViewTransport = (TextView)convertView.findViewById(R.id.textViewTransport);
        //Liste auslesen
        //Map for Times
        HashMap legHashMap = (HashMap) journeyList.get(position).get("legTime");
        int lengthLegHashMap = legHashMap.size();
        //Map for Transportation
        HashMap transHashMap = (HashMap)journeyList.get(position).get("transportation");
        int lengthTransHashMap = transHashMap.size();
        String transportModes = "";

        //Departure Output
        String departureTimeString =(String) legHashMap.get("departureTimePlanned0");
        textViewDepTim.setText(Routing_Activity.dateParse(departureTimeString));
        textViewDepLoc.setText(Routing_Activity.startHalteString);
        //Transportation
        for(int i =0;i<lengthTransHashMap;i++){
            if(i>0){
                transportModes = transportModes + ", ";
            }
            transportModes = transportModes + transHashMap.get("name"+i);
        }
        textViewTransport.setText(transportModes);

        //Arrival Output
        String arrivalTimeString = (String) legHashMap.get("arrivalTimePlanned"+((lengthLegHashMap/2)-1));
        textViewArrTim.setText(Routing_Activity.dateParse(arrivalTimeString));
        textViewArrLoc.setText(Routing_Activity.zielHalteString);

        return convertView;
    }
}

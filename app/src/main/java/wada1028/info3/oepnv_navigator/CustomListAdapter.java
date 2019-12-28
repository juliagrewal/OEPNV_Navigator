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
    //to reference teh activity
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
        //Liste auslesen

        HashMap legHashMap = (HashMap) journeyList.get(position).get("legTime");
        String departureTimeString =(String) legHashMap.get("departureTimePlanned0");
        textViewDepTim.setText(Routing_Activity.dateParse(departureTimeString));
        int lengthLegHashMap = legHashMap.size();
        String arrivalTimeString = (String) legHashMap.get("arrivalTimePlanned"+((lengthLegHashMap/2)-1));
        textViewArrTim.setText(Routing_Activity.dateParse(arrivalTimeString));

        return convertView;
    };
}

package wada1028.info3.oepnv_navigator;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.List;

import wada1028.info3.oepnv_navigator.R;
import wada1028.info3.oepnv_navigator.ui.home.ListView;

public class CustomLegListAdapter extends ArrayAdapter <List> {
    private List<Leg> legList;

    public CustomLegListAdapter(Activity context, List<Leg> legList) {
        super(context, R.layout.leglistview_one_row);
        this.legList = legList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.leglistview_one_row,parent,false);
        }
        TextView textViewDepLocLeg = (TextView)convertView.findViewById(R.id.textViewDepLocLeg);
        TextView textViewDepTimLeg = (TextView)convertView.findViewById(R.id.textViewDepTimLeg);
        TextView textViewArrLocLeg = (TextView)convertView.findViewById(R.id.textViewArrLocLeg);
        TextView textViewArrTimLeg = (TextView)convertView.findViewById(R.id.textViewArrTimLeg);
        TextView textViewTransport = (TextView)convertView.findViewById(R.id.textViewTransportation);
        //Liste auslesen
        Leg actLeg = legList.get(position);
        textViewDepLocLeg.setText(actLeg.depName);
        textViewDepTimLeg.setText(actLeg.depTime);
        textViewArrLocLeg.setText(actLeg.desName);
        textViewArrTimLeg.setText(actLeg.desTime);
        textViewTransport.setText(actLeg.transMode);
        return convertView;

    }
}

package wada1028.info3.oepnv_navigator.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;

import java.util.List;

import wada1028.info3.oepnv_navigator.R;
import wada1028.info3.oepnv_navigator.ui.db.Route;
import wada1028.info3.oepnv_navigator.ui.home.HomeFragment.FavOnClickListener;
import wada1028.info3.oepnv_navigator.ui.home.Routing_Activity;

public class CustomRouteListAdapter extends ArrayAdapter<Route> {
    private List<Route> routeList;

    public CustomRouteListAdapter(Context context, List<Route> routeList) {
        super(context, R.layout.routelistview_one_row,routeList);
        this.routeList = routeList;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.routelistview_one_row,parent,false);
        }
        TextView textViewDepNameRoute = (TextView)convertView.findViewById(R.id.textViewDepNameRoute);
        TextView textViewDestNameRoute = (TextView)convertView.findViewById(R.id.textViewDestNameRoute);

        ImageButton imageButton = (ImageButton)convertView.findViewById(R.id.textViewIsFavRouteImg);
        imageButton.setClickable(false);
        imageButton.setFocusable(false);


        //Liste auslesen
        Route actRoute = routeList.get(position);
        textViewDepNameRoute.setText(actRoute.depName);
        textViewDestNameRoute.setText(actRoute.destName);
        IconFactory iconFactory = IconFactory.getInstance(getContext());
        if (actRoute.isFav.equals("N")){
            final Icon emptyStar = iconFactory.fromResource(R.drawable.emptyStar);
        } else {
            imageButton.setImageResource(R.drawable.yellow_star);
        }
        imageButton.setOnClickListener(new FavOnClickListener(position, convertView));
        return convertView;
    }
}

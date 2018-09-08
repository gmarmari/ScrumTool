package gr.eap.dxt.tools;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import gr.eap.dxt.R;

/**
 * Created by GEO on 5/2/2017.
 */

public class SpinnerSimpleAdapter extends ArrayAdapter<String> {

    private Context context;
    private int resource;
    private ArrayList<String> items;

    public SpinnerSimpleAdapter(Context context, int resource, ArrayList<String> items) {
        super(context, resource, items);
        this.context = context;
        this.resource = resource;
        this.items = items;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(resource, parent, false);
        }

        if (items == null) {
            AppShared.writeErrorToLogString(getClass().toString(), "items == null");
            return convertView;
        }

        String item = items.get(position);
        if (item == null ) {
            AppShared.writeErrorToLogString(getClass().toString(), "item == null");
            return convertView;
        }
        if (item.isEmpty()) {
            return convertView;
        }

        TextView textView = (TextView) convertView.findViewById(R.id.text1);
        textView.setText(item);


        return convertView;
    }


}


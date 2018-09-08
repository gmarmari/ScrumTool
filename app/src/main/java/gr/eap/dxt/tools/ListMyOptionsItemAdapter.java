package gr.eap.dxt.tools;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import gr.eap.dxt.R;

/**
 * Created by GEO on 21/1/2017.
 */

public class ListMyOptionsItemAdapter extends ArrayAdapter<MyOtionsItem> {

    private Context context;
    private ArrayList<MyOtionsItem> items;
    private int resource;


    public ListMyOptionsItemAdapter(Context context, ArrayList<MyOtionsItem> items) {
        super(context, R.layout.list_my_options_item, items != null ? items : new ArrayList<MyOtionsItem>());
        this.context = context;
        this.items = items != null ? items : new ArrayList<MyOtionsItem>();
        resource = R.layout.list_my_options_item;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(resource, parent, false);
        }

        if (items == null) {
            AppShared.writeErrorToLogString(getClass().toString(), "items == null");
            return convertView;
        }
        if (position < 0) {
            AppShared.writeErrorToLogString(getClass().toString(), "position< 0");
            return convertView;
        }
        if (position >= items.size()) {
            AppShared.writeErrorToLogString(getClass().toString(), "position >= items.size()");
            return convertView;
        }
        MyOtionsItem item = items.get(position);
        if (item == null) {
            AppShared.writeErrorToLogString(getClass().toString(), "item == null");
            return convertView;
        }

        TextView textView = (TextView) convertView.findViewById(R.id.my_option);
        if (textView != null) {
            textView.setText(item.getName() != null ? item.getName() : "");
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.my_icon);
        if (imageView != null){
            if (item.getImageResource() != null){
                try {
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageResource(item.getImageResource());
                }catch (Exception e){
                    e.printStackTrace();
                    AppShared.writeErrorToLogString(getClass().toString(), e.toString());
                    imageView.setVisibility(View.GONE);
                }
            }else{
                imageView.setVisibility(View.GONE);
            }
        }


        return convertView;
    }
}

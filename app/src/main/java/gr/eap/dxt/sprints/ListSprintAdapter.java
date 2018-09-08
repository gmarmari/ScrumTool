package gr.eap.dxt.sprints;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import gr.eap.dxt.R;
import gr.eap.dxt.tools.AppShared;
import gr.eap.dxt.tools.MyColor;

/**
 * Created by GEO on 12/2/2017.
 */

class ListSprintAdapter extends ArrayAdapter<Sprint> {

    private Context context;
    private ArrayList<Sprint> items;
    private int resource;

    ListSprintAdapter(Context context, ArrayList<Sprint> items) {
        super(context, R.layout.list_sprint_item, items != null ? items : new ArrayList<Sprint>());
        this.context = context;
        this.items = items != null ? items : new ArrayList<Sprint>();
        resource = R.layout.list_sprint_item;
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
        Sprint sprint = items.get(position);
        return getSprintViewForList(context, sprint, convertView, parent);
    }

    static @NonNull View getSprintViewForList(Context context, Sprint sprint, View convertView, @NonNull ViewGroup parent){
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.list_sprint_item, parent, false);
        }

        if (sprint == null) {
            AppShared.writeErrorToLogString(ListSprintAdapter.class.toString(), "sprint == null");
            return convertView;
        }

        TextView dateTextView = (TextView) convertView.findViewById(R.id.sprint_date);
        if (dateTextView != null){
            SimpleDateFormat dateFormat = new SimpleDateFormat(context.getString(R.string.my_date_format_sprint), Locale.getDefault());

            String date = "";
            if (sprint.getStartDate() != null){
                date += dateFormat.format(sprint.getStartDate());
            }
            if (sprint.getEndDate() != null){
                date += " - ";
                date += dateFormat.format(sprint.getEndDate());
            }
            if (sprint.getDuration() != null && sprint.getDuration() > 0){
                date += "\n" + sprint.getDuration() + " ";
                if (sprint.getDuration() == 1){
                    date += context.getString(R.string.day).toLowerCase();
                }else{
                    date += context.getString(R.string.days).toLowerCase();
                }
            }

            dateTextView.setText(date);
        }

        TextView detailsTextView = (TextView) convertView.findViewById(R.id.sprint_details);
        if (detailsTextView != null){
            String details = "";
            if (sprint.getName() != null && !sprint.getName().isEmpty()){
                details += sprint.getName();
            }
            if (sprint.getDescription() != null && !sprint.getDescription().isEmpty()){
                if (!details.isEmpty()) details += "\n";
                details += sprint.getDescription();
            }

            detailsTextView.setText(details);
        }

        TextView statusTextView = (TextView) convertView.findViewById(R.id.status);
        if (statusTextView != null){
            String status = SprintStatus.getSprintStatus(context, sprint.getStatus());
            Integer color = SprintStatus.getSprintStatusColor(context, sprint.getStatus());
            if (color == null){
                color = MyColor.getColorAccordingToAndroidVersion(context, R.color.color_eap_logo_dark_blue);
            }
            statusTextView.setText(status != null ? status : "");
            statusTextView.setTextColor(color);
        }

        return convertView;
    }
}

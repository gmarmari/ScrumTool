package gr.eap.dxt.backlog;

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
import gr.eap.dxt.tools.AppShared;
import gr.eap.dxt.tools.MyColor;

/**
 * Created by GEO on 12/2/2017.
 */

class ListBacklogAdapter extends ArrayAdapter<Backlog> {

    private Context context;
    private ArrayList<Backlog> items;
    private int resource;

    ListBacklogAdapter(Context context, ArrayList<Backlog> items) {
        super(context, R.layout.list_backlog_item, items != null ? items : new ArrayList<Backlog>());
        this.context = context;
        this.items = items != null ? items : new ArrayList<Backlog>();
        resource = R.layout.list_backlog_item;
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
        Backlog backlog = items.get(position);
        return getBackLogViewForList(context, backlog, convertView, parent);
    }

    static @NonNull View getBackLogViewForList(Context context, Backlog backlog, View convertView, @NonNull ViewGroup parent){
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.list_backlog_item, parent, false);
        }

        if (backlog == null) {
            AppShared.writeErrorToLogString(ListBacklogAdapter.class.toString(), "backlog == null");
            return convertView;
        }

        TextView priorityTextView = (TextView) convertView.findViewById(R.id.priority);
        if (priorityTextView != null){
            String priority = Priority.getPriority(context, backlog.getPriority());
            String text = null;
            if (priority != null && ! priority.isEmpty()){
                text = context.getString(R.string.priority) + ": " + priority;
            }
            if (text != null){
                Integer color = Priority.getPriorityColor(context, backlog.getPriority());
                if (color == null){
                    color = MyColor.getColorAccordingToAndroidVersion(context, R.color.color_eap_logo_dark_blue);
                }
                priorityTextView.setVisibility(View.VISIBLE);
                priorityTextView.setText(text);
                priorityTextView.setTextColor(color);
            }else{
                priorityTextView.setVisibility(View.GONE);
            }

        }

        TextView typeTextView = (TextView) convertView.findViewById(R.id.type);
        if (typeTextView != null){
            String type = BacklogType.getBacklogType(context, backlog.getType());
            typeTextView.setText(type != null ? type : "");
        }

        TextView statusTextView = (TextView) convertView.findViewById(R.id.status);
        if (statusTextView != null){
            String status = BacklogStatus.getBacklogStatus(context, backlog.getStatus());
            Integer color = BacklogStatus.getBacklogStatusColor(context, backlog.getStatus());
            if (color == null){
                color = MyColor.getColorAccordingToAndroidVersion(context, R.color.color_eap_logo_dark_blue);
            }

            statusTextView.setText(status != null ? status : "");
            statusTextView.setTextColor(color);
        }

        TextView detailsTextView = (TextView) convertView.findViewById(R.id.backlog_details);
        if (detailsTextView != null){
            String details = "";

            if (backlog.getName() != null && !backlog.getName().isEmpty()){
                if (!details.isEmpty()) details += "\n";
                details += backlog.getName();
            }
            if (backlog.getDescription() != null && !backlog.getDescription().isEmpty()){
                if (!details.isEmpty()) details += "\n";
                details += context.getResources().getString(R.string.description) + ": " + backlog.getDescription();
            }

            detailsTextView.setText(details);
        }

        TextView durationTextView = (TextView) convertView.findViewById(R.id.backlog_duration);
        if (durationTextView != null){
            String duration = context.getString(R.string.duration);
            if (backlog.getDuration() != null && backlog.getDuration() > 0){
                duration += ": " + backlog.getDuration();
                if (backlog.getDuration() == 1){
                    duration += " " + context.getString(R.string.day).toLowerCase();
                }else{
                    duration += " " + context.getString(R.string.days).toLowerCase();
                }
            }
            durationTextView.setText(duration);
        }

        return convertView;
    }

}

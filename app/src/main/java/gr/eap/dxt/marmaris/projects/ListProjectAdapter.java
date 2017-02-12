package gr.eap.dxt.marmaris.projects;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import gr.eap.dxt.marmaris.R;
import gr.eap.dxt.marmaris.tools.AppShared;

/**
 * Created by GEO on 9/2/2017.
 */

public class ListProjectAdapter extends ArrayAdapter<Project> {

    private Context context;
    private ArrayList<Project> items;
    private int resource;

    public ListProjectAdapter(Context context, ArrayList<Project> items) {
        super(context, R.layout.list_project_item, items != null ? items : new ArrayList<Project>());
        this.context = context;
        this.items = items != null ? items : new ArrayList<Project>();
        resource = R.layout.list_project_item;
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
        Project project = items.get(position);
        if (project == null) {
            AppShared.writeErrorToLogString(getClass().toString(), "project == null");
            return convertView;
        }

        TextView nameTextView = (TextView) convertView.findViewById(R.id.project_name);
        if (nameTextView != null){
            nameTextView.setText(project.getProjectName() != null ? project.getProjectName() : "");
        }

        TextView statusTextView = (TextView) convertView.findViewById(R.id.project_status);
        if (statusTextView != null){
            String status = ProjectStatus.getProjectStatus(context, project.getProjectStatus());
            statusTextView.setText(status != null ? status : "");
        }

        return convertView;
    }
}


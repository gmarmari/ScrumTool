package gr.eap.dxt.persons;

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

/**
 * Created by GEO on 4/2/2017.
 */

public class ListPersonAdapter extends ArrayAdapter<Person> {

    private Context context;
    private ArrayList<Person> items;
    private int resource;

    public ListPersonAdapter(Context context, ArrayList<Person> items) {
        super(context, R.layout.list_person_item, items != null ? items : new ArrayList<Person>());
        this.context = context;
        this.items = items != null ? items : new ArrayList<Person>();
        resource = R.layout.list_person_item;
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
        Person person = items.get(position);
        if (person == null) {
            AppShared.writeErrorToLogString(getClass().toString(), "person == null");
            return convertView;
        }

        TextView detailsTextView = (TextView) convertView.findViewById(R.id.person_details);
        if (detailsTextView != null) {
            String details = "";
            if (person.getName() != null && !person.getName().isEmpty()){
                details += person.getName();
            }
            if (person.getEmail() != null && !person.getEmail().isEmpty()){
                if (person.getName() != null && !person.getName().isEmpty()) details += " <";
                details += person.getEmail();
                if (person.getName() != null && !person.getName().isEmpty()) details += ">";
            }
            detailsTextView.setText(details);
        }

        TextView roleTextView = (TextView) convertView.findViewById(R.id.person_role);
        if (roleTextView != null){
            String role = PersonRole.getPersonRole(context, person.getRole());
            roleTextView.setText(role != null ? role : "");
        }


        return convertView;
    }
}
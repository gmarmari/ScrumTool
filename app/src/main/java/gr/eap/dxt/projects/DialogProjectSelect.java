package gr.eap.dxt.projects;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

import gr.eap.dxt.R;
import gr.eap.dxt.tools.AppShared;

/**
 * Created by GEO on 18/2/2017.
 */

public class DialogProjectSelect extends Dialog {

    public interface Listener {
        void onProjectSelected(Project project);
    }
    private Listener mListener;

    private Context context;
    private ArrayList<Project> projects;

    public DialogProjectSelect(Context context, ArrayList<Project> projects, Listener mListener) {
        super(context, R.style.my_dialog_no_title);
        this.context = context;
        this.projects = projects;
        this.mListener = mListener;
        setContext();
    }

    private void setContext(){
        setContentView(R.layout.dialog_project_select);

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int height = (int) (metrics.heightPixels*0.8);
        int width = (int) (metrics.widthPixels*0.8);

        if ( getWindow() != null){
            getWindow().setLayout(width,height);
        }

        ListView listView = (ListView) findViewById(R.id.my_list_view);
        if (listView != null){
            ListProjectAdapter adapter = new ListProjectAdapter(context, projects);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                    if (projects == null){
                        AppShared.writeErrorToLogString(getClass().toString(), "projects == null");
                        return;
                    }
                    if (position < 0){
                        AppShared.writeErrorToLogString(getClass().toString(), "position < 0");
                        return;
                    }
                    if (position >= projects.size()){
                        AppShared.writeErrorToLogString(getClass().toString(), "position >= projects.size()");
                        return;
                    }

                    dismiss();
                    if (mListener != null){
                        mListener.onProjectSelected(projects.get(position));
                    }
                }
            });
        }

        ImageButton backButton = (ImageButton) findViewById(R.id.back);
        if (backButton != null){
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }
    }
}

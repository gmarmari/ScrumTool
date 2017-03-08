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
import gr.eap.dxt.tools.MyAlertDialog;

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
    private boolean active;

    private ListView listView;

    public DialogProjectSelect(Context context, boolean active, Listener mListener) {
        super(context, R.style.my_dialog_no_title);
        this.context = context;
        this.active = active;
        this.mListener = mListener;
        projects = new ArrayList<>();
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

        setCancelable(false);

        listView = (ListView) findViewById(R.id.my_list_view);
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

    @Override
    public void show() {
        super.show();

        new FirebaseProjectGetAll(context, new FirebaseProjectGetAll.Listener() {
            @Override
            public void onResponse(ArrayList<Project> _projects, String errorMsg) {

                if (errorMsg != null && !errorMsg.isEmpty()){
                    MyAlertDialog.alertError(context, null, errorMsg);
                    return;
                }
                if (_projects == null){
                    MyAlertDialog.alertError(context, null, "projects == null");
                    return;
                }

                if (active){
                    projects = new ArrayList<>();
                    for (Project project: _projects) {
                        if (project != null){
                            String status = project.getStatus() != null ? project.getStatus() : "";
                            if (!status.equals(ProjectStatus.CANCELED) && !status.equals(ProjectStatus.COMPLETED)){
                                projects.add(project);
                            }
                        }
                    }
                }else{
                    projects = _projects;
                }



                if (listView != null) {
                    ListProjectAdapter adapter = new ListProjectAdapter(context, projects);
                    listView.setAdapter(adapter);
                }

            }
        }).execute();
    }
}

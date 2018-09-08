package gr.eap.dxt.sprints;

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

public class DialogSprintSelect extends Dialog {

    public interface Listener {
        void onSprintSelected(Sprint sprint);
    }
    private Listener mListener;

    private Context context;
    private ArrayList<Sprint> sprints;
    private String projectId;
    private boolean active;

    private ListView listView;

    public DialogSprintSelect(Context context, String projectId, boolean active, Listener mListener) {
        super(context, R.style.my_dialog_no_title);
        this.context = context;
        this.projectId = projectId;
        this.active = active;
        this.mListener = mListener;
        sprints = new ArrayList<>();
        setContext();
    }

    private void setContext(){
        setContentView(R.layout.dialog_sprint_select);

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int height = (int) (metrics.heightPixels*0.8);
        int width = (int) (metrics.widthPixels*0.8);

        if ( getWindow() != null){
            getWindow().setLayout(width,height);
        }

        listView = (ListView) findViewById(R.id.my_list_view);
        if (listView != null){
            ListSprintAdapter adapter = new ListSprintAdapter(context, sprints);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                    if (sprints == null){
                        AppShared.writeErrorToLogString(getClass().toString(), "sprints == null");
                        return;
                    }
                    if (position < 0){
                        AppShared.writeErrorToLogString(getClass().toString(), "position < 0");
                        return;
                    }
                    if (position >= sprints.size()){
                        AppShared.writeErrorToLogString(getClass().toString(), "position >= sprints.size()");
                        return;
                    }

                    dismiss();
                    if (mListener != null){
                        mListener.onSprintSelected(sprints.get(position));
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

        if (projectId == null || projectId.isEmpty() || projectId.equals(AppShared.NO_ID)) {
            new FirebaseSprintGetAll(context, new FirebaseSprintGetAll.Listener() {
                @Override
                public void onResponse(ArrayList<Sprint> sprints, String errorMsg) {

                    if (errorMsg != null && !errorMsg.isEmpty()){
                        MyAlertDialog.alertError(context, null, errorMsg);
                        return;
                    }
                    sprintGotten(sprints);

                }
            }).execute();
        }else{
            new FirebaseSprintProjectGet(context, projectId, true, new FirebaseSprintProjectGet.Listener() {
                @Override
                public void onResponse(ArrayList<Sprint> sprints, String errorMsg) {

                    if (errorMsg != null && !errorMsg.isEmpty()){
                        MyAlertDialog.alertError(context, null, errorMsg);
                        return;
                    }
                    sprintGotten(sprints);

                }
            }).execute();
        }
    }

    private void sprintGotten(ArrayList<Sprint> sprints){
        if (sprints == null){
            MyAlertDialog.alertError(context, null, "sprints == null");
            return;
        }


        this.sprints = new ArrayList<>();

        Sprint noSprint = new Sprint();
        noSprint.setName(context.getString(R.string.none));
        noSprint.setSprintId(AppShared.NO_ID);
        noSprint.setProjectId(AppShared.NO_ID);
        noSprint.setStatus(null);
        this.sprints.add(noSprint);

        if (active){
            for (Sprint sprint: sprints) {
                if (sprint != null){
                    String status = sprint.getStatus() != null ? sprint.getStatus() : "";
                    if (!status.equals(SprintStatus.COMPLETED) && !status.equals(SprintStatus.CANCELED)){
                        this.sprints.add(sprint);
                    }
                }
            }
        }else{
            if (!sprints.isEmpty()) {
                this.sprints.addAll(sprints);
            }

        }

        if (listView != null) {
            ListSprintAdapter adapter = new ListSprintAdapter(context, this.sprints);
            listView.setAdapter(adapter);
        }
    }
}

package gr.eap.dxt.sprints;

import android.content.Context;

import java.util.ArrayList;

import gr.eap.dxt.backlog.Backlog;
import gr.eap.dxt.backlog.FirebaseBacklogSprintGet;
import gr.eap.dxt.projects.FirebaseProjectGet;
import gr.eap.dxt.projects.Project;
import gr.eap.dxt.tools.FirebaseCall;

/**
 * Created by GEO on 8/4/2017.
 */

public class FirebaseSprintGetData extends FirebaseCall {

    public interface Listener {
        void onResponse(Project project, ArrayList<Backlog> backlogs, String errorMsg);
    }
    private Listener mListener;

    private Sprint sprint;

    private Project project;
    private ArrayList<Backlog> backlogs;


    public FirebaseSprintGetData(Context context, Sprint sprint, Listener mListener){
        super(context, true);
        this.mListener = mListener;
        this.sprint = sprint;
    }

    public void execute(){
        super.execute();
        getProject();
    }

    private void getProject(){
        if (sprint == null) {
            addErrorNo("sprint == null");
            giveOutput();
            return;
        }

        new FirebaseProjectGet(getContext(), sprint.getProjectId(), false, new FirebaseProjectGet.Listener() {
            @Override
            public void onResponse(Project project, String errorMsg) {

                if (errorMsg != null && !errorMsg.isEmpty()){
                    addErrorNo(errorMsg);
                }

                FirebaseSprintGetData.this.project = project;
                getSprintBacklogs();

            }
        }).execute();
    }

    private void getSprintBacklogs(){
        if (sprint == null) {
            addErrorNo("sprint == null");
            giveOutput();
            return;
        }

        new FirebaseBacklogSprintGet(getContext(), sprint.getSprintId(), false, new FirebaseBacklogSprintGet.Listener() {
            @Override
            public void onResponse(ArrayList<Backlog> backlogs, String errorMsg) {


                if (errorMsg != null && !errorMsg.isEmpty()){
                    addErrorNo(errorMsg);
                }
                FirebaseSprintGetData.this.backlogs = backlogs;
                giveOutput();
            }
        }).execute();

    }



    protected void giveOutput(){
        super.giveOutput();

        if (mListener != null) mListener.onResponse(project, backlogs, getErrorno());
    }

}


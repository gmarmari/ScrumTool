package gr.eap.dxt.backlog;

import android.content.Context;



import gr.eap.dxt.persons.Person;
import gr.eap.dxt.persons.FirebasePersonGet;
import gr.eap.dxt.projects.FirebaseProjectGet;
import gr.eap.dxt.projects.Project;
import gr.eap.dxt.sprints.FirebaseSprintGet;
import gr.eap.dxt.sprints.Sprint;
import gr.eap.dxt.tools.FirebaseCall;

/**
 * Created by GEO on 8/4/2017.
 */

public class FirebaseBacklogGetData extends FirebaseCall {

    public interface Listener {
        void onResponse(Person person, Sprint sprint, Project project, String errorMsg);
    }
    private Listener mListener;

    private Backlog backlog;

    private Person person;
    private Sprint sprint;
    private Project project;


    public FirebaseBacklogGetData(Context context, Backlog backlog, Listener mListener){
        super(context, true);
        this.mListener = mListener;
        this.backlog = backlog;
    }

    public void execute(){
        super.execute();
        getPerson();
    }

    private void getPerson(){
        if (backlog == null) {
            addErrorNo("backlog == null");
            giveOutput();
            return;
        }
        new FirebasePersonGet(getContext(), backlog.getPersonId(), false, new FirebasePersonGet.Listener() {
            @Override
            public void onResponse(Person person, String errorMsg) {

                if (errorMsg != null && !errorMsg.isEmpty()){
                    addErrorNo(errorMsg);
                }

                FirebaseBacklogGetData.this.person = person;
                getSprint();
            }
        }).execute();
    }

    private void getSprint(){
        if (backlog == null) {
            addErrorNo("backlog == null");
            giveOutput();
            return;
        }
        new FirebaseSprintGet(getContext(), backlog.getSprintId(), false, new FirebaseSprintGet.Listener() {
            @Override
            public void onResponse(Sprint sprint, String errorMsg) {

                if (errorMsg != null && !errorMsg.isEmpty()){
                    addErrorNo(errorMsg);
                }

                FirebaseBacklogGetData.this.sprint = sprint;
                getProject();

            }
        }).execute();
    }

    private void getProject(){
        if (backlog == null) {
            addErrorNo("backlog == null");
            giveOutput();
            return;
        }
        new FirebaseProjectGet(getContext(), backlog.getProjectId(), false, new FirebaseProjectGet.Listener() {
            @Override
            public void onResponse(Project project, String errorMsg) {

                if (errorMsg != null && !errorMsg.isEmpty()){
                    addErrorNo(errorMsg);
                }

                FirebaseBacklogGetData.this.project = project;
                giveOutput();

            }
        }).execute();
    }

    protected void giveOutput(){
        super.giveOutput();

        if (mListener != null) mListener.onResponse(person, sprint, project, getErrorno());
    }

}

package gr.eap.dxt.projects;

import android.content.Context;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import gr.eap.dxt.tools.FirebaseCall;

/**
 * Created by GEO on 11/2/2017.
 */

class FirebaseProjectEdit extends FirebaseCall {

    public interface Listener {
        void onResponse(String errorMsg);
    }
    private Listener mListener;

    private Project project;

    FirebaseProjectEdit(Context context, Project project, Listener mListener){
        super(context, true);
        this.project = project;
        this.mListener = mListener;
    }

    public void execute(){
        super.execute();

        if (project == null){
            addErrorNo("project == null");
            giveOutput();
            return;
        }
        if (project.getProjectId() == null){
            addErrorNo("project.getProjectId() == null");
            giveOutput();
            return;
        }
        if (project.getProjectId().isEmpty()){
            addErrorNo("project.getProjectId().isEmpty()");
            giveOutput();
            return;
        }

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Project.FIREBASE_LIST);
        if (mDatabase.child(project.getProjectId()) == null){
            addErrorNo("mDatabase.child(projectId) == null");
            giveOutput();
            return;
        }

        Map<String,Object> taskMap = new HashMap<>();
        taskMap.put(Project.NAME, project.getName());
        taskMap.put(Project.STATUS, project.getStatus());
        taskMap.put(Project.DESCRIPTION, project.getDescription());
        taskMap.put(Project.START_DATE, project.getStartDate());

        mDatabase.child(project.getProjectId()).updateChildren(taskMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                if(databaseError != null){
                    addErrorNo(databaseError.getMessage());
                }

                giveOutput();

            }
        });
    }

    protected void giveOutput(){
        super.giveOutput();

        if (isCanceled()) return;
        if (mListener != null) mListener.onResponse(getErrorno() );
    }

}
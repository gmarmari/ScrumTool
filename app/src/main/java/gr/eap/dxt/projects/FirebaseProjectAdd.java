package gr.eap.dxt.projects;

import android.content.Context;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import gr.eap.dxt.tools.FirebaseCall;

/**
 * Created by GEO on 10/2/2017.
 */

class FirebaseProjectAdd extends FirebaseCall {

    interface Listener {
        void onResponse(String errorMsg);
    }
    private Listener mListener;

    private Project project;

    FirebaseProjectAdd(Context context, Project project, Listener mListener){
        super(context, true);
        this.project = project;
        this.mListener = mListener;
    }

    public void execute(){
        super.execute();

        if (project == null) {
            addErrorNo("project == null");
            giveOutput();
            return;
        }
        if (project.getName() == null) {
            addErrorNo("project.getName() == null");
            giveOutput();
            return;
        }
        if (project.getName().isEmpty()) {
            addErrorNo("project.getName().isEmpty()");
            giveOutput();
            return;
        }

        new FirebaseProjectGetByName(getContext(), project.getName(), false, new FirebaseProjectGetByName.Listener() {
            @Override
            public void onResponse(Project project, String errorMsg) {

                if (project == null){
                    addProject();
                }else{
                    addProject();
                }

            }
        }).execute();
    }

    private void addProject() {
        if (project == null) {
            addErrorNo("project == null");
            giveOutput();
            return;
        }
        if (project.getName() == null) {
            addErrorNo("project.getName() == null");
            giveOutput();
            return;
        }
        if (project.getName().isEmpty()) {
            addErrorNo("project.getName().isEmpty()");
            giveOutput();
            return;
        }

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String key = mDatabase.child(Project.FIREBASE_LIST).push().getKey();
        project.setProjectId(key);
        mDatabase.child(Project.FIREBASE_LIST).child(key).setValue(project, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    addErrorNo(databaseError.getMessage());
                }

                giveOutput();
            }
        });
    }

    protected void giveOutput(){
        super.giveOutput();

        if (isCanceled()) return;
        if (mListener != null) mListener.onResponse(getErrorno());
    }

}

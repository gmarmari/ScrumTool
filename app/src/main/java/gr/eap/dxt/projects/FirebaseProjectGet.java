package gr.eap.dxt.projects;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import gr.eap.dxt.tools.FirebaseCall;
import gr.eap.dxt.tools.FirebaseParse;

/**
 * Created by GEO on 5/3/2017.
 */

public class FirebaseProjectGet extends FirebaseCall {

    public interface Listener {
        @SuppressWarnings("UnusedParameters")
        void onResponse(Project project, String errorMsg);
    }
    private Listener mListener;
    private String projectId;

    public FirebaseProjectGet(Context context, String projectId, boolean notify, Listener mListener){
        super(context, notify);
        this.mListener = mListener;
        this.projectId = projectId;
    }

    public void execute(){
        super.execute();

        if (projectId == null){
            addErrorNo("projectId == null");
            giveOutput(null);
            return;
        }
        if (projectId.isEmpty()){
            addErrorNo("projectId.isEmpty()");
            giveOutput(null);
            return;
        }

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Project.FIREBASE_LIST);
        mDatabase.child(projectId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null) {
                    addErrorNo("dataSnapshot == null");
                    giveOutput(null);
                    return;
                }
                if (dataSnapshot.getChildren() == null) {
                    addErrorNo("dataSnapshotgetChildren() == null");
                    giveOutput(null);
                    return;
                }

                if (dataSnapshot.getChildrenCount() == 0) {
                    giveOutput(null);
                    return;
                }

                Project project;
                try {
                    project = new Project();
                    project.setProjectId(dataSnapshot.getKey());
                    project.setName(FirebaseParse.getString(dataSnapshot.child(Project.NAME)));
                    project.setDescription(FirebaseParse.getString(dataSnapshot.child(Project.DESCRIPTION)));
                    project.setStatus(FirebaseParse.getString(dataSnapshot.child(Project.STATUS)));
                    project.setStartDate(FirebaseParse.getDate(dataSnapshot.child(Project.START_DATE)));

                } catch (Exception e) {
                    project = null;
                    addErrorNo(e.toString());
                }

                giveOutput(project);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (databaseError != null) {
                    addErrorNo(databaseError.getMessage());
                }
                giveOutput(null);
            }
        });
    }

    private void giveOutput(Project project){
        super.giveOutput();

        if (isCanceled()) return;
        if (mListener != null) mListener.onResponse(project, getErrorno());
    }

}


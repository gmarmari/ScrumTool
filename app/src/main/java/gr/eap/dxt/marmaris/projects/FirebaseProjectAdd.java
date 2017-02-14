package gr.eap.dxt.marmaris.projects;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import gr.eap.dxt.marmaris.tools.FirebaseCall;

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
        super(context, true, true);
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

        // Check if a project with the same name exists
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Project.FIREBASE_LIST);
        mDatabase.orderByChild(Project.NAME).equalTo(project.getName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    if (dataSnapshot.getChildren() != null) {
                        if (dataSnapshot.getChildrenCount() > 0) {
                            addErrorNo("A project with this name exists allready");
                            giveOutput();
                            return;
                        }
                    }
                }

                addProject();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (databaseError != null) {
                    addErrorNo(databaseError.getMessage());
                }
                giveOutput();
            }
        });
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

        if (getErrorno() != null && !getErrorno().isEmpty()){
            alertError(getErrorno());
        }

        if (mListener != null) mListener.onResponse(getErrorno());
    }

}

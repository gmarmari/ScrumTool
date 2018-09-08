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
 * Created by GEO on 22/2/2017.
 */
 class FirebaseProjectGetByName extends FirebaseCall {

    interface Listener {
        @SuppressWarnings("UnusedParameters")
        void onResponse(Project project, String errorMsg);
    }
    private Listener mListener;

    private String name;

    FirebaseProjectGetByName(Context context, String name, boolean notify, Listener mListener){
        super(context, notify);
        this.name = name;
        this.mListener = mListener;
    }

    public void execute(){
        super.execute();

        if (name == null) {
            addErrorNo("name == null");
            giveOutput();
            return;
        }
        if (name.isEmpty()) {
            addErrorNo("name.isEmpty()");
            giveOutput();
            return;
        }

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Project.FIREBASE_LIST);
        mDatabase.orderByChild(Project.NAME).equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Project project = null;

                if (dataSnapshot != null) {
                    if (dataSnapshot.getChildren() != null) {
                        if (dataSnapshot.getChildrenCount() > 0) {
                            try {
                                project = new Project();
                                project.setProjectId(dataSnapshot.getKey());
                                project.setName(FirebaseParse.getString(dataSnapshot.child(Project.NAME)));
                                project.setDescription(FirebaseParse.getString(dataSnapshot.child(Project.DESCRIPTION)));
                                project.setStatus(FirebaseParse.getString(dataSnapshot.child(Project.STATUS)));
                                project.setStartDate(FirebaseParse.getDate(dataSnapshot.child(Project.START_DATE)));

                                giveOutput(project);

                            }catch (Exception e){
                                addErrorNo(e.toString());
                            }
                        }
                    }
                }

                giveOutput(project);
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

    private void giveOutput(Project project){
        super.giveOutput();

        if (isCanceled()) return;
        if (mListener != null) mListener.onResponse(project, getErrorno());
    }

}
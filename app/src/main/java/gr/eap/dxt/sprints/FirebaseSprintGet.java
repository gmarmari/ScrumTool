package gr.eap.dxt.sprints;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import gr.eap.dxt.tools.FirebaseCall;
import gr.eap.dxt.tools.FirebaseParse;

/**
 * Created by GEO on 19/2/2017.
 */

public class FirebaseSprintGet extends FirebaseCall {

    public interface Listener {
        void onResponse(Sprint sprint, String errorMsg);
    }
    private Listener mListener;

    private String sprintId;

    public FirebaseSprintGet(Context context, String sprintId, boolean notify, Listener mListener){
        super(context, notify);
        this.sprintId = sprintId;
        this.mListener = mListener;
    }

    public void execute(){
        super.execute();

        if (sprintId == null) {
            addErrorNo("sprintId == null");
            giveOutput(null);
            return;
        }
        if (sprintId.isEmpty()) {
            addErrorNo("sprintId.isEmpty()");
            giveOutput(null);
            return;
        }

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Sprint.FIREBASE_LIST);
        mDatabase.child(sprintId).addListenerForSingleValueEvent(new ValueEventListener() {
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

                try {
                    final Sprint sprint = new Sprint();
                    sprint.setSprintId(dataSnapshot.getKey());

                    sprint.setName(FirebaseParse.getString(dataSnapshot.child(Sprint.NAME)));
                    sprint.setDescription(FirebaseParse.getString(dataSnapshot.child(Sprint.DESCRIPTION)));
                    sprint.setProjectId(FirebaseParse.getString(dataSnapshot.child(Sprint.PROJECT_ID)));
                    sprint.setStatus(FirebaseParse.getString(dataSnapshot.child(Sprint.STATUS)));
                    sprint.setStartDate(FirebaseParse.getDate(dataSnapshot.child(Sprint.START_DATE)));
                    sprint.setEndDate(FirebaseParse.getDate(dataSnapshot.child(Sprint.END_DATE)));
                    sprint.setDuration(FirebaseParse.getLong(dataSnapshot.child(Sprint.DURATION)));

                    giveOutput(sprint);

                }catch (Exception e){
                    addErrorNo(e.toString());

                    giveOutput(null);
                }
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

    private void giveOutput(Sprint sprint){
        super.giveOutput();

        if (isCanceled()) return;
        if (mListener != null) mListener.onResponse(sprint, getErrorno());

        if (Sprint.checkSprintStatus(sprint)) {
            // save the changes on background
            new FirebaseSprintEdit(getContext(), sprint, false, new FirebaseSprintEdit.Listener() {
                @Override
                public void onResponse(String errorMsg) {
                    if (errorMsg != null && !errorMsg.isEmpty()) {
                        writeError(errorMsg);
                    }
                }
            }).execute();
        }
    }

}


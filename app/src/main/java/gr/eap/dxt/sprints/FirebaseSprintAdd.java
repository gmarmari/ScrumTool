package gr.eap.dxt.sprints;

import android.content.Context;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import gr.eap.dxt.tools.FirebaseCall;

/**
 * Created by GEO on 14/2/2017.
 */

class FirebaseSprintAdd extends FirebaseCall {

    interface Listener {
        void onResponse(String errorMsg);
    }
    private Listener mListener;

    private Sprint sprint;

    FirebaseSprintAdd(Context context, Sprint sprint, boolean notify, Listener mListener){
        super(context, notify);
        this.sprint = sprint;
        this.mListener = mListener;
    }

    public void execute(){
        super.execute();

        if (sprint == null) {
            addErrorNo("sprint == null");
            giveOutput();
            return;
        }


        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String key = mDatabase.child(Sprint.FIREBASE_LIST).push().getKey();
        sprint.setSprintId(key);
        mDatabase.child(Sprint.FIREBASE_LIST).child(key).setValue(sprint, new DatabaseReference.CompletionListener() {
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

package gr.eap.dxt.sprints;

import android.content.Context;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import gr.eap.dxt.tools.FirebaseCall;

/**
 * Created by GEO on 14/2/2017.
 */

class FirebaseSprintDelete extends FirebaseCall {

    public interface Listener {
        void onResponse(String errorMsg);
    }
    private Listener mListener;

    private String sprintId;


    FirebaseSprintDelete(Context context, String sprintId, boolean notify, Listener mListener){
        super(context, notify);
        this.sprintId = sprintId;
        this.mListener = mListener;
    }

    public void execute(){
        super.execute();

        if (sprintId == null){
            addErrorNo("sprintId == null");
            giveOutput();
            return;
        }
        if (sprintId.isEmpty()){
            addErrorNo("sprintId.isEmpty()");
            giveOutput();
            return;
        }


        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Sprint.FIREBASE_LIST);
        if (mDatabase.child(sprintId) == null){
            addErrorNo("mDatabase.child(sprintId) == null");
            giveOutput();
            return;
        }

        mDatabase.child(sprintId).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                if (databaseError != null){
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

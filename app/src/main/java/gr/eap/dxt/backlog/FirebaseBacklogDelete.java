package gr.eap.dxt.backlog;

import android.content.Context;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import gr.eap.dxt.tools.FirebaseCall;

/**
 * Created by GEO on 12/2/2017.
 */

class FirebaseBacklogDelete extends FirebaseCall {

    interface Listener {
        void onResponse(String errorMsg);
    }
    private Listener mListener;

    private String backlogId;


    FirebaseBacklogDelete(Context context, String backlogId, Listener mListener){
        super(context, true);
        this.backlogId = backlogId;
        this.mListener = mListener;
    }

    public void execute(){
        super.execute();

        if (backlogId == null){
            addErrorNo("backlogId == null");
            giveOutput();
            return;
        }
        if (backlogId.isEmpty()){
            addErrorNo("backlogId.isEmpty()");
            giveOutput();
            return;
        }


        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Backlog.FIREBASE_LIST);
        if (mDatabase.child(backlogId) == null){
            addErrorNo("mDatabase.child(backlogId) == null");
            giveOutput();
            return;
        }

        mDatabase.child(backlogId).removeValue(new DatabaseReference.CompletionListener() {
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

        if (mListener != null) mListener.onResponse(getErrorno());
    }

}

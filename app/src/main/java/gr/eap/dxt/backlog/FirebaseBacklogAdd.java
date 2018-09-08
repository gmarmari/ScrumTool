package gr.eap.dxt.backlog;

import android.content.Context;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import gr.eap.dxt.tools.FirebaseCall;

/**
 * Created by GEO on 12/2/2017.
 */

class FirebaseBacklogAdd extends FirebaseCall {

    interface Listener {
        void onResponse(String errorMsg);
    }
    private Listener mListener;

    private Backlog backlog;

    FirebaseBacklogAdd(Context context, Backlog backlog, Listener mListener){
        super(context, true);
        this.backlog = backlog;
        this.mListener = mListener;
    }

    public void execute(){
        super.execute();

        if (backlog == null) {
            addErrorNo("backlog == null");
            giveOutput();
            return;
        }

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        String key = mDatabase.child(Backlog.FIREBASE_LIST).push().getKey();
        backlog.setBacklogId(key);
        mDatabase.child(Backlog.FIREBASE_LIST).child(key).setValue(backlog, new DatabaseReference.CompletionListener() {
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

        if (mListener != null) mListener.onResponse(getErrorno());
    }

}


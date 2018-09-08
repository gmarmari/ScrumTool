package gr.eap.dxt.persons;

import android.content.Context;


import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import gr.eap.dxt.tools.FirebaseCall;

/**
 * Created by GEO on 5/2/2017.
 */

class FirebasePersonDelete extends FirebaseCall {

    public interface Listener {
        void onResponse(String errorMsg);
    }
    private Listener mListener;

    private String personId;

    FirebasePersonDelete(Context context, String personId, Listener mListener){
        super(context, true);
        this.personId = personId;
        this.mListener = mListener;
    }

    public void execute(){
        super.execute();

        if (personId == null){
            addErrorNo("personId == null");
            giveOutput();
            return;
        }
        if (personId.isEmpty()){
            addErrorNo("personId.isEmpty()");
            giveOutput();
            return;
        }

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Person.FIREBASE_LIST);
        if (mDatabase.child(personId) == null){
            addErrorNo("mDatabase.child(personId) == null");
            giveOutput();
            return;
        }

        mDatabase.child(personId).removeValue(new DatabaseReference.CompletionListener() {
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
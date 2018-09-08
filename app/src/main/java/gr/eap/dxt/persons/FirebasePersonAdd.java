package gr.eap.dxt.persons;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import gr.eap.dxt.tools.AppShared;
import gr.eap.dxt.tools.FirebaseCall;

/**
 * Created by GEO on 22/2/2017.
 */

public class FirebasePersonAdd extends FirebaseCall {

    public interface Listener {
        void onResponse(Person person, String errorMsg);
    }
    private Listener mListener;

    private Person person;


    public FirebasePersonAdd(@NonNull Context context, Person person, boolean notify, Listener mListener) {
        super(context, notify);
        this.person = person;
        this.mListener = mListener;
    }

    public void execute(){
        super.execute();

        if (person == null){
            addErrorNo("person == null");
            giveOutput();
            return;
        }

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String key = mDatabase.child(Person.FIREBASE_LIST).push().getKey();
        person.setPersonId(key);
        mDatabase.child(Person.FIREBASE_LIST).child(key).setValue(person, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null){
                    addErrorNo(databaseError.getMessage());
                }

                AppShared.setLogginUser(person);
                giveOutput();
            }
        });
    }

    protected void giveOutput(){
        super.giveOutput();

        if (isCanceled()) return;
        if(mListener != null) mListener.onResponse(person, getErrorno());
    }

}



package gr.eap.dxt.persons;

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

public class FirebasePersonGet extends FirebaseCall {

    public interface Listener {
        @SuppressWarnings("UnusedParameters")
        void onResponse(Person person, String errorMsg);
    }
    private Listener mListener;

    private String personId;

    public FirebasePersonGet(Context context, String personId, boolean notify, Listener mListener){
        super(context, notify);
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
        mDatabase.child(personId).addListenerForSingleValueEvent(new ValueEventListener() {
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

                Person person;
                try {
                    person = new Person();
                    person.setPersonId(dataSnapshot.getKey());

                    person.setEmail(FirebaseParse.getString(dataSnapshot.child(Person.EMAIL)));
                    person.setName(FirebaseParse.getString(dataSnapshot.child(Person.NAME)));
                    person.setRole(FirebaseParse.getString(dataSnapshot.child(Person.ROLE)));

                }catch (Exception e){
                    person= null;
                    addErrorNo(e.toString());
                }

                giveOutput(person);
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

    protected void giveOutput(Person person){
        super.giveOutput();

        if (isCanceled()) return;
        if (mListener != null) mListener.onResponse(person, getErrorno());
    }

}

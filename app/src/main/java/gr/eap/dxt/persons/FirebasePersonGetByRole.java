package gr.eap.dxt.persons;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import gr.eap.dxt.tools.FirebaseCall;
import gr.eap.dxt.tools.FirebaseParse;

/**
 * Created by GEO on 8/3/2017.
 */

public class FirebasePersonGetByRole extends FirebaseCall {

    public interface Listener {
        @SuppressWarnings("UnusedParameters")
        void onResponse(Person person, String errorMsg);
    }
    private Listener mListener;

    private String personRole;

    public FirebasePersonGetByRole(@NonNull Context context, String personRole, boolean notify, Listener mListener) {
        super(context, notify);
        this.personRole = personRole;
        this.mListener = mListener;
    }

    public void execute(){
        super.execute();

        if (personRole == null){
            addErrorNo("personRole == null");
            giveOutput(null);
            return;
        }
        if (personRole.isEmpty()){
            addErrorNo("personRole.isEmpty()");
            giveOutput(null);
            return;
        }

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Person.FIREBASE_LIST);
        if (mDatabase == null){
            addErrorNo("mDatabase == null");
            giveOutput(null);
            return;
        }

        mDatabase.orderByChild(Person.ROLE).equalTo(personRole).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null){
                    addErrorNo("dataSnapshot == null");
                    giveOutput(null);
                    return;
                }
                if (dataSnapshot.getChildren() == null){
                    giveOutput(null);
                    return;
                }

                for (DataSnapshot childShapshot : dataSnapshot.getChildren()){
                    try {
                        if (childShapshot.getChildrenCount() > 0) {
                            // User found in persons
                            Person person = new Person();
                            person.setPersonId(childShapshot.getKey());
                            person.setEmail(FirebaseParse.getString(childShapshot.child(Person.EMAIL)));
                            person.setName(FirebaseParse.getString(childShapshot.child(Person.NAME)));
                            person.setRole(FirebaseParse.getString(childShapshot.child(Person.ROLE)));

                            giveOutput(person);
                            return;
                        }
                    } catch (Exception e) {
                        addErrorNo(e.toString());
                    }
                }

                giveOutput(null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                giveOutput(null);
            }
        });
    }

    private void giveOutput(Person person){
        super.giveOutput();

        if (isCanceled()) return;
        if(mListener != null) mListener.onResponse(person, getErrorno());
    }

}

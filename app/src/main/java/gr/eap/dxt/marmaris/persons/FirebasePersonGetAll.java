package gr.eap.dxt.marmaris.persons;


import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import gr.eap.dxt.marmaris.R;
import gr.eap.dxt.marmaris.tools.FirebaseCall;
import gr.eap.dxt.marmaris.tools.FirebaseParse;

/**
 * Created by GEO on 5/2/2017.
 */

class FirebasePersonGetAll extends FirebaseCall{

    interface Listener {
        void onResponse(ArrayList<Person> persons);
    }
    private Listener mListener;


    FirebasePersonGetAll(Context context, Listener mListener){
        super(context, true, true);
        this.mListener = mListener;
        setDialogTitle(context.getString(R.string.get_persons_progress));
    }

    public void execute(){
        super.execute();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Person.FIREBASE_LIST);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
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
                    giveOutput(new ArrayList<Person>());
                    return;
                }

                ArrayList<Person> persons = new ArrayList<>();

                for (DataSnapshot childShapshot : dataSnapshot.getChildren()){

                    try {
                        Person person = new Person();
                        person.setPersonId(childShapshot.getKey());

                        person.setEmail(FirebaseParse.getString(childShapshot.child(Person.EMAIL)));
                        person.setName(FirebaseParse.getString(childShapshot.child(Person.NAME)));
                        person.setPersonRole(FirebaseParse.getString(childShapshot.child(Person.PERSON_ROLE)));

                        persons.add(person);
                    }catch (Exception e){
                        addErrorNo(e.toString());
                    }
                }

                giveOutput(persons);
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

    private void giveOutput(ArrayList<Person> persons){
        super.giveOutput();

        if (getErrorno() != null && !getErrorno().isEmpty()){
            alertError(getErrorno());
            return;
        }
        if (persons == null){
            alertError("persons == null");
            return;
        }

        if (mListener != null) mListener.onResponse(persons);
    }

}

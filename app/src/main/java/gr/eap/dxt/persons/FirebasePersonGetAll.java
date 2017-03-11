package gr.eap.dxt.persons;


import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import gr.eap.dxt.R;
import gr.eap.dxt.tools.FirebaseCall;
import gr.eap.dxt.tools.FirebaseParse;

/**
 * Created by GEO on 5/2/2017.
 */

class FirebasePersonGetAll extends FirebaseCall{

    interface Listener {
        void onResponse(ArrayList<Person> persons, String errorMsg);
    }
    private Listener mListener;

    private String personRole;


    FirebasePersonGetAll(Context context, String personRole, Listener mListener){
        super(context, true);
        this.mListener = mListener;
        this.personRole = personRole;
        setDialogTitle(context.getString(R.string.get_persons_progress));
    }

    public void execute(){
        super.execute();


        if (personRole != null && !personRole.isEmpty()){
            getPersonsWithRole();
        }else{
            getAllPersons();
        }
    }

    private void getPersonsWithRole(){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Person.FIREBASE_LIST);
        if (mDatabase == null){
            addErrorNo("mDatabase == null");
            giveOutput(null);
            return;
        }

        mDatabase.orderByChild(Person.ROLE).equalTo(personRole).addListenerForSingleValueEvent(new ValueEventListener() {
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
                        person.setRole(FirebaseParse.getString(childShapshot.child(Person.ROLE)));

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

    private void getAllPersons(){
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
                        person.setRole(FirebaseParse.getString(childShapshot.child(Person.ROLE)));

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

        if (isCanceled()) return;
        if (mListener != null) mListener.onResponse(persons, getErrorno());
    }

}

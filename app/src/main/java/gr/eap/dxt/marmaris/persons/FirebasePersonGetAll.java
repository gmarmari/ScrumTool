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

/**
 * Created by GEO on 5/2/2017.
 */

public class FirebasePersonGetAll extends FirebaseCall{

    public interface Listener {
        void onResponse(ArrayList<Person> persons);
    }
    private Listener mListener;


    public FirebasePersonGetAll(Context context, boolean notify, boolean allowCancel, Listener mListener){
        super(context, notify, allowCancel);
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
                    addErrorNo("dataSnapshot.getChildrenCount() == 0");
                    giveOutput(new ArrayList<Person>());
                    return;
                }

                ArrayList<Person> persons = new ArrayList<>();

                for (DataSnapshot childShapshot : dataSnapshot.getChildren()){

                    try {
                        Person person = new Person();
                        person.setPersonId(childShapshot.getKey());

                        if (childShapshot.child(Person.EMAIL).getValue() != null){
                            person.setEmail(childShapshot.child(Person.EMAIL).getValue().toString());
                        }
                        if (childShapshot.child(Person.NAME).getValue() != null){
                            person.setName(childShapshot.child(Person.NAME).getValue().toString());
                        }
                        if (childShapshot.child(PersonRole.FIREBASE_ITEM).getValue() != null){
                            person.setPersonRole(childShapshot.child(PersonRole.FIREBASE_ITEM).getValue().toString());
                        }

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

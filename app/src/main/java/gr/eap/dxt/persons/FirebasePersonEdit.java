package gr.eap.dxt.persons;

import android.content.Context;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import gr.eap.dxt.tools.AppShared;
import gr.eap.dxt.tools.FirebaseCall;
import gr.eap.dxt.tools.StoreManagement;

/**
 * Created by GEO on 5/2/2017.
 */

class FirebasePersonEdit extends FirebaseCall {

    public interface Listener {
        void onResponse(String errorMsg);
    }
    private Listener mListener;

    private Person person;


    FirebasePersonEdit(Context context, Person person, Listener mListener){
        super(context, true);
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
        if (person.getPersonId() == null){
            addErrorNo("person.getPersonId() == null");
            giveOutput();
            return;
        }
        if (person.getPersonId().isEmpty()){
            addErrorNo("person.getPersonId().isEmpty()");
            giveOutput();
            return;
        }

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Person.FIREBASE_LIST);
        if (mDatabase.child(person.getPersonId()) == null){
            addErrorNo("mDatabase.child(personId) == null");
            giveOutput();
            return;
        }

        Map<String,Object> taskMap = new HashMap<>();
        taskMap.put(Person.NAME, person.getName());
        taskMap.put(Person.EMAIL, person.getEmail());
        taskMap.put(Person.ROLE, person.getRole());

        mDatabase.child(person.getPersonId()).updateChildren(taskMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                if(databaseError != null){
                    addErrorNo(databaseError.getMessage());
                }

                // if the person is the current user reload it in AppShared
                if (getErrorno() == null || getErrorno().isEmpty()){
                    if (person.getEmail() != null && !person.getEmail().isEmpty()){
                        StoreManagement store = new StoreManagement(getContext());
                        String savedmail = store.getSavedEmailForLogin();
                        if (savedmail !=null){
                            if (person.getEmail().equals(savedmail)){
                                AppShared.setLogginUser(person);
                            }
                        }
                    }
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
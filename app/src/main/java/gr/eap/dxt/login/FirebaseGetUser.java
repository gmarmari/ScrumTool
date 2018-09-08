package gr.eap.dxt.login;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import gr.eap.dxt.persons.Person;
import gr.eap.dxt.tools.FirebaseCall;
import gr.eap.dxt.tools.FirebaseParse;

/**
 * Created by GEO on 7/2/2017.
 */

public class FirebaseGetUser extends FirebaseCall {

    public interface Listener {
        void onResponse(Person person, String errorMsg);
    }
    private Listener mListener;


    public FirebaseGetUser(@NonNull Context context, Listener mListener) {
        super(context, true);
        this.mListener = mListener;
    }

    public void execute(){
        super.execute();
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        if (mFirebaseAuth == null){
            addErrorNo("mFirebaseAuth == null");
            giveOutput(null);
            return;
        }

        final FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if (currentUser == null){
            giveOutput(null);
            return;
        }
        if (currentUser.getEmail() == null){
            addErrorNo("currentUser.getEmail() == null");
            giveOutput(null);
            return;
        }
        if (currentUser.getEmail().isEmpty()){
            addErrorNo("currentUser.getEmail().isEmpty()");
            giveOutput(null);
            return;
        }

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Person.FIREBASE_LIST);
        if (mDatabase == null){
            addErrorNo("mDatabase == null");
            giveOutput(null);
            return;
        }

        mDatabase.orderByChild(Person.EMAIL).equalTo(currentUser.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null){
                    addErrorNo("dataSnapshot == null");
                    giveOutput(null);
                    return;
                }
                if (dataSnapshot.getChildren() == null){
                    addErrorNo("dataSnapshot.getChildren() == null");
                    giveOutput(null);
                    return;
                }

                for (DataSnapshot childShapshot : dataSnapshot.getChildren()){
                    try {
                        if (childShapshot.child(Person.EMAIL).getValue() != null){
                            if (childShapshot.child(Person.EMAIL).getValue().toString().equals(currentUser.getEmail())){
                                // User found in persons
                                Person person = new Person();
                                person.setPersonId(childShapshot.getKey());
                                person.setEmail(FirebaseParse.getString(childShapshot.child(Person.EMAIL)));
                                person.setName(FirebaseParse.getString(childShapshot.child(Person.NAME)));
                                person.setRole(FirebaseParse.getString(childShapshot.child(Person.ROLE)));

                                giveOutput(person);
                                return;
                            }
                        }
                    }catch (Exception e){
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


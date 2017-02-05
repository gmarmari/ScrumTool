package gr.eap.dxt.marmaris.login;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import gr.eap.dxt.marmaris.persons.Person;
import gr.eap.dxt.marmaris.persons.PersonRole;
import gr.eap.dxt.marmaris.tools.AppShared;
import gr.eap.dxt.marmaris.tools.FirebaseCall;
import gr.eap.dxt.marmaris.tools.StoreManagement;

/**
 * Created by GEO on 2/2/2017.
 */

public class FirebaseLogin extends FirebaseCall{

    public interface Listener {
        void onResponse(Task<AuthResult> task);
    }
    private Listener mListener;

    private String email;
    private String password;


    public FirebaseLogin(@NonNull Activity activity, String email, String password, boolean notify, Listener mListener) {
        super(activity, notify, false);
        this.email = email;
        this.password = password;
        this.mListener = mListener;
    }

    public void execute(){
        super.execute();

        if (!LoginFragment.isEmailValid(email)){
            addErrorNo("Invalid email");
            giveOutput();
            return;
        }
        if (password == null){
            addErrorNo("Password == null");
            giveOutput();
            return;
        }
        if (password.length() < 6){
            addErrorNo("Password must contain at least 6 characters");
            giveOutput();
            return;
        }

        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener((Activity) getContext(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                checkIfPersonExist(task);

            }
        });
    }

    private void checkIfPersonExist(@NonNull final Task<AuthResult> task){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Person.FIREBASE_LIST);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot == null) {
                    addErrorNo("dataSnapshot == null");
                    giveOutput(task);
                    return;
                }

                if (dataSnapshot.getChildren() == null) {
                    addErrorNo("dataSnapshotgetChildren() == null");
                    giveOutput(task);
                    return;
                }

                if (dataSnapshot.getChildrenCount() == 0) {
                    // No persons yet, so add them
                    addPerson(task);
                    return;
                }

                for (DataSnapshot childShapshot : dataSnapshot.getChildren()){
                    try {
                        if (childShapshot.child(Person.EMAIL).getValue() == null) continue;

                        if (childShapshot.child(Person.EMAIL).getValue().equals(email)) {

                            Person person = new Person();
                            person.setPersonId(childShapshot.getKey());
                            person.setEmail(childShapshot.child(Person.EMAIL).getValue().toString());

                            if (childShapshot.child(Person.NAME).getValue() != null){
                                person.setName(childShapshot.child(Person.NAME).getValue().toString());
                            }
                            if (childShapshot.child(PersonRole.FIREBASE_ITEM).getValue() != null){
                                person.setPersonRole(childShapshot.child(PersonRole.FIREBASE_ITEM).getValue().toString());
                            }

                            AppShared.setUserLogged(person);
                            giveOutput(task);
                            return;
                        }

                    }catch (Exception e){
                        addErrorNo(e.toString());
                    }
                }

                //  Person does not exist, add it
                addPerson(task);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (databaseError != null){
                    addErrorNo(databaseError.getMessage());
                    giveOutput();
                }
            }
        });
    }

    private void addPerson(final Task<AuthResult> task){
        if (task != null){
            if (task.isSuccessful()){

                final Person person = new Person();
                person.setEmail(email);
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                String key = mDatabase.child(Person.FIREBASE_LIST).push().getKey();
                person.setPersonId(key);
                mDatabase.child(Person.FIREBASE_LIST).child(key).setValue(person, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null){
                            addErrorNo(databaseError.getMessage());
                        }

                        AppShared.setUserLogged(person);
                        giveOutput(task);
                    }
                });
            }
        }
    }

    private void giveOutput(Task<AuthResult> task){
        super.giveOutput();

        if (getErrorno() != null && !getErrorno().isEmpty()){
            alertError(getErrorno());
        }
        if (task == null){
            alertError("task == null");
            return;
        }
        if (!task.isSuccessful()){
            if (task.getException() != null) {
                addErrorNo(task.getException().getMessage());
            }
            alertError(getErrorno());
            return;
        }

        StoreManagement store = new StoreManagement(getContext());
        store.setSavedEmailForLogin(email);
        store.setSavedPasswordForLogin(password);

        if (mListener != null) mListener.onResponse(task);
    }

}

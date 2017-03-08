package gr.eap.dxt.login;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


import gr.eap.dxt.R;
import gr.eap.dxt.persons.FirebasePersonAdd;
import gr.eap.dxt.persons.FirebasePersonGetByMail;
import gr.eap.dxt.persons.Person;
import gr.eap.dxt.tools.AppShared;
import gr.eap.dxt.tools.FirebaseCall;
import gr.eap.dxt.tools.StoreManagement;

/**
 * Created by GEO on 2/2/2017.
 */

class FirebaseLogin extends FirebaseCall{

    public interface Listener {
        void onResponse(Task<AuthResult> task, String errorMsg);
    }
    private Listener mListener;

    private String email;
    private String password;


    FirebaseLogin(@NonNull Activity activity, String email, String password, Listener mListener) {
        super(activity, true);
        this.email = email;
        this.password = password;
        this.mListener = mListener;
        setDialogTitle(getContext().getString(R.string.login_progress));
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
        new FirebasePersonGetByMail(getContext(), email, false, new FirebasePersonGetByMail.Listener() {
            @Override
            public void onResponse(Person person, String errorMsg) {
                if (person != null){
                    AppShared.setLogginUser(person);
                    giveOutput(task);
                }else{
                    addPerson(task);
                }
            }
        }).execute();
    }

    private void addPerson(final Task<AuthResult> task){
        final Person person = new Person();
        person.setEmail(email);
        new FirebasePersonAdd(getContext(), person, false, new FirebasePersonAdd.Listener() {
            @Override
            public void onResponse(Person person, String errorMsg) {

                if (errorMsg != null && !errorMsg.isEmpty()){
                    addErrorNo(errorMsg);
                }

                AppShared.setLogginUser(person);
                giveOutput(task);
            }
        }).execute();
    }


    private void giveOutput(Task<AuthResult> task){
        super.giveOutput();

        if (task != null){
            if (task.isSuccessful()){
                StoreManagement store = new StoreManagement(getContext());
                store.setSavedEmailForLogin(email);
                store.setSavedPasswordForLogin(password);
            }
        }

        if (isCanceled()) return;
        if (mListener != null) mListener.onResponse(task, getErrorno());
    }

}

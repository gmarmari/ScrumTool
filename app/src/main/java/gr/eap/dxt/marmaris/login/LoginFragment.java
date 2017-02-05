package gr.eap.dxt.marmaris.login;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import gr.eap.dxt.marmaris.R;
import gr.eap.dxt.marmaris.tools.Keyboard;
import gr.eap.dxt.marmaris.tools.MyToast;
import gr.eap.dxt.marmaris.tools.StoreManagement;

public class LoginFragment extends Fragment {

    public interface FragmentInteractionListener {
        void onDidLogIn(Task<AuthResult> task);
    }
    private FragmentInteractionListener mListener;

    public LoginFragment() { }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    private String email;
    private String password;

    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (FragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("context must implement FragmentInteractionListener.");
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (FragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement FragmentInteractionListener.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        StoreManagement store = new StoreManagement(getActivity());

        EditText emailEditText = (EditText) rootView.findViewById(R.id.email);
        if (emailEditText != null){
            email = store.getSavedEmailForLogin();
            emailEditText.setText(email != null ? email : "");

            emailEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable == null) return;
                    email = editable.toString();
                }
            });
        }

        EditText passwordEditText = (EditText) rootView.findViewById(R.id.password);
        if (passwordEditText != null){
            password = store.getSavedPasswordForLogin();
            passwordEditText.setText(password != null ? password : "");

            passwordEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable == null) return;
                    password = editable.toString();
                }
            });
        }

        Button button = (Button) rootView.findViewById(R.id.my_button_login);
        if (button != null){
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    login();
                }
            });
        }

        return rootView;
    }

    private void login(){
        Keyboard.close(getActivity());

        if (email == null || email.isEmpty()){
            new MyToast(getActivity(), R.string.enter_email).show();
            return;
        }
        if (!isEmailValid(email)){
            new MyToast(getActivity(), R.string.invalid_email).show();
            return;
        }
        if (password == null || password.isEmpty()){
            new MyToast(getActivity(), R.string.enter_password).show();
            return;
        }

        new FirebaseLogin(getActivity(), email, password, true, new FirebaseLogin.Listener() {
            @Override
            public void onResponse(Task<AuthResult> task) {

                if (mListener != null) {
                    mListener.onDidLogIn(task);
                }
            }
        }).execute();
    }



    public static boolean isEmailValid(String email) {
        return !(email == null || email.isEmpty()) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}

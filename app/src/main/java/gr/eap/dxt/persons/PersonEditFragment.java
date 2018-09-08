package gr.eap.dxt.persons;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import gr.eap.dxt.R;
import gr.eap.dxt.tools.AppShared;
import gr.eap.dxt.tools.MyAlertDialog;
import gr.eap.dxt.tools.MySpinnerDialog;
import gr.eap.dxt.tools.MyToast;

/**
 * Created by GEO on 5/2/2017.
 */

public class PersonEditFragment extends Fragment{

    public PersonEditFragment(){}

    private Person person;
    private boolean changeMade;

    public static PersonEditFragment newInstance(Person person){
        PersonEditFragment fragment = new PersonEditFragment();
        fragment.person = Person.getCopy(person);
        return fragment;
    }

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_person_edit, container, false);

        if (person == null){
            AppShared.writeErrorToLogString(getClass().toString(), "person == null");
            return rootView;
        }

        setEmailTextView();
        setNameEditText();
        setRoleEditText();

        changeMade = false;

        return rootView;
    }

    private void setEmailTextView(){
        if (rootView == null) return;
        if (person == null) return;

        TextView textView = (TextView) rootView.findViewById(R.id.email);
        if (textView == null) return;

        textView.setText(person.getEmail() != null ? person.getEmail() : "");
    }

    private void setNameEditText(){
        if (rootView == null) return;
        if (person == null) return;

        EditText editText = (EditText) rootView.findViewById(R.id.name);
        if (editText == null) return;

        editText.setText(person.getName() != null ? person.getName() : "");
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable == null) return;

                person.setName(editable.toString());
                changeMade = true;
            }
        });
    }

    private void setRoleEditText(){
        if (rootView == null) return;
        if (person == null) return;

        final EditText editText = (EditText) rootView.findViewById(R.id.my_spinner_role);
        if (editText == null) return;

        final ArrayList<String> items = new ArrayList<>();
        items.add(getString(R.string.developer));
        items.add(getString(R.string.scrum_master));
        items.add(getString(R.string.product_owner));

        String role = PersonRole.getPersonRole(getActivity(), person.getRole());
        editText.setText(role != null ? role : getString(R.string.none));

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MySpinnerDialog(getActivity(), getString(R.string.role), items, new MySpinnerDialog.MyListener() {
                    @Override
                    public void onItemSelected(int position) {

                        if (position < 0 || position >= items.size()) return;
                        if (position == 0){
                            person.setRole(PersonRole.DEVELOPER);
                            editText.setText(R.string.developer);
                            changeMade = true;
                        }else if (position == 1){
                            changeRoleIfAllowed(PersonRole.SCRUM_MASTER);
                        }else if (position == 2){
                            changeRoleIfAllowed(PersonRole.PRODUCT_OWNER);
                        }else{
                            AppShared.writeErrorToLogString(getClass().toString(), "Not supported selected position for spinner person role: "+ position);
                        }
                    }
                }).show();

            }
        });
    }

    private void changeRoleIfAllowed(final String personRole){
        if (rootView == null) return;
        if (person == null) return;
        if (personRole == null || personRole.isEmpty()) return;

        new FirebasePersonGetByRole(getActivity(), personRole, true, new FirebasePersonGetByRole.Listener() {
            @Override
            public void onResponse(Person person, String errorMsg) {

                if (errorMsg != null && !errorMsg.isEmpty()){
                    MyAlertDialog.alertError(getActivity(), null, errorMsg);
                    AppShared.writeErrorToLogString(getClass().toString(), errorMsg);
                    return;
                }

                if (person == null){
                    EditText editText = (EditText) rootView.findViewById(R.id.my_spinner_role);
                    if (editText == null) return;

                    String role = PersonRole.getPersonRole(getActivity(), personRole);
                    if (role == null || role.isEmpty()) return;
                    editText.setText(role);
                    PersonEditFragment.this.person.setRole(personRole);
                    changeMade = true;
                }else{
                    String role = PersonRole.getPersonRole(getActivity(), personRole);
                    if (role == null) role = "";
                    String message = getString(R.string.role_exists).replace("-role-", role);
                    MyAlertDialog.alertError(getActivity(), getString(R.string.not_allowed), message);
                }

            }
        }).execute();
    }

    /** From {@link PersonDialogActivity} */
    protected void saveChanges(){
        if (!changeMade){
            new MyToast(getActivity(), R.string.no_change_occured).show();
            return;
        }

        new FirebasePersonEdit(getActivity(), person, new FirebasePersonEdit.Listener() {
            @Override
            public void onResponse(String errorMsg) {

                if (errorMsg != null && !errorMsg.isEmpty()){
                    AppShared.writeErrorToLogString(getClass().toString(), errorMsg);
                    MyAlertDialog.alertError(getActivity(), null, errorMsg);
                }else{
                    Intent data = new Intent();
                    data.putExtra(PersonDialogActivity.RELOAD, true);
                    getActivity().setResult(Activity.RESULT_OK, data);
                    getActivity().finish();
                }

            }
        }).execute();


    }
}

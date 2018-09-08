package gr.eap.dxt.persons;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import gr.eap.dxt.R;
import gr.eap.dxt.backlog.Backlog;
import gr.eap.dxt.backlog.BacklogStatus;
import gr.eap.dxt.backlog.FirebaseBacklogPersonGet;
import gr.eap.dxt.backlog.ListBacklogGroupedStatusAdapter;
import gr.eap.dxt.backlog.ListBacklogGroupedStatusParent;
import gr.eap.dxt.tools.AppShared;
import gr.eap.dxt.tools.MyAlertDialog;
import gr.eap.dxt.tools.MyToast;

/**
 * Created by GEO on 5/2/2017.
 */

public class PersonShowFragment extends Fragment {

    public interface FragmentInteractionListener {
        void onPersonEdit();
    }
    private FragmentInteractionListener mListener;

    public PersonShowFragment(){}

    private Person person;
    private ArrayList<Backlog> backlogs;

    private View rootView;

    private ListBacklogGroupedStatusAdapter adapter;
    private ArrayList<ListBacklogGroupedStatusParent> parents;

    public static PersonShowFragment newInstance(Person person){
        PersonShowFragment fragment = new PersonShowFragment();
        fragment.person = person;
        return fragment;
    }

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
        createParents();
        setBacklogsTextView();
    }

    private void createParents(){
        parents = new ArrayList<>();

        if (backlogs != null && !backlogs.isEmpty()){
            ListBacklogGroupedStatusParent toDoParent = new ListBacklogGroupedStatusParent(BacklogStatus.TO_DO);
            ListBacklogGroupedStatusParent inProgressParent = new ListBacklogGroupedStatusParent(BacklogStatus.IN_PROGRESS);
            ListBacklogGroupedStatusParent doneParent = new ListBacklogGroupedStatusParent(BacklogStatus.DONE);

            for (Backlog backlog : backlogs){
                if (backlog != null){
                    if (backlog.getStatus() != null){
                        switch (backlog.getStatus()) {
                            case BacklogStatus.TO_DO:
                                toDoParent.getChildren().add(backlog);
                                break;
                            case BacklogStatus.IN_PROGRESS:
                                inProgressParent.getChildren().add(backlog);
                                break;
                            case BacklogStatus.DONE:
                                doneParent.getChildren().add(backlog);
                                break;
                        }
                    }
                }
            }

            parents.add(toDoParent);
            parents.add(inProgressParent);
            parents.add(doneParent);
        }
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_person_show, container, false);
        View header = inflater.inflate(R.layout.fragment_person_show_header, null, false);
        if (rootView != null){
            ExpandableListView listView = (ExpandableListView) rootView.findViewById(R.id.my_expandable_listview);
            if (listView != null) {
                if (header != null) {
                    listView.addHeaderView(header);
                }
            }
        }

        setList();
        setEmailTextView();
        setNameTextView();
        setRoleTextView();
        setBacklogsTextView();
        setListeners();

        return rootView;
    }

    private void setList() {
        if (rootView == null) return;

        ExpandableListView listView = (ExpandableListView) rootView.findViewById(R.id.my_expandable_listview);
        if (listView == null) return;

        adapter = new ListBacklogGroupedStatusAdapter(getActivity(), null);
        listView.setAdapter(adapter);
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                if (parents == null) return false;
                if (groupPosition < 0) return false;
                if (groupPosition >= parents.size()) return false;
                ListBacklogGroupedStatusParent parent = parents.get(groupPosition);
                if (parent == null) return false;
                parent.setIsOn(!parent.isOn());
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                return false;
            }
        });
    }

    private void setEmailTextView(){
        if (rootView == null) return;
        if (person == null) return;

        TextView textView = (TextView) rootView.findViewById(R.id.email);
        if (textView == null) return;
        textView.setText(person.getEmail() != null ? person.getEmail() : "");
    }

    private void setNameTextView(){
        if (rootView == null) return;
        if (person == null) return;

        TextView textView = (TextView) rootView.findViewById(R.id.name);
        if (textView == null) return;
        textView.setText(person.getName() != null ? person.getName() : "");
    }

    private void setRoleTextView(){
        if (rootView == null) return;
        if (person == null) return;

        TextView textView = (TextView) rootView.findViewById(R.id.person_role);
        if (textView == null) return;
        String role = PersonRole.getPersonRole(getActivity(), person.getRole());
        textView.setText(role != null ? role : getString(R.string.none));
    }

    private void setBacklogsTextView(){
        if (rootView == null) return;
        if (person == null) return;

        TextView textView = (TextView) rootView.findViewById(R.id.person_backlogs);
        if (textView == null) return;
        if (backlogs == null || backlogs.isEmpty()){
            textView.setVisibility(View.GONE);
        }else{
            textView.setVisibility(View.VISIBLE);
        }
    }

    private void setListeners(){
        if (rootView == null) return;

        ImageButton editButton = (ImageButton) rootView.findViewById(R.id.my_button_edit);
        if (editButton != null) {
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) mListener.onPersonEdit();
                }
            });
        }

        ImageButton deleteButton = (ImageButton) rootView.findViewById(R.id.my_button_delete);
        if (deleteButton != null) {

            if (allowDelete()) {
                deleteButton.setVisibility(View.VISIBLE);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deletePerson();
                    }
                });
            } else {
                deleteButton.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getPersonBacklogs();
    }

    private void getPersonBacklogs(){
        if (person == null){
            AppShared.writeErrorToLogString(getClass().toString(), "person == null");
            return;
        }
        if (person.getPersonId() == null){
            AppShared.writeErrorToLogString(getClass().toString(), "person.getPersonId( == null");
            return;
        }
        if (person.getPersonId().isEmpty()){
            AppShared.writeErrorToLogString(getClass().toString(), "person.getPersonId(.isEmpty()");
            return;
        }

        new FirebaseBacklogPersonGet(getActivity(), person.getPersonId(), new FirebaseBacklogPersonGet.Listener() {
            @Override
            public void onResponse(ArrayList<Backlog> _backlogs, String errorMsg) {

                if (errorMsg != null && !errorMsg.isEmpty()){
                    MyAlertDialog.alertError(getActivity(), null, errorMsg);
                    return;
                }
                if (_backlogs == null) {
                    MyAlertDialog.alertError(getActivity(), null, "backlogs == null");
                    return;
                }

                backlogs = _backlogs;
                createParents();
                setBacklogsTextView();
                if (adapter != null){
                    adapter.setParents(parents);
                    adapter.notifyDataSetChanged();
                }
            }
        }).execute();
    }

    @SuppressWarnings("SimplifiableIfStatement")
    private boolean allowDelete(){
        if (person == null) return false;
        if (person.getEmail() == null) return false;

        Person savedPerson = AppShared.getLogginUser();
        if (savedPerson == null) return false;
        if (savedPerson.getEmail() == null) return false;

        return savedPerson.getEmail().equals(person.getEmail());
    }

    private void deletePerson(){
        if (person == null){
            AppShared.writeErrorToLogString(getClass().toString(), "person == null");
            MyAlertDialog.alertError(getActivity(), null, "person == null");
            return;
        }
        if (person.getPersonId() == null){
            AppShared.writeErrorToLogString(getClass().toString(), "person.getPersonId() == null");
            MyAlertDialog.alertError(getActivity(), null, "person.getPersonId() == null");
            return;
        }
        if (person.getPersonId().isEmpty()){
            AppShared.writeErrorToLogString(getClass().toString(), "person.getPersonId().isEmpty()");
            MyAlertDialog.alertError(getActivity(), null, "person.getPersonId().isEmpty()");
            return;
        }

        MyAlertDialog myAlertDialog = new MyAlertDialog(getActivity(), getString(R.string.delete_person),R.drawable.ic_action_warning_purple, null, MyAlertDialog.MyAlertDialogType.MESSAGE);
        myAlertDialog.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        myAlertDialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                new FirebasePersonDelete(getActivity(), person.getPersonId(), new FirebasePersonDelete.Listener() {
                    @Override
                    public void onResponse(String errorMsg) {

                        if (errorMsg != null && !errorMsg.isEmpty()){
                            AppShared.writeErrorToLogString(getClass().toString(), errorMsg);
                            MyAlertDialog.alertError(getActivity(), null, errorMsg);
                        }else{
                            new MyToast(getActivity(), R.string.person_deleted).show();

                            FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
                            mFirebaseAuth.signOut();
                            AppShared.logout();
                            Intent data = new Intent();
                            data.putExtra(PersonDialogActivity.RELOAD, true);
                            getActivity().setResult(Activity.RESULT_OK, data);
                            getActivity().finish();
                        }

                    }
                }).execute();

            }
        });
        myAlertDialog.alertMessage();
    }

}
package gr.eap.dxt.backlog;

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
import android.widget.ImageButton;
import android.widget.TextView;

import gr.eap.dxt.R;
import gr.eap.dxt.login.FirebaseGetUser;
import gr.eap.dxt.persons.Person;
import gr.eap.dxt.persons.PersonRole;
import gr.eap.dxt.projects.Project;
import gr.eap.dxt.sprints.Sprint;
import gr.eap.dxt.tools.AppShared;
import gr.eap.dxt.tools.MyAlertDialog;
import gr.eap.dxt.tools.MyToast;

/**
 * Created by GEO on 12/2/2017.
 */

public class BacklogShowFragment extends Fragment {

    public interface FragmentInteractionListener {
        void onBacklogEdit(Backlog mBacklog, Person person, Sprint sprint);
    }
    private FragmentInteractionListener mListener;

    public BacklogShowFragment(){}

    private View rootView;

    private Backlog backlog;
    private Project project;
    private Sprint sprint;
    private Person person;
    private boolean allowDelete;

    public static BacklogShowFragment newInstance(Backlog backlog, boolean allowDelete){
        BacklogShowFragment fragment = new BacklogShowFragment();
        fragment.backlog = backlog;
        fragment.allowDelete = allowDelete;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_backlog_show, container, false);

        setNameTextView();
        setDescriptionTextView();
        setDurationTextView();
        setPrioTextView();
        setStatusTextView();
        setTypeTextView();
        setPersonTextView();
        setSprintTextView();
        setProjectTextView();
        setBottomToolbar();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (backlog == null) return;
        getbacklogData();
    }

    private void getbacklogData(){
       new FirebaseBacklogGetData(getActivity(), backlog, new FirebaseBacklogGetData.Listener() {
           @Override
           public void onResponse(Person person, Sprint sprint, Project project, String errorMsg) {

               if (errorMsg != null && !errorMsg.isEmpty()) {
                   AppShared.writeErrorToLogString(getClass().toString(),errorMsg);
               }

               BacklogShowFragment.this.person = person;
               setPersonTextView();
               BacklogShowFragment.this.sprint = sprint;
               setSprintTextView();
               BacklogShowFragment.this.project = project;
               setProjectTextView();

           }
       }).execute();
    }


    private void setNameTextView(){
        if (rootView == null) return;
        if (backlog == null) return;

        TextView nameTextView = (TextView) rootView.findViewById(R.id.name);
        if (nameTextView == null) return;
        nameTextView.setText(backlog.getName() != null ? backlog.getName() : "");
    }

    private void setDescriptionTextView(){
        if (rootView == null) return;
        if (backlog == null) return;

        TextView descriptionTextView = (TextView) rootView.findViewById(R.id.description);
        if (descriptionTextView == null) return;
        descriptionTextView.setText(backlog.getDescription() != null ? backlog.getDescription() : "");
    }

    private void setDurationTextView() {
        if (rootView == null) return;
        if (backlog == null) return;

        TextView durationTextView = (TextView) rootView.findViewById(R.id.duration);
        if (durationTextView == null) return;
        String duration = "";
        if (backlog.getDuration() != null && backlog.getDuration() > 0) {
            duration += backlog.getDuration() + " ";
            if (backlog.getDuration() == 1) {
                duration += getString(R.string.day).toLowerCase();
            } else {
                duration += getString(R.string.days).toLowerCase();
            }
        }
        durationTextView.setText(duration);
    }

    private void setPrioTextView(){
        if (rootView == null) return;
        if (backlog == null) return;

        TextView textView = (TextView) rootView.findViewById(R.id.priority);
        if (textView == null) return;
        String prio = Priority.getPriority(getActivity(), backlog.getPriority());
        textView.setText(prio != null ? prio : "");
    }

    private void setStatusTextView(){
        if (rootView == null) return;
        if (backlog == null) return;

        TextView statusTextView = (TextView) rootView.findViewById(R.id.status);
        if (statusTextView == null) return;
        String status = BacklogStatus.getBacklogStatus(getActivity(), backlog.getStatus());
        statusTextView.setText(status != null ? status : "");
    }

    private void setTypeTextView() {
        if (rootView == null) return;
        if (backlog == null) return;

        TextView typeTextView = (TextView) rootView.findViewById(R.id.type);
        if (typeTextView == null) return;
        String type = BacklogType.getBacklogType(getActivity(), backlog.getType());
        typeTextView.setText(type != null ? type : "");
    }

    private void setPersonTextView(){
        if (rootView == null) return;
        if (person == null) return;

        TextView persTextView = (TextView) rootView.findViewById(R.id.backlog_person);
        if (persTextView == null) return;

        String name = "";
        boolean isName = false;
        if (person.getName() != null && !person.getName().isEmpty()){
            isName = true;
            name += person.getName();
        }
        if (person.getEmail() != null && !person.getEmail().isEmpty()){
            if (!name.isEmpty()) name += " ";
            if (isName) name += "<";
            name += person.getEmail();
            if (isName) name += ">";
        }

        persTextView.setText(name);
    }

    private void setSprintTextView(){
        if (rootView == null) return;
        if (sprint == null) return;

        TextView sprintTextView = (TextView) rootView.findViewById(R.id.backlog_sprint);
        if (sprintTextView == null) return;
        sprintTextView.setText(sprint.getName() != null ? sprint.getName() : "");
    }

    private void setProjectTextView(){
        if (rootView == null) return;
        if (backlog == null) return;

        TextView projectTextView = (TextView) rootView.findViewById(R.id.backlog_project);
        if (projectTextView == null) return;
        String projectName = project != null ? project.getName() : null;
        projectTextView.setText(projectName != null ? projectName : "");
    }

    private void setBottomToolbar(){
        if (rootView == null) return;

        ImageButton editButton = (ImageButton) rootView.findViewById(R.id.my_button_edit);
        if (editButton != null) {
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) mListener.onBacklogEdit(backlog, person, sprint);
                }
            });
        }

        ImageButton deleteButton = (ImageButton) rootView.findViewById(R.id.my_button_delete);
        if (deleteButton != null) {
            if (allowDelete) {
                deleteButton.setVisibility(View.VISIBLE);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteBacklog();
                    }
                });
            }else{
                deleteButton.setVisibility(View.GONE);
            }
        }
    }

    private void deleteBacklog(){
        Person person = AppShared.getLogginUser();
        if (person != null){
            deleteBacklog(person);
            return;
        }

        new FirebaseGetUser(getActivity(), new FirebaseGetUser.Listener() {
            @Override
            public void onResponse(Person person, String errorMsg) {

                if (errorMsg != null && !errorMsg.isEmpty()){
                    AppShared.writeErrorToLogString(getClass().toString(), errorMsg);
                    MyAlertDialog.alertError(getActivity(), null, errorMsg);
                    return;
                }

                deleteBacklog(person);
            }
        }).execute();


    }

    private void deleteBacklog(Person person){

        if (person == null){
            AppShared.writeErrorToLogString(getClass().toString(), "person == null");
            MyAlertDialog.alertError(getActivity(), null, "person == null");
            return;
        }

        String role = person.getRole() != null ? person.getRole() : "";
        if (!role.equals(PersonRole.PRODUCT_OWNER)){
            // Only Product owner can delete backlogs
            MyAlertDialog.alertError(getActivity(), getString(R.string.not_allowed), getString(R.string.backlog_delete_not_allowed_message));
            return;
        }

        if (backlog == null){
            AppShared.writeErrorToLogString(getClass().toString(), "backlog == null");
            MyAlertDialog.alertError(getActivity(), null, "backlog == null");
            return;
        }
        if (backlog.getBacklogId() == null){
            AppShared.writeErrorToLogString(getClass().toString(), "backlog.getBacklogId() == null");
            MyAlertDialog.alertError(getActivity(), null, "backlog.getBacklogId() == null");
            return;
        }
        if (backlog.getBacklogId().isEmpty()){
            AppShared.writeErrorToLogString(getClass().toString(), "backlog.getBacklogId().isEmpty()");
            MyAlertDialog.alertError(getActivity(), null, "backlog.getBacklogId().isEmpty()");
            return;
        }

        MyAlertDialog myAlertDialog = new MyAlertDialog(getActivity(), getString(R.string.delete_backlog),R.drawable.ic_action_warning_purple, null, MyAlertDialog.MyAlertDialogType.MESSAGE);
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

                new FirebaseBacklogDelete(getActivity(), backlog.getBacklogId(), new FirebaseBacklogDelete.Listener() {
                    @Override
                    public void onResponse(String errorMsg) {

                        if (errorMsg != null && !errorMsg.isEmpty()){
                            AppShared.writeErrorToLogString(getClass().toString(), errorMsg);
                            MyAlertDialog.alertError(getActivity(), null, errorMsg);
                        }else{
                            new MyToast(getActivity(), R.string.backlog_deleted).show();

                            Intent data = new Intent();
                            data.putExtra(BacklogEditFragment.RELOAD, true);
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

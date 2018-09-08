package gr.eap.dxt.sprints;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import gr.eap.dxt.R;
import gr.eap.dxt.backlog.Backlog;
import gr.eap.dxt.backlog.BacklogStatus;
import gr.eap.dxt.backlog.ListBacklogGroupedStatusAdapter;
import gr.eap.dxt.backlog.ListBacklogGroupedStatusParent;
import gr.eap.dxt.login.FirebaseGetUser;
import gr.eap.dxt.persons.Person;
import gr.eap.dxt.persons.PersonRole;
import gr.eap.dxt.projects.Project;
import gr.eap.dxt.tools.AppShared;
import gr.eap.dxt.tools.MyAlertDialog;

/**
 * Created by GEO on 15/2/2017.
 */

public class SprintShowFragment extends Fragment {

    public interface FragmentInteractionListener {
        void onSprintEdit(Sprint mSprint);
        void onAddSprintBacklog(Sprint mSprint);
        void onOpenBacklog(Backlog backlog);
    }
    private FragmentInteractionListener mListener;

    public SprintShowFragment(){}

    private View rootView;

    private Sprint sprint;
    private Project project;
    private ArrayList<Backlog> backlogs;

    private ListBacklogGroupedStatusAdapter adapter;
    private ArrayList<ListBacklogGroupedStatusParent> parents;

    public static SprintShowFragment newInstance(Sprint sprint){
        SprintShowFragment fragment = new SprintShowFragment();
        fragment.sprint = sprint;
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
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_sprint_show, container, false);
        View header = inflater.inflate(R.layout.fragment_sprint_show_header, null, false);
        if (rootView != null){
            ExpandableListView listView = (ExpandableListView) rootView.findViewById(R.id.my_expandable_listview);
            if (listView != null) {
                if (header != null) {
                    listView.addHeaderView(header);
                }
            }
        }

        setList();
        setNameTextView();
        setDescriptionTextView();
        setStartTextView();
        setEndTextView();
        setDurationTextView();
        setStatusTextView();
        setProjectTextView();
        setBottomToolbar();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getSprintData();
    }

    private void createParents(){
        parents = new ArrayList<>();

        ListBacklogGroupedStatusParent toDoParent = new ListBacklogGroupedStatusParent(BacklogStatus.TO_DO);
        ListBacklogGroupedStatusParent inProgressParent = new ListBacklogGroupedStatusParent(BacklogStatus.IN_PROGRESS);
        ListBacklogGroupedStatusParent doneParent = new ListBacklogGroupedStatusParent(BacklogStatus.DONE);

        if (backlogs != null && !backlogs.isEmpty()){
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
        }

        parents.add(toDoParent);
        parents.add(inProgressParent);
        parents.add(doneParent);
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
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                if (parents == null) return false;
                if (groupPosition < 0) return false;
                if (groupPosition >= parents.size()) return false;
                ListBacklogGroupedStatusParent parent = parents.get(groupPosition);
                if (parent == null) return false;
                if (parent.getChildren() == null) return false;
                if (childPosition < 0) return false;
                if (childPosition >= parent.getChildren().size()) return false;
                Backlog backlog = parent.getChildren().get(childPosition);
                if (backlog == null) return false;

                alertBacklogOptions(backlog);

                return false;
            }
        });
    }

    private void setNameTextView(){
        if (rootView == null) return;
        if (sprint == null) return;

        TextView nameTextView = (TextView) rootView.findViewById(R.id.name);
        if (nameTextView == null) return;
        nameTextView.setText(sprint.getName() != null ? sprint.getName() : "");
    }

    private void setDescriptionTextView(){
        if (rootView == null) return;
        if (sprint == null) return;

        TextView descrTextView = (TextView) rootView.findViewById(R.id.description);
        if (descrTextView == null) return;
        descrTextView.setText(sprint.getDescription() != null ? sprint.getDescription() : "");
    }

    private void setStartTextView(){
        if (rootView == null) return;
        if (sprint == null) return;

        TextView startTextView = (TextView) rootView.findViewById(R.id.start);
        if (startTextView == null) return;
        SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.my_date_format_sprint), Locale.getDefault());
        startTextView.setText(sprint.getStartDate() != null ? dateFormat.format(sprint.getStartDate()) : "");
    }

    private void setEndTextView(){
        if (rootView == null) return;
        if (sprint == null) return;

        TextView endTextView = (TextView) rootView.findViewById(R.id.end);
        if (endTextView == null) return;
        SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.my_date_format_sprint), Locale.getDefault());
        endTextView.setText(sprint.getEndDate() != null ? dateFormat.format(sprint.getEndDate()) : "");
    }

    private void setDurationTextView(){
        if (rootView == null) return;
        if (sprint == null) return;

        TextView durationTextView = (TextView) rootView.findViewById(R.id.duration);
        if (durationTextView != null) {
            String duration = "";
            if (sprint.getDuration() != null && sprint.getDuration() > 0){
                duration += sprint.getDuration() + " ";
                if (sprint.getDuration() == 1){
                    duration += getString(R.string.day).toLowerCase();
                }else{
                    duration += getString(R.string.days).toLowerCase();
                }
            }

            durationTextView.setText(duration);
        }
    }

    private void setStatusTextView() {
        if (rootView == null) return;
        if (sprint == null) return;

        TextView statusTextView = (TextView) rootView.findViewById(R.id.status);
        if (statusTextView == null) return;
        String status = SprintStatus.getSprintStatus(getActivity(), sprint.getStatus());
        if (status == null) status = "";

        if (sprint.getStatus() != null){
            if (sprint.getStatus().equals(SprintStatus.IN_PROGRESS)){
                long daysAll = Sprint.getDaysFromAllBacklogs(backlogs);
                if (daysAll > 0){
                    if (!status.isEmpty()) status += " ";
                    long daysDone = Sprint.getDaysFromDoneBacklogs(backlogs);
                    double percent = ((double) daysDone / (double) daysAll) * (double) 100;

                    status += "(" + String.format(Locale.getDefault(), "%.1f", percent) + " %)";
                }
            }
        }

        statusTextView.setText(status);
    }

    private void setProjectTextView(){
        if (rootView == null) return;

        TextView projectTextView = (TextView) rootView.findViewById(R.id.sprint_project);
        if (projectTextView == null) return;
        String projectName = project != null ? project.getName() : null;
        projectTextView.setText(projectName != null ? projectName : "");
    }

    private void setBottomToolbar(){
        if (rootView == null) return;
        if (sprint == null) return;

        ImageButton editButton = (ImageButton) rootView.findViewById(R.id.my_button_edit);
        if (editButton != null) {
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) mListener.onSprintEdit(sprint);
                }
            });
        }

        ImageButton addButton = (ImageButton) rootView.findViewById(R.id.my_button_add);
        if (addButton != null) {
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    addBacklogIfAllowed();
                }
            });
        }
    }

    private void getSprintData(){
        if (sprint == null){
            AppShared.writeErrorToLogString(getClass().toString(), "sprint == null");
            return;
        }
        new FirebaseSprintGetData(getActivity(), sprint, new FirebaseSprintGetData.Listener() {
            @Override
            public void onResponse(Project project, ArrayList<Backlog> backlogs, String errorMsg) {

                if (errorMsg != null && !errorMsg.isEmpty()){
                    AppShared.writeErrorToLogString(getClass().toString(), errorMsg);
                }

                SprintShowFragment.this.project = project;
                setProjectTextView();

                SprintShowFragment.this.backlogs = backlogs;
                setDurationTextView();
                setStatusTextView();
                createParents();
                if (adapter != null){
                    adapter.setParents(parents);
                    adapter.notifyDataSetChanged();
                }
            }
        }).execute();
    }

    private void alertBacklogOptions(final Backlog backlog){
        if (backlog == null) {
            AppShared.writeErrorToLogString(getClass().toString(), "backlog == null");
            MyAlertDialog.alertError(getActivity(), null, "backlog == null");
            return;
        }
        if (backlog.getSprintId() == null) {
            AppShared.writeErrorToLogString(getClass().toString(), "backlog.getSprintId() == null");
            MyAlertDialog.alertError(getActivity(), null, "backlog.getSprintId() == null");
            return;
        }
        if (backlog.getSprintId().isEmpty()) {
            AppShared.writeErrorToLogString(getClass().toString(), "backlog.getSprintId().isEmpty()");
            MyAlertDialog.alertError(getActivity(), null, "backlog.getSprintId().isEmpty()");
            return;
        }

        if (mListener != null) {
            mListener.onOpenBacklog(backlog);
        }
    }

    private void addBacklogIfAllowed(){
        if (sprint == null) return;

        String status = sprint.getStatus() != null ? sprint.getStatus() : "";
        if (!status.equals(SprintStatus.IN_PROGRESS)){
            // Only if print is in progress
            MyAlertDialog.alertError(getActivity(), getString(R.string.not_allowed), getString(R.string.sprint_backlog_add_status_error));
            return;
        }

        Person person = AppShared.getLogginUser();
        if (person != null){
            addBacklogIfAllowed(person);
            return;
        }

        new FirebaseGetUser(getActivity(), new FirebaseGetUser.Listener() {
            @Override
            public void onResponse(Person person, String errorMsg) {

                if (errorMsg != null && ! errorMsg.isEmpty()){
                    AppShared.writeErrorToLogString(getClass().toString(), errorMsg);
                    MyAlertDialog.alertError(getActivity(), null, errorMsg);
                }
                addBacklogIfAllowed(person);
            }
        }).execute();
    }

    private void addBacklogIfAllowed(Person person){
        if (person == null){
            AppShared.writeErrorToLogString(getClass().toString(), "person == null");
            MyAlertDialog.alertError(getActivity(), null, "person == null");
            return;
        }

        String role = person.getRole() != null ? person.getRole() : "";
        if (!role.equals(PersonRole.DEVELOPER)){
            // Only developers can add backlogs in sprint
            MyAlertDialog.alertError(getActivity(), getString(R.string.not_allowed), getString(R.string.sprint_backlog_add_person_role_error));
            return;
        }

        if (mListener != null) mListener.onAddSprintBacklog(sprint);
    }

}


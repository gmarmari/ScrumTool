package gr.eap.dxt.sprints;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import gr.eap.dxt.R;
import gr.eap.dxt.projects.DialogProjectSelect;
import gr.eap.dxt.projects.FirebaseProjectGetAll;
import gr.eap.dxt.projects.Project;
import gr.eap.dxt.projects.ProjectStatus;
import gr.eap.dxt.tools.AppShared;
import gr.eap.dxt.tools.FirebaseParse;
import gr.eap.dxt.tools.MyAlertDialog;
import gr.eap.dxt.tools.MyDateTimePicker;
import gr.eap.dxt.tools.MyProgressDialog;

/**
 * Created by GEO on 12/2/2017.
 */

public class SprintListFragment extends Fragment {

    private static final String THIS = "-this-";
    private static final String TOTAL = "-total-";

    public interface FragmentInteractionListener {
        void onShowSprint(Sprint sprint);
        void onAddNewSprint(Project project, Date startDate, long durationDays, int oldNumber);
    }
    private FragmentInteractionListener mListener;

    public SprintListFragment() { }
    public static SprintListFragment newInstance() {
        return new SprintListFragment();
    }

    private View rootView;
    private ListSprintAdapter adapter;
    private ArrayList<Sprint> sprints;
    private ArrayList<Project> projects;
    private static Project project; // static, in order to show the last selected project after view reload

    /**
     * Used when a project is changed or deleted
     */
    public static void clearSeavedProject(){
        project = null;
    }

    private MyProgressDialog myProgressDialog;
    private int index;
    private String myLog;
    private boolean deletingAllSprints;

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
        rootView = inflater.inflate(R.layout.fragment_sprint_list, container, false);

        deletingAllSprints = false;
        setList();
        setListeners();
        setFirebaseListeners();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (projects == null){
            getProjects();
        }else{
            setActivatedPosition(AppShared.sprintListSelPos, true);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setProjectLayoutAccordingToOrientation(newConfig.orientation);
    }

    private void setActivatedPosition(final int position, boolean scroll) {
        if (sprints == null) return;
        if (position < 0 || position >= sprints.size())  return;
        AppShared.sprintListSelPos = position;
        if (rootView == null) return;
        final ListView listView = (ListView) rootView.findViewById(R.id.my_list_view);
        listView.setItemChecked(position, true);
        if (!scroll) return;
        listView.post(new Runnable() {
            public void run() {
                listView.setSelection(position);
            }
        });
    }

    private void setList() {
        if (rootView == null) return;
        final ListView listView = (ListView) rootView.findViewById(R.id.my_list_view);
        if (listView == null) return;

        adapter = new ListSprintAdapter(getActivity(), sprints);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setActivatedPosition(i, false);

                int position = i - listView.getHeaderViewsCount();

                if (sprints == null) {
                    AppShared.writeErrorToLogString(getClass().toString(), "sprints == null");
                    return;
                }

                sprintSelected(position);
            }
        });
    }

    private void setListeners(){
        if (rootView == null) return;

        ImageButton addButton = (ImageButton) rootView.findViewById(R.id.my_button_add);
        if (addButton != null) {
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    addSprints();

                }
            });
        }

        ImageButton delButton = (ImageButton) rootView.findViewById(R.id.my_button_delete);
        if (delButton != null) {
            delButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    deleteSprints();

                }
            });
        }
    }

    private void setFirebaseListeners(){
        if (rootView == null) return;
        final ListView listView = (ListView) rootView.findViewById(R.id.my_list_view);

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Sprint.FIREBASE_LIST);
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (deletingAllSprints) return;
                if (dataSnapshot == null) return;
                try {
                    Sprint sprint = new Sprint();
                    sprint.setSprintId(dataSnapshot.getKey());

                    sprint.setName(FirebaseParse.getString(dataSnapshot.child(Sprint.NAME)));
                    sprint.setDescription(FirebaseParse.getString(dataSnapshot.child(Sprint.DESCRIPTION)));
                    sprint.setProjectId(FirebaseParse.getString(dataSnapshot.child(Sprint.PROJECT_ID)));
                    sprint.setStatus(FirebaseParse.getString(dataSnapshot.child(Sprint.STATUS)));
                    sprint.setStartDate(FirebaseParse.getDate(dataSnapshot.child(Sprint.START_DATE)));
                    sprint.setEndDate(FirebaseParse.getDate(dataSnapshot.child(Sprint.END_DATE)));
                    sprint.setDuration(FirebaseParse.getLong(dataSnapshot.child(Sprint.DURATION)));

                    sprints.add(sprint);
                    if (listView != null) {
                        adapter = new ListSprintAdapter(getActivity(), sprints);
                        listView.setAdapter(adapter);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    AppShared.writeErrorToLogString(getClass().toString(), e.toString());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (deletingAllSprints) return;
                if (dataSnapshot == null) return;
                try {
                    Sprint sprint = Sprint.getSprintWithId(dataSnapshot.getKey(), sprints);
                    if (sprint == null) return;

                    sprint.setName(FirebaseParse.getString(dataSnapshot.child(Sprint.NAME)));
                    sprint.setDescription(FirebaseParse.getString(dataSnapshot.child(Sprint.DESCRIPTION)));
                    sprint.setProjectId(FirebaseParse.getString(dataSnapshot.child(Sprint.PROJECT_ID)));
                    sprint.setStatus(FirebaseParse.getString(dataSnapshot.child(Sprint.STATUS)));
                    sprint.setStartDate(FirebaseParse.getDate(dataSnapshot.child(Sprint.START_DATE)));
                    sprint.setEndDate(FirebaseParse.getDate(dataSnapshot.child(Sprint.END_DATE)));
                    sprint.setDuration(FirebaseParse.getLong(dataSnapshot.child(Sprint.DURATION)));

                    if (adapter != null) adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                    AppShared.writeErrorToLogString(getClass().toString(), e.toString());
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (deletingAllSprints) return;
                if (dataSnapshot == null) return;
                try {
                    Sprint sprint = Sprint.getSprintWithId(dataSnapshot.getKey(), sprints);
                    if (sprint == null) return;

                    sprints.remove(sprint);
                    if (listView != null) {
                        adapter = new ListSprintAdapter(getActivity(), sprints);
                        listView.setAdapter(adapter);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    AppShared.writeErrorToLogString(getClass().toString(), e.toString());
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getProjects(){
        new FirebaseProjectGetAll(getActivity(), new FirebaseProjectGetAll.Listener() {
            @Override
            public void onResponse(ArrayList<Project> projects, String errorMsg) {

                if (errorMsg != null && !errorMsg.isEmpty()){
                    AppShared.writeErrorToLogString(getClass().toString(), errorMsg);
                    MyAlertDialog.alertError(getActivity(), null, errorMsg);
                    return;
                }
                if (projects == null){
                    AppShared.writeErrorToLogString(getClass().toString(), "projects == null");
                    MyAlertDialog.alertError(getActivity(), null, "projects == null");
                    return;
                }

                SprintListFragment.this.projects = projects;
                if (project == null) project = Project.getFirstProjectInProgress(projects);

                setProjectLayout();
                getProjectSprints();

            }
        }).execute();
    }

    private void setProjectLayout(){
        if (rootView == null) return;

        final TextView detailsTextView = (TextView) rootView.findViewById(R.id.project_details);
        if (detailsTextView != null){
            String details = "";
            if (project != null){
                if (project.getName() != null && !project.getName().isEmpty()){
                    details += project.getName();
                }
                if (project.getDescription() != null && !project.getDescription().isEmpty()){
                    if (!details.isEmpty()) details += "\n";
                    details += getString(R.string.description) +": " + project.getDescription();
                }
            }

            detailsTextView.setText(details);
        }

        TableRow tableRow = (TableRow) rootView.findViewById(R.id.project_layout);
        if (tableRow != null){
            tableRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new DialogProjectSelect(getActivity(), projects, new DialogProjectSelect.Listener() {
                        @Override
                        public void onProjectSelected(Project _project) {

                            project = _project;

                            if (detailsTextView != null){
                                String details = "";
                                if (project != null){
                                    if (project.getName() != null && !project.getName().isEmpty()){
                                        details += project.getName();
                                    }
                                    if (project.getDescription() != null && !project.getDescription().isEmpty()){
                                        if (!details.isEmpty()) details += "\n";
                                        details += getString(R.string.description) +": " + project.getDescription();
                                    }
                                }

                                detailsTextView.setText(details);
                            }

                            getProjectSprints();

                        }
                    }).show();

                }
            });
        }

        setProjectLayoutAccordingToOrientation(getResources().getConfiguration().orientation);
    }

    private void getProjectSprints(){
        if (project == null) return;
        if (project.getProjectId() == null || project.getProjectId().isEmpty()) return;

        new FirebaseSprintProjectGet(getActivity(), project.getProjectId(), true, new FirebaseSprintProjectGet.Listener() {
            @Override
            public void onResponse(ArrayList<Sprint> sprints, String errorMsg) {

                if (errorMsg != null && !errorMsg.isEmpty()){
                    MyAlertDialog.alertError(getActivity(), null, errorMsg);
                    return;
                }
                if (sprints == null) {
                    MyAlertDialog.alertError(getActivity(), null, "sprints == null");
                    return;
                }

                SprintListFragment.this.sprints = sprints;

                if (rootView == null) return;
                ListView listView = (ListView) rootView.findViewById(R.id.my_list_view);
                if (listView == null) return;
                adapter = new ListSprintAdapter(getActivity(), SprintListFragment.this.sprints);
                listView.setAdapter(adapter);
                setActivatedPosition(AppShared.sprintListSelPos, true);

            }
        }).execute();
    }

    private void sprintSelected(final int position){
        if (sprints == null){
            AppShared.writeErrorToLogString(getClass().toString(), "sprints == null");
            return;
        }
        if (position < 0){
            AppShared.writeErrorToLogString(getClass().toString(), "position < 0");
            return;
        }
        if (position >= sprints.size()){
            AppShared.writeErrorToLogString(getClass().toString(), "position >= sprints.size()");
            return;
        }

        final Sprint sprint = sprints.get(position);

        if (sprint == null){
            AppShared.writeErrorToLogString(getClass().toString(), "sprint == null");
            MyAlertDialog.alertError(getActivity(), null, "sprint == null");
            return;
        }
        if (sprint.getSprintId() == null){
            AppShared.writeErrorToLogString(getClass().toString(), "sprint.getSprintId()) == null");
            MyAlertDialog.alertError(getActivity(), null, "sprint.getSprintId() == null");
            return;
        }
        if (sprint.getSprintId().isEmpty()){
            AppShared.writeErrorToLogString(getClass().toString(), "sprint.getSprintId()).isEmpty()");
            MyAlertDialog.alertError(getActivity(), null, "sprint.getSprintId().isEmpty()");
            return;
        }

        if (mListener != null) mListener.onShowSprint(sprint);
    }

    private void deleteSprints(){
        if (project == null) return;
        if (project.getStatus() != null){
            if (project.getStatus().equals(ProjectStatus.COMPLETED)){
                MyAlertDialog.alertError(getActivity(), getString(R.string.not_allowed), getString(R.string.project_completed_sprint_del));
                return;
            }
        }

        MyAlertDialog myAlertDialog = new MyAlertDialog(getActivity(),
                getString(R.string.delete),
                R.drawable.ic_action_warning_purple,
                null, MyAlertDialog.MyAlertDialogType.LIST);

        ArrayList<String> items = new ArrayList<>();
        items.add(getString(R.string.last_sprint));
        items.add(getString(R.string.all_sprints));

        myAlertDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0){
                    deleteLastSprint();
                }else if (i == 1){
                    deleteAllSprints();
                }
            }
        });
        myAlertDialog.alertListItems();
    }

    private void deleteLastSprint(){
        if (sprints == null || sprints.isEmpty()) return;

        final Sprint sprint = sprints.get(sprints.size()-1);
        if (sprint == null){
            AppShared.writeErrorToLogString(getClass().toString(), "sprint == null");
            MyAlertDialog.alertError(getActivity(), null, "sprint == null");
            return;
        }
        if (sprint.getSprintId() == null){
            AppShared.writeErrorToLogString(getClass().toString(), "sprint.getSprintId() == null");
            MyAlertDialog.alertError(getActivity(), null, "sprint.getSprintId() == null");
            return;
        }
        if (sprint.getSprintId().isEmpty()){
            AppShared.writeErrorToLogString(getClass().toString(), "sprint.getSprintId().isEmpty()");
            MyAlertDialog.alertError(getActivity(), null, "sprint.getSprintId().isEmpty()");
            return;
        }

        new FirebaseSprintDelete(getActivity(), sprint.getSprintId(), true, new FirebaseSprintDelete.Listener() {
            @Override
            public void onResponse(String errorMsg) {

                if (errorMsg != null && !errorMsg.isEmpty()) {
                    AppShared.writeErrorToLogString(getClass().toString(), errorMsg);
                    MyAlertDialog.alertError(getActivity(), null, errorMsg);
                }

            }
        }).execute();
    }

    private void deleteAllSprints(){
        if (sprints == null || sprints.isEmpty()) return;

        MyAlertDialog myAlertDialog = new MyAlertDialog(getActivity(),
                getString(R.string.delete),
                R.drawable.ic_action_warning_purple,
                getString(R.string.delete_project_sprints), MyAlertDialog.MyAlertDialogType.MESSAGE);

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

                index = -1;
                myLog = "";
                deleteNextSprint();
            }
        });
        myAlertDialog.alertMessage();
    }

    private void deleteNextSprint(){
        if (myProgressDialog != null && myProgressDialog.isCanceled()) return;
        deletingAllSprints = true;
        index++; // At start index = -1, with this line index = 0

        if (index >= sprints.size()){
            allSprintsDeleted();
            return;
        }

        String message = getString(R.string.delete_sprints_progress)
                .replace(THIS, String.valueOf(index+1))
                .replace(TOTAL, String.valueOf(sprints.size()));

        if (myProgressDialog == null){
            myProgressDialog = new MyProgressDialog(getActivity(), message, true);
            myProgressDialog.start();
        }else{
            myProgressDialog.setMessage(message);
        }

        Sprint sprint = sprints.get(index);

        if (sprint == null){
            if (!myLog.isEmpty()) myLog += "/n";
            myLog += "sprint == null";
            deleteNextSprint();
            return;
        }
        if (sprint.getSprintId() == null){
            if (!myLog.isEmpty()) myLog += "/n";
            myLog += "sprint.getSprintId() == null";
            deleteNextSprint();
            return;
        }
        if (sprint.getSprintId().isEmpty()){
            if (!myLog.isEmpty()) myLog += "/n";
            myLog += "sprint.getSprintId().isEmpty()";
            deleteNextSprint();
            return;
        }

        new FirebaseSprintDelete(getActivity(), sprint.getSprintId(), false, new FirebaseSprintDelete.Listener() {
            @Override
            public void onResponse(String errorMsg) {

                if (errorMsg != null && !errorMsg.isEmpty()){
                    if (!myLog.isEmpty()) myLog += "/n";
                    myLog += errorMsg;
                }

                deleteNextSprint();

            }
        }).execute();
    }

    private void allSprintsDeleted(){
        deletingAllSprints = false;
        if (myProgressDialog != null){
            myProgressDialog.stop();
        }

        if (myLog != null && !myLog.isEmpty()) {
            AppShared.writeErrorToLogString(getClass().toString(), myLog);
            MyAlertDialog.alertError(getActivity(), null, myLog);
            return;
        }

        sprints.clear();

        if (rootView == null) return;
        final ListView listView = (ListView) rootView.findViewById(R.id.my_list_view);
        if (listView == null) return;
        adapter = new ListSprintAdapter(getActivity(), sprints);
        listView.setAdapter(adapter);
    }

    private void addSprints(){
        if (project == null){
            MyAlertDialog.alertError(getActivity(), getString(R.string.select_project), getString(R.string.sprint_add_project_select));
            return;
        }
        if (project.getProjectId() == null){
            AppShared.writeErrorToLogString(getClass().toString(), "project.getProjectId() == null");
            MyAlertDialog.alertError(getActivity(), getString(R.string.select_project), "project.getProjectId() == null");
            return;
        }
        if (project.getProjectId().isEmpty()){
            AppShared.writeErrorToLogString(getClass().toString(), "project.getProjectId().isEmpty()");
            MyAlertDialog.alertError(getActivity(), getString(R.string.select_project), "project.getProjectId().isEmpty()");
            return;
        }

        if (project.getStatus() != null){
            if (project.getStatus().equals(ProjectStatus.COMPLETED)){
                MyAlertDialog.alertError(getActivity(), getString(R.string.not_allowed), getString(R.string.project_completed_sprint_add));
                return;
            }
            if (project.getStatus().equals(ProjectStatus.CANCELED)){
                MyAlertDialog.alertError(getActivity(), getString(R.string.not_allowed), getString(R.string.project_canceled_sprint_add));
                return;
            }
        }

        Date startDate = null;
        Date lastEndDate = getLastEndDate();
        if (lastEndDate != null){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(lastEndDate);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            startDate = calendar.getTime();
        }

        long durationDays = getSprintDurationDays();
        int oldNumber = sprints != null ? sprints.size() : 0;

        if (mListener != null) {
            mListener.onAddNewSprint(project, startDate, durationDays, oldNumber);
        }
    }

    private Date getLastEndDate(){
        if (sprints == null || sprints.isEmpty()) return null;

        Sprint lastSprint = sprints.get(sprints.size()-1);
        if (lastSprint == null) return null;

        return lastSprint.getEndDate();
    }

    private long getSprintDurationDays() {
        if (sprints == null || sprints.isEmpty()) return 0;

        Sprint sprint = sprints.get(0);
        if (sprint == null) return 0;
        return MyDateTimePicker.calculateDays(sprint.getStartDate(), sprint.getEndDate());
    }

    private void setProjectLayoutAccordingToOrientation(int orientation){
        if (rootView == null) return;

        if (getResources().getBoolean(R.bool.large_screen)) return;

        TableRow projectRow = (TableRow) rootView.findViewById(R.id.project_layout);
        View projectDivider = rootView.findViewById(R.id.my_divider_project);
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (projectRow != null) projectRow.setVisibility(View.GONE);
            if (projectDivider != null) projectDivider.setVisibility(View.GONE);
        }else{
            if (projectRow != null) projectRow.setVisibility(View.VISIBLE);
            if (projectDivider != null) projectDivider.setVisibility(View.VISIBLE);
        }
    }

}
package gr.eap.dxt.backlog;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
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

import gr.eap.dxt.R;
import gr.eap.dxt.login.FirebaseGetUser;
import gr.eap.dxt.persons.Person;
import gr.eap.dxt.persons.PersonRole;
import gr.eap.dxt.projects.DialogProjectSelect;
import gr.eap.dxt.projects.FirebaseProjectGetAll;
import gr.eap.dxt.projects.Project;
import gr.eap.dxt.tools.AppShared;
import gr.eap.dxt.tools.FirebaseParse;
import gr.eap.dxt.tools.MyAlertDialog;

/**
 * Created by GEO on 12/2/2017.
 */

public class BacklogListFragment extends Fragment {

    public interface FragmentInteractionListener {
        void onShowBacklog(Backlog backlog);
        void onAddNewBacklog();
    }
    private FragmentInteractionListener mListener;

    public BacklogListFragment() { }
    public static BacklogListFragment newInstance() {
        return new BacklogListFragment();
    }

    private View rootView;
    private ListBacklogAdapter adapter;
    private ArrayList<Backlog> backlogs;
    private ArrayList<Project> projects;
    private static Project project; // static, in order to show the last selected project after view reload

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
        rootView = inflater.inflate(R.layout.fragment_backlog_list, container, false);

        setList();
        setFirebaseListeners();
        setBottomToolbar();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (projects == null){
            getProjects();
        }else{
            setActivatedPosition(AppShared.backlogListSelPos, true);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setProjectLayoutAccordingToOrientation(newConfig.orientation);
    }

    private void setActivatedPosition(final int position, boolean scroll) {
        if (backlogs == null) return;
        if (position < 0 || position >= backlogs.size())  return;
        AppShared.backlogListSelPos = position;
        if (rootView == null) return;
        final ListView listView = (ListView) rootView.findViewById(R.id.my_list_view);
        if (listView == null) return;
        listView.setItemChecked(position, true);
        if (!scroll) return;
        listView.post(new Runnable() {
            public void run() {
                listView.smoothScrollToPosition(position);
            }
        });
    }

    private void setList() {
        if (rootView == null) return;

        final ListView listView = (ListView) rootView.findViewById(R.id.my_list_view);
        if (listView == null) return;
        adapter = new ListBacklogAdapter(getActivity(), backlogs);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setActivatedPosition(i, false);

                int position = i - listView.getHeaderViewsCount();

                if (backlogs == null) {
                    AppShared.writeErrorToLogString(getClass().toString(), "backlogs == null");
                    return;
                }

                backlogSelected(position);
            }
        });
    }

    private void setFirebaseListeners(){
        if (rootView == null) return;
        final ListView listView = (ListView) rootView.findViewById(R.id.my_list_view);
        if (listView == null) return;

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Backlog.FIREBASE_LIST);
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot == null) return;
                try {
                    Backlog backlog = new Backlog();
                    backlog.setBacklogId(dataSnapshot.getKey());

                    backlog.setName(FirebaseParse.getString(dataSnapshot.child(Backlog.NAME)));
                    backlog.setDescription(FirebaseParse.getString(dataSnapshot.child(Backlog.DESCRIPTION)));
                    backlog.setPriority(FirebaseParse.getString(dataSnapshot.child(Backlog.PRIORITY)));
                    backlog.setStatus(FirebaseParse.getString(dataSnapshot.child(Backlog.STATUS)));
                    backlog.setDuration(FirebaseParse.getLong(dataSnapshot.child(Backlog.DURATION)));
                    backlog.setPersonId(FirebaseParse.getString(dataSnapshot.child(Backlog.PERSON_ID)));
                    backlog.setSprintId(FirebaseParse.getString(dataSnapshot.child(Backlog.SPRINT_ID)));
                    backlog.setProjectId(FirebaseParse.getString(dataSnapshot.child(Backlog.PROJECT_ID)));
                    backlog.setType(FirebaseParse.getString(dataSnapshot.child(Backlog.TYPE)));

                    backlogs.add(backlog);
                    adapter = new ListBacklogAdapter(getActivity(), backlogs);
                    listView.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                    AppShared.writeErrorToLogString(getClass().toString(), e.toString());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot == null) return;
                try {
                    Backlog backlog = Backlog.getBacklogWithId(dataSnapshot.getKey(), backlogs);
                    if (backlog == null) return;

                    backlog.setName(FirebaseParse.getString(dataSnapshot.child(Backlog.NAME)));
                    backlog.setDescription(FirebaseParse.getString(dataSnapshot.child(Backlog.DESCRIPTION)));
                    backlog.setPriority(FirebaseParse.getString(dataSnapshot.child(Backlog.PRIORITY)));
                    backlog.setStatus(FirebaseParse.getString(dataSnapshot.child(Backlog.STATUS)));
                    backlog.setDuration(FirebaseParse.getLong(dataSnapshot.child(Backlog.DURATION)));
                    backlog.setPersonId(FirebaseParse.getString(dataSnapshot.child(Backlog.PERSON_ID)));
                    backlog.setSprintId(FirebaseParse.getString(dataSnapshot.child(Backlog.SPRINT_ID)));
                    backlog.setProjectId(FirebaseParse.getString(dataSnapshot.child(Backlog.PROJECT_ID)));
                    backlog.setType(FirebaseParse.getString(dataSnapshot.child(Backlog.TYPE)));

                    if (adapter != null) adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                    AppShared.writeErrorToLogString(getClass().toString(), e.toString());
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null) return;
                try {
                    Backlog backlog = Backlog.getBacklogWithId(dataSnapshot.getKey(), backlogs);
                    if (backlog == null) return;

                    backlogs.remove(backlog);
                    adapter = new ListBacklogAdapter(getActivity(), backlogs);
                    listView.setAdapter(adapter);
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

    private void setBottomToolbar(){
        if (rootView == null) return;
        ImageButton addButton = (ImageButton) rootView.findViewById(R.id.my_button_add);
        if (addButton == null) return;
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                handleBacklogAdd();

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

                BacklogListFragment.this.projects = new ArrayList<>();

                if (projects != null) {
                    BacklogListFragment.this.projects.addAll(projects);
                }

                Project noProject = new Project();
                noProject.setName(getString(R.string.unsorted_backlogs));
                noProject.setStatus(null);
                noProject.setProjectId(AppShared.NO_ID);
                BacklogListFragment.this.projects.add(noProject);

                if (project == null) project = Project.getFirstProjectInProgress(BacklogListFragment.this.projects);

                setProjectLayout();
                getProjectBacklogs();

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

                            getProjectBacklogs();

                        }
                    }).show();
                }
            });
        }

        setProjectLayoutAccordingToOrientation(getResources().getConfiguration().orientation);
    }

    private void getProjectBacklogs(){
        if (project == null) return;
        if (project.getProjectId() == null || project.getProjectId().isEmpty()) return;

        new FirebaseProjectBacklogs(getActivity(), project.getProjectId(), true, new FirebaseProjectBacklogs.Listener() {
            @Override
            public void onResponse(ArrayList<Backlog> backlogs, String errorMsg) {

                BacklogListFragment.this.backlogs = backlogs;

                if (rootView == null) return;
                final ListView listView = (ListView) rootView.findViewById(R.id.my_list_view);
                if (listView == null) return;
                adapter = new ListBacklogAdapter(getActivity(), BacklogListFragment.this.backlogs);
                listView.setAdapter(adapter);

                setActivatedPosition(AppShared.backlogListSelPos, true);

            }
        }).execute();

    }

    private void backlogSelected(final int position){
        if (backlogs == null){
            AppShared.writeErrorToLogString(getClass().toString(), "backlogs == null");
            return;
        }
        if (position < 0){
            AppShared.writeErrorToLogString(getClass().toString(), "position < 0");
            return;
        }
        if (position >= backlogs.size()){
            AppShared.writeErrorToLogString(getClass().toString(), "position >= backlogs.size()");
            return;
        }

        final Backlog backlog = backlogs.get(position);

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

        if (mListener != null) mListener.onShowBacklog(backlog);
    }

    private void handleBacklogAdd(){
        Person person = AppShared.getLogginUser();
        if (person != null){
            handleBacklogAdd(person);
        }else{
            new FirebaseGetUser(getActivity(), new FirebaseGetUser.Listener() {
                @Override
                public void onResponse(Person person, String errorMsg) {

                    if (errorMsg != null && !errorMsg.isEmpty()){
                        MyAlertDialog.alertError(getActivity(), null, errorMsg);
                        return;
                    }
                    if (person == null){
                        MyAlertDialog.alertError(getActivity(), null, "person == null");
                        return;
                    }

                    AppShared.setLogginUser(person);
                    handleBacklogAdd(person);
                }
            }).execute();
        }
    }
    private void handleBacklogAdd(Person person){
        if (person == null) {
            MyAlertDialog.alertError(getActivity(), null, "person == null");
            return;
        }

        String personRole = person.getRole() != null ? person.getRole() : "";
        if (!personRole.equals(PersonRole.PRODUCT_OWNER)) {
            MyAlertDialog.alertError(getActivity(), getString(R.string.not_allowed), getString(R.string.backlog_new_not_allowed_message));
            return;
        }

        if (mListener != null){
            mListener.onAddNewBacklog();
        }
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

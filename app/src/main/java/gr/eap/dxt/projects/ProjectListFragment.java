package gr.eap.dxt.projects;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import gr.eap.dxt.R;
import gr.eap.dxt.tools.AppShared;
import gr.eap.dxt.tools.FirebaseParse;
import gr.eap.dxt.tools.MyAlertDialog;

/**
 * Created by GEO on 8/2/2017.
 */

public class ProjectListFragment extends Fragment {

    public interface FragmentInteractionListener {
        void onShowProject(Project project);
        void onAddNewProject();
    }
    private FragmentInteractionListener mListener;

    public ProjectListFragment() { }
    public static ProjectListFragment newInstance() {
        return new ProjectListFragment();
    }


    private View rootView;
    private ListProjectAdapter adapter;
    private ArrayList<Project> projects;

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
        rootView = inflater.inflate(R.layout.fragment_project_list, container, false);

        setList();
        setFirebaseListeners();
        setBottomToolbar();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (projects == null){
            getAllProjects();
        }else{
            setActivatedPosition(AppShared.projectListSelPos, true);
        }
    }

    private void setActivatedPosition(final int position, boolean scroll) {
        if (projects == null) return;
        if (position < 0 || position >= projects.size())  return;
        AppShared.projectListSelPos = position;
        if (rootView == null) return;
        final ListView listView = (ListView) rootView.findViewById(R.id.my_list_view);
        listView.setItemChecked(position, true);
        if (!scroll) return;
        listView.post(new Runnable() {
            public void run() {
                listView.smoothScrollToPosition(position);
            }
        });
    }

    private void setList(){
        if (rootView == null) return;
        final ListView listView = (ListView) rootView.findViewById(R.id.my_list_view);
        if (listView == null) return;
        adapter = new ListProjectAdapter(getActivity(), projects);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setActivatedPosition(i, false);

                int position = i - listView.getHeaderViewsCount();

                if (projects == null) {
                    AppShared.writeErrorToLogString(getClass().toString(), "projects == null");
                    return;
                }

                projectSelected(position);
            }
        });
    }

    private void setFirebaseListeners(){
        if (rootView == null) return;
        final ListView listView = (ListView) rootView.findViewById(R.id.my_list_view);
        if (listView == null) return;
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Project.FIREBASE_LIST);
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot == null) return;
                try {
                    Project project = new Project();
                    project.setProjectId(dataSnapshot.getKey());

                    project.setName(FirebaseParse.getString(dataSnapshot.child(Project.NAME)));
                    project.setDescription(FirebaseParse.getString(dataSnapshot.child(Project.DESCRIPTION)));
                    project.setStatus(FirebaseParse.getString(dataSnapshot.child(Project.STATUS)));
                    project.setStartDate(FirebaseParse.getDate(dataSnapshot.child(Project.START_DATE)));

                    projects.add(project);
                    adapter = new ListProjectAdapter(getActivity(), projects);
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
                    Project project = getProjectWithId(dataSnapshot.getKey());
                    if (project == null) return;

                    project.setName(FirebaseParse.getString(dataSnapshot.child(Project.NAME)));
                    project.setDescription(FirebaseParse.getString(dataSnapshot.child(Project.DESCRIPTION)));
                    project.setStatus(FirebaseParse.getString(dataSnapshot.child(Project.STATUS)));
                    project.setStartDate(FirebaseParse.getDate(dataSnapshot.child(Project.START_DATE)));

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
                    Project project = getProjectWithId(dataSnapshot.getKey());
                    if (project == null) return;

                    projects.remove(project);
                    adapter = new ListProjectAdapter(getActivity(), projects);
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
        ImageButton addButton = (ImageButton) rootView.findViewById(R.id.my_button_add);
        if (addButton == null) return;
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mListener != null) {
                    mListener.onAddNewProject();
                }

            }
        });
    }

    private void getAllProjects(){
        new FirebaseProjectGetAll(getActivity(), new FirebaseProjectGetAll.Listener() {
            @Override
            public void onResponse(ArrayList<Project> _projects, String errorMsg) {

                if (errorMsg != null && !errorMsg.isEmpty()){
                    MyAlertDialog.alertError(getActivity(), null, errorMsg);
                    return;
                }
                if (_projects == null){
                    MyAlertDialog.alertError(getActivity(), null, "projects == null");
                    return;
                }

                projects = _projects;
                if (rootView == null) return;
                final ListView listView = (ListView) rootView.findViewById(R.id.my_list_view);
                if (listView == null) return;
                adapter = new ListProjectAdapter(getActivity(), projects);
                listView.setAdapter(adapter);
                setActivatedPosition(AppShared.projectListSelPos, true);
            }
        }).execute();
    }

    private Project getProjectWithId(String projectId){
        if (projectId == null || projectId.isEmpty()) return null;
        if (projects == null || projects.isEmpty()) return null;

        for (Project project : projects) {
            if (project != null){
                if (project.getProjectId() != null){
                    if (project.getProjectId().equals(projectId)) return project;
                }
            }
        }

        return null;
    }

    private void projectSelected(final int position){
        if (projects == null){
            AppShared.writeErrorToLogString(getClass().toString(), "projects == null");
            return;
        }
        if (position < 0){
            AppShared.writeErrorToLogString(getClass().toString(), "position < 0");
            return;
        }
        if (position >= projects.size()){
            AppShared.writeErrorToLogString(getClass().toString(), "position >= projects.size()");
            return;
        }

        final Project project = projects.get(position);

        if (project == null){
            AppShared.writeErrorToLogString(getClass().toString(), "project == null");
            MyAlertDialog.alertError(getActivity(), null, "project == null");
            return;
        }
        if (project.getProjectId() == null){
            AppShared.writeErrorToLogString(getClass().toString(), "project.getProjectId() == null");
            MyAlertDialog.alertError(getActivity(), null, "project.getProjectId()) == null");
            return;
        }
        if (project.getProjectId().isEmpty()){
            AppShared.writeErrorToLogString(getClass().toString(), "project.getProjectId().isEmpty()");
            MyAlertDialog.alertError(getActivity(), null, "project.getProjectId().isEmpty()");
            return;
        }

        if (mListener != null) mListener.onShowProject(project);
    }

}

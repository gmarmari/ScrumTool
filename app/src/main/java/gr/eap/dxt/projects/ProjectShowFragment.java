package gr.eap.dxt.projects;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import gr.eap.dxt.sprints.FirebaseSprintProjectGet;
import gr.eap.dxt.sprints.ListSprintGroupedProjectAdapter;
import gr.eap.dxt.sprints.ListSprintGroupedProjectParent;
import gr.eap.dxt.sprints.Sprint;
import gr.eap.dxt.sprints.SprintListFragment;
import gr.eap.dxt.tools.AppShared;
import gr.eap.dxt.tools.MyAlertDialog;
import gr.eap.dxt.tools.MyToast;

/**
 * Created by GEO on 11/2/2017.
 */

public class ProjectShowFragment extends Fragment {

    public interface FragmentInteractionListener {
        void onProjectEdit();
    }
    private FragmentInteractionListener mListener;

    public ProjectShowFragment(){}

    private View rootView;

    private Project project;
    private ListSprintGroupedProjectAdapter adapter;
    private ListSprintGroupedProjectParent parent;
    private ArrayList<Sprint> sprints;

    public static ProjectShowFragment newInstance(Project project){
        ProjectShowFragment fragment = new ProjectShowFragment();
        fragment.project = project;
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

    private void createParents(){
        parent = new ListSprintGroupedProjectParent();
        if (sprints != null){
            parent.getChildren().addAll(sprints);
        }
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_project_show, container, false);
        View header = inflater.inflate(R.layout.fragment_project_show_header, null, false);
        if (rootView != null){
            ExpandableListView listView = (ExpandableListView) rootView.findViewById(R.id.my_expandable_listview);
            if (listView != null) {
                if (header != null) {
                    listView.addHeaderView(header);
                }
            }
        }

        setList();
        setNameTextview();
        setDescriptionTextview();
        setStatusTextview();
        setStartTextview();
        setListeners();

        return rootView;
    }

    private void setList(){
        if (rootView == null) return;
        ExpandableListView listView = (ExpandableListView) rootView.findViewById(R.id.my_expandable_listview);
        if (listView == null) return;

        adapter = new ListSprintGroupedProjectAdapter(getActivity(), parent);
        listView.setAdapter(adapter);
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                if (parent == null) return false;
                parent.setIsOn(!parent.isOn());
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                return false;
            }
        });
    }

    private void setNameTextview(){
        if (rootView == null) return;
        if (project == null) return;

        TextView nameTextView = (TextView) rootView.findViewById(R.id.name);
        if (nameTextView == null) return;
        nameTextView.setText(project.getName() != null ? project.getName() : "");
    }

    private void setDescriptionTextview(){
        if (rootView == null) return;
        if (project == null) return;

        TextView descrTextView = (TextView) rootView.findViewById(R.id.description);
        if (descrTextView == null) return;
        descrTextView.setText(project.getDescription() != null ? project.getDescription() : "");
    }

    private void setStatusTextview(){
        if (rootView == null) return;
        if (project == null) return;

        TextView statusTextView = (TextView) rootView.findViewById(R.id.status);
        if (statusTextView == null) return;
        String status = ProjectStatus.getProjectStatus(getActivity(), project.getStatus());
        statusTextView.setText(status != null ? status : "");
    }

    private void setStartTextview(){
        if (rootView == null) return;
        if (project == null) return;

        TextView startTextView = (TextView) rootView.findViewById(R.id.start);
        if (startTextView == null) return;
        SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.my_date_format_sprint), Locale.getDefault());
        startTextView.setText(project.getStartDate() != null ? dateFormat.format(project.getStartDate()) : "");
    }

    private void setListeners(){
        if (rootView == null) return;

        ImageButton editButton = (ImageButton) rootView.findViewById(R.id.my_button_edit);
        if (editButton != null) {
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) mListener.onProjectEdit();
                }
            });
        }

        ImageButton deleteButton = (ImageButton) rootView.findViewById(R.id.my_button_delete);
        if (deleteButton != null) {
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteProject();
                }
            });
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getProjectSprints();
    }

    private void getProjectSprints(){
        if (project == null){
            AppShared.writeErrorToLogString(getClass().toString(), "project == null");
            return;
        }
        if (project.getProjectId() == null){
            AppShared.writeErrorToLogString(getClass().toString(), "project.getProjectId() == null");
            return;
        }
        if (project.getProjectId().isEmpty()){
            AppShared.writeErrorToLogString(getClass().toString(), "project.getProjectId().isEmpty()");
            return;
        }

        new FirebaseSprintProjectGet(getActivity(), project.getProjectId(), true, new FirebaseSprintProjectGet.Listener() {
            @Override
            public void onResponse(ArrayList<Sprint> _sprints, String errorMsg) {

                if (errorMsg != null && !errorMsg.isEmpty()){
                    MyAlertDialog.alertError(getActivity(), null, errorMsg);
                    return;
                }
                if (_sprints == null){
                    MyAlertDialog.alertError(getActivity(), null, "sprints == null");
                    return;
                }

                sprints = _sprints;
                createParents();
                if (adapter != null){
                    adapter.setParent(parent);
                    adapter.notifyDataSetChanged();
                }
            }
        }).execute();
    }

    private void deleteProject(){
        if (project == null){
            AppShared.writeErrorToLogString(getClass().toString(), "project == null");
            MyAlertDialog.alertError(getActivity(), null, "project == null");
            return;
        }
        if (project.getProjectId() == null){
            AppShared.writeErrorToLogString(getClass().toString(), "project.getProjectId() == null");
            MyAlertDialog.alertError(getActivity(), null, "project.getProjectId() == null");
            return;
        }
        if (project.getProjectId().isEmpty()){
            AppShared.writeErrorToLogString(getClass().toString(), "project.getProjectId().isEmpty()");
            MyAlertDialog.alertError(getActivity(), null, "project.getProjectId().isEmpty()");
            return;
        }

        MyAlertDialog myAlertDialog = new MyAlertDialog(getActivity(), getString(R.string.delete_project),R.drawable.ic_action_warning_purple, null, MyAlertDialog.MyAlertDialogType.MESSAGE);
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

                new FirebaseProjectDelete(getActivity(), project.getProjectId(), new FirebaseProjectDelete.Listener() {
                    @Override
                    public void onResponse(String errorMsg) {

                        if (errorMsg != null && !errorMsg.isEmpty()){
                            AppShared.writeErrorToLogString(getClass().toString(), errorMsg);
                            MyAlertDialog.alertError(getActivity(), null, errorMsg);
                        }else{
                            new MyToast(getActivity(), R.string.project_deleted).show();

                            SprintListFragment.clearSeavedProject();
                            Intent data = new Intent();
                            data.putExtra(ProjectEditFragment.RELOAD, true);
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

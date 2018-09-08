package gr.eap.dxt.projects;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import gr.eap.dxt.R;
import gr.eap.dxt.sprints.SprintListFragment;
import gr.eap.dxt.tools.AppShared;
import gr.eap.dxt.tools.MyAlertDialog;
import gr.eap.dxt.tools.MyDateTimePicker;
import gr.eap.dxt.tools.MyToast;
import gr.eap.dxt.tools.SpinnerSimpleAdapter;

/**
 * Created by GEO on 11/2/2017.
 */

public class ProjectEditFragment extends Fragment {

    public static final String RELOAD = "gr.eap.dxt.projects.ProjectEditFragment.RELOAD";

    public ProjectEditFragment(){}

    private Project project;
    private boolean changeMade;

    public static ProjectEditFragment newInstance(Project project){
        ProjectEditFragment fragment = new ProjectEditFragment();
        fragment.project = Project.getCopy(project);
        if (fragment.project == null){
            // New project: projectId = null
            fragment.project = new Project();
            fragment.project.setStatus(ProjectStatus.NOT_STARTED);
        }
        return fragment;
    }

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_project_edit, container, false);

        if (project == null){
            AppShared.writeErrorToLogString(getClass().toString(), "project == null");
            return rootView;
        }

        setNameEditText();
        setDescriptionEditText();
        setStatusSpinner();
        setStartTextView();

        changeMade = false;

        return rootView;
    }

    private void setNameEditText(){
        if (rootView == null) return;
        if (project == null) return;

        EditText editText = (EditText) rootView.findViewById(R.id.name);
        if (editText == null) return;

        editText.setText(project.getName() != null ? project.getName() : "");
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

                project.setName(editable.toString());
                changeMade = true;
            }
        });
    }

    private void setDescriptionEditText(){
        if (rootView == null) return;
        if (project == null) return;

        EditText editText = (EditText) rootView.findViewById(R.id.description);
        if (editText == null) return;

        editText.setText(project.getDescription() != null ? project.getDescription() : "");
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

                project.setDescription(editable.toString());
                changeMade = true;
            }
        });
    }

    private void setStatusSpinner(){
        if (rootView == null) return;
        if (project == null) return;

        Spinner spinner = (Spinner) rootView.findViewById(R.id.my_spinner_status);
        if (spinner == null) return;

        ArrayList<String> items = new ArrayList<>();
        items.add(getString(R.string.not_started));
        items.add(getString(R.string.in_progress));
        items.add(getString(R.string.completed));
        items.add(getString(R.string.canceled));

        SpinnerSimpleAdapter adapter = new SpinnerSimpleAdapter(getActivity(), R.layout.spinner_simple_item_small_font, items);
        spinner.setAdapter(adapter);

        if (project.getStatus() != null){
            switch (project.getStatus()) {
                case ProjectStatus.NOT_STARTED:
                    spinner.setSelection(0);
                    break;
                case ProjectStatus.IN_PROGRESS:
                    spinner.setSelection(1);
                    break;
                case ProjectStatus.COMPLETED:
                    spinner.setSelection(2);
                    break;
                case ProjectStatus.CANCELED:
                    spinner.setSelection(3);
                    break;
            }
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                if (pos == 0){
                    if (project.getStatus() == null || !project.getStatus().equals(ProjectStatus.NOT_STARTED)){
                        project.setStatus(ProjectStatus.NOT_STARTED);
                        changeMade = true;
                    }
                }else if (pos == 1){
                    if (project.getStatus() == null || !project.getStatus().equals(ProjectStatus.IN_PROGRESS)){
                        project.setStatus(ProjectStatus.IN_PROGRESS);
                        changeMade = true;
                    }
                }else if (pos == 2){
                    if (project.getStatus() == null || !project.getStatus().equals(ProjectStatus.COMPLETED)){
                        project.setStatus(ProjectStatus.COMPLETED);
                        changeMade = true;
                    }
                }else if (pos == 3){
                    if (project.getStatus() == null || !project.getStatus().equals(ProjectStatus.CANCELED)){
                        project.setStatus(ProjectStatus.CANCELED);
                        changeMade = true;
                    }
                }else{
                    AppShared.writeErrorToLogString(getClass().toString(), "Not supported selected position for spinner project status: "+ pos);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setStartTextView(){
        if (rootView == null) return;
        if (project == null) return;

        final TextView startTextView = (TextView) rootView.findViewById(R.id.start);
        if (startTextView != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.my_date_format_sprint), Locale.getDefault());
            startTextView.setText(project.getStartDate() != null ? dateFormat.format(project.getStartDate()) : "");
        }

        TableRow tableRow = (TableRow)  rootView.findViewById(R.id.project_start_row);
        if (tableRow != null) {
            tableRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new MyDateTimePicker(getActivity(), project.getStartDate(), MyDateTimePicker.DateType.ONLY_DATE, new MyDateTimePicker.MyListener() {
                        @Override
                        public void onDateSelected(Date date) {

                            if (date == null) return;
                            project.setStartDate(date);
                            if (startTextView != null) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.my_date_format_sprint), Locale.getDefault());
                                startTextView.setText(project.getStartDate() != null ? dateFormat.format(project.getStartDate()) : "");
                            }
                            changeMade = true;

                        }
                    }).setContentAndShow();

                }
            });
        }
    }

    /** From {@link ProjectDialogActivity} and {@link ProjectNewDialogActivity}*/
    protected void saveChanges(){
        if (!changeMade){
            new MyToast(getActivity(), R.string.no_change_occured).show();
            return;
        }

        if (project == null){
            AppShared.writeErrorToLogString(getClass().toString(), "project == null");
            MyAlertDialog.alertError(getActivity(), null, "project == null");
            return;
        }

        if (project.getProjectId() == null){
            // new Project
            new FirebaseProjectAdd(getActivity(), project, new FirebaseProjectAdd.Listener() {
                @Override
                public void onResponse(String errorMsg) {

                    if (errorMsg != null && !errorMsg.isEmpty()){
                        AppShared.writeErrorToLogString(getClass().toString(), errorMsg);
                        MyAlertDialog.alertError(getActivity(), null, errorMsg);
                    }else{
                        Intent data = new Intent();
                        data.putExtra(RELOAD, true);
                        getActivity().setResult(Activity.RESULT_OK, data);
                        getActivity().finish();
                    }

                }
            }).execute();
        }else{
            // Edit Project
            new FirebaseProjectEdit(getActivity(), project, new FirebaseProjectEdit.Listener() {
                @Override
                public void onResponse(String errorMsg) {

                    if (errorMsg != null && !errorMsg.isEmpty()){
                        AppShared.writeErrorToLogString(getClass().toString(), errorMsg);
                        MyAlertDialog.alertError(getActivity(), null, errorMsg);
                    }else{
                        SprintListFragment.clearSeavedProject();
                        Intent data = new Intent();
                        data.putExtra(RELOAD, true);
                        getActivity().setResult(Activity.RESULT_OK, data);
                        getActivity().finish();
                    }

                }
            }).execute();
        }
    }
}

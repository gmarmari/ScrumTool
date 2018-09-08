package gr.eap.dxt.sprints;


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


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import gr.eap.dxt.R;
import gr.eap.dxt.projects.Project;
import gr.eap.dxt.tools.AppShared;
import gr.eap.dxt.tools.Keyboard;
import gr.eap.dxt.tools.MyAlertDialog;
import gr.eap.dxt.tools.MyDateTimePicker;
import gr.eap.dxt.tools.MyProgressDialog;

/**
 * Created by GEO on 12/3/2017.
 */

public class SprintNewFragment extends Fragment {

    public static final String RELOAD = "gr.eap.dxt.sprints.SprintNewFragment.RELOAD";

    private static final String THIS = "-this-";
    private static final String TOTAL = "-total-";

    public SprintNewFragment(){}

    private Project project;
    private Date startDate;
    private long durationDays;
    private boolean changeDateAndDuration;

    private int number;
    private int index;
    private int oldNumber;

    MyProgressDialog myProgressDialog;
    private String myLog;

    public static SprintNewFragment newInstance(Project project, Date startDate, long durationDays, int oldNumber){
        SprintNewFragment fragment = new SprintNewFragment();
        fragment.project = project;
        fragment.startDate = startDate;
        fragment.durationDays = durationDays > 0 ? durationDays : 0;
        fragment.oldNumber = oldNumber > 0 ? oldNumber : 0;
        fragment.number = 0;
        fragment.changeDateAndDuration = startDate == null;
        return fragment;
    }

    private View rootView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_sprint_new, container, false);

        setProjectTextView();
        setNumberEditText();
        setStartDateEditText();
        setDurationEditText();

        return rootView;
    }

    private void setProjectTextView(){
        if (rootView == null) return;
        if (project == null) return;

        TextView textView = (TextView) rootView.findViewById(R.id.project_details);
        if (textView == null) return;

        String details = "";
        if (project.getName() != null && !project.getName().isEmpty()){
            details += project.getName();
        }
        if (project.getDescription() != null && !project.getDescription().isEmpty()){
            if (!details.isEmpty()) details += "\n";
            details += getString(R.string.description) +": " + project.getDescription();
        }
        textView.setText(details);
    }

    private void setNumberEditText(){
        if (rootView == null) return;
        if (project == null) return;
        EditText editText = (EditText) rootView.findViewById(R.id.number);
        if (editText == null) return;

        editText.setText(number > 0 ? String.valueOf(number) : "");
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

                try {
                    number = Integer.parseInt(editable.toString());
                    if (number < 0) number = 0;
                }catch (Exception e){
                    e.printStackTrace();
                    AppShared.writeErrorToLogString(getClass().toString(), e.toString());
                    number = 0;
                }
            }
        });
    }

    private void setStartDateEditText(){
        if (rootView == null) return;
        if (project == null) return;
        final EditText editText = (EditText) rootView.findViewById(R.id.start_date);
        if (editText == null) return;
        editText.setLongClickable(false);

        final SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.my_date_format_sprint), Locale.getDefault());
        editText.setText(startDate != null ? dateFormat.format(startDate) : "");

        if (changeDateAndDuration){
            editText.setClickable(true);
            editText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new MyDateTimePicker(getActivity(), null, MyDateTimePicker.DateType.ONLY_DATE, new MyDateTimePicker.MyListener() {
                        @Override
                        public void onDateSelected(Date date) {

                            if (date.before(new Date())){
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.HOUR, 0);
                                calendar.set(Calendar.MINUTE, 0);
                                calendar.set(Calendar.SECOND, 0);
                                calendar.set(Calendar.MILLISECOND, 0);
                                startDate = calendar.getTime();
                            }else{
                                startDate = date;
                            }

                            editText.setText(dateFormat.format(startDate));

                        }
                    }).setContentAndShow();

                }
            });

        }else{
            editText.setClickable(false);
        }
    }

    private void setDurationEditText(){
        if (rootView == null) return;
        if (project == null) return;
        final EditText editText = (EditText) rootView.findViewById(R.id.duration);
        if (editText == null) return;

        editText.setText(durationDays > 0 ? String.valueOf(durationDays) : "");
        if (changeDateAndDuration){
            editText.setFocusable(true);
            editText.setClickable(true);
            editText.setLongClickable(true);

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
                    if (editable.toString().isEmpty()) return;

                    try {
                        durationDays = Integer.parseInt(editable.toString());
                        if (durationDays < 0) durationDays = 0;
                    }catch (Exception e){
                        e.printStackTrace();
                        AppShared.writeErrorToLogString(getClass().toString(), e.toString());
                        durationDays = 0;
                        editText.setText("");
                    }

                    if (durationDays > 31){
                        durationDays = 0;
                        MyAlertDialog.alertError(getActivity(), getString(R.string.not_allowed), getString(R.string.sprint_duration_error));
                        editText.setText("");
                    }
                }
            });
        }else{

            editText.setFocusable(false);
            editText.setClickable(false);
            editText.setLongClickable(false);
        }
    }

    /** From {@link SprintDialogActivity}*/
    protected void saveChanges(){
        Keyboard.close(getActivity());

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
        if (number <= 0) {
            MyAlertDialog.alertError(getActivity(), null, getString(R.string.sprint_number_set));
            return;
        }
        if (durationDays <= 0) {
            MyAlertDialog.alertError(getActivity(), null, getString(R.string.sprint_duration_set));
            return;
        }
        if (durationDays > 31) {
            MyAlertDialog.alertError(getActivity(), getString(R.string.not_allowed), getString(R.string.sprint_duration_error));
            return;
        }
        if (startDate == null){
            MyAlertDialog.alertError(getActivity(), null, getString(R.string.sprint_date_set));
            return;
        }

        index = -1;
        myLog = "";
        addNextSprint();
    }

    private void addNextSprint(){
        if (myProgressDialog != null && myProgressDialog.isCanceled()) return;

        index++; // At start index = -1, so with this line index = 0
        if (index >= number){
            allSprintsAdded();
            return;
        }

        String message = getString(R.string.add_sprints_progress)
                .replace(THIS, String.valueOf(index+1))
                .replace(TOTAL, String.valueOf(number));

        if (myProgressDialog == null){
            myProgressDialog = new MyProgressDialog(getActivity(), message, true);
            myProgressDialog.start();
        }else{
            myProgressDialog.setMessage(message);
        }


        Sprint sprint = new Sprint();
        if (project != null){ // Will not be null, check already
            sprint.setProjectId(project.getProjectId());
        }
        sprint.setStartDate(startDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DAY_OF_MONTH, (int) durationDays);
        sprint.setEndDate(calendar.getTime());

        Sprint.calculateDuration(sprint);

        String name = "";
        if (project != null){
            if (project.getName() != null && !project.getName().isEmpty()){
                name += project.getName() + ", ";
            }
        }
        name += getString(R.string.sprint) + " ";
        int no = oldNumber > 0 ? oldNumber : 0;
        no += index;
        no += 1;
        name += String.valueOf(no);
        sprint.setName(name);

        // Start Date for the next sprint = end date of this sprint + 1 day
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        startDate = calendar.getTime();

        new FirebaseSprintAdd(getActivity(), sprint, false, new FirebaseSprintAdd.Listener() {
            @Override
            public void onResponse(String errorMsg) {
                if (errorMsg != null && !errorMsg.isEmpty()){
                    if (!myLog.isEmpty()) myLog += "\n";
                    myLog += errorMsg;
                }

                addNextSprint();
            }
        }).execute();
    }

    private void allSprintsAdded(){
        if (myProgressDialog != null){
            myProgressDialog.stop();
        }

        if (myLog != null && ! myLog.isEmpty()){
            AppShared.writeErrorToLogString(getClass().toString(), myLog);
            MyAlertDialog.alertError(getActivity(), null, myLog);
        }else{
            Intent data = new Intent();
            data.putExtra(RELOAD, true);
            getActivity().setResult(Activity.RESULT_OK, data);
            getActivity().finish();
        }
    }
}

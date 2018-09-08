package gr.eap.dxt.backlog;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
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

import java.util.ArrayList;

import gr.eap.dxt.R;
import gr.eap.dxt.persons.DialogPersonSelect;
import gr.eap.dxt.persons.Person;
import gr.eap.dxt.persons.PersonRole;
import gr.eap.dxt.sprints.DialogSprintSelect;
import gr.eap.dxt.sprints.Sprint;
import gr.eap.dxt.tools.AppShared;
import gr.eap.dxt.tools.MyAlertDialog;
import gr.eap.dxt.tools.MySpinnerDialog;
import gr.eap.dxt.tools.MyToast;
import gr.eap.dxt.tools.SpinnerSimpleAdapter;

/**
 * Created by GEO on 12/2/2017.
 */

public class BacklogEditFragment extends Fragment {

    public enum BacklogEditFrom{
        NEW,
        EDIT,
        SPRINT
    }
    BacklogEditFrom from;

    public interface FragmentInteractionListener {
        void onBacklogChangesSaved(String errorMsg);
    }
    private FragmentInteractionListener mListener;

    public static final String RELOAD = "gr.eap.dxt.backlog.BacklogEditFragment.RELOAD";

    public BacklogEditFragment(){}

    private Backlog backlog;
    private Person person;
    private Sprint sprint;
    private boolean changeMade;

    private boolean canChangeStatus;
    private boolean canChangePerson;
    private boolean canChangeSprint;

    public static BacklogEditFragment newInstance(Backlog backlog, Person person, Sprint sprint, BacklogEditFrom from){
        BacklogEditFragment fragment = new BacklogEditFragment();
        fragment.backlog = Backlog.getCopy(backlog);
        fragment.person = Person.getCopy(person);
        fragment.sprint = Sprint.getCopy(sprint);

        if (fragment.backlog == null){
            // New backlog: backlogId = null
            fragment.backlog = new Backlog();
            fragment.backlog.setType(BacklogType.FEATURE);
        }

        fragment.from = from;
        return fragment;
    }

    private View rootView;

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
        rootView = inflater.inflate(R.layout.fragment_backlog_edit, container, false);

        if (backlog == null){
            AppShared.writeErrorToLogString(getClass().toString(), "project == null");
            return rootView;
        }

        canChangeStatus = Backlog.checkIfCanEditStatus(backlog, sprint);
        canChangePerson = Backlog.checkIfCanChangePerson(backlog);
        canChangeSprint = Backlog.checkIfCanChangeSprint(backlog, from);

        setNameEditText();
        setDescriptionEditText();
        setDurationEditText();
        setPrioSpinner();
        setTypeSpinner();
        setStatusSpinner();
        setPersonLayout();
        setSprintLayout();

        changeMade = false;

        return rootView;
    }



    private void setNameEditText(){
        if (rootView == null) return;
        if (backlog == null) return;

        EditText editText = (EditText) rootView.findViewById(R.id.name);
        if (editText == null) return;

        editText.setText(backlog.getName() != null ? backlog.getName() : "");
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

                backlog.setName(editable.toString());
                changeMade = true;
            }
        });
    }

    private void setDescriptionEditText(){
        if (rootView == null) return;
        if (backlog == null) return;

        EditText editText = (EditText) rootView.findViewById(R.id.description);
        if (editText == null) return;

        editText.setText(backlog.getDescription() != null ? backlog.getDescription() : "");
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

                backlog.setDescription(editable.toString());
                changeMade = true;
            }
        });
    }

    private void setDurationEditText(){
        if (rootView == null) return;
        if (backlog == null) return;

        EditText editText = (EditText) rootView.findViewById(R.id.duration);
        if (editText == null) return;

        String duration = null;
        if (backlog.getDuration() != null && backlog.getDuration() > 0){
            duration = backlog.getDuration().toString();
        }
        editText.setText(duration != null ? duration : "");
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

                Long duration;
                try {
                    if (editable.toString().isEmpty()){
                        duration = (long) 0;
                    }else {
                        duration = Long.parseLong(editable.toString());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    AppShared.writeErrorToLogString(getClass().toString(), e.toString());
                    duration = null;
                }
                if (duration != null){
                    if (duration < 0) duration = (long) 0;
                    backlog.setDuration(duration);
                    changeMade = true;
                }
            }
        });
    }

    private void setPrioSpinner(){
        if (rootView == null) return;
        if (backlog == null) return;

        Spinner spinner = (Spinner) rootView.findViewById(R.id.my_spinner_prio);
        if (spinner == null) return;

        ArrayList<String> items = new ArrayList<>();
        items.add(getString(R.string.prio_1_urgent));
        items.add(getString(R.string.prio_2_important));
        items.add(getString(R.string.prio_3_normal));
        items.add(getString(R.string.prio_4_not_important));

        SpinnerSimpleAdapter adapter = new SpinnerSimpleAdapter(getActivity(), R.layout.spinner_simple_item_small_font, items);
        spinner.setAdapter(adapter);

        if (backlog.getPriority() != null){
            switch (backlog.getPriority()) {
                case Priority.PRIO_1_URGENT:
                    spinner.setSelection(0);
                    break;
                case Priority.PRIO_2_IMPORTANT:
                    spinner.setSelection(1);
                    break;
                case Priority.PRIO_3_NORMAL:
                    spinner.setSelection(2);
                    break;
                case Priority.PRIO_4_NOT_IMPORTANT:
                    spinner.setSelection(3);
                    break;
            }
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                if (pos == 0){
                    if (backlog.getPriority() == null || !backlog.getPriority().equals(Priority.PRIO_1_URGENT)){
                        backlog.setPriority(Priority.PRIO_1_URGENT);
                        changeMade = true;
                    }
                }else if (pos == 1){
                    if (backlog.getPriority() == null || !backlog.getPriority().equals(Priority.PRIO_2_IMPORTANT)){
                        backlog.setPriority(Priority.PRIO_2_IMPORTANT);
                        changeMade = true;
                    }
                }else if (pos == 2){
                    if (backlog.getPriority() == null || !backlog.getPriority().equals(Priority.PRIO_3_NORMAL)){
                        backlog.setPriority(Priority.PRIO_3_NORMAL);
                        changeMade = true;
                    }
                }else if (pos == 3){
                    if (backlog.getPriority() == null || !backlog.getPriority().equals(Priority.PRIO_4_NOT_IMPORTANT)){
                        backlog.setPriority(Priority.PRIO_4_NOT_IMPORTANT);
                        changeMade = true;
                    }
                }else{
                    AppShared.writeErrorToLogString(getClass().toString(), "Not supported selected position for spinner prio: "+ pos);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void setTypeSpinner(){
        if (rootView == null) return;
        if (backlog == null) return;

        Spinner spinner = (Spinner) rootView.findViewById(R.id.my_spinner_type);
        if (spinner == null) return;

        ArrayList<String> items = new ArrayList<>();
        items.add(getString(R.string.feature));
        items.add(getString(R.string.bug));
        items.add(getString(R.string.other));

        SpinnerSimpleAdapter adapter = new SpinnerSimpleAdapter(getActivity(), R.layout.spinner_simple_item_small_font, items);
        spinner.setAdapter(adapter);

        if (backlog.getType() != null){
            switch (backlog.getType()) {
                case BacklogType.FEATURE:
                    spinner.setSelection(0);
                    break;
                case BacklogType.BUG:
                    spinner.setSelection(1);
                    break;
                case BacklogType.OTHER:
                    spinner.setSelection(2);
                    break;
            }
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                if (pos == 0){
                    if (backlog.getType() == null || !backlog.getType().equals(BacklogType.FEATURE)){
                        backlog.setType(BacklogType.FEATURE);
                        changeMade = true;
                    }
                }else if (pos == 1){
                    if (backlog.getType() == null || !backlog.getType().equals(BacklogType.BUG)){
                        backlog.setType(BacklogType.BUG);
                        changeMade = true;
                    }
                }else if (pos == 2){
                    if (backlog.getType() == null || !backlog.getType().equals(BacklogType.OTHER)){
                        backlog.setType(BacklogType.OTHER);
                        changeMade = true;
                    }
                }else{
                    AppShared.writeErrorToLogString(getClass().toString(), "Not supported selected position for spinner backlog type: "+ pos);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setStatusSpinner(){
        if (rootView == null) return;
        if (backlog == null) return;

        final EditText editText = (EditText) rootView.findViewById(R.id.my_spinner_status);
        if (editText == null) return;

        String status = BacklogStatus.getBacklogStatus(getActivity(), backlog.getStatus());
        editText.setText(status != null ? status : "");

        if (!canChangeStatus) {
            editText.setClickable(false);
            return;
        }
        editText.setClickable(true);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ArrayList<String> items = new ArrayList<>();
                items.add(getString(R.string.to_do));
                items.add(getString(R.string.in_progress));
                items.add(getString(R.string.done));

                new MySpinnerDialog(getActivity(), getString(R.string.status), items, new MySpinnerDialog.MyListener() {
                    @Override
                    public void onItemSelected(int position) {

                        if (position < 0 || position >= items.size()) return;
                        if (position == 0) {
                            backlog.setStatus(BacklogStatus.TO_DO);
                            editText.setText(R.string.to_do);
                            changeMade = true;
                        } else if (position == 1) {
                            backlog.setStatus(BacklogStatus.IN_PROGRESS);
                            editText.setText(R.string.in_progress);
                            changeMade = true;
                        } else if (position == 2) {
                            backlog.setStatus(BacklogStatus.DONE);
                            editText.setText(R.string.done);
                            changeMade = true;
                        } else {
                            AppShared.writeErrorToLogString(getClass().toString(), "Not supported selected position for spinner backlog status: " + position);
                        }
                    }
                }).show();

            }
        });
    }

    private void setPersonLayout(){
        if (rootView == null) return;

        final TextView textView = (TextView) rootView.findViewById(R.id.backlog_person);
        if (textView != null) {
            String personName = "";
            if (person != null) {
                if (person.getName() != null && !person.getName().isEmpty()) {
                    personName += person.getName();
                }
                if (person.getEmail() != null && !person.getEmail().isEmpty()) {
                    if (!personName.isEmpty()) personName += " ";
                    if (person.getName() != null && !person.getName().isEmpty())
                        personName += "<";
                    personName += person.getEmail();
                    if (person.getName() != null && !person.getName().isEmpty())
                        personName += ">";
                }
            }

            textView.setText(personName);
        }

        TableRow tableRow = (TableRow) rootView.findViewById(R.id.backlog_person_row);
        if (tableRow ==  null) return;

        if (!canChangePerson) {
            tableRow.setClickable(false);
            return;
        }
        tableRow.setClickable(true);

        tableRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DialogPersonSelect(getActivity(), PersonRole.DEVELOPER, new DialogPersonSelect.Listener() {
                    @Override
                    public void onPersonSelected(Person person) {

                        if (person == null) return;

                        backlog.setPersonId(person.getPersonId());
                        BacklogEditFragment.this.person = person;
                        if (textView != null) {
                            String personName = "";
                            if (person.getName() != null && !person.getName().isEmpty()) {
                                personName += person.getName();
                            }
                            if (person.getEmail() != null && !person.getEmail().isEmpty()) {
                                if (!personName.isEmpty()) personName += " ";
                                if (person.getName() != null && !person.getName().isEmpty())
                                    personName += "<";
                                personName += person.getEmail();
                                if (person.getName() != null && !person.getName().isEmpty())
                                    personName += ">";
                            }
                            textView.setText(personName);
                        }

                        changeMade = true;

                    }
                }).show();
            }
        });
    }

    private void setSprintLayout(){
        if (rootView == null) return;

        final TextView textView = (TextView) rootView.findViewById(R.id.backlog_sprint);
        if (textView != null) {
            String sprintName = sprint != null ? sprint.getName() : null;
            textView.setText(sprintName != null ? sprintName : "");
        }

        TableRow tableRow = (TableRow) rootView.findViewById(R.id.backlog_sprint_row);
        if (tableRow == null) return;

        if (!canChangeSprint) {
            tableRow.setClickable(false);
            return;
        }
        tableRow.setClickable(true);
        tableRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String projectId = backlog != null ? backlog.getProjectId() : null;
                new DialogSprintSelect(getActivity(), projectId, true, new DialogSprintSelect.Listener() {
                    @Override
                    public void onSprintSelected(Sprint sprint) {

                        if (sprint == null) return;

                        backlog.setSprintId(sprint.getSprintId());
                        backlog.setProjectId(sprint.getProjectId());
                        backlog.setStatus(BacklogStatus.TO_DO);
                        BacklogEditFragment.this.sprint = sprint;

                        if (textView != null) {
                            textView.setText(sprint.getName() != null ? sprint.getName() : "");
                        }

                        changeMade = true;

                    }
                }).show();

            }
        });
    }



    /** From {@link BacklogDialogActivity} and {@link BacklogNewDialogActivity}*/
    public void saveChanges(){
        if (!changeMade){
            new MyToast(getActivity(), R.string.no_change_occured).show();
            return;
        }

        if (backlog == null){
            AppShared.writeErrorToLogString(getClass().toString(), "backlog == null");
            MyAlertDialog.alertError(getActivity(), null, "backlog == null");
            return;
        }

        if (backlog.getBacklogId() == null){
            // new Backlog
            new FirebaseBacklogAdd(getActivity(), backlog, new FirebaseBacklogAdd.Listener() {
                @Override
                public void onResponse(String errorMsg) {

                    changesSaved(errorMsg);

                }
            }).execute();

        }else{
            // Edit Backlog
            new FirebaseBacklogEdit(getActivity(), backlog, new FirebaseBacklogEdit.Listener() {
                @Override
                public void onResponse(String errorMsg) {

                    changesSaved(errorMsg);

                }
            }).execute();
        }
    }

    private void changesSaved(String errorMsg){
        if (mListener != null) {
            mListener.onBacklogChangesSaved(errorMsg);
        }
    }
}
package gr.eap.dxt.sprints;

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
import android.widget.EditText;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.Locale;

import gr.eap.dxt.R;
import gr.eap.dxt.tools.AppShared;
import gr.eap.dxt.tools.MyAlertDialog;
import gr.eap.dxt.tools.MyToast;

/**
 * Created by GEO on 15/2/2017.
 */

public class SprintEditFragment extends Fragment {

    public interface FragmentInteractionListener {
        void onSprintChangesSaved(String errorMsg);
    }
    private FragmentInteractionListener mListener;

    public static final String RELOAD = "gr.eap.dxt.sprints.SprintEditFragment.RELOAD";

    public SprintEditFragment(){}

    private Sprint sprint;
    private boolean changeMade;

    public static SprintEditFragment newInstance(Sprint sprint){
        SprintEditFragment fragment = new SprintEditFragment();
        fragment.sprint = Sprint.getCopy(sprint);
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
        rootView = inflater.inflate(R.layout.fragment_sprint_edit, container, false);

        if (sprint == null){
            AppShared.writeErrorToLogString(getClass().toString(), "sprint == null");
            return rootView;
        }

        setNameEditText();
        setDescriptionEditText();
        setStartTextView();
        setEndTextView();
        setDurationEditText();

        changeMade = false;

        return rootView;
    }

    private void setNameEditText(){
        if (rootView == null) return;
        if (sprint == null) return;

        EditText editText = (EditText) rootView.findViewById(R.id.name);
        if (editText == null) return;

        editText.setText(sprint.getName() != null ? sprint.getName() : "");
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

                sprint.setName(editable.toString());
                changeMade = true;
            }
        });
    }

    private void setDescriptionEditText(){
        if (rootView == null) return;
        if (sprint == null) return;

        EditText editText = (EditText) rootView.findViewById(R.id.description);
        if (editText == null) return;

        editText.setText(sprint.getDescription() != null ? sprint.getDescription() : "");
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

                sprint.setDescription(editable.toString());
                changeMade = true;
            }
        });
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

    private void setDurationEditText(){
        if (rootView == null) return;
        if (sprint == null) return;

        EditText editText = (EditText) rootView.findViewById(R.id.duration);
        if (editText == null) return;

        String duration = null;
        if (sprint.getDuration() != null && sprint.getDuration() > 0){
            duration = sprint.getDuration().toString();
        }
        editText.setText(duration != null ? duration : "");

        TextView textView = (TextView) rootView.findViewById(R.id.days);
        if (textView != null){
            if (sprint.getDuration() != null && sprint.getDuration() == 1){
                textView.setText(getString(R.string.day).toLowerCase());
            }else {
                textView.setText(getString(R.string.days).toLowerCase());
            }
        }

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
                    if (editable.toString().isEmpty()) {
                        duration = (long) 0;
                    } else {
                        duration = Long.parseLong(editable.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    AppShared.writeErrorToLogString(getClass().toString(), e.toString());
                    duration = null;
                }
                if (duration != null) {
                    if (duration < 0) duration = (long) 0;
                    sprint.setDuration(duration);
                    changeMade = true;
                }
            }
        });
    }

    /** From {@link SprintDialogActivity}*/
    protected void saveChanges(){
        if (!changeMade){
            new MyToast(getActivity(), R.string.no_change_occured).show();
            return;
        }

        if (sprint == null){
            AppShared.writeErrorToLogString(getClass().toString(), "sprint == null");
            MyAlertDialog.alertError(getActivity(), null, "sprint == null");
            return;
        }
        if (sprint.getProjectId() == null){
            AppShared.writeErrorToLogString(getClass().toString(), "sprint.getProjectId() == null");
            MyAlertDialog.alertError(getActivity(), null, "sprint.getProjectId() == null");
            return;
        }
        if (sprint.getProjectId().isEmpty()){
            AppShared.writeErrorToLogString(getClass().toString(), "sprint.getProjectId().isEmpty()");
            MyAlertDialog.alertError(getActivity(), null, "sprint.getProjectId().isEmpty()");
            return;
        }

        new FirebaseSprintEdit(getActivity(), sprint, true, new FirebaseSprintEdit.Listener() {
            @Override
            public void onResponse(String errorMsg) {

                if (mListener != null) {
                    mListener.onSprintChangesSaved(errorMsg);
                }

            }
        }).execute();
    }
}

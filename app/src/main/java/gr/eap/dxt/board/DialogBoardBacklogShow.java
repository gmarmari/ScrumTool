package gr.eap.dxt.board;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


import java.util.ArrayList;

import gr.eap.dxt.R;
import gr.eap.dxt.backlog.Backlog;
import gr.eap.dxt.backlog.BacklogStatus;
import gr.eap.dxt.backlog.BacklogType;
import gr.eap.dxt.backlog.FirebaseBacklogEdit;
import gr.eap.dxt.backlog.FirebaseBacklogGetData;
import gr.eap.dxt.backlog.Priority;
import gr.eap.dxt.persons.Person;
import gr.eap.dxt.projects.Project;
import gr.eap.dxt.sprints.Sprint;
import gr.eap.dxt.tools.AppShared;
import gr.eap.dxt.tools.MyAlertDialog;

/**
 * Created by GEO on 8/4/2017.
 */

public class DialogBoardBacklogShow extends Dialog {

    public interface Listener{
        void onBacklogEditted();
    }
    private Listener mListener;

    private Context context;
    private Backlog backlog;
    private Project project;
    private Sprint sprint;
    private Person person;

    private boolean canChangeStatus;
    private boolean canChangeSprint;

    public DialogBoardBacklogShow(Context context, Backlog backlog, Listener mListener) {
        super(context, R.style.my_dialog_no_title);
        this.context = context;
        this.backlog = backlog;
        this.mListener = mListener;
        setContext();
    }

    private void setContext(){
        setContentView(R.layout.dialog_board_backlog_show);

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int height = (int) (metrics.heightPixels*0.8);
        int width = (int) (metrics.widthPixels*0.8);

        if (getWindow() != null){
            getWindow().setLayout(width,height);
        }

        canChangeStatus = false;
        canChangeSprint = false;

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

        ImageButton backButton = (ImageButton) findViewById(R.id.back);
        if (backButton != null){
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }
    }

    private void setNameTextView(){
        if (backlog == null) return;

        TextView nameTextView = (TextView) findViewById(R.id.name);
        if (nameTextView == null) return;
        nameTextView.setText(backlog.getName() != null ? backlog.getName() : "");
    }

    private void setDescriptionTextView(){
        if (backlog == null) return;

        TextView descriptionTextView = (TextView) findViewById(R.id.description);
        if (descriptionTextView == null) return;
        descriptionTextView.setText(backlog.getDescription() != null ? backlog.getDescription() : "");
    }

    private void setDurationTextView() {
        if (backlog == null) return;

        TextView durationTextView = (TextView) findViewById(R.id.duration);
        if (durationTextView == null) return;
        String duration = "";
        if (backlog.getDuration() != null && backlog.getDuration() > 0) {
            duration += backlog.getDuration() + " ";
            if (backlog.getDuration() == 1) {
                duration += context.getString(R.string.day).toLowerCase();
            } else {
                duration += context.getString(R.string.days).toLowerCase();
            }
        }
        durationTextView.setText(duration);
    }

    private void setPrioTextView(){
        if (backlog == null) return;

        TextView textView = (TextView) findViewById(R.id.priority);
        if (textView == null) return;
        String prio = Priority.getPriority(context, backlog.getPriority());
        textView.setText(prio != null ? prio : "");
    }

    private void setStatusTextView(){
        if (backlog == null) return;

        TextView statusTextView = (TextView) findViewById(R.id.status);
        if (statusTextView == null) return;
        String status = BacklogStatus.getBacklogStatus(context, backlog.getStatus());
        statusTextView.setText(status != null ? status : "");
    }

    private void setTypeTextView() {
        if (backlog == null) return;

        TextView typeTextView = (TextView) findViewById(R.id.type);
        if (typeTextView == null) return;
        String type = BacklogType.getBacklogType(context, backlog.getType());
        typeTextView.setText(type != null ? type : "");
    }

    private void setPersonTextView(){
        if (person == null) return;

        TextView persTextView = (TextView) findViewById(R.id.backlog_person);
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
        if (sprint == null) return;

        TextView sprintTextView = (TextView) findViewById(R.id.backlog_sprint);
        if (sprintTextView == null) return;
        sprintTextView.setText(sprint.getName() != null ? sprint.getName() : "");
    }

    private void setProjectTextView(){
        if (backlog == null) return;

        TextView projectTextView = (TextView) findViewById(R.id.backlog_project);
        if (projectTextView == null) return;
        String projectName = project != null ? project.getName() : null;
        projectTextView.setText(projectName != null ? projectName : "");
    }

    private void setBottomToolbar(){
        ImageButton editButton = (ImageButton) findViewById(R.id.my_button_edit);
        if (editButton == null) return;

        if (canChangeSprint || canChangeStatus) {
            editButton.setVisibility(View.VISIBLE);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertBacklogEditOptions();
                }
            });
        }else{
            editButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void show() {
        super.show();

        new FirebaseBacklogGetData(context, backlog, new FirebaseBacklogGetData.Listener() {
            @Override
            public void onResponse(Person person, Sprint sprint, Project project, String errorMsg) {

                if (errorMsg != null && !errorMsg.isEmpty()) {
                    AppShared.writeErrorToLogString(getClass().toString(),errorMsg);
                }

                DialogBoardBacklogShow.this.person = person;
                setPersonTextView();
                DialogBoardBacklogShow.this.sprint = sprint;
                setSprintTextView();
                DialogBoardBacklogShow.this.project = project;
                setProjectTextView();

                canChangeStatus = Backlog.checkIfCanEditStatus(backlog, sprint);
                canChangeSprint = Backlog.checkIfCanChangeSprint(backlog, null);
                if (canChangeSprint) {
                    // If no sprint (unsorted backlog, disable option remove from sprint)
                    if (sprint == null || sprint.getSprintId() == null || sprint.getSprintId().isEmpty() || sprint.getSprintId().equals(AppShared.NO_ID)) {
                        canChangeSprint = false;
                    }
                }
                setBottomToolbar();
            }
        }).execute();
    }

    private void alertBacklogEditOptions(){
        if (!canChangeStatus && !canChangeSprint) return;

        final ArrayList<String> items = new ArrayList<>();
        if (canChangeStatus) {
            String status = backlog.getStatus() != null ? backlog.getStatus() : "";
            if (!status.equals(BacklogStatus.TO_DO)){
                items.add(context.getString(R.string.to_do));
            }
            if (!status.equals(BacklogStatus.IN_PROGRESS)){
                items.add(context.getString(R.string.in_progress));
            }
            if (!status.equals(BacklogStatus.DONE)){
                items.add(context.getString(R.string.done));
            }
        }
        if (canChangeSprint){
            items.add(context.getString(R.string.backlog_sprint_remove));
        }

        MyAlertDialog myAlertDialog = new MyAlertDialog(context, null, R.drawable.ic_action_backlog_purple, null, MyAlertDialog.MyAlertDialogType.LIST);
        myAlertDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (i < 0 || i >= items.size()) return;

                String item = items.get(i);
                if (item == null) return;

                if (item.equals(context.getString(R.string.to_do))){
                    backlog.setStatus(BacklogStatus.TO_DO);
                }else if (item.equals(context.getString(R.string.in_progress))){
                    backlog.setStatus(BacklogStatus.IN_PROGRESS);
                }else if (item.equals(context.getString(R.string.done))){
                    backlog.setStatus(BacklogStatus.DONE);
                }else if (item.equals(context.getString(R.string.backlog_sprint_remove))){
                    backlog.setSprintId(AppShared.NO_ID);
                    backlog.setProjectId(AppShared.NO_ID);
                    backlog.setStatus(BacklogStatus.TO_DO);
                }else{
                    return;
                }

                new FirebaseBacklogEdit(context, backlog, new FirebaseBacklogEdit.Listener() {
                    @Override
                    public void onResponse(String errorMsg) {


                        if (errorMsg != null && !errorMsg.isEmpty()){
                            AppShared.writeErrorToLogString(getClass().toString(), errorMsg);
                            MyAlertDialog.alertError(context, null, errorMsg);
                            return;
                        }

                        dismiss();
                        if (mListener != null) {
                            mListener.onBacklogEditted();
                        }
                    }
                }).execute();
            }
        });
        myAlertDialog.alertListItems();
    }

}



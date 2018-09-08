package gr.eap.dxt.board;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import gr.eap.dxt.R;
import gr.eap.dxt.backlog.Backlog;
import gr.eap.dxt.projects.Project;
import gr.eap.dxt.sprints.FirebaseSprintGetData;
import gr.eap.dxt.sprints.Sprint;
import gr.eap.dxt.sprints.SprintStatus;
import gr.eap.dxt.tools.AppShared;

/**
 * Created by GEO on 8/4/2017.
 */

public class DialogBoardSprintShow extends Dialog {

    private final Context context;
    private Sprint sprint;
    private Project project;
    private ArrayList<Backlog> backlogs;

    public DialogBoardSprintShow(Context context, Sprint sprint) {
        super(context, R.style.my_dialog_no_title);
        this.context = context;
        this.sprint = sprint;
        setContext();
    }

    private void setContext(){
        setContentView(R.layout.dialog_board_sprint_show);

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int height = (int) (metrics.heightPixels*0.8);
        int width = (int) (metrics.widthPixels*0.8);

        if (getWindow() != null){
            getWindow().setLayout(width,height);
        }

        setNameTextView();
        setDescriptionTextView();
        setStartTextView();
        setEndTextView();
        setDurationTextView();
        setStatusTextView();
        setProjectTextView();

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
        if (sprint == null) return;

        TextView nameTextView = (TextView) findViewById(R.id.name);
        if (nameTextView == null) return;
        nameTextView.setText(sprint.getName() != null ? sprint.getName() : "");
    }

    private void setDescriptionTextView(){
        if (sprint == null) return;

        TextView descrTextView = (TextView) findViewById(R.id.description);
        if (descrTextView == null) return;
        descrTextView.setText(sprint.getDescription() != null ? sprint.getDescription() : "");
    }

    private void setStartTextView(){
        if (sprint == null) return;

        TextView startTextView = (TextView) findViewById(R.id.start);
        if (startTextView == null) return;
        SimpleDateFormat dateFormat = new SimpleDateFormat(context.getString(R.string.my_date_format_sprint), Locale.getDefault());
        startTextView.setText(sprint.getStartDate() != null ? dateFormat.format(sprint.getStartDate()) : "");
    }

    private void setEndTextView(){
        if (sprint == null) return;

        TextView endTextView = (TextView) findViewById(R.id.end);
        if (endTextView == null) return;
        SimpleDateFormat dateFormat = new SimpleDateFormat(context.getString(R.string.my_date_format_sprint), Locale.getDefault());
        endTextView.setText(sprint.getEndDate() != null ? dateFormat.format(sprint.getEndDate()) : "");
    }

    private void setDurationTextView(){
        if (sprint == null) return;

        TextView durationTextView = (TextView) findViewById(R.id.duration);
        if (durationTextView != null) {
            String duration = "";
            if (sprint.getDuration() != null && sprint.getDuration() > 0){
                duration += sprint.getDuration() + " ";
                if (sprint.getDuration() == 1){
                    duration += context.getString(R.string.day).toLowerCase();
                }else{
                    duration += context.getString(R.string.days).toLowerCase();
                }
            }

            durationTextView.setText(duration);
        }
    }

    private void setStatusTextView() {
        if (sprint == null) return;

        TextView statusTextView = (TextView) findViewById(R.id.status);
        if (statusTextView == null) return;
        String status = SprintStatus.getSprintStatus(context, sprint.getStatus());
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

        TextView projectTextView = (TextView) findViewById(R.id.sprint_project);
        if (projectTextView == null) return;
        String projectName = project != null ? project.getName() : null;
        projectTextView.setText(projectName != null ? projectName : "");
    }

    @Override
    public void show() {
        super.show();

        new FirebaseSprintGetData(context, sprint, new FirebaseSprintGetData.Listener() {
            @Override
            public void onResponse(Project project, ArrayList<Backlog> backlogs, String errorMsg) {

                if (errorMsg != null && !errorMsg.isEmpty()) {
                    AppShared.writeErrorToLogString(getClass().toString(), errorMsg);
                }

                DialogBoardSprintShow.this.project = project;
                setProjectTextView();

                DialogBoardSprintShow.this.backlogs = backlogs;
                setStatusTextView();

            }
        }).execute();
    }

}


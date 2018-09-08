package gr.eap.dxt.board;

import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import gr.eap.dxt.R;
import gr.eap.dxt.backlog.Backlog;
import gr.eap.dxt.projects.DialogProjectSelect;
import gr.eap.dxt.projects.FirebaseProjectGetAll;
import gr.eap.dxt.projects.Project;
import gr.eap.dxt.sprints.Sprint;
import gr.eap.dxt.tools.AppShared;
import gr.eap.dxt.tools.MyAlertDialog;

/**
 * Created by GEO on 6/4/2017.
 */

public class BoardFragment extends Fragment {

    private enum BacklogType{
        TODO,
        IN_PROG,
        DONE
    }

    public BoardFragment() { }
    public static BoardFragment newInstance() {
        return new BoardFragment();
    }

    private  View rootView;

    private ArrayList<Project> projects;
    private static Project project;

    private ArrayList<BoardSprintItem> items;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_board, container, false);

        setProjectLayout(null, true);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (projects == null){
            getProjects();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setProjectLayoutAccordingToOrientation(newConfig.orientation);
    }

    private void setProjectLayout(String progress, boolean setListener){
        if (rootView == null) return;

        TextView nameTextView = (TextView) rootView.findViewById(R.id.project_name);
        if (nameTextView != null){
            String name = project != null ? project.getName() : null;
            nameTextView.setText(name != null ? name : "");
        }

        TextView descriptionTextView = (TextView) rootView.findViewById(R.id.project_description);
        if (descriptionTextView != null) {
            String decription = project != null ? project.getDescription() : null;
            descriptionTextView.setText(decription != null ? decription : "");
        }

        TextView progressTextView = (TextView) rootView.findViewById(R.id.project_progress);
        if (progressTextView != null) {
            String details = "";
            if (project != null) {
                if (project.getStartDate() != null) {
                    details += getString(R.string.start) + ": ";
                    SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.my_date_format_sprint), Locale.getDefault());
                    details += dateFormat.format(project.getStartDate());
                }
                if (progress != null && !progress.isEmpty()) {
                    if (!details.isEmpty()) details += ", ";
                    details += getString(R.string.completed) +": " + progress;
                }
            }

            progressTextView.setText(details);
        }

        if (!setListener) return;
        LinearLayout projectLayout = (LinearLayout) rootView.findViewById(R.id.project_layout);
        if (projectLayout != null){
            projectLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new DialogProjectSelect(getActivity(), projects, new DialogProjectSelect.Listener() {
                        @Override
                        public void onProjectSelected(Project _project) {

                            project = _project;
                            setProjectLayout(null, false);
                            getProjectData();

                        }
                    }).show();
                }
            });
        }

        setProjectLayoutAccordingToOrientation(getResources().getConfiguration().orientation);
    }

    private void getProjects(){
        new FirebaseProjectGetAll(getActivity(), new FirebaseProjectGetAll.Listener() {
            @Override
            public void onResponse(ArrayList<Project> _projects, String errorMsg) {

                if (errorMsg != null && !errorMsg.isEmpty()){
                    AppShared.writeErrorToLogString(getClass().toString(), errorMsg);
                    MyAlertDialog.alertError(getActivity(), null, errorMsg);
                    return;
                }

                projects = new ArrayList<>();
                if (_projects != null) {
                    projects.addAll(_projects);
                }

                Project noProject = new Project();
                noProject.setName(getString(R.string.unsorted_backlogs));
                noProject.setProjectId(AppShared.NO_ID);
                noProject.setStatus(null);
                projects.add(noProject);

                if (project == null) project = Project.getFirstProjectInProgress(projects);

                setProjectLayout(null, true);
                getProjectData();

            }
        }).execute();
    }

    private void getProjectData(){
        if (project == null) return;
        if (project.getProjectId() == null || project.getProjectId().isEmpty()) return;

        new FirebaseBoardData(getActivity(), project.getProjectId(), new FirebaseBoardData.Listener() {
            @Override
            public void onResponse(ArrayList<BoardSprintItem> _items, String errorMsg) {

                if (errorMsg != null && !errorMsg.isEmpty()){
                    MyAlertDialog.alertError(getActivity(), null, errorMsg);
                }

                items = _items;

                fillSprints();
                fillInBacklogs(BacklogType.TODO);
                fillInBacklogs(BacklogType.IN_PROG);
                fillInBacklogs(BacklogType.DONE);
                String progress = calculateProjectProgress();
                setProjectLayout(progress, false);

            }
        }).execute();
    }

    private String calculateProjectProgress(){
        if (items == null || items.isEmpty()) return null;

        long daysDone = 0;
        long daysAll = 0;
        for (BoardSprintItem item : items) {
            if (item == null) continue;
            if (item.getBacklogsToDo() != null) {
                for (Backlog backlog : item.getBacklogsToDo()) {
                    if (backlog == null) continue;
                    if (backlog.getDuration() == null) continue;
                    if (backlog.getDuration() < 0) continue;
                    daysAll += backlog.getDuration();
                }
            }
            if (item.getBacklogsInProg() != null) {
                for (Backlog backlog : item.getBacklogsInProg()) {
                    if (backlog == null) continue;
                    if (backlog.getDuration() == null) continue;
                    if (backlog.getDuration() < 0) continue;
                    daysAll += backlog.getDuration();
                }
            }
            if (item.getBacklogsDone() != null) {
                for (Backlog backlog : item.getBacklogsDone()) {
                    if (backlog == null) continue;
                    if (backlog.getDuration() == null) continue;
                    if (backlog.getDuration() < 0) continue;
                    daysAll += backlog.getDuration();
                    daysDone += backlog.getDuration();
                }
            }
        }

        if (daysAll == 0) return null;
        double percent = ((double) daysDone / (double) daysAll) * (double) 100;
        return String.format(Locale.getDefault(), "%.1f", percent) + " %";
    }

    private void fillSprints(){
        if (rootView == null) return;
        RelativeLayout relativeLayout = (RelativeLayout) rootView.findViewById(R.id.sprints_layout);
        if (relativeLayout == null) return;
        relativeLayout.removeAllViews();
        if (items == null || items.isEmpty()) return;

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float density = metrics.density;

        int height = 0;
        for (BoardSprintItem item : items) {
            if (item == null) continue;
            if (item.getSprint() == null) continue;
            if (item.getMaxHeight() > 0){
                height += item.getMaxHeight();
            }
        }
        int heightDensity = (int) density*height;

        ViewGroup.LayoutParams params = relativeLayout.getLayoutParams();
        params.height = heightDensity;
        relativeLayout.setLayoutParams(params);
        int marginTopDensity = 0;

        for (final BoardSprintItem item : items){
            if (item == null) continue;
            if (item.getSprint() == null) continue;
            int sprintHeight = item.getMaxHeight();
            int sprintHeightDensity = (int) density*sprintHeight;
            BoardSprintLayout boardSprintLayout = new BoardSprintLayout(getActivity(), item.getSprint(), marginTopDensity, sprintHeightDensity, new BoardSprintLayout.Listener() {
                @Override
                public void onSprintSelected(Sprint sprint) {

                    if (sprint == null){
                        MyAlertDialog.alertError(getActivity(), null, "sprint == null");
                        return;
                    }
                    new DialogBoardSprintShow(getActivity(), sprint).show();

                }
            });
            if (boardSprintLayout.setContent()) {
                relativeLayout.addView(boardSprintLayout);
                marginTopDensity += sprintHeightDensity;
            }
        }
    }

    private void fillInBacklogs(BacklogType backlogType){
        if (backlogType == null) return;
        if (rootView == null) return;
        RelativeLayout relativeLayout;
        if (backlogType.equals(BacklogType.TODO)){
            relativeLayout = (RelativeLayout) rootView.findViewById(R.id.backlog_to_do_layout);
        }else if (backlogType.equals(BacklogType.IN_PROG)){
            relativeLayout = (RelativeLayout) rootView.findViewById(R.id.backlog_in_progress_layout);
        }else if (backlogType.equals(BacklogType.DONE)){
            relativeLayout = (RelativeLayout) rootView.findViewById(R.id.backlog_done_layout);
        }else{
            relativeLayout = null;
        }

        if (relativeLayout == null) return;
        relativeLayout.removeAllViews();
        if (items == null || items.isEmpty()) return;

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float density = metrics.density;

        int backlogHeightDensity = (int) density*BoardSprintItem.BOARD_BACKLOG_HEIGHT;


        for (int i=0;i<items.size();i++){
            int sprintStart = 0;
            int index = 0;
            while (index < i){
                BoardSprintItem anItem = items.get(index);
                if (anItem != null) {
                    if (anItem.getMaxHeight() > 0) {
                        sprintStart += anItem.getMaxHeight();
                    }
                }
                index++;
            }
            int sprintStartDensity = (int) density*sprintStart;

            BoardSprintItem item = items.get(i);
            if (item == null) continue;

            ArrayList<Backlog> arrayList;
            if (backlogType.equals(BacklogType.TODO)){
                arrayList = item.getBacklogsToDo();
            }else if (backlogType.equals(BacklogType.IN_PROG)){
                arrayList = item.getBacklogsInProg();
            }else if (backlogType.equals(BacklogType.DONE)){
                arrayList = item.getBacklogsDone();
            }else{
                arrayList = null;
            }
            if (arrayList == null || arrayList.isEmpty()) continue;

            for (int j=0; j<arrayList.size();j++) {
                Backlog backlog = arrayList.get(j);
                if (backlog == null) continue;

                int topMargin = sprintStartDensity +j*backlogHeightDensity;
                BoardBacklogLayout boardBacklogLayout = new BoardBacklogLayout(getActivity(), backlog, topMargin, backlogHeightDensity, new BoardBacklogLayout.Listener() {
                    @Override
                    public void onBacklogSelected(Backlog backlog) {

                        if (backlog == null){
                            MyAlertDialog.alertError(getActivity(), null, "backlog == null");
                            return;
                        }
                        new DialogBoardBacklogShow(getActivity(), backlog, new DialogBoardBacklogShow.Listener() {
                            @Override
                            public void onBacklogEditted() {

                                getProjectData();

                            }
                        }).show();

                    }
                });
                if (boardBacklogLayout.setContent()) {
                    relativeLayout.addView(boardBacklogLayout);
                }
            }
        }
    }

    private void setProjectLayoutAccordingToOrientation(int orientation){
        if (rootView == null) return;

        if (getResources().getBoolean(R.bool.large_screen)) return;

        LinearLayout projectLayout = (LinearLayout) rootView.findViewById(R.id.project_layout);
        View projectDivider = rootView.findViewById(R.id.my_divider_project);
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (projectLayout != null) projectLayout.setVisibility(View.GONE);
            if (projectDivider != null) projectDivider.setVisibility(View.GONE);
        }else{
            if (projectLayout != null) projectLayout.setVisibility(View.VISIBLE);
            if (projectDivider != null) projectDivider.setVisibility(View.VISIBLE);
        }
    }
}

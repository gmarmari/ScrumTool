package gr.eap.dxt.board;

import android.content.Context;

import java.util.ArrayList;

import gr.eap.dxt.R;
import gr.eap.dxt.backlog.Backlog;
import gr.eap.dxt.backlog.FirebaseBacklogSprintGet;
import gr.eap.dxt.sprints.FirebaseSprintProjectGet;
import gr.eap.dxt.sprints.Sprint;
import gr.eap.dxt.tools.AppShared;
import gr.eap.dxt.tools.FirebaseCall;

/**
 * Created by GEO on 6/4/2017.
 */

public class FirebaseBoardData extends FirebaseCall {

    interface Listener {
        void onResponse(ArrayList<BoardSprintItem> items, String errorMsg);
    }
    private FirebaseBoardData.Listener mListener;

    private String projectId;

    private ArrayList<Sprint> sprints;
    private int index;

    private ArrayList<BoardSprintItem> items;

    FirebaseBoardData(Context context, String projectId, Listener mListener){
        super(context, true);
        this.projectId = projectId;
        this.mListener = mListener;
    }

    public void execute(){
        super.execute();

        new FirebaseSprintProjectGet(getContext(), projectId, false, new FirebaseSprintProjectGet.Listener() {
            @Override
            public void onResponse(ArrayList<Sprint> sprints, String errorMsg) {

                if (errorMsg != null && !errorMsg.isEmpty()){
                    addErrorNo(errorMsg);
                }
                if (sprints == null){
                    addErrorNo("sprints == null");
                }

                FirebaseBoardData.this.sprints = sprints;

                if (projectId != null && projectId.equals(AppShared.NO_ID)){
                    Sprint noSprint = new Sprint();
                    noSprint.setName(getContext().getString(R.string.unsorted_backlogs));
                    noSprint.setSprintId(AppShared.NO_ID);
                    noSprint.setProjectId(AppShared.NO_ID);
                    noSprint.setStatus(null);
                    FirebaseBoardData.this.sprints.add(noSprint);
                }

                items = new ArrayList<>();
                index = -1;
                getNextSprintBacklogs();
            }
        }).execute();

    }
    private void getNextSprintBacklogs(){
        index++; // at start index = -1, so in this line index = 0
        if (sprints == null || sprints.isEmpty()){
            giveOutput();
            return;
        }
        if (index >= sprints.size()) {
            giveOutput();
            return;
        }

        Sprint sprint = sprints.get(index);
        if (sprint == null) {
            getNextSprintBacklogs();
            return;
        }
        if (sprint.getSprintId() == null) {
            getNextSprintBacklogs();
            return;
        }
        if (sprint.getSprintId().isEmpty())  {
            getNextSprintBacklogs();
            return;
        }

        final BoardSprintItem item = new BoardSprintItem(sprint);

        new FirebaseBacklogSprintGet(getContext(), sprint.getSprintId(), false, new FirebaseBacklogSprintGet.Listener() {
            @Override
            public void onResponse(ArrayList<Backlog> backlogs, String errorMsg) {

                if (errorMsg != null && !errorMsg.isEmpty()){
                    addErrorNo(errorMsg);
                }
                item.setBacklogs(backlogs);
                items.add(item);
                getNextSprintBacklogs();
            }
        }).execute();
    }

    protected void giveOutput(){
        super.giveOutput();

        if (mListener != null) mListener.onResponse(items, getErrorno());
    }

}

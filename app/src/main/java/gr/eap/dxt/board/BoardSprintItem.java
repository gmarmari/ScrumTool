package gr.eap.dxt.board;


import java.util.ArrayList;

import gr.eap.dxt.backlog.Backlog;
import gr.eap.dxt.backlog.BacklogStatus;
import gr.eap.dxt.sprints.Sprint;

/**
 * Created by GEO on 6/4/2017.
 */

public class BoardSprintItem {

    public static int BOARD_BACKLOG_HEIGHT = 80;

    public BoardSprintItem(Sprint sprint){
        this.sprint = sprint;
    }

    private Sprint sprint;
    public Sprint getSprint() {
        return sprint;
    }

    public void setBacklogs(ArrayList<Backlog> backlogs){
        backlogsToDo = new ArrayList<>();
        backlogsInProg = new ArrayList<>();
        backlogsDone = new ArrayList<>();

        for (Backlog backlog: backlogs) {
            if (backlog == null) continue;
            if (backlog.getStatus() == null || backlog.getStatus().isEmpty()) continue;

            switch (backlog.getStatus()) {
                case BacklogStatus.TO_DO:
                    backlogsToDo.add(backlog);
                    break;
                case BacklogStatus.IN_PROGRESS:
                    backlogsInProg.add(backlog);
                    break;
                case BacklogStatus.DONE:
                    backlogsDone.add(backlog);
                    break;
            }
        }

        calculateMaxHeight();
    }

    private ArrayList<Backlog> backlogsToDo;
    public ArrayList<Backlog> getBacklogsToDo() {
        return backlogsToDo;
    }

    private ArrayList<Backlog> backlogsInProg;
    public ArrayList<Backlog> getBacklogsInProg() {
        return backlogsInProg;
    }

    private ArrayList<Backlog> backlogsDone;
    public ArrayList<Backlog> getBacklogsDone() {
        return backlogsDone;
    }

    private int maxHeight;
    public int getMaxHeight() {
        return maxHeight;
    }
    private void calculateMaxHeight(){
        int numberToDo = backlogsToDo != null ? backlogsToDo.size() : 0;
        int numberInProg = backlogsInProg != null ? backlogsInProg.size() : 0;
        int numberDone = backlogsDone != null ? backlogsDone.size() : 0;

        int temp = Math.max(numberToDo, numberInProg);
        int maxNumber = Math.max(temp, numberDone);
        if (maxNumber < 2) maxNumber = 2;


        maxHeight = maxNumber*BOARD_BACKLOG_HEIGHT;
    }
}
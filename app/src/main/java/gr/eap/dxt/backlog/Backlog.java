package gr.eap.dxt.backlog;


import java.util.ArrayList;
import java.util.Date;

import gr.eap.dxt.sprints.Sprint;
import gr.eap.dxt.tools.AppShared;

/**
 * Created by GEO on 12/2/2017.
 */

public class Backlog {

    public static final String FIREBASE_LIST = "Backlogs";

    public Backlog(){
        priority = Priority.PRIO_3_NORMAL;
        status = BacklogStatus.TO_DO;
        type = BacklogType.OTHER;
        projectId = AppShared.NO_ID;
        personId = AppShared.NO_ID;
        sprintId = AppShared.NO_ID;
    }

    private String backlogId;
    public String getBacklogId() {
        return backlogId;
    }
    public void setBacklogId(String backlogId) {
        this.backlogId = backlogId;
    }

    public static final String NAME = "name";
    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public static final String DESCRIPTION = "description";
    private String description;
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public static final String DURATION = "duration";
    private Long duration;
    public Long getDuration() {
        return duration;
    }
    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public static final String PRIORITY = "priority";
    private String priority;
    public String getPriority(){
        return priority;
    }
    public void setPriority(String priority) {
        this.priority = priority;
    }

    public static final String STATUS = "status";
    private String status;
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public static final String TYPE = "type";
    private String type;
    public String getType(){
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public static final String PERSON_ID = "personId";
    private String personId;
    public String getPersonId() {
        return personId;
    }
    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public static final String SPRINT_ID = "sprintId";
    private String sprintId;
    public String getSprintId() {
        return sprintId;
    }
    public void setSprintId(String sprintId) {
        this.sprintId = sprintId;
    }

    public static String PROJECT_ID = "projectId";
    private String projectId;
    public String getProjectId() {
        return projectId;
    }
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public static Backlog getCopy(Backlog backlog){
        if (backlog == null) return null;

        Backlog copy = new Backlog();
        copy.backlogId = backlog.backlogId;
        copy.name = backlog.name;
        copy.description = backlog.description;
        copy.duration = backlog.duration;
        copy.priority = backlog.priority;
        copy.status = backlog.status;
        copy.type = backlog.type;
        copy.personId = backlog.personId;
        copy.sprintId = backlog.sprintId;
        copy.projectId = backlog.projectId;
        return copy;
    }

    public static Backlog getBacklogWithId(String backlogId, ArrayList<Backlog> backlogs){
        if (backlogId == null || backlogId.isEmpty()) return null;
        if (backlogs == null || backlogs.isEmpty()) return null;

        for (Backlog backlog : backlogs) {
            if (backlog != null){
                if (backlog.getBacklogId() != null){
                    if (backlog.getBacklogId().equals(backlogId)) return backlog;
                }
            }
        }

        return null;
    }

    public static boolean checkIfCanEditStatus(Backlog backlog, Sprint sprint) {
        if (backlog == null) return false;

        if (sprint == null) return false;
        if (sprint.getSprintId() == null) return false;
        if (sprint.getSprintId().isEmpty()) return false;
        if (sprint.getSprintId().equals(AppShared.NO_ID)) return false;
        if (sprint.getStartDate() == null) return false;
        if (sprint.getEndDate() == null) return false;

        Date currentDate = new Date();
        return sprint.getStartDate().before(currentDate); // if sprint started: allow status change, even if the sprint ended
    }

    public static boolean checkIfCanChangePerson(Backlog backlog){
        if (backlog == null) return false;
        String status = backlog.getStatus() != null ? backlog.getStatus() : "";
        return !status.equals(BacklogStatus.DONE);
    }

    public static boolean checkIfCanChangeSprint(Backlog backlog, BacklogEditFragment.BacklogEditFrom from){
        if (from == BacklogEditFragment.BacklogEditFrom.SPRINT) {
            return false;
        }else if(from == BacklogEditFragment.BacklogEditFrom.NEW){
            return true;
        }else {
            String status = backlog.getStatus() != null ? backlog.getStatus() : "";
            return !status.equals(BacklogStatus.DONE);
        }
    }

}

package gr.eap.dxt.sprints;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import gr.eap.dxt.backlog.Backlog;
import gr.eap.dxt.backlog.BacklogStatus;
import gr.eap.dxt.tools.AppShared;

/**
 * Created by GEO on 12/2/2017.
 */

public class Sprint {

    public static String FIREBASE_LIST = "Sprints";

    public Sprint(){
        status = SprintStatus.NOT_STARTED;
        projectId = AppShared.NO_ID;
    }

    private String sprintId;
    public String getSprintId() {
        return sprintId;
    }
    public void setSprintId(String sprintId) {
        this.sprintId = sprintId;
    }

    public static String NAME = "name";
    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public static String DESCRIPTION = "description";
    private String description;
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public static String PROJECT_ID = "projectId";
    private String projectId;
    public String getProjectId() {
        return projectId;
    }
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public static String STATUS = "status";
    private String status;
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public static String START_DATE = "startDate";
    private Date startDate;
    public Date getStartDate() {
        return startDate;
    }
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }


    public static String END_DATE = "endDate";
    private Date endDate;
    public Date getEndDate() {
        return endDate;
    }
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public static String DURATION = "duration";
    private Long duration;
    public Long getDuration() {
        return duration;
    }
    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public static Sprint getCopy(Sprint sprint){
        if (sprint == null) return null;

        Sprint copy = new Sprint();
        copy.sprintId = sprint.sprintId;
        copy.name = sprint.name;
        copy.description = sprint.description;
        copy.projectId = sprint.projectId;
        copy.status = sprint.status;
        copy.startDate = sprint.startDate;
        copy.endDate = sprint.endDate;
        copy.duration = sprint.duration;
        return copy;
    }

    /**
     * Check and change the sprint status according to dates start and end
     */
    public static boolean checkSprintStatus(Sprint sprint){
        if (sprint == null) return false;
        if (sprint.getStatus() == null) return false;
        if (sprint.getStartDate() == null) return false;
        if (sprint.getEndDate() == null) return false;

        if (sprint.getStartDate().after(new Date())){
            // Not started
            if (!sprint.getStatus().equals(SprintStatus.NOT_STARTED)){
                sprint.setStatus(SprintStatus.NOT_STARTED);
                return true;
            }
        }

        if (sprint.getStartDate().before(new Date())){
            // Started
            if (sprint.getEndDate().before(new Date())) {
                // Ended
                if (!sprint.getStatus().equals(SprintStatus.COMPLETED) &&
                        !sprint.getStatus().equals(SprintStatus.CANCELED)) {
                    sprint.setStatus(SprintStatus.COMPLETED);
                    return true;
                }
            }

            if (sprint.getEndDate().after(new Date())) {
                // Not ended
                if (sprint.getStatus().equals(SprintStatus.NOT_STARTED)) {
                    sprint.setStatus(SprintStatus.IN_PROGRESS);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Calculate sprints duration in days according to start and until dates
     * Weekends are not included
     */
    public static void calculateDuration(Sprint sprint){
        if (sprint == null) return;
        if (sprint.getStartDate() == null) return;
        if (sprint.getEndDate() == null) return;
        if (sprint.getStartDate().after(sprint.getEndDate())){
            sprint.setDuration((long) 0);
            return;
        }

        int days = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sprint.getStartDate());
        do {
            if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
                    && calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
                days++;
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }while (calendar.getTime().before(sprint.getEndDate()));

        sprint.setDuration((long) days);
    }

    /** returns the sum backlogs.duration from those backlogs  */
    public static long getDaysFromAllBacklogs(ArrayList<Backlog> backlogs){
        long days = 0;

        if (backlogs == null || backlogs.isEmpty()) return days;

        for (Backlog backlog : backlogs) {
            if (backlog != null) {
                if (backlog.getDuration() != null && backlog.getDuration() > 0) {
                    days += backlog.getDuration();
                }

            }
        }

        return days;
    }

    /** returns the sum backlogs.duration from those backlogs that status == done */
    public static long getDaysFromDoneBacklogs(ArrayList<Backlog> backlogs){
        long days = 0;

        if (backlogs == null || backlogs.isEmpty()) return days;

        for (Backlog backlog : backlogs) {
            if (backlog != null){
                if (backlog.getStatus() != null){
                    if (backlog.getStatus().equals(BacklogStatus.DONE)){
                        if (backlog.getDuration() != null && backlog.getDuration() > 0){
                            days += backlog.getDuration();
                        }
                    }
                }
            }
        }

        return days;
    }

    public static Sprint getSprintWithId(String sprintId, ArrayList<Sprint> sprints){
        if (sprintId == null || sprintId.isEmpty()) return null;
        if (sprints == null || sprints.isEmpty()) return null;

        for (Sprint sprint : sprints) {
            if (sprint != null){
                if (sprint.getSprintId() != null){
                    if (sprint.getSprintId().equals(sprintId)) return sprint;
                }
            }
        }

        return null;
    }

}

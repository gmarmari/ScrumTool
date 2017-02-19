package gr.eap.dxt.marmaris.projects;

/**
 * Created by GEO on 22/1/2017.
 */

public class Project {

    public static String FIREBASE_LIST = "Projects";

    public Project(){
        status = ProjectStatus.NOT_STARTED;
    }

    private String projectId;
    public String getProjectId() {
        return projectId;
    }
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public static String NAME = "name";
    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public static String STATUS = "status";
    private String status;
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public static String DESCRIPTION = "description";
    private String description;
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public static Project getCopy(Project project){
        if (project == null) return null;

        Project copy = new Project();
        copy.projectId = project.projectId;
        copy.name = project.name;
        copy.description = project.description;
        copy.status = project.status;
        return copy;
    }
}

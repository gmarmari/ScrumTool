package gr.eap.dxt.marmaris.projects;

/**
 * Created by GEO on 22/1/2017.
 */

public class Project {

    public static String FIREBASE_LIST = "Projects";

    public Project(){}

    public static String PROJECT_NAME = "projectName";
    private String projectName;
    public String getProjectName() {
        return projectName;
    }
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public static String PROJECT_ID = "projectId";
    private String projectId;
    public String getProjectId() {
        return projectId;
    }
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public static String PROJECT_STATUS = "projectStatus";
    private String projectStatus;
    public String getProjectStatus() {
        return projectStatus;
    }
    public void setProjectStatus(String projectStatus) {
        this.projectStatus = projectStatus;
    }

    public static Project getCopy(Project project){
        if (project == null) return null;

        Project copy = new Project();
        copy.projectId = project.projectId;
        copy.projectName = project.projectName;
        copy.projectStatus = project.projectStatus;
        return copy;
    }
}

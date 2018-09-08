package gr.eap.dxt.backlog;

import java.util.ArrayList;

/**
 * Created by GEO on 19/2/2017.
 */

public class ListBacklogGroupedStatusParent {

    public ListBacklogGroupedStatusParent(String status){
        isOn = false;
        this.status = status;
        children = new ArrayList<>();
    }

    private String status;
    public String getStatus() {
        return status;
    }

    ArrayList<Backlog> children;
    public ArrayList<Backlog> getChildren() {
        return children;
    }

    private boolean isOn;
    public boolean isOn(){
        return isOn;
    }
    public void setIsOn(boolean isOn){
        this.isOn = isOn;
    }
}

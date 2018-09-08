package gr.eap.dxt.sprints;

import java.util.ArrayList;

/**
 * Created by GEO on 19/2/2017.
 */

public class ListSprintGroupedProjectParent {

    public ListSprintGroupedProjectParent(){
        children = new ArrayList<>();
    }

    private ArrayList<Sprint> children;
    public ArrayList<Sprint> getChildren() {
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

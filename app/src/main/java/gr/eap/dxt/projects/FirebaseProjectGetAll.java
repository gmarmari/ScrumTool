package gr.eap.dxt.projects;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import gr.eap.dxt.R;
import gr.eap.dxt.tools.FirebaseCall;
import gr.eap.dxt.tools.FirebaseParse;

/**
 * Created by GEO on 8/2/2017.
 */

public class FirebaseProjectGetAll extends FirebaseCall {

    public interface Listener {
        void onResponse(ArrayList<Project> projects, String errorMsg);
    }
    private Listener mListener;

    private class StatusComparator implements Comparator<Project> {

        @Override
        public int compare(Project left, Project right) {
            if (left == null) return 0;
            if (right == null) return 0;
            String leftString = left.getStatus();
            if (leftString == null) return 0;
            String rightString = right.getStatus();
            if (rightString == null) return 0;
            return leftString.compareTo(rightString);
        }
    }

    public FirebaseProjectGetAll(Context context, Listener mListener){
        super(context, true);
        this.mListener = mListener;
        setDialogTitle(context.getString(R.string.get_projects_progress));
    }

    public void execute(){
        super.execute();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Project.FIREBASE_LIST);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null) {
                    addErrorNo("dataSnapshot == null");
                    giveOutput(null);
                    return;
                }
                if (dataSnapshot.getChildren() == null) {
                    addErrorNo("dataSnapshotgetChildren() == null");
                    giveOutput(null);
                    return;
                }

                if (dataSnapshot.getChildrenCount() == 0) {
                    giveOutput(new ArrayList<Project>());
                    return;
                }

                ArrayList<Project> projects = new ArrayList<>();

                for (DataSnapshot childShapshot : dataSnapshot.getChildren()){

                    try {
                        Project project = new Project();
                        project.setProjectId(childShapshot.getKey());
                        project.setName(FirebaseParse.getString(childShapshot.child(Project.NAME)));
                        project.setDescription(FirebaseParse.getString(childShapshot.child(Project.DESCRIPTION)));
                        project.setStatus(FirebaseParse.getString(childShapshot.child(Project.STATUS)));
                        project.setStartDate(FirebaseParse.getDate(childShapshot.child(Project.START_DATE)));

                        projects.add(project);
                    }catch (Exception e){
                        addErrorNo(e.toString());
                    }
                }


                Collections.sort(projects, new StatusComparator());
                giveOutput(projects);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (databaseError != null) {
                    addErrorNo(databaseError.getMessage());
                }
                giveOutput(null);
            }
        });
    }

    private void giveOutput(ArrayList<Project> projects){
        super.giveOutput();

        if (isCanceled()) return;
        if (mListener != null) mListener.onResponse(projects, getErrorno());
    }

}

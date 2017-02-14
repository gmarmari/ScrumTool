package gr.eap.dxt.marmaris.projects;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import gr.eap.dxt.marmaris.R;
import gr.eap.dxt.marmaris.tools.FirebaseCall;
import gr.eap.dxt.marmaris.tools.FirebaseParse;

/**
 * Created by GEO on 8/2/2017.
 */

class FirebaseProjectGetAll extends FirebaseCall {

    interface Listener {
        void onResponse(ArrayList<Project> projects);
    }
    private Listener mListener;


    FirebaseProjectGetAll(Context context, Listener mListener){
        super(context, true, true);
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

                        projects.add(project);
                    }catch (Exception e){
                        addErrorNo(e.toString());
                    }
                }

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

        if (getErrorno() != null && !getErrorno().isEmpty()){
            alertError(getErrorno());
            return;
        }
        if (projects == null){
            alertError("projects == null");
            return;
        }

        if (mListener != null) mListener.onResponse(projects);
    }

}

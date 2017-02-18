package gr.eap.dxt.marmaris.sprints;

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
 * Created by GEO on 12/2/2017.
 */

class FirebaseSprintGetAll extends FirebaseCall {

    interface Listener {
        void onResponse(ArrayList<Sprint> sprints);
    }
    private Listener mListener;


    FirebaseSprintGetAll(Context context, Listener mListener){
        super(context, true, true);
        this.mListener = mListener;
        setDialogTitle(context.getString(R.string.get_sprints_progress));
    }

    public void execute(){
        super.execute();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Sprint.FIREBASE_LIST);
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
                    giveOutput(new ArrayList<Sprint>());
                    return;
                }

                ArrayList<Sprint> sprints = new ArrayList<>();

                for (DataSnapshot childShapshot : dataSnapshot.getChildren()){

                    try {
                        Sprint sprint = new Sprint();
                        sprint.setSprintId(childShapshot.getKey());

                        sprint.setName(FirebaseParse.getString(childShapshot.child(Sprint.NAME)));
                        sprint.setDescription(FirebaseParse.getString(childShapshot.child(Sprint.DESCRIPTION)));
                        sprint.setProjectId(FirebaseParse.getString(childShapshot.child(Sprint.PROJECT_ID)));
                        sprint.setProjectName(FirebaseParse.getString(childShapshot.child(Sprint.PROJECT_NAME)));
                        sprint.setStatus(FirebaseParse.getString(childShapshot.child(Sprint.STATUS)));
                        sprint.setStartDate(FirebaseParse.getDate(childShapshot.child(Sprint.START_DATE)));
                        sprint.setEndDate(FirebaseParse.getDate(childShapshot.child(Sprint.END_DATE)));
                        sprint.setDuration(FirebaseParse.getLong(childShapshot.child(Sprint.DURATION)));

                        sprints.add(sprint);
                    }catch (Exception e){
                        addErrorNo(e.toString());
                    }
                }

                giveOutput(sprints);
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

    private void giveOutput(ArrayList<Sprint> sprints){
        super.giveOutput();

        if (getErrorno() != null && !getErrorno().isEmpty()){
            alertError(getErrorno());
            return;
        }
        if (sprints == null){
            alertError("sprints == null");
            return;
        }

        if (mListener != null) mListener.onResponse(sprints);
    }

}

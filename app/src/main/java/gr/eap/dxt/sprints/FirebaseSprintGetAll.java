package gr.eap.dxt.sprints;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import gr.eap.dxt.R;
import gr.eap.dxt.tools.FirebaseCall;
import gr.eap.dxt.tools.FirebaseParse;

/**
 * Created by GEO on 12/2/2017.
 */

class FirebaseSprintGetAll extends FirebaseCall {

    interface Listener {
        void onResponse(ArrayList<Sprint> sprints, String errorMsg);
    }
    private Listener mListener;

    private class StartDateComparator implements Comparator<Sprint> {

        @Override
        public int compare(Sprint left, Sprint right) {
            if (left == null) return 0;
            if (right == null) return 0;
            Date leftDate = left.getStartDate();
            if (leftDate == null) return 0;
            Date rightDate = right.getStartDate();
            if (rightDate == null) return 0;
            return leftDate.compareTo(rightDate);
        }
    }

    FirebaseSprintGetAll(Context context, Listener mListener){
        super(context, true);
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
                    giveOutput(null, null);
                    return;
                }
                if (dataSnapshot.getChildren() == null) {
                    addErrorNo("dataSnapshotgetChildren() == null");
                    giveOutput(null, null);
                    return;
                }

                if (dataSnapshot.getChildrenCount() == 0) {
                    giveOutput(new ArrayList<Sprint>(), null);
                    return;
                }

                ArrayList<Sprint> sprints = new ArrayList<>();
                ArrayList<Sprint> sprintsToChange = new ArrayList<>();

                for (DataSnapshot childShapshot : dataSnapshot.getChildren()){

                    try {
                        Sprint sprint = new Sprint();
                        sprint.setSprintId(childShapshot.getKey());

                        sprint.setName(FirebaseParse.getString(childShapshot.child(Sprint.NAME)));
                        sprint.setDescription(FirebaseParse.getString(childShapshot.child(Sprint.DESCRIPTION)));
                        sprint.setProjectId(FirebaseParse.getString(childShapshot.child(Sprint.PROJECT_ID)));
                        sprint.setStatus(FirebaseParse.getString(childShapshot.child(Sprint.STATUS)));
                        sprint.setStartDate(FirebaseParse.getDate(childShapshot.child(Sprint.START_DATE)));
                        sprint.setEndDate(FirebaseParse.getDate(childShapshot.child(Sprint.END_DATE)));
                        sprint.setDuration(FirebaseParse.getLong(childShapshot.child(Sprint.DURATION)));

                        sprints.add(sprint);
                        if (Sprint.checkSprintStatus(sprint)){
                            sprintsToChange.add(sprint);
                        }

                    }catch (Exception e){
                        addErrorNo(e.toString());
                    }
                }

                Collections.sort(sprints, new StartDateComparator());
                giveOutput(sprints, sprintsToChange);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (databaseError != null) {
                    addErrorNo(databaseError.getMessage());
                }
                giveOutput(null, null);
            }
        });
    }

    private void giveOutput(ArrayList<Sprint> sprints,  ArrayList<Sprint> sprintsToChange){
        super.giveOutput();

        if (isCanceled()) return;
        if (mListener != null) mListener.onResponse(sprints, getErrorno());

        if (sprintsToChange != null && !sprintsToChange.isEmpty()){
            // save the changes on background
            new FirebaseSprintEditMany(getContext(), sprintsToChange, new FirebaseSprintEditMany.Listener() {
                @Override
                public void onResponse(String errorMsg) {

                    if (errorMsg != null && !errorMsg.isEmpty()){
                        writeError(errorMsg);
                    }

                }
            }).execute();
        }
    }

}

package gr.eap.dxt.backlog;

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
 * Created by GEO on 4/3/2017.
 */

public class FirebaseBacklogPersonGet extends FirebaseCall {

    public interface Listener {
        void onResponse(ArrayList<Backlog> backlogs, String errorMsg);
    }
    private Listener mListener;

    private class NameComparator implements Comparator<Backlog> {

        @Override
        public int compare(Backlog left, Backlog right) {
            if (left == null) return 0;
            if (right == null) return 0;
            String leftString = left.getName();
            if (leftString == null) return 0;
            String rightString = right.getName();
            if (rightString == null) return 0;
            return leftString.compareTo(rightString);
        }
    }

    private String personId;

    public FirebaseBacklogPersonGet(Context context, String personId, Listener mListener){
        super(context, true);
        this.personId = personId;
        this.mListener = mListener;
        setDialogTitle(context.getString(R.string.get_backlog_progress));
    }

    public void execute(){
        super.execute();

        if (personId == null) {
            addErrorNo("personId == null");
            giveOutput(null);
            return;
        }
        if (personId.isEmpty()) {
            addErrorNo("personId.isEmpty()");
            giveOutput(null);
            return;
        }

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Backlog.FIREBASE_LIST);
        mDatabase.orderByChild(Backlog.PERSON_ID).equalTo(personId).addListenerForSingleValueEvent(new ValueEventListener() {
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
                    giveOutput(new ArrayList<Backlog>());
                    return;
                }

                ArrayList<Backlog> backlogs = new ArrayList<>();

                for (DataSnapshot childShapshot : dataSnapshot.getChildren()){

                    try {
                        Backlog backlog = new Backlog();
                        backlog.setBacklogId(childShapshot.getKey());

                        backlog.setName(FirebaseParse.getString(childShapshot.child(Backlog.NAME)));
                        backlog.setDescription(FirebaseParse.getString(childShapshot.child(Backlog.DESCRIPTION)));
                        backlog.setPriority(FirebaseParse.getString(childShapshot.child(Backlog.PRIORITY)));
                        backlog.setStatus(FirebaseParse.getString(childShapshot.child(Backlog.STATUS)));
                        backlog.setDuration(FirebaseParse.getLong(childShapshot.child(Backlog.DURATION)));
                        backlog.setPersonId(FirebaseParse.getString(childShapshot.child(Backlog.PERSON_ID)));
                        backlog.setSprintId(FirebaseParse.getString(childShapshot.child(Backlog.SPRINT_ID)));
                        backlog.setProjectId(FirebaseParse.getString(childShapshot.child(Backlog.PROJECT_ID)));
                        backlog.setType(FirebaseParse.getString(childShapshot.child(Backlog.TYPE)));

                        backlogs.add(backlog);
                    }catch (Exception e){
                        addErrorNo(e.toString());
                    }
                }

                Collections.sort(backlogs, new NameComparator());
                giveOutput(backlogs);
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

    private void giveOutput(ArrayList<Backlog> backlogs){
        super.giveOutput();

        if (mListener != null) mListener.onResponse(backlogs, getErrorno());
    }

}

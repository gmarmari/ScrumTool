package gr.eap.dxt.sprints;

import android.content.Context;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import gr.eap.dxt.tools.FirebaseCall;

/**
 * Created by GEO on 14/2/2017.
 */

public class FirebaseSprintEdit extends FirebaseCall {

    public interface Listener {
        void onResponse(String errorMsg);
    }
    private Listener mListener;

    private Sprint sprint;


    public FirebaseSprintEdit(Context context, Sprint sprint, boolean notify, Listener mListener){
        super(context, notify);
        this.sprint = sprint;
        this.mListener = mListener;
    }

    public void execute(){
        super.execute();

        if (sprint == null){
            addErrorNo("sprint == null");
            giveOutput();
            return;
        }
        if (sprint.getSprintId() == null){
            addErrorNo("sprint.getSprintId() == null");
            giveOutput();
            return;
        }
        if (sprint.getSprintId().isEmpty()){
            addErrorNo("sprint.getSprintId().isEmpty()");
            giveOutput();
            return;
        }

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Sprint.FIREBASE_LIST);
        if (mDatabase.child(sprint.getSprintId()) == null){
            addErrorNo("mDatabase.child(sprintId) == null");
            giveOutput();
            return;
        }

        Map<String,Object> taskMap = new HashMap<>();
        taskMap.put(Sprint.NAME, sprint.getName());
        taskMap.put(Sprint.DESCRIPTION, sprint.getDescription());
        taskMap.put(Sprint.PROJECT_ID, sprint.getProjectId());
        taskMap.put(Sprint.STATUS, sprint.getStatus());
        taskMap.put(Sprint.START_DATE, sprint.getStartDate());
        taskMap.put(Sprint.END_DATE, sprint.getEndDate());
        taskMap.put(Sprint.DURATION, sprint.getDuration());

        mDatabase.child(sprint.getSprintId()).updateChildren(taskMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                if(databaseError != null){
                    addErrorNo(databaseError.getMessage());
                }

                giveOutput();

            }
        });
    }

    protected void giveOutput(){
        super.giveOutput();

        if (isCanceled()) return;
        if (mListener != null) mListener.onResponse(getErrorno() );
    }

}

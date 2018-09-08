package gr.eap.dxt.backlog;

import android.content.Context;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import gr.eap.dxt.tools.FirebaseCall;

/**
 * Created by GEO on 12/2/2017.
 */

public class FirebaseBacklogEdit extends FirebaseCall {

    public interface Listener {
        void onResponse(String errorMsg);
    }
    private Listener mListener;

    private Backlog backlog;

    public FirebaseBacklogEdit(Context context, Backlog backlog, Listener mListener){
        super(context, true);
        this.backlog = backlog;
        this.mListener = mListener;
    }

    public void execute(){
        super.execute();

        if (backlog == null){
            addErrorNo("backlog == null");
            giveOutput();
            return;
        }
        if (backlog.getBacklogId() == null){
            addErrorNo("backlog.getBacklogId() == null");
            giveOutput();
            return;
        }
        if (backlog.getBacklogId().isEmpty()){
            addErrorNo("backlog.getBacklogId().isEmpty()");
            giveOutput();
            return;
        }

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Backlog.FIREBASE_LIST);
        if (mDatabase.child(backlog.getBacklogId()) == null){
            addErrorNo("mDatabase.child(backlogId) == null");
            giveOutput();
            return;
        }

        Map<String,Object> taskMap = new HashMap<>();
        taskMap.put(Backlog.NAME, backlog.getName());
        taskMap.put(Backlog.DESCRIPTION, backlog.getDescription());
        taskMap.put(Backlog.PRIORITY, backlog.getPriority());
        taskMap.put(Backlog.STATUS, backlog.getStatus());
        taskMap.put(Backlog.DURATION, backlog.getDuration());
        taskMap.put(Backlog.TYPE, backlog.getType());
        taskMap.put(Backlog.PERSON_ID, backlog.getPersonId());
        taskMap.put(Backlog.SPRINT_ID, backlog.getSprintId());
        taskMap.put(Backlog.PROJECT_ID, backlog.getProjectId());

        mDatabase.child(backlog.getBacklogId()).updateChildren(taskMap, new DatabaseReference.CompletionListener() {
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

        if (mListener != null) mListener.onResponse(getErrorno() );
    }

}

package gr.eap.dxt.projects;

import android.content.Context;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import gr.eap.dxt.tools.FirebaseCall;

/**
 * Created by GEO on 11/2/2017.
 */

class FirebaseProjectDelete extends FirebaseCall {

    public interface Listener {
        void onResponse(String errorMsg);
    }
    private Listener mListener;

    private String projectId;


    FirebaseProjectDelete(Context context, String projectId, Listener mListener){
        super(context, true);
        this.projectId = projectId;
        this.mListener = mListener;
    }

    public void execute(){
        super.execute();

        if (projectId == null){
            addErrorNo("projectId == null");
            giveOutput();
            return;
        }
        if (projectId.isEmpty()){
            addErrorNo("projectId.isEmpty()");
            giveOutput();
            return;
        }

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Project.FIREBASE_LIST);
        if (mDatabase.child(projectId) == null){
            addErrorNo("mDatabase.child(projectId) == null");
            giveOutput();
            return;
        }

        mDatabase.child(projectId).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                if (databaseError != null){
                    addErrorNo(databaseError.getMessage());
                }

                giveOutput();
            }
        });
    }

    protected void giveOutput(){
        super.giveOutput();

        if (isCanceled()) return;
        if (mListener != null) mListener.onResponse(getErrorno());
    }

}
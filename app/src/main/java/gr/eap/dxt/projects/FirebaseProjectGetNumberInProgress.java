package gr.eap.dxt.projects;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import gr.eap.dxt.tools.FirebaseCall;

/**
 * Created by GEO on 10/2/2017.
 */

public class FirebaseProjectGetNumberInProgress extends FirebaseCall {

    public interface Listener {
        void onResponse(long number, String errorMsg);
    }
    private Listener mListener;


    public FirebaseProjectGetNumberInProgress(Context context, boolean notify, Listener mListener){
        super(context, notify);
        this.mListener = mListener;
    }

    public void execute(){
        super.execute();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Project.FIREBASE_LIST);
        if (mDatabase == null){
            addErrorNo("mDatabase == null");
            giveOutput(0);
            return;
        }

        mDatabase.orderByChild(Project.STATUS).equalTo(ProjectStatus.IN_PROGRESS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null) {
                    addErrorNo("dataSnapshot == null");
                    giveOutput(0);
                    return;
                }
                if (dataSnapshot.getChildren() == null) {
                    addErrorNo("dataSnapshotgetChildren() == null");
                    giveOutput(0);
                    return;
                }

                giveOutput(dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (databaseError != null) {
                    addErrorNo(databaseError.getMessage());
                }
                giveOutput(0);
            }
        });
    }

    private void giveOutput(long number){
        super.giveOutput();

        if (isCanceled()) return;
        if (mListener != null) mListener.onResponse(number, getErrorno());
    }

}

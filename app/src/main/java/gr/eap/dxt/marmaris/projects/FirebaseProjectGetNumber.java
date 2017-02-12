package gr.eap.dxt.marmaris.projects;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import gr.eap.dxt.marmaris.tools.FirebaseCall;

/**
 * Created by GEO on 10/2/2017.
 */

public class FirebaseProjectGetNumber extends FirebaseCall {

    public interface Listener {
        void onResponse(long number);
    }
    private Listener mListener;


    public FirebaseProjectGetNumber(Context context, boolean notify, Listener mListener){
        super(context, notify, false);
        this.mListener = mListener;
    }

    public void execute(){
        super.execute();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Project.FIREBASE_LIST);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
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

        if (getErrorno() != null && !getErrorno().isEmpty()){
            alertError(getErrorno());
        }

        if (mListener != null) mListener.onResponse(number);
    }

}

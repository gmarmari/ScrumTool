package gr.eap.dxt.sprints;

import android.content.Context;
import java.util.ArrayList;


import gr.eap.dxt.tools.FirebaseCall;

/**
 * Created by GEO on 19/2/2017.
 */

class FirebaseSprintEditMany  extends FirebaseCall {

    public interface Listener {
        void onResponse(String errorMsg);
    }
    private Listener mListener;

    private ArrayList<Sprint> sprints;
    private int index;

    FirebaseSprintEditMany(Context context, ArrayList<Sprint> sprints, Listener mListener){
        super(context, false);
        this.sprints = sprints;
        this.mListener = mListener;
    }

    public void execute(){
        super.execute();

        if (sprints == null){
            addErrorNo("sprints == null");
            giveOutput();
            return;
        }
        if (sprints.isEmpty()){
            addErrorNo("sprints.isEmpty()");
            giveOutput();
            return;
        }

        index = 0;
        editNextSprint();
    }


    private void editNextSprint(){
        if (sprints == null){
            addErrorNo("sprints == null");
            giveOutput();
            return;
        }
        if (index < 0) {
            addErrorNo("index < 0");
            giveOutput();
            return;
        }
        if (index >= sprints.size()){
            giveOutput();
            return;
        }

        Sprint sprint = sprints.get(0);

        new FirebaseSprintEdit(getContext(), sprint, false, new FirebaseSprintEdit.Listener() {
            @Override
            public void onResponse(String errorMsg) {
                if (errorMsg != null && !errorMsg.isEmpty()){
                    addErrorNo(errorMsg);
                }
                index++;
                editNextSprint();
            }
        }).execute();
    }

    protected void giveOutput(){
        super.giveOutput();

        if (isCanceled()) return;
        if (mListener != null) mListener.onResponse(getErrorno() );
    }
}

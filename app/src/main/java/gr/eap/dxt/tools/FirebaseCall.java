package gr.eap.dxt.tools;

import android.content.Context;
import android.support.annotation.NonNull;


import java.util.Timer;
import java.util.TimerTask;

import gr.eap.dxt.R;

/**
 * Created by GEO on 5/2/2017.
 */

public class FirebaseCall {

    private @NonNull
    Context context;
    protected @NonNull Context getContext(){
        return context;
    }

    private String errorno;
    protected String getErrorno(){
        return errorno;
    }
    protected void addErrorNo(String errorno){
        if (errorno == null || errorno.isEmpty()) return;

        if (this.errorno == null) this.errorno = "";
        if (!this.errorno.isEmpty()) this.errorno += "\n";
        this.errorno += errorno;
    }

    private boolean isCanceled;
    public boolean isCanceled() {
        return isCanceled;
    }

    public void cancel(){
        isCanceled = true;
    }

    private boolean notify;

    public FirebaseCall(@NonNull Context context, boolean notify){
        this.context = context;
        this.notify = notify;
        isCanceled = false;
    }

    private MyProgressDialog myDialog;

    private String dialogTitle;
    protected void setDialogTitle(String dialogTitle){
        this.dialogTitle = dialogTitle;
    }

    protected void execute(){
        if (notify){
            if (dialogTitle == null || dialogTitle.isEmpty()) dialogTitle = context.getResources().getString(R.string.loading);
            if (myDialog == null) {
                myDialog = new MyProgressDialog(context, dialogTitle , true);
            }else{
                myDialog.setMessage(dialogTitle);
            }
            myDialog.setFirebaseCall(this);
            myDialog.start();

            final Timer timer = new Timer();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    if(myDialog.isShowing()){
                        myDialog.stop();
                        isCanceled = true;
                    }
                    timer.cancel();
                }
            }, 30000);
        }
    }

    protected void giveOutput(){
        if (errorno != null && !errorno.isEmpty()){
            writeError(errorno);
        }
        if (notify){
            if (myDialog != null) myDialog.stop();
        }
    }

    protected void writeError(String error){
        if (error == null || error.isEmpty()) return;
        AppShared.writeErrorToLogString(getClass().toString(), error);
    }
}

package gr.eap.dxt.tools;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.util.Timer;
import java.util.TimerTask;

import gr.eap.dxt.R;

/**
 * Created by GEO on 22/1/2017.
 */

public class MyProgressDialog {

    private Context context;
    private ProgressDialog dialog;

    private FirebaseCall mFirebaseCall;
    public void setFirebaseCall(FirebaseCall mFirebaseCall){
        this.mFirebaseCall = mFirebaseCall;
    }

    private boolean isCanceled;
    public boolean isCanceled() {
        return isCanceled;
    }

    // Constructor for Styled Dialog with custom title
    public MyProgressDialog(Context context, String message, boolean allowCancel) {
        this.context = context;
        dialog = new ProgressDialog(context);

        if (message == null) message = context.getResources().getString(R.string.loading);
        dialog.setMessage(message);

        dialog.setCancelable(false);

        isCanceled = false;
        if (allowCancel) setCancelButton();
    }


    public void setMessage(String message) {
        if (dialog == null || message == null) return;
        dialog.setMessage(message);
    }

    public void start(){
        if (dialog.isShowing()) return;
        dialog.show();

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                if(dialog.isShowing()){
                    dialog.dismiss();
                    isCanceled = true;
                    if (mFirebaseCall != null){
                        mFirebaseCall.cancel();
                    }
                }
                timer.cancel();
            }
        }, 130000);
    }

    public void stop(){
        if(dialog.isShowing()) dialog.dismiss();
    }

    public boolean isShowing(){
        return dialog != null && dialog.isShowing();
    }

    private void setCancelButton(){
        if (dialog == null) return;
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                isCanceled = true;
                if (mFirebaseCall != null){
                    mFirebaseCall.cancel();
                }
            }
        });
    }

}

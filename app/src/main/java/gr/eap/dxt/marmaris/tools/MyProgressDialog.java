package gr.eap.dxt.marmaris.tools;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import java.util.Timer;
import java.util.TimerTask;

import gr.eap.dxt.R;

/**
 * Created by GEO on 22/1/2017.
 */

public class MyProgressDialog {

    private Context context;
    private ProgressDialog dialog;

    @SuppressWarnings("rawtypes")
    private AsyncTask mAsyncTask;
    @SuppressWarnings("rawtypes")
    public void setAsyncTask(AsyncTask mAsyncTask){
        this.mAsyncTask = mAsyncTask;
    }

    // Constructor for Styled Dialog with custom title
    public MyProgressDialog(Context context, String message, boolean allowCancel) {
        this.context = context;
        dialog = new ProgressDialog(context);

        if (message == null) message = context.getResources().getString(R.string.loading);
        dialog.setMessage(message);

        dialog.setCancelable(false);

        if (allowCancel) setCancelButton();
    }

    // Constructor for Styled Dialog
    public MyProgressDialog(Context context, int messageID,  boolean allowCancel) {
        this.context = context;
        dialog = new ProgressDialog(context,  R.style.my_progress_dialog);

        String message = null;
        try {
            message = context.getResources().getString(messageID);
        } catch (Exception e) {
            e.printStackTrace();
            AppShared.writeErrorToLogString(getClass().toString(), e.toString());
        }
        if (message == null) message = context.getResources().getString(R.string.loading);
        dialog.setMessage(message);

        dialog.setCancelable(false);
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
                    if (mAsyncTask != null) mAsyncTask.cancel(true);
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
                if (mAsyncTask != null) mAsyncTask.cancel(true);
            }
        });
    }

}

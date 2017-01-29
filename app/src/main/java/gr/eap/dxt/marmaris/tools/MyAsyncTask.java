package gr.eap.dxt.marmaris.tools;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import gr.eap.dxt.marmaris.R;

/**
 * Created by GEO on 22/1/2017.
 */

public abstract class MyAsyncTask extends AsyncTask<Void, Integer, Void> implements MyAsyncTaskListeners{

    private @NonNull Context context;
    protected @NonNull Context getContext(){
        return context;
    }

    private String errorno;
    protected String getErrorno(){
        return errorno;
    }

    private boolean allowCancel;

    private boolean notify;
    protected boolean getNotify(){
        return notify;
    }

    protected MyProgressDialog myDialog;

    private String dialogTitle;
    protected void setDialogTitle(String dialogTitle){
        this.dialogTitle = dialogTitle;
    }

    private boolean dontStopDialogOnPostExecute;
    protected void dontStopDialogOnPostExecute(){
        dontStopDialogOnPostExecute = true;
    }

    public MyAsyncTask(@NonNull Context context, boolean notify, boolean allowCancel){
        this.context = context;
        this.notify = notify;
        this.allowCancel = allowCancel;
        dontStopDialogOnPostExecute = false;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        myOnPreExecute();

        if (notify){
            if (dialogTitle == null || dialogTitle.isEmpty()) dialogTitle = context.getResources().getString(R.string.loading);
            if (myDialog == null) {
                myDialog = new MyProgressDialog(context, dialogTitle , allowCancel);
            }else{
                myDialog.setMessage(dialogTitle);
            }
            myDialog.setAsyncTask(this);
            myDialog.start();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            myDoInBackground();
        } catch (Exception e) {
            e.printStackTrace();
            errorno = e.toString();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (notify && !dontStopDialogOnPostExecute && myDialog != null) myDialog.stop();
        myOnPostExecute();
        if (myDialog != null && myDialog.isShowing() && !dontStopDialogOnPostExecute) myDialog.stop();
    }

    protected boolean isError(){
        return errorno != null && !errorno.isEmpty();
    }

    protected void alertError(String error){
        if (error == null || error.isEmpty()) return;
        writeError(error);
       // if (notify) new AlertWithDetails(context).showAlert(error);
    }



    protected void writeError(String error){
        if (error == null || error.isEmpty()) return;
        AppShared.writeErrorToLogString(getClass().toString(), error);
    }
}

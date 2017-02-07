package gr.eap.dxt.marmaris.tools;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.ContextThemeWrapper;

import java.util.ArrayList;

import gr.eap.dxt.marmaris.R;

/**
 * Created by GEO on 22/1/2017.
 */

public class MyAlertDialog {

    public enum MyAlertDialogType{
        MESSAGE,
        LIST
    }

    private Context context;

    private MyAlertDialogType type;

    private String title;
    private Integer icon;
    private String message;

    public MyAlertDialog(Context context, String title, Integer icon, String message, MyAlertDialogType type) {
        this.context = context;
        this.title = title;
        this.icon = icon;
        this.message = message;
        this.type = type != null ? type : MyAlertDialogType.MESSAGE;
    }

    private String negativeButtonText;
    private DialogInterface.OnClickListener negativeButtonListener;
    public void setNegativeButton(String text, DialogInterface.OnClickListener listener){
        if (listener == null) return;

        negativeButtonText = text != null ? text : context.getResources().getString(R.string.cancel);
        negativeButtonListener = listener;
    }

    private String positiveButtonText;
    private DialogInterface.OnClickListener positiveButtonListener;
    public void setPositiveButton(String text, DialogInterface.OnClickListener listener){
        if (listener == null) return;

        positiveButtonText = text != null ? text : context.getResources().getString(R.string.ok);
        positiveButtonListener = listener;
    }

    private ArrayList<String> items;
    private DialogInterface.OnClickListener itemSelectedListener;
    public void setItems(ArrayList<String> items, DialogInterface.OnClickListener listener){
        if (listener == null) return;

        this.items = items != null ? items : new ArrayList<String>();
        itemSelectedListener = listener;
    }

    public void alertMessage(){
        if (!type.equals(MyAlertDialogType.MESSAGE)) return;
        if (title == null && message == null) return;

        @SuppressWarnings("deprecation")
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(new ContextThemeWrapper(context, android.R.style.Theme_Material_Dialog));
        }else{
            builder = new AlertDialog.Builder(context);
        }

        if (title != null) builder.setTitle(title);
        if (message != null)  builder.setMessage(message);
        if (icon != null) builder.setIcon(icon);
        if (negativeButtonListener != null){
            builder.setNegativeButton(negativeButtonText, negativeButtonListener);
        }
        if (positiveButtonListener != null){
            builder.setPositiveButton(positiveButtonText, positiveButtonListener);
        }

        if (negativeButtonListener == null && positiveButtonListener == null){
            builder.setNegativeButton(R.string.ok,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        }

        builder.show();
    }

    public void alertListItems(){
        if (!type.equals(MyAlertDialogType.LIST)) return;
        if (items == null || items.isEmpty()) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (title != null) builder.setTitle(title);
        if (icon != null) builder.setIcon(icon);

        String[] itemsToArray = items.toArray(new String[items.size()]);
        builder.setItems(itemsToArray, itemSelectedListener);

        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


        builder.show();
    }

    @SuppressWarnings("SameParameterValue")
    public static void alertError(Context context, String title, String error){
        if (context == null) return;
        if (error == null || error.isEmpty()) return;

        if (title == null) title = context.getResources().getString(R.string.error_occured);
        new MyAlertDialog(context,
                title,
                R.drawable.ic_action_warning_purple,
                error,
                MyAlertDialog.MyAlertDialogType.MESSAGE).alertMessage();
    }

}

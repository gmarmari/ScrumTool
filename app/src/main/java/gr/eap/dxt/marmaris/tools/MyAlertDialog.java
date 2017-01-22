package gr.eap.dxt.marmaris.tools;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.util.ArrayList;

import gr.eap.dxt.R;

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

    private void alertMessage(){
        if (!type.equals(MyAlertDialogType.MESSAGE)) return;
        if (title == null && message == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
            builder.setNegativeButton(R.string.cancel,
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

}

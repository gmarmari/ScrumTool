package gr.eap.dxt.marmaris.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by GEO on 22/1/2017.
 */

public class MyToast {

    private Context context;
    private Toast toast;

    @SuppressLint("ShowToast")
    public MyToast(Context _context, int messageID){
        context = _context;
        toast = Toast.makeText(context, context.getResources().getString(messageID), Toast.LENGTH_SHORT);
    }

    @SuppressLint("ShowToast")
    public MyToast(Context _context, String message){
        context = _context;
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
    }

    public void show(){
        toast.show();
    }

    public void showInCenter(){
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

}

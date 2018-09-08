package gr.eap.dxt.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by GEO on 22/1/2017.
 */

public class MyToast {

    private Toast toast;

    @SuppressLint("ShowToast")
    public MyToast(Context context, int messageID){
        toast = Toast.makeText(context, context.getResources().getString(messageID), Toast.LENGTH_SHORT);
    }

    public void show(){
        if (toast != null){
            toast.show();
        }
    }

}

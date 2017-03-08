package gr.eap.dxt.tools;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by GEO on 31/1/2017.
 */

public class Keyboard {

    public static void close(Context context){
        if (context == null) return;
        View view = ((Activity) context).getCurrentFocus();
        if (view == null) return;
        try {
            InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        } catch (Exception e) {
            e.printStackTrace();
            AppShared.writeErrorToLogString(Keyboard.class.toString(), e.toString());
        }

    }

}

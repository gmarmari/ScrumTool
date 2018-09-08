package gr.eap.dxt.tools;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;

/**
 * Created by GEO on 29/1/2017.
 */

public class MyColor {

    @SuppressWarnings("deprecation")
    public static int getColorAccordingToAndroidVersion(Context context, int colorId){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.getColor(context, colorId);
        }else{
            return context.getResources().getColor(colorId);
        }
    }

}

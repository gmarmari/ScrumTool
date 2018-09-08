package gr.eap.dxt.backlog;

import android.content.Context;

import gr.eap.dxt.R;
import gr.eap.dxt.tools.MyColor;

/**
 * Created by GEO on 12/2/2017.
 */

public class BacklogStatus {

    public static final String TO_DO = "to_do";
    public static final String IN_PROGRESS = "in_progress";
    public static final String DONE = "done";

    public static String getBacklogStatus(Context context, String status){
        if (context == null) return null;
        if (status == null || status.isEmpty()) return null;

        if (status.equals(TO_DO)){
            return context.getResources().getString(R.string.to_do);
        }
        if (status.equals(IN_PROGRESS)){
            return context.getResources().getString(R.string.in_progress);
        }
        if (status.equals(DONE)){
            return context.getResources().getString(R.string.done);
        }

        return null;
    }

    public static Integer getBacklogStatusColor(Context context, String status){
        if (context == null) return null;
        if (status == null || status.isEmpty()) return null;

        if (status.equals(TO_DO)){
            return MyColor.getColorAccordingToAndroidVersion(context, R.color.color_blue);
        }
        if (status.equals(IN_PROGRESS)){
            return MyColor.getColorAccordingToAndroidVersion(context, R.color.color_orange);
        }
        if (status.equals(DONE)){
            return MyColor.getColorAccordingToAndroidVersion(context, R.color.color_green);
        }

        return null;
    }

}

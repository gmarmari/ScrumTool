package gr.eap.dxt.marmaris.sprints;

import android.content.Context;

import gr.eap.dxt.marmaris.R;

/**
 * Created by GEO on 12/2/2017.
 */

public class SprintStatus {

    public static final String NOT_STARTED = "not_Started";
    public static final String IN_PROGRESS = "in_progress";
    public static final String COMPLETED = "completed";
    public static final String CANCELED = "canceled";

    public static String getSprintStatus(Context context, String status){
        if (context == null) return null;
        if (status == null || status.isEmpty()) return null;

        if (status.equals(NOT_STARTED)){
            return context.getResources().getString(R.string.not_started);
        }
        if (status.equals(IN_PROGRESS)){
            return context.getResources().getString(R.string.in_progress);
        }
        if (status.equals(COMPLETED)){
            return context.getResources().getString(R.string.completed);
        }
        if (status.equals(CANCELED)){
            return context.getResources().getString(R.string.canceled);
        }

        return null;
    }

}

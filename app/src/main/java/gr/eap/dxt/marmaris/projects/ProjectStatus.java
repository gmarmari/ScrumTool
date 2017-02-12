package gr.eap.dxt.marmaris.projects;

import android.content.Context;

import gr.eap.dxt.marmaris.R;

/**
 * Created by GEO on 22/1/2017.
 */

public class ProjectStatus {

    public static final String NOT_STARTED = "not_started";
    public static final String IN_PROGRESS = "inProgress";
    public static final String ENDED = "ended";
    public static final String CANCELED = "canceled";

    public static String getProjectStatus(Context context, String status){
        if (context == null) return null;
        if (status == null || status.isEmpty()) return null;

        if (status.equals(NOT_STARTED)){
            return context.getResources().getString(R.string.not_started);
        }
        if (status.equals(IN_PROGRESS)){
            return context.getResources().getString(R.string.in_progress);
        }
        if (status.equals(ENDED)){
            return context.getResources().getString(R.string.ended);
        }
        if (status.equals(CANCELED)){
            return context.getResources().getString(R.string.canceled);
        }

        return null;
    }

}

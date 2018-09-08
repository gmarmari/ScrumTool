package gr.eap.dxt.backlog;

import android.content.Context;

import gr.eap.dxt.R;

/**
 * Created by GEO on 12/2/2017.
 */

public class BacklogType {

    public static final String FEATURE = "Feature";
    public static final String BUG = "Bug";
    public static final String OTHER = "Other";

    public static String getBacklogType(Context context, String backlogType){
        if (context == null) return null;
        if (backlogType == null || backlogType.isEmpty()) return null;

        if (backlogType.equals(BacklogType.FEATURE)){
            return context.getResources().getString(R.string.feature);
        }
        if (backlogType.equals(BacklogType.BUG)){
            return context.getResources().getString(R.string.bug);
        }
        if (backlogType.equals(BacklogType.OTHER)){
            return context.getResources().getString(R.string.other);
        }

        return null;
    }

}

package gr.eap.dxt.backlog;

import android.content.Context;

import gr.eap.dxt.R;
import gr.eap.dxt.tools.MyColor;

/**
 * Created by GEO on 10/3/2017.
 */

public class Priority {

    public static final String PRIO_1_URGENT = "1_Urgent";
    public static final String PRIO_2_IMPORTANT = "2_Important";
    public static final String PRIO_3_NORMAL = "3_Normal";
    public static final String PRIO_4_NOT_IMPORTANT = "4_NotImportant";

    public static String getPriority(Context context, String prio){
        if (context == null) return null;
        if (prio == null || prio.isEmpty()) return null;

        if (prio.equals(PRIO_1_URGENT)){
            return context.getResources().getString(R.string.prio_1_urgent);
        }
        if (prio.equals(PRIO_2_IMPORTANT)){
            return context.getResources().getString(R.string.prio_2_important);
        }
        if (prio.equals(PRIO_3_NORMAL)){
            return context.getResources().getString(R.string.prio_3_normal);
        }
        if (prio.equals(PRIO_4_NOT_IMPORTANT)){
            return context.getResources().getString(R.string.prio_4_not_important);
        }

        return null;
    }

    public static Integer getPriorityColor(Context context, String prio){
        if (context == null) return null;
        if (prio == null || prio.isEmpty()) return null;

        if (prio.equals(PRIO_1_URGENT)){
            return MyColor.getColorAccordingToAndroidVersion(context, R.color.color_red);
        }
        if (prio.equals(PRIO_2_IMPORTANT)){
            return MyColor.getColorAccordingToAndroidVersion(context, R.color.color_orange);
        }
        if (prio.equals(PRIO_3_NORMAL)){
            return MyColor.getColorAccordingToAndroidVersion(context, R.color.color_green);
        }
        if (prio.equals(PRIO_4_NOT_IMPORTANT)){
            return MyColor.getColorAccordingToAndroidVersion(context, R.color.color_blue);
        }

        return null;
    }

}

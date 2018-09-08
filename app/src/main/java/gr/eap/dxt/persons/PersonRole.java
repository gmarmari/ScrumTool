package gr.eap.dxt.persons;

import android.content.Context;

import gr.eap.dxt.R;

/**
 * Created by GEO on 22/1/2017.
 */

public class PersonRole {

    public static final String DEVELOPER = "developer";
    public static final String SCRUM_MASTER = "scrum_master";
    public static final String PRODUCT_OWNER = "product_owner";

    public static String getPersonRole(Context context, String role){
        if (context == null) return null;
        if (role == null || role.isEmpty()) return null;

        if (role.equals(PersonRole.DEVELOPER)){
            return context.getResources().getString(R.string.developer);
        }
        if (role.equals(PersonRole.PRODUCT_OWNER)){
            return context.getResources().getString(R.string.product_owner);
        }
        if (role.equals(PersonRole.SCRUM_MASTER)){
            return context.getResources().getString(R.string.scrum_master);
        }

        return null;
    }

}

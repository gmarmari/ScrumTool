package gr.eap.dxt.tools;

import com.google.firebase.database.DataSnapshot;

import java.util.Date;
import java.util.HashMap;


/**
 * Created by GEO on 14/2/2017.
 */

public class FirebaseParse {

    @SuppressWarnings("unchecked")
    public static Date getDate(DataSnapshot dataSnapshot) throws Exception{
        if (dataSnapshot == null) return null;

        HashMap<String, Long> map = (HashMap<String, Long>) dataSnapshot.getValue();
        if (map != null) {
            Long msec = map.get("time");
            if (msec != null && msec > 0){
                return new Date(msec);
            }
        }

        return null;
    }

    public static String getString(DataSnapshot dataSnapshot) throws Exception{
        if (dataSnapshot == null) return null;
        if (dataSnapshot.getValue() == null) return null;
        return dataSnapshot.getValue().toString();
    }

    public static Long getLong(DataSnapshot dataSnapshot) throws Exception{
        if (dataSnapshot == null) return null;
        if (dataSnapshot.getValue() == null) return null;
        return (Long) dataSnapshot.getValue();
    }
}

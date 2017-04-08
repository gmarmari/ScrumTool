package gr.eap.dxt.tools;

import android.util.Log;
import android.widget.ListView;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import gr.eap.dxt.persons.Person;

/**
 * Created by GEO on 21/1/2017.
 */

public class AppShared {

    public static final String NO_ID = "no_id";

    public static int projectListSelPos = ListView.INVALID_POSITION;
    public static int personListSelPos = ListView.INVALID_POSITION;
    public static int backlogListSelPos = ListView.INVALID_POSITION;
    public static int sprintListSelPos = ListView.INVALID_POSITION;

    private static Person logginUser;
    public static Person getLogginUser(){
        return logginUser;
    }
    public static void setLogginUser(Person person){
        if (person == null) return;
        if (person.getPersonId() == null || person.getPersonId().isEmpty()) return;

        logginUser = person;
    }
    public static void logout(){
        logginUser = null;
    }

    private static String logString = "";

    private static SimpleDateFormat dateFormat;

    public static void writeInfoToLogString(String TAG, String message){
        if (message == null || message.isEmpty()) return;

        Log.i(TAG, message);
        writeToMyLogString(TAG, message);
    }

    public static void writeErrorToLogString(String TAG, String message){
        if (message == null || message.isEmpty()) return;

        Log.e(TAG, message);
        writeToMyLogString(TAG, message);
    }

    private static void writeToMyLogString(String TAG, String message){
        if (logString == null) logString = "";

        String timeStamp = null;
        try {
            if(dateFormat == null) dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ", Locale.getDefault());
            timeStamp = dateFormat.format(new Date()) + ": ";
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (timeStamp != null && !timeStamp.isEmpty()) logString += timeStamp;
        if (TAG != null && !TAG.isEmpty()) logString += TAG + ": ";

        if (message != null){
            if (!message.isEmpty()) logString += message;
            if (!message.endsWith("\n")) logString += "\n";
        }
    }


}

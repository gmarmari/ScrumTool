package gr.eap.dxt.marmaris.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Created by GEO on 22/1/2017.
 */

public class StoreManagement {

    private SharedPreferences prefs;
    private static final String PREF_NAME = "com.staware.androidApp.SharedPreferences";

    public StoreManagement(Context context){
        if (context != null) {
            prefs = context.getSharedPreferences(PREF_NAME, 0);
        }
    }

    //Registration ID
    private static final String PROPERTY_FCM_REG_ID = "FCMRegistrationId";
    public String getFCMRegistrationId(){
        if (prefs == null) return "";
        return prefs.getString(PROPERTY_FCM_REG_ID, "");
    }
    public void setFCMRegistrationId(String regid){
        if (regid == null) return;
        if (prefs == null) return;
        Editor editor = prefs.edit();
        if (editor == null) return;
        editor.putString(PROPERTY_FCM_REG_ID, regid);
        editor.apply();
    }

    private static final String PREF_USER_LEARNED_MAIN_DRAWER = "pref_user_learned_main_drawer";
    public boolean userLearnedMainDrawer(){
        return prefs != null && prefs.getBoolean(PREF_USER_LEARNED_MAIN_DRAWER, false);
    }
    public void setUserLearnedMainDrawer(boolean value){
        if (prefs == null) return;
        Editor editor = prefs.edit();
        if (editor == null) return;
        editor.putBoolean(PREF_USER_LEARNED_MAIN_DRAWER, value);
        editor.apply();
    }

    private static final String PREF_FIRST_RUN_AFTER_INSTAL = "pref_first_raun_after_instal";
    public boolean isFirstRunAfterInstall(){
        return prefs != null && prefs.getBoolean(PREF_FIRST_RUN_AFTER_INSTAL, true);
    }
    public void setFirstRunAfterInstal(boolean value){
        if (prefs == null) return;
        Editor editor = prefs.edit();
        if (editor == null) return;
        editor.putBoolean(PREF_FIRST_RUN_AFTER_INSTAL, value);
        editor.apply();
    }

    private static final String PREF_IS_LOGGED_IN = "pref_is_logged_in";
    public boolean isLoggedIn(){
        return prefs != null && prefs.getBoolean(PREF_IS_LOGGED_IN, false);
    }
    public void setIsLoggedIn(boolean value){
        if (prefs == null) return;
        Editor editor = prefs.edit();
        if (editor == null) return;
        editor.putBoolean(PREF_IS_LOGGED_IN, value);
        editor.apply();
    }
}

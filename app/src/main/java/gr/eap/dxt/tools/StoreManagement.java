package gr.eap.dxt.tools;

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
// --Commented out by Inspection START (5/3/2017 6:43 μμ):
//    public String getFCMRegistrationId(){
//        if (prefs == null) return "";
//        return prefs.getString(PROPERTY_FCM_REG_ID, "");
//    }
// --Commented out by Inspection STOP (5/3/2017 6:43 μμ)
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

    // Saved e-mail for execute
    private static final String SAVED_EMAIL_FOR_LOGIN = "saved_email_for_login";
    public String getSavedEmailForLogin(){
        if (prefs == null) return "";
        return prefs.getString(SAVED_EMAIL_FOR_LOGIN, "");
    }
    public void setSavedEmailForLogin(String email){
        if (email == null) return;
        if (prefs == null) return;
        Editor editor = prefs.edit();
        if (editor == null) return;
        editor.putString(SAVED_EMAIL_FOR_LOGIN, email);
        editor.apply();
    }

    // Saved passwort for execute
    private static final String SAVED_PASSWORD_FOR_LOGIN = "saved_password_for_login";
    public String getSavedPasswordForLogin(){
        if (prefs == null) return "";
        return prefs.getString(SAVED_PASSWORD_FOR_LOGIN, "");
    }
    public void setSavedPasswordForLogin(String password){
        if (password == null) return;
        if (prefs == null) return;
        Editor editor = prefs.edit();
        if (editor == null) return;
        editor.putString(SAVED_PASSWORD_FOR_LOGIN, password);
        editor.apply();
    }

}

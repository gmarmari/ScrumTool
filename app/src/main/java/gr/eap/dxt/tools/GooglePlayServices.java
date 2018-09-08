package gr.eap.dxt.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

/**
 * Created by GEO on 29/1/2017.
 */

public class GooglePlayServices {

    private boolean playServicesAvailable;

    private Context context;

    //constructor
    public GooglePlayServices(Context context){
        this.context = context;
    }

    public int checkPlayServices(boolean showDialog) {

        GoogleApiAvailability gApi = GoogleApiAvailability.getInstance();
        int resultCode = gApi.isGooglePlayServicesAvailable(context);

        if (resultCode != ConnectionResult.SUCCESS) {
            AppShared.writeErrorToLogString(getClass().toString(), "No Google Play Services Available");
            AppShared.writeErrorToLogString(getClass().toString(), "result code: "+resultCode);

            if (showDialog) {
                if (gApi.isUserResolvableError(resultCode)) {
                    gApi.getErrorDialog((Activity) context, resultCode, MyRequestCodes.PLAY_SERVICES_RESOLUTION_REQUEST).show();
                }
            }

            playServicesAvailable = false;

        }else{
            playServicesAvailable = true;
        }
        return resultCode;
    }

    public void getRegistrationID(){
        if (playServicesAvailable)registerInBackground();
    }

    private void registerInBackground() {
        Intent intent = new Intent(context, FCMRegistrationIntentService.class);
        context.startService(intent);
    }

}

package gr.eap.dxt.tools;


import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;


import gr.eap.dxt.R;

/**
 * Created by GEO on 29/1/2017.
 */

public class FCMRegistrationIntentService extends IntentService {

    public FCMRegistrationIntentService() {
        super("FCMRegistrationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            if (token != null && !token.isEmpty()){
                StoreManagement store = new StoreManagement(this);
                store.setFCMRegistrationId(token);
                AppShared.writeInfoToLogString(getClass().toString(), "FCM Registration Id (puch id): "+token);
            }
        }catch (Exception e){
            e.printStackTrace();
            AppShared.writeErrorToLogString(getClass().toString(),  "Error during push registration. Message: "+e.toString());
        }
    }
}


package gr.eap.dxt.tools;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by GEO on 29/1/2017.
 */

public class FCMInstanceIDService extends FirebaseInstanceIdService {

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();

        if (token != null && !token.isEmpty()){
            StoreManagement store = new StoreManagement(this);
            store.setFCMRegistrationId(token);
            AppShared.writeInfoToLogString(getClass().toString(), "FCM Registration Id (puch id): "+token);
        }

    }

}

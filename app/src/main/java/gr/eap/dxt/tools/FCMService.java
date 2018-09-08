package gr.eap.dxt.tools;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import gr.eap.dxt.R;
import gr.eap.dxt.main.MainNavigationActivity;

/**
 * Created by GEO on 29/1/2017.
 */

public class FCMService extends FirebaseMessagingService {

    private static final int NOTIFICATION_ID = 1987;

    public static final String OBJECT_TYPE = "object_type";
    public static final String OBJECT_ID = "object_id";


    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
        AppShared.writeInfoToLogString(getClass().toString(), "Deleted messages on server");
    }

    @Override
    public void onSendError(String s, Exception e) {
        super.onSendError(s, e);
        AppShared.writeErrorToLogString(getClass().toString(), "Send error, messageId: "+ s + ", error: " +e.toString());
    }


    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
        AppShared.writeInfoToLogString(getClass().toString(), "Sending message: " +s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        AppShared.writeInfoToLogString(getClass().toString(), "Push notification received");
        if (remoteMessage == null){
            AppShared.writeInfoToLogString(getClass().toString(), "remoteMessage == null");
            return;
        }
        if (remoteMessage.getFrom() != null){
            AppShared.writeInfoToLogString(getClass().toString(), "From: " +remoteMessage.getFrom());
        }
        if (remoteMessage.getNotification() != null){
            AppShared.writeInfoToLogString(getClass().toString(), "Message Notification Body: " +remoteMessage.getNotification().getBody());
        }
        if (remoteMessage.getData() != null){
            AppShared.writeInfoToLogString(getClass().toString(), "Message data payload: " +remoteMessage.getData());
        }

        sendNotification(remoteMessage);
    }

    private void sendNotification(RemoteMessage remoteMessage){
        if (remoteMessage == null) return;
        // Notification
        if (remoteMessage.getNotification() == null){
            AppShared.writeInfoToLogString(getClass().toString(), "remoteMessage.getNotification() == null");
            return;
        }

        // Title
        String title = remoteMessage.getNotification().getTitle();
        AppShared.writeInfoToLogString(getClass().toString(), "Title: "+title);

        // Body
        String body = remoteMessage.getNotification().getBody();
        AppShared.writeInfoToLogString(getClass().toString(), "Body: "+body);

        boolean noTitle = title == null || title.isEmpty();
        boolean noBody = body == null || body.isEmpty();
        if (noTitle && noBody) {
            AppShared.writeInfoToLogString(getClass().toString(), "noTitle && noBody");
            return;
        }
        if (noTitle) title = getResources().getString(R.string.app_name);

        // Data
        int object_type = -1;
        long object_id = -1;

        if (remoteMessage.getData() != null){
            // object_type
            try {
                String object_type_string = remoteMessage.getData().get(OBJECT_TYPE);
                object_type = Integer.parseInt(object_type_string);
            } catch (Exception e) {
                object_type = -1;
            }

            // object_id
            try {
                String object_id_string =remoteMessage.getData().get(OBJECT_ID);
                object_id = Long.parseLong(object_id_string);
            } catch (Exception e) {
                object_id = -1;
            }
        }

        if (object_type == -1 && object_id == -1){
            AppShared.writeInfoToLogString(getClass().toString(), "object_type == -1 && object_id == -1");
            return;
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);



        mBuilder.setSmallIcon(R.drawable.ic_action_about_purple);
        mBuilder.setColor(MyColor.getColorAccordingToAndroidVersion(this, R.color.color_eap_logo_background));

        mBuilder.setContentTitle(title);
        mBuilder.setContentText(body);

        Intent intent = new Intent(this, MainNavigationActivity.class);
        intent.putExtra(OBJECT_TYPE, String.valueOf(object_type)); // when app is in background this is automated created and all data are strings..
        intent.putExtra(OBJECT_ID, String.valueOf(object_id)); // ..thus object type and id are send as strings
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, intent, PendingIntent.FLAG_ONE_SHOT);
        if (pendingIntent != null) {
            mBuilder.setContentIntent(pendingIntent);
        }

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification mNotification = mBuilder.build();

        mNotification.flags = Notification.FLAG_ONLY_ALERT_ONCE | Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_AUTO_CANCEL;
        mNotification.defaults |= Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS;

        mNotificationManager.notify(NOTIFICATION_ID , mNotification);
    }

}

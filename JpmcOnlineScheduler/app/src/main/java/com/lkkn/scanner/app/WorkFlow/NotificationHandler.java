package com.lkkn.scanner.app.WorkFlow;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.lkkn.scanner.app.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Date;
import java.util.Map;

/**
 * Created by RANJITH on 13-05-2018.
 */

public class NotificationHandler extends FirebaseMessagingService {

    private static final String TAG = "NotificationHandler";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());
        int m=(int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Map<String,String> data = remoteMessage.getData();
            if(!data.isEmpty()) {
                if(data.containsKey("body")) {
                    //CharSequence cs=data.get("body").toString();
                    //NotificationManager notif=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                   // String branchname=data.get("branch").toString();

                    final Notification.Builder builder = new Notification.Builder(this);
                    builder.setStyle(new Notification.BigTextStyle(builder)
                            .bigText(data.get("body").toString())
                            .setBigContentTitle(data.get("title").toString())
                            .setSummaryText(data.get("summary").toString()))
                            .setContentTitle(data.get("title").toString())
                            .setContentText(data.get("summary").toString())
                            .setSmallIcon(R.drawable.appicon)
                            .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                                    R.drawable.appicon));


                    final NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    nm.notify(m, builder.build());

//                    Notification notify=new Notification.Builder
//                            (getApplicationContext()).setContentTitle(data.get("title").toString())
//                            .setContentText(data.get("body").toString())
//                            .setStyle(new NotificationCompat.BigTextStyle().bigText(cs))
//
//                            .setSmallIcon(R.drawable.ic_error_outline_black_24dp).build();
//
//                    notify.flags |= Notification.FLAG_AUTO_CANCEL;
//                    notif.notify(0, notify);

                }
            }
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
            } else {
                // Handle message within 10 seconds
//                handleNow();
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }
}

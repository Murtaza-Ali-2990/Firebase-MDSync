package com.example.fbtestapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FBMessageService extends FirebaseMessagingService {

    String TAG = "FBMessageService";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        DatabaseHandler databaseHandler = new DatabaseHandler(this);
        Log.i(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.i(TAG, "Message data payload: " + remoteMessage.getData());
            if(remoteMessage.getData().containsKey("delete")){
                databaseHandler.deleteDataNotif(Long.valueOf(remoteMessage.getData().get("id")));
                sendNotification("Data deleted", remoteMessage.getData().get("name"), Long.valueOf(remoteMessage.getData().get("id")));
                return;
            }
            UserData userData = UserData.makeUserData(remoteMessage.getData());
            if(databaseHandler.doesIdExists(userData.getId())){
                Log.i(TAG, "onMessageReceived: UPDATES " + userData.getUpdates());
                if(userData.getUpdates() == 1) {
                    databaseHandler.updateDataNotif(userData);
                    sendNotification("Data updated", userData.getName(), userData.getId());
                }
            } else {
                databaseHandler.addDataNotif(userData);
                sendNotification("Data added", userData.getName(), userData.getId());
            }
        }
    }

    private void sendNotification(String messageBody, String title, long id) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "id000" + id;
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            try {
                assert notificationManager != null;
                notificationManager.createNotificationChannel(channel);
            } catch (NullPointerException e) {
                Log.i(TAG, "sendNotification: NULL POINTER");
            }
        }

        assert notificationManager != null;
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        Log.i(TAG, "onNewToken: " + s);
    }
}

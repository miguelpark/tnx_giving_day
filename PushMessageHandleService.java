package com.blackrabbit.tpoit.push;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.blackrabbit.spoit.repo.SpoitDataReadableFunc;
import com.blackrabbit.tpoit.R;
import com.mingoon.motify.MotifyConstants;

public class PushMessageHandleService extends Service {
    private static final String TAG = "PushMessageHandleService";

    private static final String CHANNEL_NAME = "Spoit Notification Channel";
    private static final String CHANNEL_ID = "spoit_push";
    private static final String GROUP_ID = "spoit_push_group";
    private static final int SUMMARY_ID = 0;


    private PushServiceManager mPushServiceManager;
    private int counter = 1;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand :" + intent);
        if(intent != null) {
            String message = intent.getStringExtra(MotifyConstants.KEY_EXTRA_RECEIVED_MESSAGE);
            if(message != null )
                showNotificationAndPopupWindow(message);
        }

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        mPushServiceManager = new PushServiceManager(getApplicationContext());
        mPushServiceManager.registerPushInfo(SpoitDataReadableFunc.create(getApplicationContext()).getUserInfo());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        mPushServiceManager.clearAllRegisteredPushInfo();
        mPushServiceManager = null;
    }

    private void showNotificationAndPopupWindow(String message) {
        createNotificationChannel();

        Intent popupIntent = new Intent(getApplicationContext(), PopupActivity.class);
        popupIntent.putExtra(MotifyConstants.KEY_EXTRA_RECEIVED_MESSAGE, message);
        PendingIntent popupPendingIntent = PendingIntent.getActivity(getApplicationContext(), counter, popupIntent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_main)
                .setContentTitle("SPOIT Message")
                .setContentText(message)
                .setGroup(GROUP_ID)
                .setContentIntent(popupPendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(counter++, builder.build());

        NotificationCompat.Builder summaryBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_main)
                .setContentTitle("SPOIT Push Message")
                .setContentText("You have " + (counter - 1) + " messages")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setGroup(GROUP_ID)
                .setGroupSummary(true)
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY);

        Notification summaryNotification = summaryBuilder.build();
        notificationManager.notify(SUMMARY_ID, summaryNotification);
    }

    private void createNotificationChannel() {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        int channelImportance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, channelImportance);
        notificationChannel.setDescription("SPOIT Push Message");
        notificationManager.createNotificationChannel(notificationChannel);
    }
}

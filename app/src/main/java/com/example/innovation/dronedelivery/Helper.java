package com.example.innovation.dronedelivery;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.WindowManager;

/**
 * Created by 145747 on 9/9/2015.
 */
public class Helper {

    public static boolean deliveryAccepted = false;
    public static boolean deliveryArrived = false;
    public static boolean disableConfirmButton = true;
    public static boolean stopFindingDrone = false;
    public static final String TAG = "DroneDelivery";
    public static Context mainContext;

    public void createNotification(Context context){

        if (mainContext == null){
            Log.i(TAG,"mainContext is NULL");
            mainContext = context;
        }
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mainContext)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Drone Delivery")
                .setContentText(" You have got a Delivery ")
                .setCategory(Notification.CATEGORY_PROMO)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setPriority(Notification.PRIORITY_MAX)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setAutoCancel(true)
                .setSound(alarmSound);
        /*((Activity) context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);*/

        Intent resultIntent = new Intent(mainContext,DeliveryConfirmation.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mainContext);
        stackBuilder.addParentStack(DeliveryConfirmation.class);
        stackBuilder.addNextIntentWithParentStack(resultIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_ONE_SHOT);

        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)mainContext.getSystemService(Context.NOTIFICATION_SERVICE);

        Log.i(TAG, "I am calling createNotification");
        notificationManager.notify(1, builder.build());
    }
}

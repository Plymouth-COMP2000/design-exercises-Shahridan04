package com.example.restaurantapp.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.restaurantapp.R;
import com.example.restaurantapp.activity.ReservationsActivity;
import com.example.restaurantapp.activity.MyReservationsActivity;

public class NotificationHelper {
    
    private static final String CHANNEL_ID = "restaurant_app_channel";
    private static final String CHANNEL_NAME = "Restaurant App Notifications";
    private static final int NOTIFICATION_ID_NEW_RESERVATION = 1001;
    private static final int NOTIFICATION_ID_RESERVATION_UPDATE = 1002;
    
    private Context context;
    private NotificationManager notificationManager;
    
    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notifications for reservations and updates");
            notificationManager.createNotificationChannel(channel);
        }
    }
    
    public static boolean areNotificationsEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        return prefs.getBoolean("notifications_enabled", true); // Default to enabled
    }
    
    public void showNewReservationNotification(String guestName, String date, String time) {
        if (!areNotificationsEnabled(context)) {
            return;
        }
        
        Intent intent = new Intent(context, ReservationsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("New Reservation")
                .setContentText(guestName + " has made a reservation for " + date + " at " + time)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(guestName + " has made a reservation for " + date + " at " + time))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        
        notificationManager.notify(NOTIFICATION_ID_NEW_RESERVATION, builder.build());
    }
    
    public void showReservationUpdateNotification(String message, String status) {
        if (!areNotificationsEnabled(context)) {
            return;
        }
        
        Intent intent = new Intent(context, MyReservationsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        String title = "Reservation " + status;
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        
        notificationManager.notify(NOTIFICATION_ID_RESERVATION_UPDATE, builder.build());
    }
}


package com.e.arena.Utility;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.e.arena.MyFirebaseMessagingService;
import com.e.arena.R;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.e.arena.NotificationClass.DEMO_CHANNEL_ID;

public class Utils {

    public  final String CREDITS_PAID_TO_TEXT ="Credits Paid to:\n" ;
    public String MONEY_ADD="ADD";
    public String MONEY_PAID="PAID";
    public String FAILED_MSG="Transaction Failed";
    public String TRANSACTION_INSUFFICIENT ="Insufficient Credits";
    public String AVAILABLE_CREDITS="Available Credits ";
    public String MONEY_ADDED_TO_WALLET="Money Added to Wallet:\nvia: ";
    NotificationManagerCompat managerCompat;
    MyFirebaseMessagingService myFirebaseMessagingService;
    FirebaseFirestore db;


    public Utils() {
    }

    public Utils(MyFirebaseMessagingService myFirebaseMessagingService) {
        this.myFirebaseMessagingService = myFirebaseMessagingService;
        managerCompat = NotificationManagerCompat.from( myFirebaseMessagingService );
    }

    public void showNotification(String title, String body, String click_action, String notificationId) {

       /* Intent intent = new Intent( click_action );
        intent.putExtra( "notification_id", notificationId );
        PendingIntent pendingIntent = PendingIntent.getActivity( myFirebaseMessagingService, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );*/
        Notification notification = new NotificationCompat.Builder( myFirebaseMessagingService, DEMO_CHANNEL_ID )
                .setSmallIcon( R.drawable.ic_launcher_foreground )
                .setContentTitle( title )
                .setContentText( body )
                .setPriority( NotificationCompat.PRIORITY_HIGH )
                .setCategory( NotificationCompat.CATEGORY_MESSAGE )
                //.setContentIntent( pendingIntent )
                .build();

        managerCompat.notify( (int) System.currentTimeMillis(), notification );
    }

}

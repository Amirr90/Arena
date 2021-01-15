package com.e.arena;

import com.e.arena.Utility.Utils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseMessagingService extends FirebaseMessagingService {


    Utils utils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived( remoteMessage );
        if (remoteMessage.getNotification() != null) {
            String body = remoteMessage.getNotification().getBody();
            String title = remoteMessage.getNotification().getTitle();
            //String click_action = remoteMessage.getNotification().getClickAction();
            String notificationId = remoteMessage.getData().get( "notification_id" );
            utils = new Utils( this );
            if (utils != null) {
                utils.showNotification( title, body, "", notificationId );
            }

        }
    }

}

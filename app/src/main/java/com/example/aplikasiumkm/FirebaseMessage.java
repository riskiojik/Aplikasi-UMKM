package com.example.aplikasiumkm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.dynamicanimation.animation.SpringAnimation;

import com.example.aplikasiumkm.user.DetailPesananPenggunaActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class FirebaseMessage extends FirebaseMessagingService {
    private static final String NOTIFICATION_CHANEL_ID = "MY_NOTIFICATION_CHANNEL_ID";

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        //getNotification
        String notificationType = remoteMessage.getData().get("notificationType");

        if (notificationType.equals("PesananMasuk")){
            String pembeliUid = remoteMessage.getData().get("pembeliUid");
            String penjualUid = remoteMessage.getData().get("penjualUid");
            String pesananId = remoteMessage.getData().get("pesananId");
            String notificationTitle = remoteMessage.getData().get("notificationTitle ");
            String notificationDescription = remoteMessage.getData().get("notificationMessage");

            if (firebaseUser !=null && firebaseAuth.getUid().equals(penjualUid)){
                showNotification(pesananId, penjualUid, pembeliUid, notificationTitle, notificationDescription, notificationType);

            }
        }if (notificationType.equals("StatusPesanan")){

            String pembeliUid = remoteMessage.getData().get("pembeliUid");
            String penjualUid = remoteMessage.getData().get("penjualUid");
            String pesananId = remoteMessage.getData().get("pesananId");
            String notificationTitle = remoteMessage.getData().get("notificationTitle");
            String notificationDescription = remoteMessage.getData().get("notificationMessage");

            if (firebaseUser !=null && firebaseAuth.getUid().equals(pembeliUid)){
                showNotification(pesananId, penjualUid, pembeliUid,notificationTitle, notificationDescription, notificationType);

            }

        }

    }
    private void showNotification(String pesananId, String penjualUid, String pembeliUid, String notificationTitle
            , String notificationDescription, String notificationType){
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationID = new Random().nextInt(3000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            setUpNotificationChannel(notificationManager);
        }
        Intent intent = null;
        if (notificationType.equals("PesananMasuk")){
            intent = new Intent(this,DetailPesananPenjualActivity.class);
            intent.putExtra("pesananId", pesananId);
            intent.putExtra("pesananBy", pembeliUid);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        }else if (notificationType.equals("StatusPesanan")){
            intent = new Intent(this, DetailPesananPenggunaActivity.class);
            intent.putExtra("pesananId", pesananId);
            intent.putExtra("pesananTo", penjualUid);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),R.drawable.logo);

        Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANEL_ID);
        notificationBuilder.setSmallIcon(R.drawable.logo)
                .setLargeIcon(largeIcon)
                .setContentTitle(notificationTitle)
                .setContentText(notificationDescription)
                .setSound(notificationUri)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify(notificationID,notificationBuilder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setUpNotificationChannel(NotificationManager notificationManager) {
        CharSequence channelName ="Some Sample Text";
        String channelDescription = "Channel Description Here";

        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setDescription(channelDescription);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.enableVibration(true);

        if (notificationManager !=null){
            notificationManager.createNotificationChannel(notificationChannel);
        }

    }
}
